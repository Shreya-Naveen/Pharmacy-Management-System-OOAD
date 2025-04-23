package com.pharmacy.Management.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pharmacy.Management.models.Bill;
import com.pharmacy.Management.models.BillItem;
import com.pharmacy.Management.models.Customer;
import com.pharmacy.Management.models.Stock;
import com.pharmacy.Management.repository.BillItemRepository;
import com.pharmacy.Management.repository.BillRepository;
import com.pharmacy.Management.repository.CustomerRepository;
import com.pharmacy.Management.repository.StockRepository;

@Service
public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private BillRepository billRepository;
    
    @Autowired
    private BillItemRepository billItemRepository;
    
    @Autowired
    private StockRepository stockRepository;
    
    @Transactional
    public Bill createOrder(Customer customer, List<BillItem> items) {
        logger.info("Starting order creation for customer: {}", customer.getName());

        // Collect drug names from items
        String orderedDrugs = items.stream()
                .map(item -> item.getStock().getDrugName())
                .collect(Collectors.joining(", "));
        logger.info("Ordered drugs: {}", orderedDrugs);

        // Check if customer exists by contact
        Customer existingCustomer = customerRepository.findByContact(customer.getContact());
        if (existingCustomer != null) {
            logger.info("Found existing customer: {}", existingCustomer.getName());
            // Update existing customer's last visit and total spent
            existingCustomer.setLastVisit(LocalDateTime.now());
            existingCustomer.setTotalSpent(existingCustomer.getTotalSpent() + calculateTotal(items));
            // Append new drugs to existing ordered drugs
            String existingDrugs = existingCustomer.getOrderedDrugs();
            existingCustomer.setOrderedDrugs(existingDrugs != null && !existingDrugs.isEmpty() 
                ? existingDrugs + ", " + orderedDrugs 
                : orderedDrugs);
            customerRepository.save(existingCustomer);
            
            // Use existing customer for the bill
            customer = existingCustomer;
        } else {
            logger.info("Creating new customer: {}", customer.getName());
            customer.setLastVisit(LocalDateTime.now());
            customer.setTotalSpent(calculateTotal(items));
            customer.setOrderedDrugs(orderedDrugs);
            customer = customerRepository.save(customer);
        }
        
        // Create new bill
        Bill bill = new Bill();
        bill.setCustomer(customer);
        bill.setBillDate(LocalDateTime.now());
        bill.setPaymentStatus("PAID");
        bill.setTotalAmount(calculateTotal(items));
        bill = billRepository.save(bill);
        logger.info("Created new bill with ID: {}", bill.getId());
        
        // Process each item
        for (BillItem item : items) {
            Stock stock = item.getStock();
            int quantity = item.getQuantity();
            
            // Update stock quantity
            if (stock.getQuantity() < quantity) {
                throw new RuntimeException("Insufficient stock for " + stock.getDrugName());
            }
            stock.setQuantity(stock.getQuantity() - quantity);
            stockRepository.save(stock);
            logger.info("Updated stock for {}: -{} units", stock.getDrugName(), quantity);
            
            // Save bill item
            item.setBill(bill);
            item.setUnitPrice((double) stock.getPrice());
            item.setTotalPrice((double) (stock.getPrice() * quantity));
            billItemRepository.save(item);
            logger.info("Saved bill item: {} x {}", stock.getDrugName(), quantity);
        }
        
        logger.info("Order creation completed successfully");
        return bill;
    }
    
    private double calculateTotal(List<BillItem> items) {
        return items.stream()
                .mapToDouble(item -> item.getStock().getPrice() * item.getQuantity())
                .sum();
    }
    
    public List<Bill> getCustomerOrders(Long customerId) {
        return billRepository.findByCustomerId(customerId);
    }
    
    public List<BillItem> getOrderItems(Long orderId) {
        return billItemRepository.findByBillId(orderId);
    }
} 