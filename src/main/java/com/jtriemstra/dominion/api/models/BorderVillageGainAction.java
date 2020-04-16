package com.jtriemstra.dominion.api.models;

import java.util.ArrayList;
import java.util.List;

public class BorderVillageGainAction extends EventAction {
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
				return bank.getNamesByMaxCost(5);
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
