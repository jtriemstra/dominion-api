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
		
		temporaryTreasure = 0;
		temporaryBuys = 0;
		temporaryActions = 1;
		
		deck = game.getBank().newDeck();
		deck = shuffle(deck);
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
	
	public List<Card> shuffle(List<Card> input) {
		List<Card> output = new ArrayList<>();
		Random r = new Random();
		while (input.size() > 0) {
			int i = r.nextInt(input.size());
			output.add(input.remove(i));
		}
		
		return output;
	}
	
	private Card privateDraw() {
		if (deck.size() == 0) {
			if (discard.size() == 0) {
				throw new RuntimeException("no cards to draw in deck");
			}
			else {
				deck.addAll(shuffle(discard));
				discard.clear();
			}
		}
		
		Card newCard = deck.remove(0);
		hand.add(newCard);
		
		return newCard;
	}
	
	@JsonGetter(value = "numberOfActions")
	public int numberOfActions() {
		
		return temporaryActions;
	}
	
	@JsonGetter(value = "hasActions")
	public boolean hasActions() {
		if (played.size() == 0) return true;
		
		return numberOfActions() > 0;
	}
	
	public void play(String name) {
		play(name, false);
	}
		
	public void play(String name, boolean isThroneRoom) {
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
		
		if (!isThroneRoom) {
			if (cardToPlay.getType() == Card.CardType.ACTION && !hasActions()) {
				throw new RuntimeException("no actions left to play");
			}
			
			if (cardToPlay.getType() == Card.CardType.ACTION) {
				temporaryActions--;
			}
		}
		
		hand.remove(cardToPlay);
		played.add(cardToPlay);
		
		doCard(cardToPlay);
	}
	
	public void doCard(Card cardToPlay) {
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
	
	public void finishAction(List<String> options) {
		if (currentChoice == null) {
			throw new RuntimeException("player does not currently have an action choice waiting");
		}
		
		currentChoice.doOptions(this, options);
		
		if (currentChoice == null && throneRoomActions.size() > 0) {
			
			Card c = throneRoomActions.pop();
			if (c != null) {
				doCard(c);
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
		
		game.testGameOver();
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
		temporaryActions = 1;
		throneRoomActions.clear();
		
		for (int i=0; i<5; i++) {
			draw();
		}
		
		game.moveToNextPlayer();
	}
	
	@JsonGetter(value = "hasBuys")
	public boolean hasBuys() {
		return numberOfBuys() > 0;
	}
	
	@JsonGetter(value = "numberOfBuys")
	public int numberOfBuys() {
		return 1 + temporaryBuys - bought.size();
	}
	
	@JsonGetter(value = "treasureAvailable")
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
	
	public int getPoints() {
		List<Card> allCards = new ArrayList<>();
		allCards.addAll(deck);
		allCards.addAll(hand);
		allCards.addAll(played);
		allCards.addAll(bought);
		allCards.addAll(discard);
		
		int points = 0;
		for(Card c : allCards) {
			// TODO: account for multiple types
			if (c.getType() == Card.CardType.VICTORY) {
				points += c.getVictoryPoints();
			}
		}
		
		for(Card c : allCards) {
			if (c.getVictoryFunction() != null) {
				points += c.getVictoryFunction().getPoints(allCards);
			}
		}
		
		return points;
	}
}
