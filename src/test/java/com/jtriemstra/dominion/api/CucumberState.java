package com.jtriemstra.dominion.api;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.util.Arrays;

import com.jtriemstra.dominion.api.models.Bank;
import com.jtriemstra.dominion.api.models.Card;
import com.jtriemstra.dominion.api.models.Game;
import com.jtriemstra.dominion.api.models.Player;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CucumberState {
	static Bank realBank = new Bank(java.util.Arrays.asList("Smithy", "Village", "Throne Room", "Festival", "Moneylender", "Mine", "Remodel", "Library", "Cellar", "Chancellor"));
	static Bank mockBank = spy(realBank);
	static Game game = new Game(mockBank);
	static Player player;
	
	static {
		
		log.info("calling static initializer");
		
		
	}
}
