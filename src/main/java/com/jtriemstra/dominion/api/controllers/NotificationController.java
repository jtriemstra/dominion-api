package com.jtriemstra.dominion.api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jtriemstra.dominion.api.service.NotificationService;

@RestController
@CrossOrigin(origins = {"http://localhost:8001", "https://jtriemstra-dominion-ui.azurewebsites.net", "http://jtriemstra-dominion-ui.s3-website.us-east-2.amazonaws.com", "https://master.d1a91xjfjcbhnv.amplifyapp.com", "http://localhost:3000", "http://localhost:6006"})
public class NotificationController {
	
	@Autowired
	private NotificationService service;
	
	@GetMapping("/v3/notifications")
	public List<String> notifications() {
		return service.getNotifications();
	}
}
