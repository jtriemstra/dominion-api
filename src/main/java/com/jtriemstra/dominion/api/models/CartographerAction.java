package com.jtriemstra.dominion.api.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CartographerAction extends CardAction  {
	@Override
	public void execute(Player player) {
		player.setCurrentChoice( new ActionChoice() {
			@Override
			public String getPrompt() { 
				return "Choose cards from the deck to discard";
			}
			
			@Override
			public List<String> getOptions(){
				List<Card> lookingAt = player.lookAt(4);
				List<String> options = new ArrayList<>();
				for(Card c : lookingAt) {
					options.add(c.getName());
				}
				
				return options;
			}
			
			@Override
			public void doOptions(Player player, List<String> options) {
				player.setCurrentChoice(null);
				
				List<Card> cardsToDiscard = new ArrayList<>();
				List<Card> cardsToTopDeck = new ArrayList<>();
				
				for (Card c : player.getLiminal()) {
					if (options.contains(c.getName())) {
						cardsToDiscard.add(c);
					}
					else {
						cardsToTopDeck.add(c);
					}
				}
				
				for (Card c : cardsToDiscard) {
					player.discardFromLiminal(c);
				}
				
				// TODO: allow player to set the order of these
				for (Card c : cardsToTopDeck) {
					player.getLiminal().remove(c);
					player.getDeck().add(0, c);
				}				
			}
		});
	}
}
