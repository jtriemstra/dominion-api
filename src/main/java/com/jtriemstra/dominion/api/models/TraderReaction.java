package com.jtriemstra.dominion.api.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TraderReaction extends CardAction {
	private Bank bank;
	private Card cardToBeGained;
	
	public TraderReaction(Bank bank, Card cardToBeGained) {
		this.bank = bank;
		this.cardToBeGained = cardToBeGained;
	}
	
	@Override
	public void execute(Player player) {
		player.setCurrentChoice( new ActionChoice() {
			@Override
			public String getPrompt() { 
				return "Would you like to gain a silver instead of the regular card " + cardToBeGained.getName();
			}
			
			@Override
			public List<String> getOptions(){
				return Arrays.asList("Yes", "No");
			}

			@Override
			public int getMinOptions() {
				return 1;
			}

			@Override
			public int getMaxOptions() {
				return 1;
			}

			@Override
			public void doOptions(Player player, List<String> options) {
				if (options.size() != 1) {
					throw new RuntimeException("One and only one option can be chosen");
				}
				
				player.setCurrentChoice(null);
				
				if (options.get(0).equals("Yes")) {
					if (!(player.getBuyStages().get(0) instanceof Player.GainAction)) {
						throw new RuntimeException("Buy stages have gotten out of sync");
					}
					//TODO: is there a cleaner interface here?
					//TODO: per wiki, the silver should always go to discard/bought, not to hand/deck, even if that's where the original card was going
					Player.GainAction a = (Player.GainAction) player.getBuyStages().get(0);
					a.setNewCard(bank.getByName("Silver"));
					bank.returnCard(cardToBeGained.getName());
				}
			}
		});		
	}


}
