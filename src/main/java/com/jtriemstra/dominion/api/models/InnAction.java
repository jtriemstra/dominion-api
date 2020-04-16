package com.jtriemstra.dominion.api.models;

import java.util.ArrayList;
import java.util.List;

public class InnAction extends CardAction {

	@Override
	public void execute(Player player) {
		player.setCurrentChoice( new ActionChoice() {
			@Override
			public String getPrompt() { 
				return "Which cards would you like to discard?";
			}
			
			@Override
			public List<String> getOptions(){
				List<String> cardNames = new ArrayList<String>();
				for (Card c : player.getHand()) {
					cardNames.add(c.getName());
				}
				return cardNames;
			}

			@Override
			public void doOptions(Player player, List<String> options) {
				if (options.size() > 2) {
					throw new RuntimeException("You can only discard 2 cards");
				}
				
				if (options.size() < 2 && player.getHand().size() >= 2) {
					throw new RuntimeException("You must discard 2 cards");
				}
				
				player.setCurrentChoice(null);
				
				for(String cardName : options) {
					for (Card c : player.getHand()) {
						if (cardName.equals(c.getName())) {
							player.discardFromHand(c);
							break;
						}
					}
				}				
			}
		});
	}

}
