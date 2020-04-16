package com.jtriemstra.dominion.api.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IllGottenGainsAction extends CardAction {

	private Bank bank;
	
	public IllGottenGainsAction(Bank bank) {
		this.bank = bank;
	}
	
	@Override
	public void execute(Player player) {
		player.setCurrentChoice( new ActionChoice() {
			@Override
			public String getPrompt() { 
				return "Would you like to gain a Copper?";
			}
			
			@Override
			public List<String> getOptions(){
				return Arrays.asList("Yes", "No");
			}

			@Override
			public void doOptions(Player player, List<String> options) {
				if (options.size() != 1) {
					throw new RuntimeException("You must choose either Yes or No");
				}
				
				player.setCurrentChoice(null);
				
				if ("Yes".equals(options.get(0))) {
					player.gainTo(bank.getByName("Copper"), player.getHand());
				}				
			}
		});
	}

}
