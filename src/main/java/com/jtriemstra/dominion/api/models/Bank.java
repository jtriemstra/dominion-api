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
	
	public List<BankCard> getBankCards() {
		List<BankCard> result = new ArrayList<>();
		for (BankCard b : bank.values()) {
			if (b.getCard().getName().equals("Gold") ||
				b.getCard().getName().equals("Silver") ||
				b.getCard().getName().equals("Copper") ||
				b.getCard().getName().equals("Estate") ||
				b.getCard().getName().equals("Duchy") ||
				b.getCard().getName().equals("Province") ||
				b.getCard().getName().equals("Curse")) {				
			}
			else {
				result.add(b);
			}			
		}
		
		result.sort((c1, c2) -> c1.getCard().getCost() - c2.getCard().getCost());
		
		result.add(0, bank.get("Copper"));
		result.add(0, bank.get("Silver"));
		result.add(0, bank.get("Gold"));
		result.add(bank.get("Estate"));
		result.add(bank.get("Duchy"));
		result.add(bank.get("Province"));
		result.add(bank.get("Curse"));
		
		return result;
	}
	
	private void addCard(String name, Card card, int quantity) {
		bank.put(name, new BankCard(card, quantity));
	}
	
	public Bank() {
		this(false);
	}
	
	public Bank(boolean isRandom) {
		if (!isRandom) {
			addCard("Gold", gold(), 30);
			addCard("Silver", silver(), 40);
			addCard("Copper", copper(), 60);
			addCard("Estate", estate(), 24);
			addCard("Duchy",  duchy(), 12);
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
			addCard("Gold", gold(), 30);
			addCard("Silver", silver(), 40);
			addCard("Copper", copper(), 60);
			addCard("Estate", estate(), 24);
			addCard("Duchy",  duchy(), 12);
			addCard("Province", province(), 12);
			addCard("Curse", curse(), 30);
			
			List<String> names = new ArrayList<String>(Arrays.asList("Village", "Smithy", "Chapel", "Throne Room", "Workshop", "Laboratory", "Woodcutter", "Adventurer", "Bureaucrat", 
					"Cellar", "Chancellor", "Council Room", "Feast", "Festival", "Library", "Market", "Militia", "Mine", "Moneylender", "Remodel", "Thief", "Gardens",
					/*"Spy",*/ "Witch", "Cartographer", "Oracle", "Highway", "Noble Brigand", "Jack of All Trades", "Inn", "Farmland", "Mandarin",
					"Scheme", "Haggler", "Fools Gold", "Tunnel", "Margrave", "Ill-Gotten Gains", "Cache", "Embassy", "Nomad Camp", "Border Village",
					"Develop", "Silk Road", "Stables", "Margrave", "Spice Merchant", "Oasis", "Crossroads"));
			Random indexGenerator = new Random();
			for(int i=0; i<10; i++) {
				int cardIndex = indexGenerator.nextInt(names.size());
				addCardByName(names.remove(cardIndex));
			}
		}
	}
	
	public Bank(List<String> cardNames) {
		addCard("Gold", gold(), 30);
		addCard("Silver", silver(), 40);
		addCard("Copper", copper(), 60);
		addCard("Estate", estate(), 24);
		addCard("Duchy",  duchy(), 12);
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
		case "Oasis": addCard(s, oasis(), 10); break;
		case "Stables": addCard(s, stables(), 10); break;
		case "Silk Road": addCard(s, silkroad(), 10); break;
		case "Develop": addCard(s, develop(), 10); break;
		case "Crossroads": addCard(s, crossroads(), 10); break;
		case "Spice Merchant": addCard(s, spicemerchant(), 10); break;
		case "Margrave": addCard(s, margrave(), 10); break;
		case "Moat": addCard(s, moat(), 10); break;
		case "Embassy": addCard(s, embassy(), 10); break;
		case "Cache": addCard(s, cache(), 10); break;
		case "Nomad Camp": addCard(s, nomadcamp(), 10); break;
		case "Border Village": addCard(s, bordervillage(), 10); break;
		case "Ill-Gotten Gains": addCard(s, illgottengains(), 10); break;
		case "Farmland": addCard(s, farmland(), 10); break;
		case "Mandarin": addCard(s, mandarin(), 10); break;
		case "Inn": addCard(s, inn(), 10); break;
		case "Cartographer": addCard(s, cartographer(), 10); break;
		case "Jack of All Trades": addCard(s, jackofalltrades(), 10); break;
		case "Noble Brigand": addCard(s, noblebrigand(), 10); break;
		case "Tunnel": addCard(s, tunnel(), 10); break;
		case "Haggler": addCard(s, haggler(), 10); break;
		case "Fools Gold": addCard(s, foolsgold(), 10); break;
		case "Scheme": addCard(s, scheme(), 10); break;
		case "Trader": addCard(s, trader(), 10); break;
		case "Duchess": addCard(s, duchess(), 10); break;
		case "Thief": addCard(s, thief(), 10); break;
		case "Gardens": addCard(s, gardens(), 10); break;
		case "Highway": addCard(s, highway(), 10); break;
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
	
	public void returnCard(String cardName) {
		bank.get(cardName).setQuantity(bank.get(cardName).getQuantity() + 1);
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
	
	public List<String> getNamesByExactCost(int cost) {
		List<String> cardNames = new ArrayList<String>();
		for (BankCard bc : bank.values()) {
			if (bc.getCard().getCost() == cost) {
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
	
	public List<String> getNamesByNonTypeAndMaxCost(Card.CardType type, int cost) {
		List<String> cardNames = new ArrayList<String>();
		for (BankCard bc : bank.values()) {
			if (bc.getCard().getType() != type && bc.getCard().getCost() <= cost) {
				cardNames.add(bc.getCard().getName());
			}
		}
		return cardNames;
	}
	
	public boolean hasCard(String cardName) {
		return bank.containsKey(cardName);
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
						p.gainTo(getByName("Curse"), p.getDiscard());
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
	
	
	
	public Card oasis() {
		Card c = new Card(3, "Oasis", 0, 1, Card.CardType.ACTION, 0, 1, 1);
		c.setSpecialAction(new OasisAction());
		return c;
	}
	public Card margrave() {
		Card c = new Card(5, "Margrave", 0, 0, Card.CardType.ACTION, 1, 0, 3);
		c.setSpecialAction(new MargraveAction());
		return c;
	}
	public Card stables() {
		Card c = new Card(5, "Stables", 0, 0, Card.CardType.ACTION, 0, 0, 0);
		c.setSpecialAction(new StablesAction());
		return c;
	}
	public Card silkroad() {
		Card c = new Card(4, "Silk Road", 0, 0, Card.CardType.VICTORY, 0, 0, 0);
		c.setVictoryFunction(new SilkRoadVictory());
		return c;
	}
	public Card develop() {
		Card c = new Card(3, "Develop", 0, 0, Card.CardType.ACTION, 0, 0, 0);
		c.setSpecialAction(new DevelopAction(this));
		return c;
	}
	public Card crossroads() {
		Card c = new Card(2, "Crossroads", 0, 0, Card.CardType.ACTION, 0, 0, 0);
		c.setSpecialAction(new CrossroadsAction());
		return c;
	}
	public Card cartographer() {
		Card c = new Card(5, "Cartographer", 0, 1, Card.CardType.ACTION, 0, 0, 1);
		c.setSpecialAction(new CartographerAction());
		return c;
	}
	public Card spicemerchant() {
		Card c = new Card(4, "Spice Merchant", 0, 0, Card.CardType.ACTION, 0, 0, 0);
		c.setSpecialAction(new SpiceMerchantAction());
		return c;
	}
	public Card nomadcamp() {
		Card c = new Card(4, "Nomad Camp", 0, 0, Card.CardType.ACTION, 1, 2, 0);
		c.setBuyDestination(() -> Player.CardSet.DECK);
		return c;
	}
	public Card bordervillage() {
		Card c = new Card(6, "Border Village", 0, 2, Card.CardType.ACTION, 0, 0, 1);
		c.setGainAction(new BorderVillageGainAction(this));
		return c;
	}
	public Card cache() {
		Card c = new Card(5, "Cache", 0, 0, Card.CardType.TREASURE, 0, 3, 0);
		c.setGainAction(new CardAction() {
			@Override
			public void execute(Player player) {
				log.info("Calling gain from Cache");
				player.gainTo(getByName("Copper"), player.getBought());
				player.gainTo(getByName("Copper"), player.getBought());
			}
		});
		return c;
	}
	public Card embassy() {
		Card c = new Card(5, "Embassy", 0, 0, Card.CardType.ACTION, 0, 0, 5);
		c.setGainAction(new CardAction() {
			@Override
			public void execute(Player player) {
				for(Player p : player.getGame().getOtherPlayers(player)) {
					p.gainTo(getByName("Silver"), p.getDiscard());
				}
			}
			
		});
		c.setSpecialAction(new EmbassyAction());
		return c;
	}
	public Card illgottengains() {
		Card c = new Card(5, "Ill-Gotten Gains", 0, 0, Card.CardType.TREASURE, 0, 1, 0);
		c.setGainAction(new CardAction() {
			@Override
			public void execute(Player player) {
				for(Player p : player.getGame().getOtherPlayers(player)) {
					p.gainTo(getByName("Curse"), p.getDiscard());
				}
			}
		});
		c.setSpecialAction(new IllGottenGainsAction(this));
		return c;
	}
	public Card farmland() {
		Card c = new Card(6, "Farmland", 2, 0, Card.CardType.VICTORY, 0, 0, 0);
		c.setBuyAction(new FarmlandBuyAction(this));
		return c;
	}
	public Card mandarin() {
		Card c = new Card(5, "Mandarin", 0, 0, Card.CardType.ACTION, 0, 3, 0);
		c.setSpecialAction(new MandarinAction());
		c.setGainAction(new MandarinGainAction());
		return c;
	}
	public Card inn() {
		Card c = new Card(5, "Inn", 0, 2, Card.CardType.ACTION, 0, 0, 2);
		c.setSpecialAction(new InnAction());
		c.setGainAction(new InnGainAction());
		return c;
	}
	public Card highway() {
		Card c = new Card(5, "Highway", 0, 1, Card.CardType.ACTION, 0, 0, 1);
		c.setSpecialAction(new CardAction() {
			@Override
			public void execute(Player player) {
				player.getGame().addCardModifier("Highway");
			}
		});
		return c;
	}
	public Card jackofalltrades() {
		Card c = new Card(4, "Jack of All Trades", 0, 0, Card.CardType.ACTION, 0, 0, 0);
		c.setSpecialAction(new JackOfAllTradesAction(this));
		return c;
	}
	public Card noblebrigand() {
		Card c = new Card(4, "Noble Brigand", 0, 0, Card.CardType.ACTION, 0, 1, 0);
		c.setSpecialAction(new NobleBrigandAction(this));
		c.setBuyAction(new NobleBrigandAction(this));
		return c;
	}
	public Card tunnel() {
		Card c = new Card(3, "Tunnel", 2, 0, Card.CardType.VICTORY, 0, 0, 0);
		c.setDiscardAction(new TunnelDiscardAction(this));
		return c;
	}
	public Card haggler() {
		return new Card(5, "Haggler", 0, 0, Card.CardType.ACTION, 0, 2, 0);
	}
	public Card foolsgold() {
		Card c = new Card(2, "Fools Gold", 0, 0, Card.CardType.TREASURE, 0, 0, 0);
		c.setTreasureFunction(new FoolsGoldTreasureFunction());
		return c;
	}
	public Card scheme() {
		return new Card(3, "Scheme", 0, 1, Card.CardType.ACTION, 0, 0, 1);
	}
	public Card trader() {
		Card c = new Card(4, "Trader", 0, 0, Card.CardType.ACTION, 0, 0, 0);
		c.setSpecialAction(new TraderAction(this));
		return c;
	}
	public Card oracle() {
		Card c = new Card(3, "Oracle", 0, 0, Card.CardType.ACTION, 0, 0, 0);
		c.setSpecialAction(new OracleAction());
		return c;
	}
	public Card duchess() {
		Card c = new Card(2, "Duchess", 0, 0, Card.CardType.ACTION, 0, 2, 0);
		c.setSpecialAction(new DuchessAction());
		return c;
	}
	public Card thief() {
		Card c = new Card(4, "Thief", 0, 0, Card.CardType.ACTION, 0, 0, 0);
		c.setSpecialAction(new ThiefAction());
		return c;
	}
}
