package com.jtriemstra.dominion.api.service;

import com.jtriemstra.dominion.api.dto.CardSource;
import com.jtriemstra.dominion.api.dto.GameState;

public interface DiscardEvent {
	void execute(GameState game, String player, ActionService.CardSources discardSource);
}
