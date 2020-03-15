package com.jtriemstra.dominion.api.models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.AccessLevel;

import java.util.*;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Slf4j
@Data
public class Player {
	private String name;
	private List<Card> deck = new ArrayList<>();
	private List<Card> hand = new ArrayList<>();
	private List<Card> played = new ArrayList<>();
	private List<Card> discard = new ArrayList<>();
	private List<Card> bought = new ArrayList<>();
	@JsonIgnore private int numberOfBuysMade;
	private ActionChoice currentChoice;
	@JsonIgnore private int temporaryTreasure; 
	@JsonIgnore private int temporaryBuys;
	@JsonIgnore private int temporaryActions;
	
	@JsonIgnore
	private Game game;
	
	//TODO: can I get the throne room specific stuff out of here?
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private Stack<Card> throneRoomActions = new Stack<>();
	
	public Player(String name) {
		this.name = name;
	}
	
	public void addThroneRoomAction(Card c) {
		throneRoomActions.push(c);
	}
	
	public void init(Game game) {
		this.game = game;
		deck.clear();
		hand.clear();
		played.clear();
		discard.clear();
		bought.clear();
		currentChoice = null;
		
		deck = game.getBank().newDeck();		
		for (int i=0; i<5; i++) {
			draw();
		}
	}
	
	public Card reveal() {
		return deck.remove(0);
	}
	
	public void discardFromTemp(Card c) {
		discard.add(c);
	}
	
	public void discardFromHand(Card c) {
		hand.remove(c);
		discard.add(c);
	}
	
	public void addToHand(Card c) {
		hand.add(c);
	}
	
	public Card draw() {
		return privateDraw();
	}
	
	private Card privateDraw() {
		if (deck.size() == 0) {
			if (discard.size() == 0) {
				throw new RuntimeException("no cards to draw in deck");
			}
			else {
				deck.addAll(discard);
				discard.clear();
			}
		}
		
		Card newCard = deck.remove(0);
		hand.add(newCard);
		
		return newCard;
	}
	
	@JsonGetter(value = "hasActions")
	public boolean hasActions() {
		if (played.size() == 0) return true;
		
		int actionsAvailable = 1 + temporaryActions;
		
		for (int cardIndex=0; cardIndex < played.size(); cardIndex++) {
			Card c = played.get(cardIndex);
			//TODO: this ultimately needs to handle multiple types
			if (Card.CardType.ACTION.equals(c.getType())) {
				actionsAvailable--;
			}					
		}
		
		return actionsAvailable > 0;
	}
	
	//NOTE: this only applies to the throne room right now
	public void play(Card cardToPlay) {
		temporaryTreasure += cardToPlay.getTreasure();
		temporaryBuys += cardToPlay.getAdditionalBuys();
		temporaryActions += cardToPlay.getAdditionalActions();
		
		for (int i=0; i<cardToPlay.getAdditionalCards(); i++) {
			draw();
		}
		
		if (cardToPlay.getSpecialAction() != null) {
			cardToPlay.getSpecialAction().execute(this);
		}
	}
	
	public void play(String name) {
		log.info("calling play");
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
		
		temporaryTreasure += cardToPlay.getTreasure();
		temporaryBuys += cardToPlay.getAdditionalBuys();
		temporaryActions += cardToPlay.getAdditionalActions();
		
		for (int i=0; i<cardToPlay.getAdditionalCards(); i++) {
			log.info("drawing card");
			draw();
		}
		
		if (cardToPlay.getSpecialAction() != null) {
			cardToPlay.getSpecialAction().execute(this);
		}
	}
	
	public void finishAction(List<String> options) {
		if (currentChoice == null) {
			throw new RuntimeException("player does not currently have an action choice waiting");
		}
		
		currentChoice.doOptions(this, options);
		
		if (currentChoice == null && throneRoomActions.size() > 0) {
			
			Card c = throneRoomActions.pop();
			if (c != null) {
				play(c);
				temporaryActions--;
			}
		}
		
	}
	
	public void buy(String name) {
		if (!StringUtils.hasText(name)) {
			throw new RuntimeException("no card name passed to buy");
		}
		
		if (!hasBuys()) {
			throw new RuntimeException("no buys left");
		}
		
		Card newCard = game.getBank().tryToBuy(name, treasureAvailable());
		
		bought.add(newCard);
	}
	
	public void cleanup() {
		discard.addAll(bought);
		bought.clear();
		
		discard.addAll(played);
		played.clear();
		
		discard.addAll(hand);
		hand.clear();
		
		currentChoice = null;
		temporaryTreasure = 0;
		temporaryBuys = 0;
		temporaryActions = 0;
		throneRoomActions.clear();
		
		for (int i=0; i<5; i++) {
			draw();
		}
		
		game.moveToNextPlayer();
	}
	
	@JsonGetter(value = "hasBuys")
	public boolean hasBuys() {
		return 1 + temporaryBuys - bought.size() > 0;
	}
	
	public int treasureAvailable() {
		int spent = 0;
		
		for (Card c : bought) {
			spent += c.getCost();
		}
		
		return temporaryTreasure - spent;
	}
		
	public boolean hasCard(String name) {
		for (Card c : hand) {
			if (name.equals(c.getName())) {
				return true;
			}
		}
		return false;
	}
}
