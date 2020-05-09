package com.jtriemstra.dominion.api.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JackOfAllTradesAction extends CardAction  {
	private Bank bank;
	
	public JackOfAllTradesAction(Bank bank) {
		this.bank = bank;
	}
	
	@Override
	public void execute(Player player) {
		player.gainTo(bank.getByName("Silver"), player.getBought());
		List<Card> lookingAt = player.lookAt(1);
		
		player.setCurrentChoice( new ActionChoice() {
			@Override
			public String getPrompt() { 
				return "Would you like to discard:";
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
			public void doOptions(Player player, List<String> options) {
				if (options.size() > 1) {
					throw new RuntimeException("You can only discard one card");
				}
				if (player.getLiminal().size() > 1 || (player.getLiminal().size() == 0 && options.size() == 1)) {
					throw new RuntimeException("Temporary list of cards has gotten out of sync on the server");
				}
				if (options.size() == 1) {
					player.discardFromLiminal((player.getLiminal().get(0)));
				}
				else {
					player.getDeck().addAll(0, player.getLiminal());
					player.getLiminal().clear();
				}
				
				while(player.getHand().size() < 5) {
					player.draw();
				}
				
				player.setCurrentChoice(null);
				player.setCurrentChoice(new ActionChoice() {
					@Override
					public String getPrompt() { 
						return "Would you like to trash one:";
					}
					
					@Override
					public List<String> getOptions(){
						List<String> options = new ArrayList<>();
						for(Card c : player.getHand()) {
							if (c.getType() != Card.CardType.TREASURE) {
								options.add(c.getName());	
							}							
						}
						
						return options;
					}
					
					@Override
					public void doOptions(Player player, List<String> options) {
						if (options.size() > 1) {
							throw new RuntimeException("you can only trash one card");
						}
						
						player.setCurrentChoice(null);
						
						if (options.size() == 0) {
							return;
						}
						
						Card cardToTrash = null;
						for(Card c : player.getHand()) {
							if (options.get(0).equals(c.getName())) {
								cardToTrash = c;
								break;
							}
						}
						
						if (cardToTrash != null) {
							player.getHand().remove(cardToTrash);
						}
						
					}
				
				});
			}
		});
	}
}
