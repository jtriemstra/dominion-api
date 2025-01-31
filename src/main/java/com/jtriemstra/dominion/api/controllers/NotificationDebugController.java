package com.jtriemstra.dominion.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import com.jtriemstra.dominion.api.service.NotificationService;

@Controller
@CrossOrigin(origins = {"http://localhost:8001", "https://jtriemstra-dominion-ui.azurewebsites.net", "http://jtriemstra-dominion-ui.s3-website.us-east-2.amazonaws.com", "https://master.d1a91xjfjcbhnv.amplifyapp.com", "http://localhost:3000", "http://localhost:6006"})
public class NotificationDebugController {
	
	@Autowired
	private NotificationService service;
	
	@GetMapping("/notificationDebug")
	public String notifications(Model model) {
		model.addAttribute("notifications", service.getNotifications());
		return "notifications";
	}
}
