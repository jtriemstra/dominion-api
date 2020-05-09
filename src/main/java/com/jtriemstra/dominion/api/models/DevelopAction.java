package com.jtriemstra.dominion.api.models;

import java.util.ArrayList;
import java.util.List;

public class DevelopAction extends CardAction {
	private Bank bank;
	
	public DevelopAction(Bank bank) {
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
				if (options.size() > 1) {
					throw new RuntimeException("At most one option can be chosen");
				}
				
				if (options.size() == 0) {
					// TODO: only allow if there are no cards in hand
					player.setCurrentChoice(null);
					return;
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
						List<String> cardNames = new ArrayList<>();
						cardNames.addAll(bank.getNamesByExactCost(trashedCost + 1));
						cardNames.addAll(bank.getNamesByExactCost(trashedCost - 1));
						return cardNames;
					}
					
					@Override
					public void doOptions(Player player, List<String> options){
						if (options.size() > 1) {
							throw new RuntimeException("At most one option can be chosen");
						}
						
						player.setCurrentChoice(null);
						
						if (options.size() == 0) {
							// TODO: only allow if there are no options to start with
							return;
						}
						
						Card newCard = bank.getByName(options.get(0));
						int nextCost = newCard.getCost() == trashedCost + 1 ? trashedCost - 1 : trashedCost + 1;
						player.gainTo(newCard, player.getDeck());
						
						player.setCurrentChoice( new ActionChoice() {
							@Override
							public String getPrompt() { 
								return "Choose a card to gain";
							}
							
							@Override
							public List<String> getOptions(){
								List<String> cardNames = new ArrayList<>();
								cardNames.addAll(bank.getNamesByExactCost(nextCost));
								return cardNames;
							}
							
							@Override
							public void doOptions(Player player, List<String> options){
								if (options.size() > 1) {
									throw new RuntimeException("At most one option can be chosen");
								}
								
								player.setCurrentChoice(null);
								
								if (options.size() == 0) {
									// TODO: only allow if there are no options to start with
									return;
								}
								
								Card newCard = bank.getByName(options.get(0));
								player.gainTo(newCard, player.getDeck());						
							}							
						});
					}							
				});
				
			}
		});
	}

}
