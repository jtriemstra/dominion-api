package com.jtriemstra.dominion.api.models;

import java.util.ArrayList;
import java.util.List;

public class CellarAction extends CardAction {

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
			public int getMinOptions() {
				return 0;
			}

			@Override
			public int getMaxOptions() {
				return 1000;
			}

			@Override
			public void doOptions(Player player, List<String> options) {
				player.setCurrentChoice(null);
				
				for(String cardName : options) {
					for (Card c : player.getHand()) {
						if (cardName.equals(c.getName())) {
							player.discardFromHand(c);
							player.draw();
							break;
						}
					}
				}				
			}
		});
	}

}
