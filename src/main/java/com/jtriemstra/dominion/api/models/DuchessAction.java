package com.jtriemstra.dominion.api.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DuchessAction extends CardAction  {
	@Override
	public void execute(Player player) {
		
		player.setCurrentChoice( new ActionChoice() {
			@Override
			public String getPrompt() { 
				return "Would you like to discard:";
			}
			
			@Override
			public List<String> getOptions(){
				List<Card> lookingAt = player.lookAt(1);
				List<String> options = new ArrayList<>();
				for(Card c : lookingAt) {
					options.add(c.getName());
				}
				
				return options;
			}
			
			@Override
			public void doOptions(Player player, List<String> options) {
				if (options.size() > 1) {
					throw new RuntimeException("You can only discard one card");
				}
				if (player.getLiminal().size() > 1 || (player.getLiminal().size() == 0 && options.size() == 1)) {
					throw new RuntimeException("Temporary list of cards has gotten out of sync on the server");
				}
				
				player.setCurrentChoice(null);
				
				if (options.size() == 1) {
					player.discardFromLiminal((player.getLiminal().get(0)));
				}
			}
		});
	}
}
