package com.jtriemstra.dominion.api.dto;

import java.util.List;

public interface ChoiceOptionCreator {
	List<String> createChoices(GameState game, PlayerState player);
}
