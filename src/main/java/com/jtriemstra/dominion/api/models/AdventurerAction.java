package com.jtriemstra.dominion.api.models;

public class AdventurerAction extends CardAction  {
	@Override
	public void execute(Player player) {
		int treasureCardsFound = 0;
		while (treasureCardsFound < 2) {
			//TODO: this could return null
			Card c = player.lookAt(1).get(0);
			if (c.getType() == Card.CardType.TREASURE) {
				treasureCardsFound++;
				player.addToHand(c);
			}
			else {
				player.discardFromLiminal(c);
			}
		}
	}
}
