package com.jtriemstra.dominion.api.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.jtriemstra.dominion.api.models.Card;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class V3GameState {
	private PlayerState thisPlayer;
	private List<String> playerNames;
	private int currentPlayer;
	
	public V3GameState(GameState game, String playerName) {
		if (game != null) {
			thisPlayer = game.getPlayers().get(playerName);
			playerNames = new ArrayList<>(game.getPlayerNames());
			currentPlayer = game.getCurrentPlayer();
		}
	}
	
	@JsonGetter(value = "isCurrentPlayer")
	public boolean isCurrentPlayer() {
		if (isGameOver()) return false;
		
		return thisPlayer.getName().equals(playerNames.get(currentPlayer));
	}
	
	@JsonGetter(value = "currentPlayerIndex")
	public int getCurrentPlayerIndex() {
		return currentPlayer;
	}
	
	@JsonGetter(value = "isGameOver")
	public boolean isGameOver() {
		return currentPlayer == -1;
	}
	
	//TODO: does this really make sense, since it only matters at end of game?
	@JsonGetter(value = "points")
	public int points() {
		if (isGameOver()) {
			return thisPlayer.getPoints();
		}
		
		return 0;
	}

	//TODO: does this really make sense, since it only matters at end of game?
	@JsonGetter(value = "cards")
	public Map<String, Integer> allCards() {
		HashMap<String, Integer> allCards = new HashMap<>();
		for (String c : thisPlayer.getHand().getCards()) {
			allCards.merge(c, 1, Integer::sum);
		}
		for (String c : thisPlayer.getDeck().getCards()) {
			allCards.merge(c, 1, Integer::sum);
		}
		for (String c : thisPlayer.getDiscard().getCards()) {
			allCards.merge(c, 1, Integer::sum);
		}
		return allCards;
	}
}
