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

import com.pharmacy.Management.models.Customer;

@Repository
public class CustomerRepository {
    
    private final JdbcTemplate jdbcTemplate;
    
    @Autowired
    public CustomerRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    private final RowMapper<Customer> customerRowMapper = (ResultSet rs, int rowNum) -> {
        Customer customer = new Customer();
        customer.setId(rs.getLong("id"));
        customer.setName(rs.getString("name"));
        customer.setContact(rs.getString("contact"));
        customer.setAddress(rs.getString("address"));
        customer.setAge(rs.getInt("age"));
        customer.setGender(rs.getString("gender"));
        customer.setLastVisit(rs.getTimestamp("last_visit").toLocalDateTime());
        customer.setTotalSpent(rs.getDouble("total_spent"));
        customer.setEmail(rs.getString("email"));
        customer.setOrderedDrugs(rs.getString("ordered_drugs"));
        return customer;
    };
    
    public List<Customer> findAll() {
        String sql = "SELECT * FROM customers";
        return jdbcTemplate.query(sql, customerRowMapper);
    }
    
    public Optional<Customer> findById(Long id) {
        String sql = "SELECT * FROM customers WHERE id = ?";
        List<Customer> results = jdbcTemplate.query(sql, customerRowMapper, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
    
    public Customer findByContact(String contact) {
        String sql = "SELECT * FROM customers WHERE contact = ?";
        List<Customer> results = jdbcTemplate.query(sql, customerRowMapper, contact);
        return results.isEmpty() ? null : results.get(0);
    }
    
    public Customer save(Customer customer) {
        if (customer.getId() == null) {
            return insert(customer);
        } else {
            return update(customer);
        }
    }
    
    private Customer insert(Customer customer) {
        String sql = "INSERT INTO customers (name, contact, address, age, gender, last_visit, total_spent, email, ordered_drugs) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, customer.getName());
            ps.setString(2, customer.getContact());
            ps.setString(3, customer.getAddress());
            ps.setInt(4, customer.getAge());
            ps.setString(5, customer.getGender());
            ps.setTimestamp(6, java.sql.Timestamp.valueOf(customer.getLastVisit()));
            ps.setDouble(7, customer.getTotalSpent());
            ps.setString(8, customer.getEmail());
            ps.setString(9, customer.getOrderedDrugs());
            return ps;
        }, keyHolder);
        
        customer.setId(keyHolder.getKey().longValue());
        return customer;
    }
    
    private Customer update(Customer customer) {
        String sql = "UPDATE customers SET name = ?, contact = ?, address = ?, age = ?, gender = ?, " +
                     "last_visit = ?, total_spent = ?, email = ?, ordered_drugs = ? WHERE id = ?";
        
        jdbcTemplate.update(sql, 
            customer.getName(),
            customer.getContact(),
            customer.getAddress(),
            customer.getAge(),
            customer.getGender(),
            java.sql.Timestamp.valueOf(customer.getLastVisit()),
            customer.getTotalSpent(),
            customer.getEmail(),
            customer.getOrderedDrugs(),
            customer.getId()
        );
        
        return customer;
    }
    
    public void deleteById(Long id) {
        String sql = "DELETE FROM customers WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
} 