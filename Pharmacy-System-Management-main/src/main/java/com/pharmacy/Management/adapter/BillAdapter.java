package com.pharmacy.Management.adapter;

import com.pharmacy.Management.models.Bill;

public interface BillAdapter {
    Bill adaptToBill();
    void adaptFromBill(Bill bill);
    String getFormattedBill();
    String getPaymentDetails();
} 