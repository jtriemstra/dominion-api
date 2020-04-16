package com.jtriemstra.dominion.api.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NobleBrigandAction extends CardAction  {
	private Bank bank;
	
	public NobleBrigandAction(Bank bank) {
		this.bank = bank;
	}
	
	@Override
	public void execute(Player player) {
		player.setCurrentChoice( new ActionChoice() {
			@Override
			public String getPrompt() { 
				return "Choose a Silver or Gold from each player to steal:";
			}
			
			@Override
			public List<String> getOptions(){
				List<String> options = new ArrayList<>();
				
				for (Player p : player.getGame().getOtherPlayers(player)) {
					List<Card> lookingAt = p.lookAt(2);
					
					for(Card c : lookingAt) {
						options.add(p.getName() + " : " + c.getName());
					}	
				}
				
				return options;
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
								if (c.getName().equals(tokens[1]) && (c.getName() == "Silver" || c.getName() == "Gold")) {
									cardToSteal = c;
									break;
								}
							}
							
							if (cardToSteal != null) {
								p.getLiminal().remove(cardToSteal);
								player.getBought().add(cardToSteal);
							}
						}
					}
				}

				for (Player p : player.getGame().getOtherPlayers(player)) {
					boolean treasureFound = false;
					for (Card c : p.getLiminal()) {
						if (c.getType() == Card.CardType.TREASURE) {
							treasureFound = true;
						}
					}
					
					if (!treasureFound) {
						p.gainTo(bank.getByName("Copper"), p.getDiscard());
					}
					
					List<Card> cardsToDiscard = new ArrayList<>();
					cardsToDiscard.addAll(p.getLiminal());
					
					for (Card c : cardsToDiscard) {
						p.discardFromLiminal(c);
					}
				}
			}
		});
	}
}
