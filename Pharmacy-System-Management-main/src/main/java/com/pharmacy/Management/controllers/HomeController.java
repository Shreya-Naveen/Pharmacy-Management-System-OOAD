package com.pharmacy.Management.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.pharmacy.Management.repository.StockRepository;
import com.pharmacy.Management.services.HomeService;

@Controller
public class HomeController {
	@Autowired
	private HomeService homeService;

	@Autowired
	private StockRepository stockRepository;

	@GetMapping({"", "/", "/home"}) 
	public String home() { 
		return "home";
	}
	
	@GetMapping("/expert")
	public String expert() {
		return "AskExpert";
	}
	
	@GetMapping("/info")
	public String info() {
		return "info";
	}

	@GetMapping("/billing")
	public String redirectBillingToOrder() {
		return "redirect:/order";
	}

	@PostMapping("/question")
	public String submitQuestion(@RequestParam String name, 
							   @RequestParam String email, 
							   @RequestParam String question) {
		// Here you can handle the question submission
		// For now, we'll just redirect back to the expert page
		return "redirect:/expert?submitted";
	}
}
