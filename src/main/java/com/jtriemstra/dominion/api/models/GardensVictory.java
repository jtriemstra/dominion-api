package com.jtriemstra.dominion.api.models;

import java.util.List;

public class GardensVictory implements VictoryFunction {

	@Override
	public int getPoints(List<Card> cards) {
		return cards.size() / 10;
	}
	
}
