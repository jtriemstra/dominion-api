package com.jtriemstra.dominion.api.models;

import java.util.*;

public class CardFactory {
	
	private static HashMap<String, Card> bank = new HashMap<>();
	
	static {
		bank.put("Gold", gold());
		bank.put("Silver", silver());
		bank.put("Copper", copper());
		bank.put("Estate", estate());
		bank.put("Duchy",  duchy());
		bank.put("Province", province());
		bank.put("Village",  village());
		bank.put("Smithy", smithy());
	}
	
	public static List<Card> newDeck(){
		List<Card> newDeck = new ArrayList<>();
		for (int i=0; i<3; i++) {
			newDeck.add(bank.get("Estate"));
		}
		for (int i=0; i<7; i++) {
			newDeck.add(bank.get("Copper"));
		}
		return newDeck;
	}
	
	public static Card tryToBuy(String name, int treasureAvailable) {
		Card c = bank.get(name);
		
		if (c == null) {
			throw new RuntimeException("no card found in bank with this name");
		}
		
		if (c.getCost() <= treasureAvailable) {
			return c;
		}
		
		throw new RuntimeException("not enough money to buy this card");
	}
	
	public static Card gold() {
		return new Card(6, "Gold", 0, 0, Card.CardType.TREASURE, 0, 3, 0);
	}
	public static Card silver() {
		return new Card(3, "Silver", 0, 0, Card.CardType.TREASURE, 0, 2, 0);
	}
	public static Card copper() {
		return new Card(0, "Copper", 0, 0, Card.CardType.TREASURE, 0, 1, 0);
	}
	public static Card estate() {
		return new Card(2, "Estate", 1, 0, Card.CardType.VICTORY, 0, 0, 0);
	}
	public static Card duchy() {
		return new Card(5, "Duchy", 3, 0, Card.CardType.VICTORY, 0, 0, 0);
	}
	public static Card province() {
		return new Card(8, "Province", 6, 0, Card.CardType.VICTORY, 0, 0, 0);
	}
	public static Card village() {
		return new Card(3, "Village", 0, 2, Card.CardType.ACTION, 0, 0, 1);
	}
	public static Card smithy() {
		return new Card(4, "Smithy", 0, 0, Card.CardType.ACTION, 0, 0, 3);
	}
	public static Card laboratory() {
		return new Card(5, "Laboratory", 0, 1, Card.CardType.ACTION, 0, 0, 2);
	}
	public static Card woodcutter() {
		return new Card(3, "Woodcutter", 0, 0, Card.CardType.ACTION, 1, 2, 0);
	}
	public static Card festival() {
		return new Card(5, "Festival", 0, 2, Card.CardType.ACTION, 1, 2, 0);
	}
	public static Card market() {
		return new Card(5, "Market", 0, 1, Card.CardType.ACTION, 1, 1, 1);
	}
	public static Card adventurer() {
		Card c = new Card(6, "Adventurer", 0, 0, Card.CardType.ACTION, 0, 0, 0);
		c.setExtraAction(new CardAction() {
			@Override
			public void execute(Player player) {
				int treasureCardsFound = 0;
				while (treasureCardsFound < 2) {
					Card c = player.reveal();
					if (c.getType() == Card.CardType.TREASURE) {
						treasureCardsFound++;
						player.addToHand(c);
					}
					else {
						player.discard(c);
					}
				}
			}
		});
		return c;
	}
}
