package com.jtriemstra.dominion.api.models;

import java.util.ArrayList;
import java.util.List;

public class BorderVillageGainAction extends CardAction {
	private Bank bank;
	
	public BorderVillageGainAction(Bank bank) {
		this.bank = bank;
	}
	
	@Override
	public void execute(Player player) {
		player.setCurrentChoice( new ActionChoice() {
			@Override
			public String getPrompt() { 
				return "Choose an extra card to gain";
			}
			
			@Override
			public List<String> getOptions(){
				int cost = bank.getByName("Border Village").getCost();
				return bank.getNamesByMaxCost(cost - 1);
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
				
				player.setCurrentChoice(null);
				
				player.gainTo(bank.getByName(options.get(0)), player.getBought());				
			}
		});
	}


}
