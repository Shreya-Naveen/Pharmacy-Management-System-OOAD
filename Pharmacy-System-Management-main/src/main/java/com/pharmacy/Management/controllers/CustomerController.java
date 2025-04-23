package com.pharmacy.Management.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.pharmacy.Management.facade.CustomerFacade;
import com.pharmacy.Management.models.Customer;

@Controller
@RequestMapping("/customers")
public class CustomerController {
    
    @Autowired
    private CustomerFacade customerFacade;
    
    @GetMapping
    public String viewCustomers(Model model) {
        model.addAttribute("customers", customerFacade.getAllCustomers());
        return "customers";
    }
    
    @GetMapping("/{id}")
    public String viewCustomerDetails(@PathVariable Long id, Model model) {
        Customer customer = customerFacade.getCustomerById(id);
        if (customer != null) {
            model.addAttribute("customer", customer);
            model.addAttribute("bills", customerFacade.getCustomerBills(id));
            model.addAttribute("orders", customerFacade.getCustomerOrders(id));
            return "customer-details";
        }
        return "redirect:/customers";
    }
    
    @GetMapping("/new")
    public String showCustomerForm(Model model) {
        model.addAttribute("customer", new Customer());
        return "customer-form";
    }
    
    @PostMapping("/save")
    public String saveCustomer(@ModelAttribute Customer customer) {
        customerFacade.createOrUpdateCustomer(customer);
        return "redirect:/customers";
    }
    
    @GetMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable Long id) {
        customerFacade.deleteCustomer(id);
        return "redirect:/customers";
    }
} 