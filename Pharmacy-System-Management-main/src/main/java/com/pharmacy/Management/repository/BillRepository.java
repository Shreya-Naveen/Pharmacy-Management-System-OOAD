package com.pharmacy.Management.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.pharmacy.Management.models.Bill;
import com.pharmacy.Management.models.Customer;

@Repository
public class BillRepository {
    
    private final JdbcTemplate jdbcTemplate;
    
    @Autowired
    public BillRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    private final RowMapper<Bill> billRowMapper = (ResultSet rs, int rowNum) -> {
        Bill bill = new Bill();
        bill.setId(rs.getLong("id"));
        
        // Create a Customer object with just the ID
        Customer customer = new Customer();
        customer.setId(rs.getLong("customer_id"));
        bill.setCustomer(customer);
        
        bill.setBillDate(rs.getTimestamp("bill_date").toLocalDateTime());
        bill.setTotalAmount(rs.getDouble("total_amount"));
        bill.setPaymentStatus(rs.getString("payment_status"));
        bill.setPaymentMethod(rs.getString("payment_method"));
        return bill;
    };
    
    public List<Bill> findAll() {
        String sql = "SELECT * FROM bills";
        return jdbcTemplate.query(sql, billRowMapper);
    }
    
    public Optional<Bill> findById(Long id) {
        String sql = "SELECT * FROM bills WHERE id = ?";
        List<Bill> results = jdbcTemplate.query(sql, billRowMapper, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
    
    public List<Bill> findByCustomerId(Long customerId) {
        String sql = "SELECT * FROM bills WHERE customer_id = ?";
        return jdbcTemplate.query(sql, billRowMapper, customerId);
    }
    
    public Bill save(Bill bill) {
        if (bill.getId() == null) {
            return insert(bill);
        } else {
            return update(bill);
        }
    }
    
    private Bill insert(Bill bill) {
        String sql = "INSERT INTO bills (customer_id, bill_date, total_amount, payment_status, payment_method) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, bill.getCustomer().getId());
            ps.setTimestamp(2, java.sql.Timestamp.valueOf(bill.getBillDate()));
            ps.setDouble(3, bill.getTotalAmount());
            ps.setString(4, bill.getPaymentStatus());
            ps.setString(5, bill.getPaymentMethod());
            return ps;
        }, keyHolder);
        
        bill.setId(keyHolder.getKey().longValue());
        return bill;
    }
    
    private Bill update(Bill bill) {
        String sql = "UPDATE bills SET customer_id = ?, bill_date = ?, total_amount = ?, " +
                     "payment_status = ?, payment_method = ? WHERE id = ?";
        
        jdbcTemplate.update(sql, 
            bill.getCustomer().getId(),
            java.sql.Timestamp.valueOf(bill.getBillDate()),
            bill.getTotalAmount(),
            bill.getPaymentStatus(),
            bill.getPaymentMethod(),
            bill.getId()
        );
        
        return bill;
    }
    
    public void deleteById(Long id) {
        String sql = "DELETE FROM bills WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
} 