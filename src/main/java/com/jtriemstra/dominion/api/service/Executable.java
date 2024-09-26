package com.jtriemstra.dominion.api.service;

import com.jtriemstra.dominion.api.dto.GameState;

public interface Executable {
	void execute(GameState game, String player);
}
