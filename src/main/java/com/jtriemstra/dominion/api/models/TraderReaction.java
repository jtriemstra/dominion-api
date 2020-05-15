package com.jtriemstra.dominion.api.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TraderReaction extends CardAction {
	private Bank bank;
	private List<Card> gainDestination;
	private Card cardToBeGained;
	
	public TraderReaction(Bank bank, List<Card> gainDestination, Card cardToBeGained) {
		this.bank = bank;
		this.gainDestination = gainDestination;
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
					player.finishGain(bank.getByName("Silver"), gainDestination);
					bank.returnCard(cardToBeGained.getName());
				}
				else {
					player.finishGain(cardToBeGained, gainDestination);
				}
			}
		});		
	}


}
