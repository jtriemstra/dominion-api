package com.jtriemstra.dominion.api.controllers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	@RequestMapping("/start")
	public Player init(String playerName) {
		if (game.getPlayerCount() >= 4) {
			throw new RuntimeException("game is full");
		}
		
		Player newPlayer = new Player(playerName);
		game.addPlayer(newPlayer);
		newPlayer.init(game);
		
		return newPlayer;
	}
	
	@CrossOrigin(origins = "http://localhost:8001")
	@RequestMapping("/play")
	public Player play(String card, String playerName) {
		game.getPlayer(playerName).play(card);
		
		return game.getPlayer(playerName);
	}
	
	@CrossOrigin(origins = "http://localhost:8001")
	@RequestMapping("/buy")
	public Player buy(String card, String playerName) {
		game.getPlayer(playerName).buy(card);
		
		return game.getPlayer(playerName);
	}
	
	@CrossOrigin(origins = "http://localhost:8001")
	@RequestMapping("/action")
	public Player action(String[] options, String playerName) {
		game.getPlayer(playerName).finishAction(Arrays.asList(options));
		
		return game.getPlayer(playerName);
	}
	
	@CrossOrigin(origins = "http://localhost:8001")
	@RequestMapping("/cleanup")
	public Player cleanup(String playerName) {
		game.getPlayer(playerName).cleanup();
		
		return game.getPlayer(playerName);
	}
	
	@CrossOrigin(origins = "http://localhost:8001")
	@RequestMapping("/refresh")
	public Player refresh(String playerName) {
		
		return game.getPlayer(playerName);
	}
	
	@CrossOrigin(origins = "http://localhost:8001")
	@RequestMapping("/bank")
	public HashMap<String, Card> bank() {
		//TODO: return an array like the properties of the Player object, so UI code is consistent
		return game.getBank().getBankCards();
	}
}
