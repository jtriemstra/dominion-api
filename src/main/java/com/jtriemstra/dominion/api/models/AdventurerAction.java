package com.jtriemstra.dominion.api.models;

public class AdventurerAction extends CardAction  {
	@Override
	public void execute(Player player) {
		int treasureCardsFound = 0;
		while (treasureCardsFound < 2) {
			Card c = player.reveal();
			if (c.getType() == Card.CardType.TREASURE) {
				treasureCardsFound++;
				player.addToHand(c);
			}
			else {
				player.discardFromTemp(c);
			}
		}
	}
}
