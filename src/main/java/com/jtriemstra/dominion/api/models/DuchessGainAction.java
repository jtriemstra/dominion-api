package com.jtriemstra.dominion.api.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DuchessGainAction extends CardAction {
	private Bank bank;
	
	public DuchessGainAction(Bank bank) {
		this.bank = bank;
	}
	
	@Override
	public void execute(Player player) {
		player.setCurrentChoice( new ActionChoice() {
			@Override
			public String getPrompt() { 
				return "Would you like to gain a Duchess";
			}
			
			@Override
			public List<String> getOptions(){
				return Arrays.asList("Yes", "No");
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
				
				if (options.get(0).equals("Yes")) {
					player.gainTo(bank.getByName("Duchess"), player.getBought());
				}
			}
		});
	}


}
