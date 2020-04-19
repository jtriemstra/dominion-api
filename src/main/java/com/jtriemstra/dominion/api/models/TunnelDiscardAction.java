package com.jtriemstra.dominion.api.models;

import java.util.Arrays;
import java.util.List;

public class TunnelDiscardAction extends EventAction {
	private Bank bank;
	
	public TunnelDiscardAction(Bank bank) {
		this.bank = bank;
	}
	
	@Override
	public void execute(Player player) {
		player.addCurrentChoice( new ActionChoice() {
			@Override
			public String getPrompt() { 
				return "Do you want to gain a gold for discarding the Tunnel?";
			}
			
			@Override
			public List<String> getOptions(){
				return Arrays.asList("Yes", "No");
			}
			
			@Override
			public void doOptions(Player player, List<String> options) {
				if (options.size() != 1) {
					throw new RuntimeException("One and only one option can be chosen");
				}
				
				player.setCurrentChoice(null);
				
				if (options.get(0).equals("Yes")) {
					player.gainTo(bank.getByName("Gold"), player.getBought());
				}
			}
		});
	}


}
