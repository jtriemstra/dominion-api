package com.jtriemstra.dominion.api.controllers;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jtriemstra.dominion.api.models.Game;
import com.jtriemstra.dominion.api.models.Player;

@RestController
public class MainController {
	
	Game game = new Game();
	
	@PostConstruct
	public void initialize() {
		game.getPlayers().add(new Player());		
	}

	@RequestMapping("/start")
	public Player init(String card) {
		game.getPlayers().get(0).init();
		
		return game.getPlayers().get(0);
	}
	
	@RequestMapping("/play")
	public Player play(String card) {
		game.getPlayers().get(0).play(card);
		
		return game.getPlayers().get(0);
	}
	
	@RequestMapping("/buy")
	public Player buy(String card) {
		game.getPlayers().get(0).buy(card);
		
		return game.getPlayers().get(0);
	}
	
	@RequestMapping("/action")
	public Player action(String[] options) {
		game.getPlayers().get(0).finishAction(Arrays.asList(options));
		
		return game.getPlayers().get(0);
	}
	
	@RequestMapping("/cleanup")
	public Player cleanup() {
		game.getPlayers().get(0).cleanup();
		
		return game.getPlayers().get(0);
	}
	
}
