package com.jtriemstra.dominion.api.models;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HagglerAction extends CardAction {
	private Bank bank;
	private int boughtCardCost;
	
	public HagglerAction(Bank bank, int boughtCardCost) {
		this.bank = bank;
		this.boughtCardCost = boughtCardCost;
	}
	
	@Override
	public void execute(Player player) {
		player.addCurrentChoice( new ActionChoice() {
			@Override
			public String getPrompt() { 
				return "Choose a card to gain from Haggler";
			}
			
			@Override
			public List<String> getOptions(){
				List<String> cardNames = bank.getNamesByNonTypeAndMaxCost(Card.CardType.VICTORY, boughtCardCost - 1);
				
				return cardNames;
			}

			@Override
			public void doOptions(Player player, List<String> options) {
				if (options.size() != 1) {
					throw new RuntimeException("One and only one option can be chosen");
				}
				
				player.setCurrentChoice(null);
				
				log.info("Calling gain from Haggler");
				player.gainTo(bank.getByName(options.get(0)), player.getBought());
				
			}
		});
	}


}
