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
		c.setSpecialAction(new CardAction() {
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
						player.discardFromTemp(c);
					}
				}
			}
		});
		return c;
	}
	public  Card chancellor() {
		Card c = new Card(3, "Chancellor", 0, 0, Card.CardType.ACTION, 0, 2, 0);
		c.setSpecialAction(new CardAction() {
			@Override
			public void execute(Player player) {
				player.setCurrentChoice( new ActionChoice() {
					@Override
					public String getPrompt() { 
						return "Do you want to put your deck into your discard pile?";
					}
					
					@Override
					public List<String> getOptions(){
						return Arrays.asList("Yes", "No");
					}
					
					@Override
					public void doOptions(Player player, List<String> options) {
						if (options.size() != 1) {
							throw new RuntimeException("One and only one option can be chosen");
						}
						
						if (options.get(0).equals("Yes")) {
							player.getDiscard().addAll(player.getDeck());
							player.getDeck().clear();
						}

						player.setCurrentChoice(null);
					}
				});
			}
			
		});
		return c;
	}
	public  Card chapel() {
		Card c = new Card(2, "Chapel", 0, 0, Card.CardType.ACTION, 0, 0, 0);
		c.setSpecialAction(new CardAction() {
			@Override
			public void execute(Player player) {
				player.setCurrentChoice( new ActionChoice() {
					@Override
					public String getPrompt() { 
						return "Which cards would you like to trash (max 4)?";
					}
					
					@Override
					public List<String> getOptions(){
						List<String> cardNames = new ArrayList<String>();
						for (Card c : player.getHand()) {
							cardNames.add(c.getName());
						}
						return cardNames;
					}

					@Override
					public void doOptions(Player player, List<String> options) {
						for(String cardName : options) {
							for (Card c : player.getHand()) {
								if (cardName.equals(c.getName())) {
									player.getHand().remove(c);
									break;
								}
							}
						}
						
						player.setCurrentChoice(null);
					}
				});
			}
			
		});
		return c;
	}
	public  Card cellar() {
		Card c = new Card(2, "Cellar", 0, 1, Card.CardType.ACTION, 0, 0, 0);
		c.setSpecialAction(new CardAction() {
			@Override
			public void execute(Player player) {
				player.setCurrentChoice( new ActionChoice() {
					@Override
					public String getPrompt() { 
						return "Which cards would you like to discard?";
					}
					
					@Override
					public List<String> getOptions(){
						List<String> cardNames = new ArrayList<String>();
						for (Card c : player.getHand()) {
							cardNames.add(c.getName());
						}
						return cardNames;
					}

					@Override
					public void doOptions(Player player, List<String> options) {
						for(String cardName : options) {
							for (Card c : player.getHand()) {
								if (cardName.equals(c.getName())) {
									player.discardFromHand(c);
									player.draw();
									break;
								}
							}
						}
						
						player.setCurrentChoice(null);
					}
				});
			}
			
		});
		return c;
	}
	public  Card workshop() {
		Card c = new Card(3, "Workshop", 0, 0, Card.CardType.ACTION, 0, 0, 0);
		c.setSpecialAction(new CardAction() {
			@Override
			public void execute(Player player) {
				player.setCurrentChoice( new ActionChoice() {
					@Override
					public String getPrompt() { 
						return "Choose a card costing up to 4";
					}
					
					@Override
					public List<String> getOptions(){
						List<String> cardNames = new ArrayList<String>();
						for (Card c : bank.values()) {
							if (c.getCost() <= 4) {
								cardNames.add(c.getName());
							}
						}
						return cardNames;
					}

					@Override
					public void doOptions(Player player, List<String> options) {
						if (options.size() != 1) {
							throw new RuntimeException("One and only one option can be chosen");
						}
						
						player.getDiscard().add(bank.get(options.get(0)));
log.info("setting current choice null");
						player.setCurrentChoice(null);
					}
				});
			}			
		});
		return c;
	}
	public  Card feast() {
		Card c = new Card(4, "Feast", 0, 0, Card.CardType.ACTION, 0, 0, 0);
		c.setSpecialAction(new CardAction() {
			@Override
			public void execute(Player player) {
				player.getPlayed().remove(c);
				
				player.setCurrentChoice( new ActionChoice() {
					@Override
					public String getPrompt() { 
						return "Choose a card costing up to 5";
					}
					
					@Override
					public List<String> getOptions(){
						List<String> cardNames = new ArrayList<String>();
						for (Card c : bank.values()) {
							if (c.getCost() <= 5) {
								cardNames.add(c.getName());
							}
						}
						return cardNames;
					}

					@Override
					public void doOptions(Player player, List<String> options) {
						if (options.size() != 1) {
							throw new RuntimeException("One and only one option can be chosen");
						}
						
						player.getDiscard().add(bank.get(options.get(0)));
						
						player.setCurrentChoice(null);
					}
				});
			}
			
		});
		return c;
	}
	public  Card mine() {
		Card c = new Card(5, "Mine", 0, 0, Card.CardType.ACTION, 0, 0, 0);
		c.setSpecialAction(new CardAction() {
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
						if (options.size() != 1) {
							throw new RuntimeException("One and only one option can be chosen");
						}
						
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
						
						int trashedCost = cardToTrash.getCost();
						player.getHand().remove(cardToTrash);
						
						player.setCurrentChoice( new ActionChoice() {
							@Override
							public String getPrompt() { 
								return "Choose a treasure card to gain";
							}
							
							@Override
							public List<String> getOptions(){
								List<String> cardNames = new ArrayList<String>();
								for (Card c : bank.values()) {
									if (c.getType() == Card.CardType.TREASURE && c.getCost() <= trashedCost + 3) {
										cardNames.add(c.getName());
									}
								}
								return cardNames;
							}
							
							@Override
							public void doOptions(Player player, List<String> options){
								if (options.size() != 1) {
									throw new RuntimeException("One and only one option can be chosen");
								}
								
								//TODO: validate choice
								
								player.addToHand(bank.get(options.get(0)));
								
								player.setCurrentChoice(null);
							}							
						});
						
					}
				});
			}
			
		});
		return c;
	}
	public  Card remodel() {
		Card c = new Card(4, "Remodel", 0, 0, Card.CardType.ACTION, 0, 0, 0);
		c.setSpecialAction(new CardAction() {
			@Override
			public void execute(Player player) {
				player.setCurrentChoice( new ActionChoice() {
					@Override
					public String getPrompt() { 
						return "Choose a card to trash";
					}
					
					@Override
					public List<String> getOptions(){
						List<String> cardNames = new ArrayList<String>();
						for (Card c : player.getHand()) {
							cardNames.add(c.getName());							
						}
						return cardNames;
					}

					@Override
					public void doOptions(Player player, List<String> options) {
						if (options.size() != 1) {
							throw new RuntimeException("One and only one option can be chosen");
						}
						
						Card cardToTrash = null;
						
						for (Card c : player.getHand()) {
							if (c.getName().equals(options.get(0))) {
								cardToTrash = c;
								break;
							}
						}
						
						if (cardToTrash == null) {
							throw new RuntimeException("selected card not found in hand");
						}
						
						int trashedCost = cardToTrash.getCost();
						player.getHand().remove(cardToTrash);
						
						player.setCurrentChoice( new ActionChoice() {
							@Override
							public String getPrompt() { 
								return "Choose a card to gain";
							}
							
							@Override
							public List<String> getOptions(){
								List<String> cardNames = new ArrayList<String>();
								for (Card c : bank.values()) {
									if (c.getCost() <= trashedCost + 2) {
										cardNames.add(c.getName());
									}
								}
								return cardNames;
							}
							
							@Override
							public void doOptions(Player player, List<String> options){
								if (options.size() != 1) {
									throw new RuntimeException("One and only one option can be chosen");
								}
										
								player.discardFromTemp(bank.get(options.get(0)));
							}							
						});
						
					}
				});
			}
			
		});
		return c;
	}
	public  Card moneylender() {
		Card c = new Card(4, "Moneylender", 0, 0, Card.CardType.ACTION, 0, 0, 0);
		c.setSpecialAction(new CardAction() {
			@Override
			public void execute(Player player) {
				for (Card c : player.getHand()) {
					if (c.getName().equals("Copper")) {
						player.getHand().remove(c);
						player.setTemporaryTreasure(player.getTemporaryTreasure() + 3);
						return;
					}
				}
				
				throw new RuntimeException("no coppers found in hand to trash");
			}
			
		});
		return c;
	}
	public  Card library() {
		Card c = new Card(5, "Library", 0, 0, Card.CardType.ACTION, 0, 0, 0);
		c.setSpecialAction(new LibraryAction());
		return c;		
	}
	public  Card throneroom() {
		Card c = new Card(4, "Throne Room", 0, 0, Card.CardType.ACTION, 0, 0, 0);
		c.setSpecialAction(new CardAction() {
			@Override
			public void execute(Player player) {
				player.setTemporaryActions(player.getTemporaryActions() + 2); //this isn't strictly true, but the card triggered by the throne room needs some space
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
								player.play(options.get(0));
								player.setTemporaryActions(player.getTemporaryActions() - 1);
								
								break;
							}
						}
						
					}
				});
			}
			
		});
		return c;
	}
	public  Card militia() {
		Card c = new Card(4, "Militia", 0, 0, Card.CardType.ACTION, 0, 2, 0);
		c.setSpecialAction(new CardAction() {
			@Override
			public void execute(Player player) {
				//TODO: account for Moat
				for(Player p : player.getGame().getOtherPlayers(player)) {
					if (p.getHand().size() > 3) {
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
							public void doOptions(Player player, List<String> options) {
								if (player.getHand().size() - options.size() != 3) {
									throw new RuntimeException("You must discard down to three cards in your hand");
								}
								
								for (String cardName : options) {
									for (Card c : player.getHand()) {
										if (c.getName().equals(cardName)) {
											player.getHand().remove(c);
											break;
										}
									}
								}
							}
							
						});
					}
				}
			}
			
		});
		return c;
	}
	public  Card councilroom() {
		Card c = new Card(5, "Council Room", 0, 0, Card.CardType.ACTION, 1, 0, 4);
		c.setSpecialAction(new CardAction() {
			@Override
			public void execute(Player player) {
				for(Player p : player.getGame().getOtherPlayers(player)) {
					p.draw();
				}
			}
			
		});
		return c;
	}
	public  Card witch() {
		//TODO: account for the Moat
		Card c = new Card(5, "Witch", 0, 0, Card.CardType.ACTION, 0, 0, 2);
		c.setSpecialAction(new CardAction() {
			@Override
			public void execute(Player player) {
				for(Player p : player.getGame().getOtherPlayers(player)) {
					p.getDiscard().add(bank.get("Curse"));
				}
			}
			
		});
		return c;
	}
	public  Card bureaucrat() {
		//TODO: account for the Moat
		Card c = new Card(4, "Bureaucrat", 0, 0, Card.CardType.ACTION, 0, 0, 0);
		c.setSpecialAction(new CardAction() {
			@Override
			public void execute(Player player) {
				player.getDeck().add(0, bank.get("Silver"));
				
				for(Player p : player.getGame().getOtherPlayers(player)) {
					for (Card c : p.getHand()) {
						if (c.getType() == Card.CardType.VICTORY) {
							p.getHand().remove(c);
							p.getDeck().add(0, c);
							break;
						}
					}
				}
			}
			
		});
		return c;
	}
	public  Card spy() {
		Card c = new Card(4, "Spy", 0, 1, Card.CardType.ACTION, 0, 0, 1);
		c.setSpecialAction(new CardAction() {
			@Override
			public void execute(Player player) {
				//TODO: account for Moat
				player.setCurrentChoice(new ActionChoice() {
					
					@Override
					public String getPrompt() {
						return "Choose cards for opponents to discard";
					}

					@Override
					public List<String> getOptions() {
						List<String> cardNames = new ArrayList<>();
						for (Player p : player.getGame().getOtherPlayers(player)) {
							cardNames.add(p.getDeck().get(0).getName());
						}
						
						return cardNames;
					}

					@Override
					public void doOptions(Player player, List<String> options) {
						
					}
					
				});
			}
			
		});
		return c;
	}
}
