package com.pharmacy.Management.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.pharmacy.Management.models.BillItem;
import com.pharmacy.Management.models.Customer;
import com.pharmacy.Management.models.Stock;
import com.pharmacy.Management.repository.StockRepository;
import com.pharmacy.Management.services.BillingService;

@Controller
public class BillingController {

    @Autowired
    private BillingService billingService;

    @Autowired
    private StockRepository stockRepository;

    @PostMapping("/billing/create")
    public String createBill(
            @RequestParam String customerName,
            @RequestParam String customerContact,
            @RequestParam String customerEmail,
            @RequestParam String customerAddress,
            @RequestParam Integer customerAge,
            @RequestParam String customerGender,
            @RequestParam Map<String, String> allParams,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Create customer
            Customer customer = new Customer();
            customer.setName(customerName);
            customer.setContact(customerContact);
            customer.setEmail(customerEmail);
            customer.setAddress(customerAddress);
            customer.setAge(customerAge);
            customer.setGender(customerGender);

            // Create bill items
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
                    }
                }
            }

            // Create the bill
            billingService.createBill(customer, items);
            redirectAttributes.addFlashAttribute("success", "Invoice generated successfully!");
            return "redirect:/billing";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/billing";
        }
    }
} 