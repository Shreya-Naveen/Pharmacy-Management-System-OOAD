package com.pharmacy.Management.adapter;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pharmacy.Management.models.Bill;
import com.pharmacy.Management.models.BillItem;
import com.pharmacy.Management.models.Customer;
import com.pharmacy.Management.repository.BillItemRepository;

@Component
public class DefaultBillAdapter implements BillAdapter {
    private Bill bill;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Autowired
    private BillItemRepository billItemRepository;

    public DefaultBillAdapter() {
    }
    
    public DefaultBillAdapter(Bill bill) {
        this.bill = bill;
    }

    @Override
    public Bill adaptToBill() {
        return bill;
    }

    @Override
    public void adaptFromBill(Bill bill) {
        this.bill = bill;
    }

    @Override
    public String getFormattedBill() {
        StringBuilder sb = new StringBuilder();
        Customer customer = bill.getCustomer();
        
        sb.append("Bill Details:\n");
        sb.append("Bill ID: ").append(bill.getId()).append("\n");
        sb.append("Date: ").append(bill.getBillDate().format(DATE_FORMATTER)).append("\n");
        sb.append("Customer: ").append(customer.getName()).append("\n");
        sb.append("Contact: ").append(customer.getContact()).append("\n");
        sb.append("Payment Status: ").append(bill.getPaymentStatus()).append("\n");
        sb.append("Payment Method: ").append(bill.getPaymentMethod()).append("\n\n");
        
        sb.append("Items:\n");
        List<BillItem> items = billItemRepository.findByBillId(bill.getId());
        for (BillItem item : items) {
            sb.append("- ").append(item.getStock().getDrugName())
              .append(" x").append(item.getQuantity())
              .append(" @ ").append(item.getUnitPrice())
              .append(" = ").append(item.getTotalPrice()).append("\n");
        }
        
        sb.append("\nTotal Amount: ").append(bill.getTotalAmount());
        return sb.toString();
    }

    @Override
    public String getPaymentDetails() {
        return String.format("Payment Status: %s\nPayment Method: %s\nAmount: %.2f",
            bill.getPaymentStatus(),
            bill.getPaymentMethod(),
            bill.getTotalAmount());
    }
} 