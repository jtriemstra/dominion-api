package com.jtriemstra.dominion.api.models;

import java.util.ArrayList;
import java.util.List;

public class TraderAction extends CardAction {
	private Bank bank;
	
	public TraderAction(Bank bank) {
		this.bank = bank;
	}
	
	@Override
	public void execute(Player player) {
		player.setCurrentChoice( new ActionChoice() {
			@Override
			public String getPrompt() { 
				return "Choose a card to trash";
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
				if (options.size() == 0 && player.getHand().size() > 0) {
					throw new RuntimeException("You must trash a card");
				}
				if (options.size() != 1) {
					throw new RuntimeException("One and only one option can be chosen");
				}
				
				Card cardToTrash = null;
				
				for (Card c : player.getHand()) {
					if (c.getName().equals(options.get(0))) {
						cardToTrash = c;
						break;
					}
				}
				
				if (cardToTrash == null) {
					throw new RuntimeException("selected card not found in hand");
				}
				
				player.setCurrentChoice(null);
				
				int trashedCost = cardToTrash.getCost();
				player.getHand().remove(cardToTrash);
				
				for (int i=0; i<trashedCost; i++) {
					player.gainTo(bank.getByName("Silver"), player.getBought());
				}
			}
		});
	}


}
