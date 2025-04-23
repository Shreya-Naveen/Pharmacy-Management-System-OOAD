package com.pharmacy.Management.factory;

import org.springframework.stereotype.Component;

import com.pharmacy.Management.models.Role;
import com.pharmacy.Management.models.User;

@Component
public class UserFactoryImpl implements UserFactory {
    
    @Override
    public User createUser(String username, String password, String email) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setRole(Role.CUSTOMER); // Default role
        return user;
    }
    
    public User createAdmin(String username, String password, String email) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setRole(Role.ADMIN);
        return user;
    }
} 