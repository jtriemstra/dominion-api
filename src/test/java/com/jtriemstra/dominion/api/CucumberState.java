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
	static Bank realBank;
	static Bank mockBank;
	static Game game;
	static Player player;
	
	static void init(List<String> cardNames) {
		realBank = new Bank(cardNames);
		mockBank = spy(realBank);
		game = new Game(mockBank);
	}
}
