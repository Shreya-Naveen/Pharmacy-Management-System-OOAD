package com.pharmacy.Management.factory;

import com.pharmacy.Management.models.User;

public interface UserFactory {
    User createUser(String username, String password, String email);
} 