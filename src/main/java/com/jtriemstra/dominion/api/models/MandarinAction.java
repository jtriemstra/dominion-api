package com.jtriemstra.dominion.api.models;

import java.util.ArrayList;
import java.util.List;

public class MandarinAction extends CardAction {

	@Override
	public void execute(Player player) {
		player.setCurrentChoice( new ActionChoice() {
			@Override
			public String getPrompt() { 
				return "Choose a card to put back on your deck";
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
				return 1;
			}

			@Override
			public void doOptions(Player player, List<String> options) {
				if (options.size() > 1) {
					throw new RuntimeException("You can only choose 1 card");
				}
				
				if (options.size() == 0 && player.getHand().size() > 0){
					throw new RuntimeException("You must place a card on your deck");
				}
				
				Card cardToMove = null;
						
				for(String cardName : options) {
					for (Card c : player.getHand()) {
						if (cardName.equals(c.getName())) {
							cardToMove = c;
							break;
						}
					}
				}
				
				player.getHand().remove(cardToMove);
				player.getDeck().add(0, cardToMove);

				
				player.setCurrentChoice(null);
			}
		});


	}

}
