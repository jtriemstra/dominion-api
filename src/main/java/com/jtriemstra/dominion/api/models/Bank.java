package com.jtriemstra.dominion.api.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Bank {
	
	private HashMap<String, Card> bank = new HashMap<>();
	
	public HashMap<String, Card> getBankCards() {
		return bank;
	}
	
	public Bank() {
		bank.put("Gold", gold());
		bank.put("Silver", silver());
		bank.put("Copper", copper());
		bank.put("Estate", estate());
		bank.put("Duchy",  duchy());
		bank.put("Province", province());
		
		bank.put("Village",  village());
		bank.put("Smithy", smithy());
		bank.put("Chapel", chapel());
		bank.put("Throne Room", throneroom());
		bank.put("Workshop", workshop());
		bank.put("Mine", mine());
		bank.put("Cellar", cellar());
		bank.put("Militia", militia());
		bank.put("Market", market());
	}
	
	public Bank(List<String> cardNames) {
		bank.put("Gold", gold());
		bank.put("Silver", silver());
		bank.put("Copper", copper());
		bank.put("Estate", estate());
		bank.put("Duchy",  duchy());
		bank.put("Province", province());
		bank.put("Curse", curse());
		
		for (String s : cardNames) {
			switch(s) {
			case "Village": bank.put(s, village()); break;
			case "Smithy": bank.put(s, smithy()); break;
			case "Chapel": bank.put(s, chapel()); break;
			case "Throne Room": bank.put(s, throneroom()); break;
			case "Workshop": bank.put(s, workshop()); break;
			case "Laboratory": bank.put(s, laboratory()); break;
			case "Woodcutter": bank.put(s, woodcutter()); break;
			case "Adventurer": bank.put(s, adventurer()); break;
			case "Bureaucrat": bank.put(s, bureaucrat()); break;
			case "Cellar": bank.put(s, cellar()); break;
			case "Chancellor": bank.put(s, chancellor()); break;
			case "Council Room": bank.put(s, councilroom()); break;
			case "Feast": bank.put(s, feast()); break;
			case "Festival": bank.put(s, festival()); break;
			case "Library": bank.put(s, library()); break;
			case "Market": bank.put(s, market()); break;
			case "Militia": bank.put(s, militia()); break;
			case "Mine": bank.put(s, mine()); break;
			case "Moneylender": bank.put(s, moneylender()); break;
			case "Remodel": bank.put(s, remodel()); break;
			case "Spy": bank.put(s, spy()); break;
			case "Witch": bank.put(s, witch()); break;
			
			}
		}
	}
	
	public List<Card> newDeck(){
		List<Card> newDeck = new ArrayList<>();
		for (int i=0; i<3; i++) {
			newDeck.add(bank.get("Estate"));
		}
		for (int i=0; i<7; i++) {
			newDeck.add(bank.get("Copper"));
		}
		return newDeck;
	}
	
	public Card tryToBuy(String name, int treasureAvailable) {
		Card c = bank.get(name);
		
		if (c == null) {
			throw new RuntimeException("no card found in bank with this name");
		}
		
		if (c.getCost() <= treasureAvailable) {
			return c;
		}
		
		throw new RuntimeException("not enough money to buy this card");
	}
	
	public Card getByName(String cardName) {
		if (!StringUtils.hasText(cardName)) {
			throw new RuntimeException("no card name was passed to getByName");
		}
		
		if (!bank.containsKey(cardName)) {
			throw new RuntimeException("this card does not exist in the bank");
		}
		
		return bank.get(cardName);
	}
	
	public List<String> getNamesByMaxCost(int cost) {
		List<String> cardNames = new ArrayList<String>();
		for (Card c : bank.values()) {
			if (c.getCost() <= cost) {
				cardNames.add(c.getName());
			}
		}
		return cardNames;
	}
	
	public List<String> getNamesByTypeAndMaxCost(Card.CardType type, int cost) {
		List<String> cardNames = new ArrayList<String>();
		for (Card c : bank.values()) {
			if (c.getType() == type && c.getCost() <= cost) {
				cardNames.add(c.getName());
			}
		}
		return cardNames;
	}
	
	public  Card gold() {
		return new Card(6, "Gold", 0, 0, Card.CardType.TREASURE, 0, 3, 0);
	}
	public  Card silver() {
		return new Card(3, "Silver", 0, 0, Card.CardType.TREASURE, 0, 2, 0);
	}
	public  Card copper() {
		return new Card(0, "Copper", 0, 0, Card.CardType.TREASURE, 0, 1, 0);
	}
	public  Card estate() {
		return new Card(2, "Estate", 1, 0, Card.CardType.VICTORY, 0, 0, 0);
	}
	public  Card duchy() {
		return new Card(5, "Duchy", 3, 0, Card.CardType.VICTORY, 0, 0, 0);
	}
	public  Card province() {
		return new Card(8, "Province", 6, 0, Card.CardType.VICTORY, 0, 0, 0);
	}
	public  Card curse() {
		return new Card(0, "Curse", -1, 0, Card.CardType.VICTORY, 0, 0, 0);
	}
	public  Card village() {
		return new Card(3, "Village", 0, 2, Card.CardType.ACTION, 0, 0, 1);
	}
	public  Card smithy() {
		return new Card(4, "Smithy", 0, 0, Card.CardType.ACTION, 0, 0, 3);
	}
	public  Card laboratory() {
		return new Card(5, "Laboratory", 0, 1, Card.CardType.ACTION, 0, 0, 2);
	}
	public  Card woodcutter() {
		return new Card(3, "Woodcutter", 0, 0, Card.CardType.ACTION, 1, 2, 0);
	}
	public  Card festival() {
		return new Card(5, "Festival", 0, 2, Card.CardType.ACTION, 1, 2, 0);
	}
	public  Card market() {
		return new Card(5, "Market", 0, 1, Card.CardType.ACTION, 1, 1, 1);
	}
	public  Card adventurer() {
		Card c = new Card(6, "Adventurer", 0, 0, Card.CardType.ACTION, 0, 0, 0);
		c.setSpecialAction(new AdventurerAction());
		return c;
	}
	public  Card chancellor() {
		Card c = new Card(3, "Chancellor", 0, 0, Card.CardType.ACTION, 0, 2, 0);
		c.setSpecialAction(new ChancellorAction());
		return c;
	}
	public  Card chapel() {
		Card c = new Card(2, "Chapel", 0, 0, Card.CardType.ACTION, 0, 0, 0);
		c.setSpecialAction(new ChapelAction());
		return c;
	}
	public  Card cellar() {
		Card c = new Card(2, "Cellar", 0, 1, Card.CardType.ACTION, 0, 0, 0);
		c.setSpecialAction(new CellarAction());
		return c;
	}
	public  Card workshop() {
		Card c = new Card(3, "Workshop", 0, 0, Card.CardType.ACTION, 0, 0, 0);
		c.setSpecialAction(new WorkshopAction(this));
		return c;
	}
	public  Card feast() {
		Card c = new Card(4, "Feast", 0, 0, Card.CardType.ACTION, 0, 0, 0);
		c.setSpecialAction(new FeastAction(this, c));
		return c;
	}
	public  Card mine() {
		Card c = new Card(5, "Mine", 0, 0, Card.CardType.ACTION, 0, 0, 0);
		c.setSpecialAction(new MineAction(this) );
		return c;
	}
	public  Card remodel() {
		Card c = new Card(4, "Remodel", 0, 0, Card.CardType.ACTION, 0, 0, 0);
		c.setSpecialAction(new RemodelAction(this));
		return c;
	}
	public  Card moneylender() {
		Card c = new Card(4, "Moneylender", 0, 0, Card.CardType.ACTION, 0, 0, 0);
		c.setSpecialAction(new MoneylenderAction());
		return c;
	}
	public  Card library() {
		Card c = new Card(5, "Library", 0, 0, Card.CardType.ACTION, 0, 0, 0);
		c.setSpecialAction(new LibraryAction());
		return c;		
	}
	public  Card throneroom() {
		Card c = new Card(4, "Throne Room", 0, 0, Card.CardType.ACTION, 0, 0, 0);
		c.setSpecialAction(new ThroneRoomAction());
		return c;
	}
	public  Card militia() {
		Card c = new Card(4, "Militia", 0, 0, Card.CardType.ACTION, 0, 2, 0);
		c.setSpecialAction(new MilitiaAction());
		return c;
	}
	public  Card councilroom() {
		Card c = new Card(5, "Council Room", 0, 0, Card.CardType.ACTION, 1, 0, 4);
		c.setSpecialAction(new CouncilRoomAction());
		return c;
	}
	public  Card witch() {
		Card c = new Card(5, "Witch", 0, 0, Card.CardType.ACTION, 0, 0, 2);
		c.setSpecialAction(new CardAction() {
			@Override
			public void execute(Player player) {
				for(Player p : player.getGame().getOtherPlayers(player)) {
					if (!p.hasCard("Moat")) {
						p.getDiscard().add(bank.get("Curse"));
					}
				}
			}
			
		});
		return c;
	}
	public  Card bureaucrat() {
		Card c = new Card(4, "Bureaucrat", 0, 0, Card.CardType.ACTION, 0, 0, 0);
		c.setSpecialAction(new BureaucratAction(this));
		return c;
	}
	public  Card spy() {
		Card c = new Card(4, "Spy", 0, 1, Card.CardType.ACTION, 0, 0, 1);
		c.setSpecialAction(new SpyAction());
		return c;
	}
}
