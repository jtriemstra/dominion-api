package com.jtriemstra.dominion.api.models;

import java.util.ArrayList;
import java.util.List;

public class MilitiaAction extends CardAction {

	@Override
	public void execute(Player player) {
		for(Player p : player.getGame().getOtherPlayers(player)) {
			// TODO: there may be a more generic way to handle Reaction cards than what I'm doing with Moat
			if (p.getHand().size() > 3 && !p.hasCard("Moat")) {
				p.setCurrentChoice(new ActionChoice() {

					@Override
					public String getPrompt() {
						return "Choose cards to discard until you have only 3 in your hand";
					}

					@Override
					public List<String> getOptions() {
						List<String> cardNames = new ArrayList<>();
						for (Card c : p.getHand()) {
							cardNames.add(c.getName());
						}
						
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
						if (player.getHand().size() - options.size() != 3) {
							throw new RuntimeException("You must discard down to three cards in your hand");
						}
						
						for (String cardName : options) {
							for (Card c : player.getHand()) {
								if (c.getName().equals(cardName)) {
									player.getHand().remove(c);
									player.getDiscard().add(c);
									break;
								}
							}
						}
						
						player.setCurrentChoice(null);
					}
					
				});
			}
		}
	}

}
