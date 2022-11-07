package com.jtriemstra.dominion.api.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtriemstra.dominion.api.dto.PlayerGameState;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DynamoDbLogService implements ILogService {
	private static final String AWS_REGION = "us-east-2";
	private static final String LOG_TABLE_NAME = "dominion-api-log";
	
	private Table logTable;
	
	@PostConstruct
	public void init() {
		AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion(AWS_REGION).build();
		DynamoDB dynamoDB = new DynamoDB(client);
		
		logTable = dynamoDB.getTable(LOG_TABLE_NAME);
	}
			
	@Override
	public void logResponse(String playerName, String action, String card, String[] options, String result) {
		
		Item item = new Item()
			    .withPrimaryKey("rowid", UUID.randomUUID().toString())
			    .withString("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss.SSS")))
			    .withString("player", playerName)
			    .withString("action", action)
			    .withString("nonRefreshAction", action.equals("/refresh") ? "no" : "yes")
			    .withString("card", card == null ? "" : card)
			    .withList("options", options == null ? new String[] {""} : options)
			    .withJSON("actionResult", result)
			    .withString("dummy", "dummy for sorting");				
		
		logTable.putItem(item);					
	}
	
	@Override
	public void logResponse(PlayerGameState result, String action, String card, String[] options) {
		log.info("Trying to log");
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			logResponse(result.getThisPlayer().getName(), action, card, options, objectMapper.writeValueAsString(result));
		}
		catch (Exception e) {
			log.error("Error logging response", e);
		}
	}
}
