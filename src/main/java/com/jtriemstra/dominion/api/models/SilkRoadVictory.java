package com.jtriemstra.dominion.api.models;

import java.util.List;

public class SilkRoadVictory implements VictoryFunction {

	@Override
	public int getPoints(List<Card> cards) {
		int numberOfVictory = 0;
		for (Card c : cards) {
			if (c.getType() == Card.CardType.VICTORY) {
				numberOfVictory++;
			}
		}
		return numberOfVictory / 4;
	}
	
}
