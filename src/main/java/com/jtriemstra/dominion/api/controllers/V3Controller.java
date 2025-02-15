package com.jtriemstra.dominion.api.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.jtriemstra.dominion.api.dto.BankState;
import com.jtriemstra.dominion.api.dto.CardData;
import com.jtriemstra.dominion.api.dto.GameState;
import com.jtriemstra.dominion.api.dto.PlayerGameState;
import com.jtriemstra.dominion.api.dto.PlayerState;
import com.jtriemstra.dominion.api.dto.V3BankCard;
import com.jtriemstra.dominion.api.dto.V3GameState;
import com.jtriemstra.dominion.api.models.Bank;
import com.jtriemstra.dominion.api.models.BankCard;
import com.jtriemstra.dominion.api.models.Card;
import com.jtriemstra.dominion.api.models.Game;
import com.jtriemstra.dominion.api.models.Player;
import com.jtriemstra.dominion.api.service.ActionService;
import com.jtriemstra.dominion.api.service.BankService;
import com.jtriemstra.dominion.api.service.GameService;
import com.jtriemstra.dominion.api.service.NotificationService;
import com.jtriemstra.dominion.api.service.PlayerService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@CrossOrigin(origins = {"http://localhost:8001", "https://jtriemstra-dominion-ui.azurewebsites.net", "http://jtriemstra-dominion-ui.s3-website.us-east-2.amazonaws.com", "https://master.d1a91xjfjcbhnv.amplifyapp.com", "http://localhost:3000"})
public class V3Controller {
	
	GameState game;
	
	@Autowired
	GameService gameService;
	@Autowired
	BankService bankService;
	@Autowired
	ActionService actionService;
	@Autowired
	PlayerService playerService;
	@Autowired
	NotificationService notificationService;
	
	
	@PostConstruct
	public void initialize() {
		
	    
	}
	
	@RequestMapping("/v3/cards")
	public Map<String, CardData> cards() {
		return CardData.cardInfo;
	}
	
	@RequestMapping("/v3/join")
	public V3GameState join(String playerName, HttpServletRequest request) {
		if (game.getPlayers().size() >= 4) {
			//throw new RuntimeException("game is full");
			game.getBank().getSupplies().get(ActionService.PROVINCE).setCount(15);
		}
		
		gameService.addPlayer(game, playerName);
			
		return new V3GameState(game, playerName);
	}
	
	@RequestMapping("/v3/start")
	public V3GameState init(String playerName, HttpServletRequest request, boolean randomCards, @RequestParam(required=false) List<String> cardNames) {
		BankState bank;
		if (cardNames != null && cardNames.size() > 0) {			
			bank = bankService.create(cardNames);
		}
		else {
			bank = bankService.createRandom();
		}
		game = new GameState(bank);
		
		gameService.addPlayer(game, playerName);
		actionService.init();
		playerService.startTurn(game, playerName);
		
		return new V3GameState(game, playerName);
	}
	
	@RequestMapping("/v3/play")
	public V3GameState play(String card, String playerName) {
		validateCurrentPlayer(playerName);
		
		actionService.turnPlay(game, playerName, card);
		
		return new V3GameState(game, playerName);
	}
	
	@RequestMapping("/v3/buy")
	public V3GameState buy(String card, String playerName) {
		validateCurrentPlayer(playerName);
		
		actionService.doBuy(game, playerName, card);
		
		return new V3GameState(game, playerName);
	}
	
	@RequestMapping("/v3/action")
	public V3GameState action(String[] options, String playerName) {
		List<String> optionsList = options == null ? new ArrayList<String>() : Arrays.asList(options);
		game.getPlayers().get(playerName).getTurn().getChoicesMade().addAll(optionsList);
		
		actionService.doChoice(game, playerName);

		return new V3GameState(game, playerName);
	}
	
	@RequestMapping("/v3/cleanup")
	public V3GameState cleanup(String playerName) {
		validateCurrentPlayer(playerName);
		
		if (game.getPlayers().get(playerName).getTurn().getChoicesAvailable().size() == 0) {
			actionService.cleanup(game, playerName);
			gameService.endTurn(game);
		}
		
		return new V3GameState(game, playerName);
	}

	@RequestMapping("/v3/skipAction")
	public V3GameState skipAction(String playerName) {
		validateCurrentPlayer(playerName);
		
		if (game.getPlayers().get(playerName).getTurn().getChoicesAvailable().size() == 0) {
			playerService.skipActions(game, playerName);
		}
		
		return new V3GameState(game, playerName);
	}
	
	@RequestMapping("/v3/refresh")
	public V3GameState refresh(String playerName) {
		if (game != null) {
			return new V3GameState(game, playerName);
		}
		return null;
	}
	
	@RequestMapping("/v3/bank")
	public List<V3BankCard> bank() {
		//TODO: return an array like the properties of the Player object, so UI code is consistent
		List<V3BankCard> result = new ArrayList<>();
		if (game != null) {
			for (String card : game.getBank().getSupplies().keySet()) {
				if (card.equals("Gold") ||
					card.equals("Silver") ||
					card.equals("Copper") ||
					card.equals("Estate") ||
					card.equals("Duchy") ||
					card.equals("Province") ||
					card.equals("Curse")) {				
				}
				else {
					result.add(new V3BankCard(card, game.getBank().getSupplies().get(card).getCount()));
				}			
			}
			
			result.sort((c1, c2) -> CardData.cardInfo.get(c1.getName()).getCost() - CardData.cardInfo.get(c2.getName()).getCost());
			result.add(0, new V3BankCard("Copper", game.getBank().getSupplies().get("Copper").getCount()));
			result.add(0, new V3BankCard("Silver", game.getBank().getSupplies().get("Silver").getCount()));
			result.add(0, new V3BankCard("Gold", game.getBank().getSupplies().get("Gold").getCount()));
			result.add(new V3BankCard("Estate", game.getBank().getSupplies().get("Estate").getCount()));
			result.add(new V3BankCard("Duchy", game.getBank().getSupplies().get("Duchy").getCount()));
			result.add(new V3BankCard("Province", game.getBank().getSupplies().get("Province").getCount()));
			result.add(new V3BankCard("Curse", game.getBank().getSupplies().get("Curse").getCount()));
		}
		
		return result;
	}
	
	
	@RequestMapping("/v3/end")
	public void end() {
		game = null;
		notificationService.clearNotifications();
	}
	
	@RequestMapping("/v3/activeGame")
	public HashMap<String, String> activeGame() {
		HashMap<String, String> result = new HashMap<>();
		result.put("activeGame", (game != null ? game.getId().toString() : ""));
		result.put("players", (game != null ? Integer.toString(game.getPlayers().size()) : ""));
		return result;
	}
	
	private void validateCurrentPlayer(String playerName) {
		/*
		 * PlayerState player = game.getPlayers().get(playerName);
		 * 
		 * if (!player.getTurn().isActive()) { throw new
		 * RuntimeException("this player is not the current player"); }
		 */
	}
}
