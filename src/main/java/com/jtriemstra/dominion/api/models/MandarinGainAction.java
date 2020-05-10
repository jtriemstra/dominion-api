package com.jtriemstra.dominion.api.models;

import java.util.ArrayList;
import java.util.List;

public class MandarinGainAction extends CardAction {

	@Override
	public void execute(Player player) {
		// TODO: allow for ordering the treasure cards
		List<Card> cardsToMove = new ArrayList<Card>();
		
		for (Card c : player.getPlayed()) {
			if (c.getType() == Card.CardType.TREASURE) {
				cardsToMove.add(c);				
			}
		}
		
		for (Card c : cardsToMove) {
			player.getPlayed().remove(c);
			player.getDeck().add(0,c);
		}
	}

}
