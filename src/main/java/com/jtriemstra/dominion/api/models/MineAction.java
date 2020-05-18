package com.jtriemstra.dominion.api.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MineAction extends CardAction {
	
	private Bank bank;
	
	public MineAction(Bank bank) {
		this.bank = bank;
	}
	
	
	
	@Override
	public void execute(Player player) {
		player.setCurrentChoice( new ActionChoice() {
			@Override
			public String getPrompt() { 
				return "Choose a treasure card to trash";
			}
			
			@Override
			public List<String> getOptions(){
				List<String> cardNames = new ArrayList<String>();
				for (Card c : player.getHand()) {
					if (c.getType() == Card.CardType.TREASURE) {
						cardNames.add(c.getName());
					}
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
					throw new RuntimeException("One and only one option can be chosen");
				}
				
				if (options.size() == 0 && player.getHand().size() > 0) {
					throw new RuntimeException("One and only one option can be chosen");
				}
				
				player.setCurrentChoice(null);
				
				Card cardToTrash = null;
				//TODO: validate is treasure card - introduce validation function?
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
				
				player.setCurrentChoice( new ActionChoice() {
					@Override
					public String getPrompt() { 
						return "Choose a treasure card to gain";
					}
					
					@Override
					public List<String> getOptions(){
						return bank.getNamesByTypeAndMaxCost(Card.CardType.TREASURE, trashedCost + 3);
					}

					@Override
					public int getMinOptions() {
						return 1;
					}

					@Override
					public int getMaxOptions() {
						return 1;
					}
					
					@Override
					public void doOptions(Player player, List<String> options){
						if (options.size() != 1) {
							throw new RuntimeException("One and only one option can be chosen");
						}
						
						//TODO: validate choice
						
						player.gainTo(bank.getByName(options.get(0)), player.getHand());
						
						player.setCurrentChoice(null);
					}							
				});
				
			}
		});
	}

	
}
