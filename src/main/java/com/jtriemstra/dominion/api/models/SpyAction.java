package com.jtriemstra.dominion.api.models;

import java.util.ArrayList;
import java.util.List;

public class SpyAction extends CardAction {
	@Override
	public void execute(Player player) {
		//TODO: account for Moat
		player.setCurrentChoice(new ActionChoice() {
			
			@Override
			public String getPrompt() {
				return "Choose cards for opponents to discard";
			}

			@Override
			public List<String> getOptions() {
				List<String> cardNames = new ArrayList<>();
				for (Player p : player.getGame().getOtherPlayers(player)) {
					cardNames.add(p.getDeck().get(0).getName());
				}
				
				return cardNames;
			}

			@Override
			public void doOptions(Player player, List<String> options) {
				//TODO: finish
			}
			
		});
	}

}
