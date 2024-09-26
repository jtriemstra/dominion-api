package com.jtriemstra.dominion.api.service;

import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jtriemstra.dominion.api.dto.GameState;
import com.jtriemstra.dominion.api.dto.PlayerState;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class GameService {
	
	ActionService actionService;
	PlayerService playerService;
	
	public void addPlayer(GameState game, String name) {
		game.getPlayers().put(name, new PlayerState(name));
		game.getPlayerNames().add(name);
		actionService.startingHand(game, name); 
	}
	
	public boolean isGameOver(GameState game) {
		boolean result = game.getBank().getSupplies().values().stream().filter(s -> s.getCount() == 0).count() >= (long) 3;
		result = result || game.getBank().getSupplies().get(ActionService.PROVINCE).getCount() == 0;
		
		return result;
	}
	
	public void endTurn(GameState game) {
		if (isGameOver(game)) {
			game.setCurrentPlayer(-1);
			for (String s : game.getPlayerNames()) {
				playerService.calculatePoints(game, s);
			}
		} else {
			nextPlayer(game);
		}
	}
	
	public void nextPlayer(GameState game) {
		int playerIndex = game.getCurrentPlayer();
		playerIndex = playerIndex >= game.getPlayers().size() - 1 ? 0 : playerIndex + 1;
		game.setCurrentPlayer(playerIndex);
		playerService.startTurn(game, game.getPlayerNames().get(playerIndex));
	}
}
