package com.pharmacy.Management.adapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pharmacy.Management.models.Bill;

@Component
public class BillAdapterFactory {
    
    @Autowired
    private DefaultBillAdapter defaultBillAdapter;
    
    public BillAdapter createBillAdapter(Bill bill) {
        DefaultBillAdapter adapter = new DefaultBillAdapter();
        adapter.adaptFromBill(bill);
        return adapter;
    }
} 