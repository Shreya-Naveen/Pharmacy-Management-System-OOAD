package com.pharmacy.Management.facade;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pharmacy.Management.models.Bill;
import com.pharmacy.Management.models.BillItem;
import com.pharmacy.Management.models.Customer;
import com.pharmacy.Management.repository.CustomerRepository;
import com.pharmacy.Management.services.BillingService;
import com.pharmacy.Management.services.OrderService;

@Service
public class CustomerFacade {
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private BillingService billingService;
    
    @Autowired
    private OrderService orderService;
    
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }
    
    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id).orElse(null);
    }
    
    public Customer getCustomerByContact(String contact) {
        return customerRepository.findByContact(contact);
    }
    
    public Customer createOrUpdateCustomer(Customer customer) {
        Customer existingCustomer = customerRepository.findByContact(customer.getContact());
        if (existingCustomer != null) {
            // Update existing customer
            existingCustomer.setName(customer.getName());
            existingCustomer.setEmail(customer.getEmail());
            existingCustomer.setAddress(customer.getAddress());
            existingCustomer.setAge(customer.getAge());
            existingCustomer.setGender(customer.getGender());
            existingCustomer.setLastVisit(LocalDateTime.now());
            return customerRepository.save(existingCustomer);
        } else {
            // Create new customer
            customer.setLastVisit(LocalDateTime.now());
            customer.setTotalSpent(0.0);
            return customerRepository.save(customer);
        }
    }
    
    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }
    
    public Bill createOrder(Customer customer, List<BillItem> items) {
        return orderService.createOrder(customer, items);
    }
    
    public Bill createBill(Customer customer, List<BillItem> items) {
        return billingService.createBill(customer, items);
    }
    
    public List<Bill> getCustomerBills(Long customerId) {
        return billingService.getCustomerBills(customerId);
    }
    
    public List<Bill> getCustomerOrders(Long customerId) {
        return orderService.getCustomerOrders(customerId);
    }
    
    public List<BillItem> getOrderItems(Long orderId) {
        return orderService.getOrderItems(orderId);
    }
    
    public List<BillItem> getBillItems(Long billId) {
        return billingService.getBillItems(billId);
    }
} 