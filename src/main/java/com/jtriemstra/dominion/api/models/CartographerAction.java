package com.jtriemstra.dominion.api.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CartographerAction extends CardAction  {
	@Override
	public void execute(Player player) {
		List<Card> lookingAt = player.lookAt(4);
		
		player.setCurrentChoice( new ActionChoice() {
			@Override
			public String getPrompt() { 
				return "Choose cards from the deck to discard";
			}
			
			@Override
			public List<String> getOptions(){
				List<String> options = new ArrayList<>();
				for(Card c : lookingAt) {
					options.add(c.getName());
				}
				
				return options;
			}
			
			@Override
			public int getMinOptions() {
				return 0;
			}

			@Override
			public int getMaxOptions() {
				return 4;
			}
			
			@Override
			public void doOptions(Player player, List<String> options) {
				player.setCurrentChoice(null);
				
				List<Card> cardsToDiscard = new ArrayList<>();
				List<Card> cardsToTopDeck = new ArrayList<>();
				
				for (String s : options) {
					for (Card c : player.getLiminal()) {
						if (s.equals(c.getName())) {
							cardsToDiscard.add(c);
							break;
						}
					}
				}
				
				for (Card c : cardsToDiscard) {
					player.discardFromLiminal(c);
				}
				
				// TODO: allow player to set the order of these
				for (Card c : player.getLiminal()) {
					player.getDeck().add(0, c);
				}	
				
				player.getLiminal().clear();
			}
		});
	}
}
