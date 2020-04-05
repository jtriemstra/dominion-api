package com.jtriemstra.dominion.api.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Bank {
	
	private HashMap<String, BankCard> bank = new LinkedHashMap<>();
	
	public HashMap<String, BankCard> getBankCards() {
		return bank;
	}
	
	private void addCard(String name, Card card, int quantity) {
		bank.put(name, new BankCard(card, quantity));
	}
	
	public Bank() {
		this(false);
	}
	
	public Bank(boolean isRandom) {
		if (!isRandom) {
			addCard("Gold", gold(), 50);
			addCard("Silver", silver(), 100);
			addCard("Copper", copper(), 200);
			addCard("Estate", estate(), 30);
			addCard("Duchy",  duchy(), 20);
			addCard("Province", province(), 12);
			addCard("Curse", curse(), 30);
			
			addCard("Village",  village(), 10);
			addCard("Smithy", smithy(), 10);
			addCard("Chapel", chapel(), 10);
			addCard("Throne Room", throneroom(), 10);
			addCard("Workshop", workshop(), 10);
			addCard("Mine", mine(), 10);
			addCard("Cellar", cellar(), 10);
			addCard("Militia", militia(), 10);
			addCard("Market", market(), 10);
			addCard("Moat", moat(), 10);
		}
		else {
			addCard("Gold", gold(), 50);
			addCard("Silver", silver(), 100);
			addCard("Copper", copper(), 200);
			addCard("Estate", estate(), 30);
			addCard("Duchy",  duchy(), 20);
			addCard("Province", province(), 12);
			addCard("Curse", curse(), 30);
			
			List<String> names = new ArrayList<String>(Arrays.asList("Village", "Smithy", "Chapel", "Throne Room", "Workshop", "Laboratory", "Woodcutter", "Adventurer", "Bureaucrat", 
					"Cellar", "Chancellor", "Council Room", "Feast", "Festival", "Library", "Market", "Militia", "Mine", "Moneylender", "Remodel", 
					/*"Spy",*/ "Witch"));
			Random indexGenerator = new Random();
			for(int i=0; i<10; i++) {
				int cardIndex = indexGenerator.nextInt(names.size());
				addCardByName(names.remove(cardIndex));
			}
		}
	}
	
	public Bank(List<String> cardNames) {
		addCard("Gold", gold(), 50);
		addCard("Silver", silver(), 100);
		addCard("Copper", copper(), 200);
		addCard("Estate", estate(), 30);
		addCard("Duchy",  duchy(), 20);
		addCard("Province", province(), 12);
		addCard("Curse", curse(), 30);
		
		for (String s : cardNames) {
			addCardByName(s);
		}
	}
	
	private void addCardByName(String s) {
		switch(s) {
		case "Village": addCard(s, village(), 10); break;
		case "Smithy": addCard(s, smithy(), 10); break;
		case "Chapel": addCard(s, chapel(), 10); break;
		case "Throne Room": addCard(s, throneroom(), 10); break;
		case "Workshop": addCard(s, workshop(), 10); break;
		case "Laboratory": addCard(s, laboratory(), 10); break;
		case "Woodcutter": addCard(s, woodcutter(), 10); break;
		case "Adventurer": addCard(s, adventurer(), 10); break;
		case "Bureaucrat": addCard(s, bureaucrat(), 10); break;
		case "Cellar": addCard(s, cellar(), 10); break;
		case "Chancellor": addCard(s, chancellor(), 10); break;
		case "Council Room": addCard(s, councilroom(), 10); break;
		case "Feast": addCard(s, feast(), 10); break;
		case "Festival": addCard(s, festival(), 10); break;
		case "Library": addCard(s, library(), 10); break;
		case "Market": addCard(s, market(), 10); break;
		case "Militia": addCard(s, militia(), 10); break;
		case "Mine": addCard(s, mine(), 10); break;
		case "Moneylender": addCard(s, moneylender(), 10); break;
		case "Remodel": addCard(s, remodel(), 10); break;
		case "Spy": addCard(s, spy(), 10); break;
		case "Witch": addCard(s, witch(), 10); break;
		
		}
	}
	
	public List<Card> newDeck(){
		List<Card> newDeck = new ArrayList<>();
		for (int i=0; i<3; i++) {
			newDeck.add(bank.get("Estate").getCard());
		}
		for (int i=0; i<7; i++) {
			newDeck.add(bank.get("Copper").getCard());
		}
		return newDeck;
	}
	
	public boolean isGameOver() {
		if (bank.get("Province").getQuantity() == 0) {
			return true;
		}
		
		int emptyKingdomCards = 0;
		for(String s : bank.keySet()) {
			Card c = bank.get(s).getCard();
			//TODO: account for multiple types
			if (c.getType() == Card.CardType.ACTION) {
				if (bank.get(s).getQuantity() == 0) {
					emptyKingdomCards++;
				}
			}
		}
		
		return emptyKingdomCards >= 3;
	}
	
	public Card tryToBuy(String name, int treasureAvailable) {
		Card c = bank.get(name).getCard();
		
		if (bank.get(name).getQuantity() <= 0) {
			throw new RuntimeException("the bank has run out of this card");
		}
		
		if (c == null) {
			throw new RuntimeException("no card found in bank with this name");
		}
		
		//TODO: review atomicity
		if (c.getCost() <= treasureAvailable) {
			bank.get(name).setQuantity(bank.get(name).getQuantity() - 1);
			return c;
		}
		
		throw new RuntimeException("not enough money to buy this card");
	}
	
	//TODO: rename to "gain" or something
	public Card getByName(String cardName) {
		if (!StringUtils.hasText(cardName)) {
			throw new RuntimeException("no card name was passed to getByName");
		}
		
		if (!bank.containsKey(cardName)) {
			throw new RuntimeException("this card does not exist in the bank");
		}
		
		//TODO: review atomicity
		if (bank.get(cardName).getQuantity() > 0) {
			bank.get(cardName).setQuantity(bank.get(cardName).getQuantity() - 1);
			return bank.get(cardName).getCard();
		}
		else {
			throw new RuntimeException("the bank has run out of this card");
		}
	}
	
	public List<String> getNamesByMaxCost(int cost) {
		List<String> cardNames = new ArrayList<String>();
		for (BankCard bc : bank.values()) {
			if (bc.getCard().getCost() <= cost) {
				cardNames.add(bc.getCard().getName());
			}
		}
		return cardNames;
	}
	
	public List<String> getNamesByTypeAndMaxCost(Card.CardType type, int cost) {
		List<String> cardNames = new ArrayList<String>();
		for (BankCard bc : bank.values()) {
			if (bc.getCard().getType() == type && bc.getCard().getCost() <= cost) {
				cardNames.add(bc.getCard().getName());
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
	public  Card moat() {
		return new Card(2, "Moat", 0, 0, Card.CardType.ACTION, 0, 0, 2);
	}
	public Card gardens() {
		Card c = new Card(4, "Gardens", 0, 0, Card.CardType.VICTORY, 0, 0, 0);
		c.setVictoryFunction(new GardensVictory());
		return c;
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
						p.getDiscard().add(bank.get("Curse").getCard());
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
