package com.jtriemstra.dominion.api.models;

import lombok.Data;
import java.util.*;

import org.springframework.util.StringUtils;

@Data
public class Player {
	private String name;
	private List<Card> deck = new ArrayList<>();
	private List<Card> hand = new ArrayList<>();
	private List<Card> played = new ArrayList<>();
	private List<Card> discard = new ArrayList<>();
	private List<Card> bought = new ArrayList<>();
	private int numberOfBuysMade;
	
	public void init() {
		deck = CardFactory.newDeck();
		for (int i=0; i<5; i++) {
			draw();
		}
	}
	
	public Card reveal() {
		return deck.remove(0);
	}
	
	public void discard(Card c) {
		discard.add(c);
	}
	
	public void addToHand(Card c) {
		hand.add(c);
	}
	
	public void draw() {
		if (deck.size() == 0) {
			throw new RuntimeException("no cards to draw in deck");
		}
		hand.add(deck.remove(0));
	}
	
	public boolean hasActions() {
		if (played.size() == 0) return true;
		
		int actionsAvailable = 1;
		
		for (int cardIndex=0; cardIndex < played.size(); cardIndex++) {
			Card c = played.get(cardIndex);
			//TODO: this ultimately needs to handle multiple types
			if (Card.CardType.ACTION.equals(c.getType())) {
				actionsAvailable--;
			}
			
			actionsAvailable += c.getAdditionalActions();			
		}
		
		return actionsAvailable > 0;
	}
	
	public void play(String name) {
		if (hand.size() == 0) {
			throw new RuntimeException("no cards to play in hand");
		}
		
		if (!StringUtils.hasText(name)) {
			throw new RuntimeException("no card name passed to play");
		}
		
		Card cardToPlay = null;
		for (int cardIndex=0; cardIndex<hand.size(); cardIndex++) {
			Card c = hand.get(cardIndex);
			if (name.equals(c.getName())) {
				cardToPlay = c;
				break;
			}
		}
		
		if (cardToPlay == null) {
			throw new RuntimeException("no card found matching name");
		}
		
		if (cardToPlay.getType() == Card.CardType.ACTION && !hasActions()) {
			throw new RuntimeException("no actions left to play");
		}
		
		hand.remove(cardToPlay);
		played.add(cardToPlay);
		
		for (int i=0; i<cardToPlay.getAdditionalCards(); i++) {
			draw();
		}
		
		if (cardToPlay.getSpecialAction() != null) {
			cardToPlay.getSpecialAction().execute(this);
		}
	}
	
	public void buy(String name) {
		if (!StringUtils.hasText(name)) {
			throw new RuntimeException("no card name passed to buy");
		}
		
		if (!hasBuys()) {
			throw new RuntimeException("no buys left");
		}
		
		Card newCard = CardFactory.tryToBuy(name, treasureAvailable());
		
		bought.add(newCard);
	}
	
	public void cleanup() {
		discard.addAll(bought);
		bought.clear();
		
		discard.addAll(played);
		played.clear();
		
		discard.addAll(hand);
		hand.clear();
		
		for (int i=0; i<5; i++) {
			draw();
		}
	}
	
	public boolean hasBuys() {
		if (played.size() == 0) return true;
		
		int buysAvailable = 1;
		
		for (int cardIndex=0; cardIndex < played.size(); cardIndex++) {
			Card c = played.get(cardIndex);
			
			buysAvailable += c.getAdditionalBuys();			
		}
		
		return buysAvailable > 0;
	}
	
	public int treasureAvailable() {
		int treasure = 0;
		
		for (int cardIndex=0; cardIndex < played.size(); cardIndex++) {
			Card c = played.get(cardIndex);
			
			treasure += c.getTreasure();
		}
		
		for (Card c : bought) {
			treasure -= c.getCost();
		}
		
		return treasure;
	}
}
