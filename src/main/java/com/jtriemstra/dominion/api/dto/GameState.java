package com.jtriemstra.dominion.api.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor // support deserialization in tests
public class GameState {
	private Map<String, PlayerState> players;
	private List<String> playerNames;
	private BankState bank;
	private TrashState trash;
	private int currentPlayer;
	
	public GameState(BankState bank) {
		players = new HashMap<>();
		this.bank = bank;
		trash = new TrashState();
		playerNames = new ArrayList<>();
		currentPlayer = 0;
	}
}
