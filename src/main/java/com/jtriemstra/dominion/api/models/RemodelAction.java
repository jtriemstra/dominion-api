package com.jtriemstra.dominion.api.models;

import java.util.ArrayList;
import java.util.List;

public class RemodelAction extends CardAction {
	private Bank bank;
	
	public RemodelAction(Bank bank) {
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
			public void doOptions(Player player, List<String> options) {
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
				
				int trashedCost = cardToTrash.getCost();
				player.getHand().remove(cardToTrash);
				
				player.setCurrentChoice(null);
				player.setCurrentChoice( new ActionChoice() {
					@Override
					public String getPrompt() { 
						return "Choose a card to gain";
					}
					
					@Override
					public List<String> getOptions(){
						return bank.getNamesByMaxCost(trashedCost + 2);
					}
					
					@Override
					public void doOptions(Player player, List<String> options){
						if (options.size() != 1) {
							throw new RuntimeException("One and only one option can be chosen");
						}
						
						player.setCurrentChoice(null);
								
						player.gainTo(bank.getByName(options.get(0)), player.getBought());
					}							
				});
				
			}
		});
	}


}
