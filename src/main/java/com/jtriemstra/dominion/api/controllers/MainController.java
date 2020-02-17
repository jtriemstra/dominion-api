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
		game.getPlayers().add(new Player());		
	}

	@CrossOrigin(origins = "http://localhost:8001")
	@RequestMapping("/start")
	public Player init(String card) {
		game.getPlayers().get(0).init(game);
		
		return game.getPlayers().get(0);
	}
	
	@CrossOrigin(origins = "http://localhost:8001")
	@RequestMapping("/play")
	public Player play(String card) {
		game.getPlayers().get(0).play(card);
		
		return game.getPlayers().get(0);
	}
	
	@CrossOrigin(origins = "http://localhost:8001")
	@RequestMapping("/buy")
	public Player buy(String card) {
		game.getPlayers().get(0).buy(card);
		
		return game.getPlayers().get(0);
	}
	
	@CrossOrigin(origins = "http://localhost:8001")
	@RequestMapping("/action")
	public Player action(String[] options) {
		game.getPlayers().get(0).finishAction(Arrays.asList(options));
		
		return game.getPlayers().get(0);
	}
	
	@CrossOrigin(origins = "http://localhost:8001")
	@RequestMapping("/cleanup")
	public Player cleanup() {
		game.getPlayers().get(0).cleanup();
		
		return game.getPlayers().get(0);
	}
	
	@CrossOrigin(origins = "http://localhost:8001")
	@RequestMapping("/refresh")
	public Player refresh() {
		
		return game.getPlayers().get(0);
	}
	
	@CrossOrigin(origins = "http://localhost:8001")
	@RequestMapping("/bank")
	public HashMap<String, Card> bank() {
		//TODO: return an array like the properties of the Player object, so UI code is consistent
		return game.getBank().getBankCards();
	}
}
