package com.jtriemstra.dominion.api.models;

import java.util.ArrayList;
import java.util.List;

public class InnGainAction extends CardAction {

	@Override
	public void execute(Player player) {
		player.setCurrentChoice( new ActionChoice() {
			@Override
			public String getPrompt() { 
				return "Which action cards would you like to put in your deck?";
			}
			
			@Override
			public List<String> getOptions(){
				List<String> cardNames = new ArrayList<String>();
				for (Card c : player.getBought()) {
					if (c.getType() == Card.CardType.ACTION) {
						cardNames.add(c.getName());
					}
				}
				for (Card c : player.getDiscard()) {
					if (c.getType() == Card.CardType.ACTION) {
						cardNames.add(c.getName());
					}
				}
				return cardNames;
			}

			@Override
			public void doOptions(Player player, List<String> options) {
				
				for(String cardName : options) {
					Card cardToMove = null;
					for (Card c : player.getDiscard()) {
						if (cardName.equals(c.getName())) {
							cardToMove = c;
							break;
						}
					}
					if (cardToMove != null) {
						player.getDiscard().remove(cardToMove);
						player.getDeck().add(cardToMove);
					}
					else {
						for (Card c : player.getBought()) {
							if (cardName.equals(c.getName())) {
								cardToMove = c;
								break;
							}
						}
						if (cardToMove != null) {
							player.getBought().remove(cardToMove);
							player.getDeck().add(cardToMove);
						}
						else {
							throw new RuntimeException("card not found to move to deck");
						}
					}
				}
				
				player.setDeck(player.shuffle(player.getDeck()));

				player.setCurrentChoice(null);
			}
		});
	}

}
