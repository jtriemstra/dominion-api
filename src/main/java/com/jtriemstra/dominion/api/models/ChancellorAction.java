package com.jtriemstra.dominion.api.models;

import java.util.Arrays;
import java.util.List;

public class ChancellorAction extends CardAction {

	@Override
	public void execute(Player player) {
		player.setCurrentChoice( new ActionChoice() {
			@Override
			public String getPrompt() { 
				return "Do you want to put your deck into your discard pile?";
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
				
				if (options.get(0).equals("Yes")) {
					player.getDiscard().addAll(player.getDeck());
					player.getDeck().clear();
				}

				player.setCurrentChoice(null);
			}
		});
	}


}
