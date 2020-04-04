package com.jtriemstra.dominion.api.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jtriemstra.dominion.api.dto.PlayerGameState;
import com.jtriemstra.dominion.api.models.Bank;
import com.jtriemstra.dominion.api.models.BankCard;
import com.jtriemstra.dominion.api.models.Card;
import com.jtriemstra.dominion.api.models.Game;
import com.jtriemstra.dominion.api.models.Player;

@RestController
public class MainController {
	
	Game game = new Game();
	
	@PostConstruct
	public void initialize() {

	}

	@CrossOrigin(origins = "http://localhost:8001")
	@RequestMapping("/join")
	public PlayerGameState join(String playerName, HttpServletRequest request) {
		if (game.getPlayerCount() >= 4) {
			throw new RuntimeException("game is full");
		}
		
		Player newPlayer = new Player(playerName);
		game.addPlayer(newPlayer);
		newPlayer.init(game);
		
		return new PlayerGameState(newPlayer, game.getPlayerNames(), game.getCurrentPlayerIndex());
	}
	
	@CrossOrigin(origins = "http://localhost:8001")
	@RequestMapping("/start")
	public PlayerGameState init(String playerName, HttpServletRequest request, boolean randomCards) {
		Bank bank = new Bank(randomCards);
		game = new Game(bank);
		
		Player newPlayer = new Player(playerName);
		game.addPlayer(newPlayer);
		newPlayer.init(game);
		
		return new PlayerGameState(newPlayer, game.getPlayerNames(), game.getCurrentPlayerIndex());
	}
	
	@CrossOrigin(origins = "http://localhost:8001")
	@RequestMapping("/play")
	public PlayerGameState play(String card, String playerName) {
		validateCurrentPlayer(playerName);
		
		game.getPlayer(playerName).play(card);
		
		return new PlayerGameState(game.getPlayer(playerName), game.getPlayerNames(), game.getCurrentPlayerIndex());
	}
	
	@CrossOrigin(origins = "http://localhost:8001")
	@RequestMapping("/buy")
	public PlayerGameState buy(String card, String playerName) {
		validateCurrentPlayer(playerName);
		
		game.getPlayer(playerName).buy(card);
		
		return new PlayerGameState(game.getPlayer(playerName), game.getPlayerNames(), game.getCurrentPlayerIndex());
	}
	
	@CrossOrigin(origins = "http://localhost:8001")
	@RequestMapping("/action")
	public PlayerGameState action(String[] options, String playerName) {
		List<String> optionsList = options == null ? new ArrayList<String>() : Arrays.asList(options);
		
		game.getPlayer(playerName).finishAction(optionsList);
		
		return new PlayerGameState(game.getPlayer(playerName), game.getPlayerNames(), game.getCurrentPlayerIndex());
	}
	
	@CrossOrigin(origins = "http://localhost:8001")
	@RequestMapping("/cleanup")
	public PlayerGameState cleanup(String playerName) {
		validateCurrentPlayer(playerName);
		
		game.getPlayer(playerName).cleanup();
		
		return new PlayerGameState(game.getPlayer(playerName), game.getPlayerNames(), game.getCurrentPlayerIndex());
	}
	
	@CrossOrigin(origins = "http://localhost:8001")
	@RequestMapping("/refresh")
	public PlayerGameState refresh(String playerName) {
		
		return new PlayerGameState(game.getPlayer(playerName), game.getPlayerNames(), game.getCurrentPlayerIndex());
	}
	
	@CrossOrigin(origins = "http://localhost:8001")
	@RequestMapping("/bank")
	public HashMap<String, BankCard> bank() {
		//TODO: return an array like the properties of the Player object, so UI code is consistent
		
		return game.getBank().getBankCards();
	}
	
	@CrossOrigin(origins = "http://localhost:8001")
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
