package com.jtriemstra.dominion.api.service;

import com.jtriemstra.dominion.api.dto.CardDestination;
import com.jtriemstra.dominion.api.dto.CardSource;
import com.jtriemstra.dominion.api.dto.GameState;

public interface CardSourceFunction {
	public CardSource get(GameState game, String player);
}
