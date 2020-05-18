package com.jtriemstra.dominion.api.models;

import java.util.ArrayList;
import java.util.List;

public class FeastAction extends CardAction {
	private Bank bank;
	private Card c;
	
	public FeastAction(Bank bank, Card c) {
		this.bank = bank;
		this.c = c;
	}
	
	
	@Override
	public void execute(Player player) {
		player.getPlayed().remove(c);
		
		player.setCurrentChoice( new ActionChoice() {
			@Override
			public String getPrompt() { 
				return "Choose a card costing up to 5";
			}
			
			@Override
			public List<String> getOptions(){
				return bank.getNamesByMaxCost(5);
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
			public void doOptions(Player player, List<String> options) {
				if (options.size() != 1) {
					throw new RuntimeException("One and only one option can be chosen");
				}
				
				player.gainTo(bank.getByName(options.get(0)), player.getBought());
				
				player.setCurrentChoice(null);
			}
		});
	}
	

}
