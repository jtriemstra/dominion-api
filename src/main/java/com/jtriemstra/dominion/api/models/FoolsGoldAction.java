package com.jtriemstra.dominion.api.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FoolsGoldAction extends CardAction {
	private Bank bank;
	
	public FoolsGoldAction(Bank bank) {
		this.bank = bank;
	}
	
	@Override
	public void execute(Player player) {
		player.addCurrentChoice( new ActionChoice() {
			@Override
			public String getPrompt() { 
				return "Would you like to trash this Fools Gold to gain a real gold to deck?";
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
				
				player.setCurrentChoice(null);
				
				if (options.get(0).equals("Yes")) {
					Card cardToTrash = null;
					for (Card c : player.getHand()) {
						if (c.getName().equals("Fools Gold")) {
							cardToTrash = c;
							break;
						}
					}
					
					if (cardToTrash != null) {
						player.getHand().remove(cardToTrash);
						player.gainTo(bank.getByName("Gold"), player.getDeck());	
					}
				}				
			}
		});
	}


}
