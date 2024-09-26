package com.jtriemstra.dominion.api.service;

import com.jtriemstra.dominion.api.dto.GameState;

public interface GainReaction {
	void execute(GameState game, String player, String cardName);
}
