package com.jtriemstra.dominion.api.models;

import java.util.Arrays;
import java.util.List;

public class LibraryAction extends CardAction {

	@Override
	public void execute(Player player) {
		while (player.getHand().size() < 7) {
			Card newCard = player.draw();
			if (newCard.getType() == Card.CardType.ACTION) {
				player.setCurrentChoice( new ActionChoice() {
					@Override
					public String getPrompt() { 
						return "Do you want to discard the action " + newCard.getName() + "?";
					}
					
					@Override
					public List<String> getOptions(){
						return Arrays.asList("Yes", "No");
					}
					
					@Override
					public void doOptions(Player player, List<String> options) {
						if (options.size() != 1) {
							throw new RuntimeException("One and only one option can be chosen");
						}
						
						if (options.get(0).equals("Yes")) {
							player.discardFromHand(newCard);
						}

						player.setCurrentChoice(null);
						
						LibraryAction recursiveCall = new LibraryAction();
						recursiveCall.execute(player);
					}
				});
				return;
			}
		}		
	}

}
