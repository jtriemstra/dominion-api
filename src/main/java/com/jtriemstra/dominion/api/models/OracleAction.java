package com.jtriemstra.dominion.api.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OracleAction extends CardAction  {
	private Bank bank;
	
	@Override
	public void execute(Player player) {
		player.lookAt(2);
		for (Player p : player.getGame().getOtherPlayers(player)) {
			if (!p.hasCard("Moat")) {
				p.lookAt(2);
			}			
		}
		
		player.setCurrentChoice( new ActionChoice() {
			
			@Override
			public String getPrompt() { 
				return "Choose cards to discard:";
			}
			
			@Override
			public List<String> getOptions(){
				List<String> options = new ArrayList<>();
				 
				for(Card c : player.getLiminal()) {
					options.add(player.getName() + " : " + c.getName());
				}
				
				for (Player p : player.getGame().getOtherPlayers(player)) {
					for(Card c : p.getLiminal()) {
						options.add(p.getName() + " : " + c.getName());
					}	
				}
				
				return options;
			}

			@Override
			public int getMinOptions() {
				return 0;
			}

			@Override
			public int getMaxOptions() {
				return 2;
			}
			
			@Override
			public void doOptions(Player player, List<String> options) {
				player.setCurrentChoice(null);
				
				// TODO: validation
				List<Player> allPlayers = new ArrayList<>();
				allPlayers.add(player);
				allPlayers.addAll(player.getGame().getOtherPlayers(player));
				
				for (String s : options) {
					String[] tokens = s.split(" : ");
					for (Player p : allPlayers) {
						if (tokens[0].equals(p.getName())) {
							Card cardToDiscard = null;
														
							for (Card c : p.getLiminal()) {
								if (c.getName().equals(tokens[1])) {
									cardToDiscard = c;
									break;
								}
							}
							
							p.discardFromLiminal(cardToDiscard);							
						}
					}					
				}
				
				for (Player p : allPlayers) {
					//TODO: choose order of return
					p.getDeck().addAll(0, p.getLiminal());
					p.getLiminal().clear();
				}
				
				player.draw();
				player.draw();				
			}
		});
	}
}
