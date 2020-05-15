package com.jtriemstra.dominion.api.models;

import java.util.ArrayList;
import java.util.List;

public class SpyAction extends CardAction {
	@Override
	public void execute(Player player) {
		
		List<String> cardNames = new ArrayList<>();
		List<Card> cards = player.lookAt(1);
		cardNames.add(player.getName() + " : " + cards.get(0).getName());
		
		for (Player p : player.getGame().getOtherPlayers(player)) {
			if (!p.hasCard("Moat")) {
				cards = p.lookAt(1);
				cardNames.add(p.getName() + " : " + cards.get(0).getName());
			}					
		}

		player.setCurrentChoice(new ActionChoice() {
			
			@Override
			public String getPrompt() {
				return "Choose cards for opponents to discard";
			}

			@Override
			public List<String> getOptions() {
				return cardNames;
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
					p.getDeck().addAll(0, p.getLiminal());
					p.getLiminal().clear();
				}
			}
			
		});
	}

}
