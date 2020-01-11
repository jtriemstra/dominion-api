package com.jtriemstra.dominion.api;

import static org.mockito.Mockito.spy;

import com.jtriemstra.dominion.api.models.Bank;
import com.jtriemstra.dominion.api.models.Game;
import com.jtriemstra.dominion.api.models.Player;

public class CucumberTestBase {
	
	CucumberState state = new CucumberState();
	
	Player getPlayer() {return state.player;}
	Bank getBank() { return state.mockBank;}
	
}
