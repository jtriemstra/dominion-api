package com.jtriemstra.dominion.api.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpiceMerchantAction extends CardAction {

	@Override
	public void execute(Player player) {
		player.setCurrentChoice( new ActionChoice() {
			@Override
			public String getPrompt() { 
				return "Choose a treasure card to trash";
			}
			
			@Override
			public List<String> getOptions(){
				List<String> cardNames = new ArrayList<String>();
				for (Card c : player.getHand()) {
					if (c.getType() == Card.CardType.TREASURE) {
						cardNames.add(c.getName());
					}
				}
				return cardNames;
			}

			@Override
			public void doOptions(Player player, List<String> options) {
				Card cardToTrash = null;
				//TODO: validate is treasure card - introduce validation function?
				for (Card c : player.getHand()) {
					if (c.getName().equals(options.get(0))) {
						cardToTrash = c;
						break;
					}
				}
				
				if (cardToTrash == null) {
					throw new RuntimeException("selected card not found in hand");
				}

				player.getHand().remove(cardToTrash);
				
				player.setCurrentChoice(null);
				player.setCurrentChoice( new ActionChoice() {
					@Override
					public String getPrompt() { 
						return "Choose one:";
					}
					
					@Override
					public List<String> getOptions(){
						return Arrays.asList("2 Cards; 1 Action", "1 Buy; 2 Treasure");
					}
					
					@Override
					public void doOptions(Player player, List<String> options){
						if (options.size() > 1) {
							throw new RuntimeException("At most one option can be chosen");
						}
						
						if (options.size() == 0) {
							return;
						}
						System.out.println(options.get(0));
						if (options.get(0).equals("2 Cards; 1 Action")) {
							player.draw();
							player.draw();
							player.setTemporaryActions(player.getTemporaryActions() + 1);
						}
						else if (options.get(0).equals("1 Buy; 2 Treasure")) {
							player.setTemporaryBuys(player.getTemporaryBuys() + 1);
							player.setTemporaryTreasure(player.getTemporaryTreasure() + 2);
						}
						
						player.setCurrentChoice(null);
					}							
				});
			}
		});		
	}

}

