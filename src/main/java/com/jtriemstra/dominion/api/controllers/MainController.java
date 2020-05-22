package com.jtriemstra.dominion.api.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtriemstra.dominion.api.dto.PlayerGameState;
import com.jtriemstra.dominion.api.models.Bank;
import com.jtriemstra.dominion.api.models.BankCard;
import com.jtriemstra.dominion.api.models.Card;
import com.jtriemstra.dominion.api.models.Game;
import com.jtriemstra.dominion.api.models.Player;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class MainController {
	
	Game game = new Game();
	
	@PostConstruct
	public void initialize() {
		
	    
	}
	
	/*@ExceptionHandler({ RuntimeException.class})
	private void handleError(RuntimeException e, HttpServletRequest request) {
		log.error("Handling error", e);
		String card = request.getParameter("card");
		String[] options = request.getParameterValues("options");
		logResponse(request.getParameter("playerName"), request.getServletPath().substring(1), card, options, "{\"error\":\"" + e.getMessage() + "\"}");
	}*/
	
	

	@CrossOrigin(origins = {"http://localhost:8001", "https://jtriemstra-dominion-ui.azurewebsites.net", "http://jtriemstra-dominion-ui.s3-website.us-east-2.amazonaws.com", "https://master.d1a91xjfjcbhnv.amplifyapp.com/"})
	@RequestMapping("/join")
	public PlayerGameState join(String playerName, HttpServletRequest request) {
		if (game.getPlayerCount() >= 4) {
			throw new RuntimeException("game is full");
		}
		
		Player newPlayer = new Player(playerName);
		game.addPlayer(newPlayer);
		newPlayer.init(game);
		
		PlayerGameState result = new PlayerGameState(newPlayer, game.getPlayerNames(), game.getCurrentPlayerIndex());
		//logResponse(result, "join", null, null);
		return result;
	}
	
	@CrossOrigin(origins = {"http://localhost:8001", "https://jtriemstra-dominion-ui.azurewebsites.net", "http://jtriemstra-dominion-ui.s3-website.us-east-2.amazonaws.com", "https://master.d1a91xjfjcbhnv.amplifyapp.com/"})
	@RequestMapping("/start")
	public PlayerGameState init(String playerName, HttpServletRequest request, boolean randomCards, @RequestParam(required=false) List<String> cardNames) {
		Bank bank;
		if (cardNames != null && cardNames.size() > 0) {			
			bank = new Bank(cardNames);
		}
		else {
			bank = new Bank(randomCards);
		}
		game = new Game(bank);
		
		Player newPlayer = new Player(playerName);
		game.addPlayer(newPlayer);
		newPlayer.init(game);
		
		PlayerGameState result = new PlayerGameState(newPlayer, game.getPlayerNames(), game.getCurrentPlayerIndex());
		//logResponse(result, "start", null, null);
		return result;
	}
	
	@CrossOrigin(origins = {"http://localhost:8001", "https://jtriemstra-dominion-ui.azurewebsites.net", "http://jtriemstra-dominion-ui.s3-website.us-east-2.amazonaws.com", "https://master.d1a91xjfjcbhnv.amplifyapp.com/"})
	@RequestMapping("/play")
	public PlayerGameState play(String card, String playerName) {
		validateCurrentPlayer(playerName);
		
		game.getPlayer(playerName).play(card);
		
		PlayerGameState result = new PlayerGameState(game.getPlayer(playerName), game.getPlayerNames(), game.getCurrentPlayerIndex());
		//logResponse(result, "play", card, null);
		return result;
	}
	
	@CrossOrigin(origins = {"http://localhost:8001", "https://jtriemstra-dominion-ui.azurewebsites.net", "http://jtriemstra-dominion-ui.s3-website.us-east-2.amazonaws.com", "https://master.d1a91xjfjcbhnv.amplifyapp.com/"})
	@RequestMapping("/buy")
	public PlayerGameState buy(String card, String playerName) {
		validateCurrentPlayer(playerName);
		
		game.getPlayer(playerName).buy(card);
		
		PlayerGameState result = new PlayerGameState(game.getPlayer(playerName), game.getPlayerNames(), game.getCurrentPlayerIndex());
		//logResponse(result, "buy", card, null);
		return result;
	}
	
	@CrossOrigin(origins = {"http://localhost:8001", "https://jtriemstra-dominion-ui.azurewebsites.net", "http://jtriemstra-dominion-ui.s3-website.us-east-2.amazonaws.com", "https://master.d1a91xjfjcbhnv.amplifyapp.com/"})
	@RequestMapping("/action")
	public PlayerGameState action(String[] options, String playerName) {
		List<String> optionsList = options == null ? new ArrayList<String>() : Arrays.asList(options);
		
		game.getPlayer(playerName).finishAction(optionsList);
		
		PlayerGameState result = new PlayerGameState(game.getPlayer(playerName), game.getPlayerNames(), game.getCurrentPlayerIndex());
		//logResponse(result, "action", null, options);
		return result;
	}
	
	@CrossOrigin(origins = {"http://localhost:8001", "https://jtriemstra-dominion-ui.azurewebsites.net", "http://jtriemstra-dominion-ui.s3-website.us-east-2.amazonaws.com", "https://master.d1a91xjfjcbhnv.amplifyapp.com/"})
	@RequestMapping("/cleanup")
	public PlayerGameState cleanup(String playerName) {
		validateCurrentPlayer(playerName);
		
		game.getPlayer(playerName).startCleanup();
		
		PlayerGameState result = new PlayerGameState(game.getPlayer(playerName), game.getPlayerNames(), game.getCurrentPlayerIndex());
		return result;
	}
	
	@CrossOrigin(origins = {"http://localhost:8001", "https://jtriemstra-dominion-ui.azurewebsites.net", "http://jtriemstra-dominion-ui.s3-website.us-east-2.amazonaws.com", "https://master.d1a91xjfjcbhnv.amplifyapp.com/"})
	@RequestMapping("/refresh")
	public PlayerGameState refresh(String playerName) {
		
		PlayerGameState result = new PlayerGameState(game.getPlayer(playerName), game.getPlayerNames(), game.getCurrentPlayerIndex());
		return result;
	}
	
	@CrossOrigin(origins = {"http://localhost:8001", "https://jtriemstra-dominion-ui.azurewebsites.net", "http://jtriemstra-dominion-ui.s3-website.us-east-2.amazonaws.com", "https://master.d1a91xjfjcbhnv.amplifyapp.com/"})
	@RequestMapping("/bank")
	public List<BankCard> bank() {
		//TODO: return an array like the properties of the Player object, so UI code is consistent
		log.info("bank called");
		return game.getBank().getBankCards();
	}
	
	@CrossOrigin(origins = {"http://localhost:8001", "https://jtriemstra-dominion-ui.azurewebsites.net", "http://jtriemstra-dominion-ui.s3-website.us-east-2.amazonaws.com", "https://master.d1a91xjfjcbhnv.amplifyapp.com/"})
	@RequestMapping("/end")
	public void end() {
		
		game = new Game();
	}
	
	private void validateCurrentPlayer(String playerName) {
		if (!playerName.equals(game.getCurrentPlayer().getName())) {
			throw new RuntimeException("this player is not the current player");
		}
	}
}
