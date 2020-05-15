package com.jtriemstra.dominion.api.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ThiefAction extends CardAction  {
	
	@Override
	public void execute(Player player) {
		for (Player p : player.getGame().getOtherPlayers(player)) {
			if (!p.hasCard("Moat")) {
				p.lookAt(2);
			}
		}
			
		player.setCurrentChoice( new ActionChoice() {
			@Override
			public String getPrompt() { 
				return "Choose a treasure card from each player to steal or trash:";
			}
			
			@Override
			public List<String> getOptions(){
				List<String> options = new ArrayList<>();
				
				for (Player p : player.getGame().getOtherPlayers(player)) {
					if (!p.hasCard("Moat")) {
						for(Card c : p.getLiminal()) {
							if (c.getType() == Card.CardType.TREASURE) {
								options.add(p.getName() + " : " + c.getName());
							}
						}	
					}
				}
				
				return options;
			}

			@Override
			public int getMinOptions() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public int getMaxOptions() {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public void doOptions(Player player, List<String> options) {
				// TODO: validation
				
				player.setCurrentChoice(null);
				
				for (String s : options) {
					String[] tokens = s.split(" : ");
					for (Player p : player.getGame().getOtherPlayers(player)) {
						if (tokens[0].equals(p.getName())) {
							Card cardToSteal = null;
							for (Card c : p.getLiminal()) {
								if (c.getName().equals(tokens[1]) && (c.getType() == Card.CardType.TREASURE)) {
									cardToSteal = c;
									break;
								}
							}
							
							if (cardToSteal != null) {
								p.getLiminal().remove(cardToSteal);
								player.getLiminal().add(cardToSteal);
							}
						}
					}
				}

				for (Player p : player.getGame().getOtherPlayers(player)) {
					List<Card> cardsToDiscard = new ArrayList<>();
					cardsToDiscard.addAll(p.getLiminal());
					
					for (Card c : cardsToDiscard) {
						p.discardFromLiminal(c);
					}
				}
				
				player.setCurrentChoice(new ActionChoice() {
					@Override
					public String getPrompt() { 
						return "Choose treasure cards to keep:";
					}
					
					@Override
					public List<String> getOptions(){
						List<String> options = new ArrayList<>();
						
						for(Card c : player.getLiminal()) {
							options.add(c.getName());							
						}	
					
						return options;
					}

					@Override
					public int getMinOptions() {
						return 0;
					}

					@Override
					public int getMaxOptions() {
						return 1000;
					}
					
					@Override
					public void doOptions(Player player, List<String> options) {
						player.setCurrentChoice(null);
						
						for (String s : options) {
							for (Card c : player.getLiminal()) {
								if (c.getName().equals(s)) {
									player.getBought().add(c);
									break;
								}
							}

						}
						
						player.getLiminal().clear();
					}
				});
			}
		});
	}
}
