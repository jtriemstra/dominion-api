package com.jtriemstra.dominion.api.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.jtriemstra.dominion.api.models.Player;

import lombok.Data;

@Data
public class PlayerGameState {
	private Player thisPlayer;
	private String[] playerNames;
	private int currentPlayer;
	
	public PlayerGameState(Player p, String[] names, int currentPlayer) {
		this.thisPlayer = p;
		this.playerNames = names;
		this.currentPlayer = currentPlayer;
	}
	
	@JsonGetter(value = "isCurrentPlayer")
	public boolean isCurrentPlayer() {
		if (isGameOver()) return false;
		
		return thisPlayer.getName().equals(playerNames[currentPlayer]);
	}
	
	@JsonGetter(value = "currentPlayerIndex")
	public int getCurrentPlayerIndex() {
		return currentPlayer;
	}
	
	@JsonGetter(value = "isGameOver")
	public boolean isGameOver() {
		return currentPlayer == -1;
	}
	
	@JsonGetter(value = "points")
	public int points() {
		if (isGameOver()) {
			return thisPlayer.getPoints();
		}
		
		return 0;
	}
}
