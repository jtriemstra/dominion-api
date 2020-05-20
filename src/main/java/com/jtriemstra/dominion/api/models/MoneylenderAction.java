package com.jtriemstra.dominion.api.models;

public class MoneylenderAction extends CardAction {

	@Override
	public void execute(Player player) {
		for (Card c : player.getHand()) {
			if (c.getName().equals("Copper")) {
				player.getHand().remove(c);
				player.setTemporaryTreasure(player.getTemporaryTreasure() + 3);
				return;
			}
		}
		
		//TODO: anything?
		//throw new RuntimeException("no coppers found in hand to trash");
	}

}
