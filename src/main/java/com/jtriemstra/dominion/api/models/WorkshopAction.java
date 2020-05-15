package com.jtriemstra.dominion.api.models;

import java.util.ArrayList;
import java.util.List;

public class WorkshopAction extends CardAction {
	private Bank bank;
	
	public WorkshopAction(Bank bank) {
		this.bank = bank;
	}
	
	@Override
	public void execute(Player player) {
		player.setCurrentChoice( new ActionChoice() {
			@Override
			public String getPrompt() { 
				return "Choose a card costing up to 4";
			}
			
			@Override
			public List<String> getOptions(){
				return bank.getNamesByMaxCost(4);
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
				
				player.gainTo(bank.getByName(options.get(0)), player.getDiscard());

				player.setCurrentChoice(null);
			}
		});
	}

}
