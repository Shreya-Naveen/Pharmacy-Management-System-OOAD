package com.pharmacy.Management.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pharmacy.Management.adapter.BillAdapter;
import com.pharmacy.Management.adapter.BillAdapterFactory;
import com.pharmacy.Management.models.Bill;
import com.pharmacy.Management.models.BillItem;
import com.pharmacy.Management.models.Customer;
import com.pharmacy.Management.models.Stock;
import com.pharmacy.Management.repository.BillItemRepository;
import com.pharmacy.Management.repository.BillRepository;
import com.pharmacy.Management.repository.CustomerRepository;
import com.pharmacy.Management.repository.StockRepository;

@Service
public class BillingService {
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private BillRepository billRepository;
    
    @Autowired
    private BillItemRepository billItemRepository;
    
    @Autowired
    private StockRepository stockRepository;
    
    @Autowired
    private BillAdapterFactory billAdapterFactory;
    
    @Transactional
    public Bill createBill(Customer customer, List<BillItem> items) {
        // Save or update customer
        Customer existingCustomer = customerRepository.findByContact(customer.getContact());
        if (existingCustomer != null) {
            existingCustomer.setLastVisit(LocalDateTime.now());
            customer = customerRepository.save(existingCustomer);
        } else {
            customer.setLastVisit(LocalDateTime.now());
            customer.setTotalSpent(0.0);
            customer = customerRepository.save(customer);
        }
        
        // Create new bill
        Bill bill = new Bill();
        bill.setCustomer(customer);
        bill.setBillDate(LocalDateTime.now());
        bill.setPaymentStatus("PENDING");
        bill.setTotalAmount(0.0);
        
        double totalAmount = 0.0;
        
        // Process each item
        for (BillItem item : items) {
            // Get stock item
            Optional<Stock> stockOpt = stockRepository.findById(item.getStock().getDrugID());
            if (!stockOpt.isPresent()) {
                throw new RuntimeException("Stock item not found");
            }
            
            Stock stock = stockOpt.get();
            
            // Check if enough quantity available
            if (stock.getQuantity() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for " + stock.getDrugName());
            }
            
            // Update stock quantity
            stock.setQuantity(stock.getQuantity() - item.getQuantity());
            stockRepository.save(stock);
            
            // Set item details
            item.setBill(bill);
            item.setUnitPrice((double) stock.getPrice());
            item.setTotalPrice((double) (stock.getPrice() * item.getQuantity()));
            
            totalAmount += item.getTotalPrice();
        }
        
        // Set bill total
        bill.setTotalAmount(totalAmount);
        bill = billRepository.save(bill);
        
        // Save bill items
        for (BillItem item : items) {
            billItemRepository.save(item);
        }
        
        // Update customer's total spent
        customer.setTotalSpent(customer.getTotalSpent() + totalAmount);
        customerRepository.save(customer);
        
        return bill;
    }
    
    public List<Bill> getCustomerBills(Long customerId) {
        return billRepository.findByCustomerId(customerId);
    }
    
    public List<BillItem> getBillItems(Long billId) {
        return billItemRepository.findByBillId(billId);
    }
    
    public String getFormattedBill(Bill bill) {
        BillAdapter adapter = billAdapterFactory.createBillAdapter(bill);
        return adapter.getFormattedBill();
    }
    
    public String getPaymentDetails(Bill bill) {
        BillAdapter adapter = billAdapterFactory.createBillAdapter(bill);
        return adapter.getPaymentDetails();
    }
} 