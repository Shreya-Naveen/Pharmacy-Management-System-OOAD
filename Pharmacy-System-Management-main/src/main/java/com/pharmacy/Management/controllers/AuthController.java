package com.pharmacy.Management.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.pharmacy.Management.models.User;
import com.pharmacy.Management.services.UserService;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user) {
        try {
            userService.registerUser(user);
            return "redirect:/login?registered";
        } catch (RuntimeException e) {
            return "redirect:/register?error";
        }
    }
    
    @GetMapping("/register/admin")
    public String showAdminRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register-admin";
    }
    
    @PostMapping("/register/admin")
    public String registerAdmin(@ModelAttribute("user") User user) {
        try {
            userService.registerAdmin(user);
            return "redirect:/login?registered";
        } catch (RuntimeException e) {
            return "redirect:/register/admin?error";
        }
    }
} 