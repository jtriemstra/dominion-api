package com.jtriemstra.dominion.api.models;

import java.util.ArrayList;
import java.util.List;

public class OasisAction extends CardAction {

	@Override
	public void execute(Player player) {
		player.setCurrentChoice( new ActionChoice() {
			@Override
			public String getPrompt() { 
				return "Which card would you like to discard?";
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
				if (options.size() > 1) {
					throw new RuntimeException("At most one option can be chosen");
				}
				
				if (options.size() == 0 && player.getHand().size() > 0) {
					throw new RuntimeException("One option must be chosen");
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
