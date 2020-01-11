package com.jtriemstra.dominion.api;

import static org.mockito.Mockito.spy;

import com.jtriemstra.dominion.api.models.Bank;
import com.jtriemstra.dominion.api.models.Game;
import com.jtriemstra.dominion.api.models.Player;

public class CucumberState {
	static Bank mockBank = spy(Bank.class);
	static Game game = new Game(mockBank);
	static Player player;
	
}
