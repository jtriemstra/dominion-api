package com.jtriemstra.dominion.api.service;

import com.jtriemstra.dominion.api.dto.CardDestination;
import com.jtriemstra.dominion.api.dto.GameState;

public interface CardDestinationFunction {
	public CardDestination get(GameState game, String player);
}
