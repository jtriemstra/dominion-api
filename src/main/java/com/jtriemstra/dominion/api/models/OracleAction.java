package com.jtriemstra.dominion.api.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OracleAction extends CardAction  {
	private Bank bank;
	
	@Override
	public void execute(Player player) {
		player.setCurrentChoice( new ActionChoice() {
			@Override
			public String getPrompt() { 
				return "Choose cards to discard:";
			}
			
			@Override
			public List<String> getOptions(){
				List<String> options = new ArrayList<>();
				
				List<Card> lookingAt = player.lookAt(2);
				for(Card c : lookingAt) {
					options.add(player.getName() + " : " + c.getName());
				}
				
				for (Player p : player.getGame().getOtherPlayers(player)) {
					lookingAt = p.lookAt(2);
					
					for(Card c : lookingAt) {
						options.add(p.getName() + " : " + c.getName());
					}	
				}
				
				return options;
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
							List<Card> cardsToDiscard = new ArrayList<>();
														
							for (Card c : p.getLiminal()) {
								if (c.getName().equals(tokens[1])) {
									cardsToDiscard.add(c);
								}
							}
							
							for (Card c : cardsToDiscard) {
								p.discardFromLiminal(c);
							}
							
							//TODO: choose order of return
							p.getDeck().addAll(0, p.getLiminal());
						}
					}					
				}
				
				player.draw();
				player.draw();				
			}
		});
	}
}
