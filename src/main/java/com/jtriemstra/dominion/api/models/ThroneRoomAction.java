package com.jtriemstra.dominion.api.models;

import java.util.ArrayList;
import java.util.List;

public class ThroneRoomAction extends CardAction {

	@Override
	public void execute(Player player) {
		
		player.setCurrentChoice( new ActionChoice() {
			@Override
			public String getPrompt() { 
				return "Choose an action card to play twice";
			}
			
			@Override
			public List<String> getOptions(){
				List<String> cardNames = new ArrayList<String>();
				for (Card c : player.getHand()) {
					if (c.getType().equals(Card.CardType.ACTION)) {
						cardNames.add(c.getName());
					}
				}
				return cardNames;
			}

			@Override
			public void doOptions(Player player, List<String> options) {
				if (options.size() > 1) {
					throw new RuntimeException("you can only select one card");
				}
				
				player.setCurrentChoice(null);
				
				if (options.size() == 0) return;
				
				for (Card c : player.getHand()) {
					
					if (options.get(0).equals(c.getName())) {
						player.addThroneRoomAction(c);
						player.play(c.getName(), true);
						
						break;
					}
				}
				
			}
		});
	}


}
