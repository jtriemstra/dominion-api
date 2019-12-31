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
		bank.put("Chapel", chapel());
		bank.put("Throne Room", throneroom());
	}
	
	public static List<Card> newDeck(){
		List<Card> newDeck = new ArrayList<>();
		/*for (int i=0; i<3; i++) {
			newDeck.add(bank.get("Estate"));
		}*/
		newDeck.add(bank.get("Throne Room"));
		newDeck.add(bank.get("Village"));
		for (int i=0; i<8; i++) {
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
	public static Card chancellor() {
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
	public static Card chapel() {
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
	public static Card cellar() {
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
	public static Card workshop() {
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

						player.setCurrentChoice(null);
					}
				});
			}			
		});
		return c;
	}
	public static Card feast() {
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
	public static Card mine() {
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
								
								player.addToHand(bank.get(options.get(0)));
							}							
						});
						
					}
				});
			}
			
		});
		return c;
	}
	public static Card remodel() {
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
																
								player.addToHand(bank.get(options.get(0)));
							}							
						});
						
					}
				});
			}
			
		});
		return c;
	}
	public static Card moneylender() {
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
	public static Card library() {
		Card c = new Card(5, "Library", 0, 0, Card.CardType.ACTION, 0, 0, 0);
		c.setSpecialAction(new LibraryAction());
		return c;		
	}
	public static Card throneroom() {
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
								break;
							}
						}
						
					}
				});
			}
			
		});
		return c;
	}	
}
