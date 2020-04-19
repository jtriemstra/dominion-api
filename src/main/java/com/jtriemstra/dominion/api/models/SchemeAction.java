package com.jtriemstra.dominion.api.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SchemeAction extends CardAction {
	
	@Override
	public void execute(Player player) {
		player.setCurrentChoice( new ActionChoice() {
			@Override
			public String getPrompt() { 
				return "Choose an action card to put on your deck";
			}
			
			@Override
			public List<String> getOptions(){
				List<String> actionNames = new ArrayList<>();
				for (Card c : player.getPlayed()) {
					if (c.getType() == Card.CardType.ACTION) {
						actionNames.add(c.getName());
					}
				}
				return actionNames;
			}

			@Override
			public void doOptions(Player player, List<String> options) {
				if (options.size() > 1) {
					throw new RuntimeException("Only one option can be chosen");
				}
				
				player.setCurrentChoice(null);
				
				if (options.size() == 1) {
					Card cardToMove = null;
					for (Card c : player.getPlayed()) {
						if (c.getName().equals(options.get(0))) {
							cardToMove = c;
							break;
						}
					}
					
					if (cardToMove != null) {
						player.getPlayed().remove(cardToMove);
						player.getDeck().add(0, cardToMove);	
					}
				}
				
				player.cleanup();
			}
		});
	}


}
