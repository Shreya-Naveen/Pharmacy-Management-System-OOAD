package com.pharmacy.Management.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.pharmacy.Management.models.BillItem;
import com.pharmacy.Management.models.Customer;
import com.pharmacy.Management.models.Stock;
import com.pharmacy.Management.repository.StockRepository;
import com.pharmacy.Management.services.OrderService;

@Controller
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private StockRepository stockRepository;

    @GetMapping("/order")
    public String showOrderForm(Model model) {
        model.addAttribute("stocks", stockRepository.findAll());
        return "invoice"; // We're still using the invoice.html template but with modified content
    }

    @PostMapping("/order/create")
    public String createOrder(
            @RequestParam String customerName,
            @RequestParam String customerContact,
            @RequestParam String customerEmail,
            @RequestParam String customerAddress,
            @RequestParam Integer customerAge,
            @RequestParam String customerGender,
            @RequestParam Map<String, String> allParams,
            RedirectAttributes redirectAttributes) {
        
        try {
            logger.info("Creating order for customer: {}", customerName);
            
            // Create customer
            Customer customer = new Customer();
            customer.setName(customerName);
            customer.setContact(customerContact);
            customer.setEmail(customerEmail);
            customer.setAddress(customerAddress);
            customer.setAge(customerAge);
            customer.setGender(customerGender);

            // Create order items
            List<BillItem> items = new ArrayList<>();
            
            // Process each item from the form
            for (Map.Entry<String, String> entry : allParams.entrySet()) {
                String key = entry.getKey();
                if (key.startsWith("items[") && key.endsWith("].quantity")) {
                    // Extract drug ID from the key
                    String drugIdStr = key.substring(key.indexOf("[") + 1, key.indexOf("]"));
                    int drugId = Integer.parseInt(drugIdStr);
                    
                    // Get the quantity
                    int quantity = Integer.parseInt(entry.getValue());
                    
                    // Get the stock
                    Stock stock = stockRepository.findById(drugId).orElse(null);
                    if (stock != null) {
                        BillItem item = new BillItem();
                        item.setStock(stock);
                        item.setQuantity(quantity);
                        items.add(item);
                        logger.info("Added item to order: {} - Quantity: {}", stock.getDrugName(), quantity);
                    }
                }
            }

            if (items.isEmpty()) {
                throw new RuntimeException("No items added to the order");
            }

            // Create the order
            orderService.createOrder(customer, items);
            logger.info("Order created successfully for customer: {}", customerName);
            redirectAttributes.addFlashAttribute("success", "Order placed successfully!");
            return "redirect:/order";
        } catch (Exception e) {
            logger.error("Error creating order: ", e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/order";
        }
    }
} 