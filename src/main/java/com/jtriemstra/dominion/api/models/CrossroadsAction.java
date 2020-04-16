package com.jtriemstra.dominion.api.models;

public class CrossroadsAction extends CardAction {

	@Override
	public void execute(Player player) {
		int numberOfVictoryCards = 0;
		
		for(Card c : player.getHand()) {
			if (c.getType() == Card.CardType.VICTORY) {
				numberOfVictoryCards++;
			}
		}
		
		for (int i=0; i<numberOfVictoryCards; i++) {
			player.draw();
		}
		
		int numberOfCrossroadsCards = 0;
		for (Card c : player.getPlayed()) {
			if (c.getName().equals("Crossroads")) {
				numberOfCrossroadsCards++;
			}			
		}
		
		if (numberOfCrossroadsCards == 1) {
			player.setTemporaryActions(player.getTemporaryActions() + 3);
		}
	}

}
