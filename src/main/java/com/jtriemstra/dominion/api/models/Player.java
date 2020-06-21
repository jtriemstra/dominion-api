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
	//TODO: rename this to "revealed"
	private List<Card> liminal = new ArrayList<>();
	@JsonIgnore private int numberOfBuysMade;
	private List<ActionChoice> currentChoice = new ArrayList<>();
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
		liminal.clear();
		currentChoice.clear();
		
		temporaryTreasure = 0;
		temporaryBuys = 0;
		temporaryActions = 1;
		
		deck = game.getBank().newDeck();
		deck = shuffle(deck);
		for (int i=0; i<5; i++) {
			draw();
		}
		
	}
		
	public void discardFromLiminal(Card c) {
		discardFrom(c, liminal);
	}
	
	public void discardFromHand(Card c) {
		discardFrom(c, hand);
	}
	
	private void discardFrom(Card c, List<Card> source) {
		if (c.getDiscardAction() != null) {
			c.getDiscardAction().execute(this);
		}
		source.remove(c);
		discard.add(c);
	}
	
	public void addToHand(Card c) {
		hand.add(c);
	}
	
	public Card draw() {
		return privateDraw(hand);
	}
	
	public List<Card> lookAt(int count){
		for (int i=0; i<count; i++) {
			privateDraw(liminal);
		}
		
		return liminal;
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
		
	private Card privateDraw(List<Card> destination) {
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
		destination.add(newCard);
		
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
		if (cardToPlay.getTreasureFunction() == null) {
			temporaryTreasure += cardToPlay.getTreasure();
		}
		else {
			temporaryTreasure += cardToPlay.getTreasureFunction().getTreasure(this);
		}
		
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
		if (currentChoice.size() == 0) {
			throw new RuntimeException("player does not currently have an action choice waiting");
		}
		
		currentChoice.get(0).doOptions(this, options);
		
		if (currentChoice.size() == 0 && throneRoomActions.size() > 0) {
			
			Card c = throneRoomActions.pop();
			if (c != null) {
				doCard(c);
			}
		}
	}
	
	public void setCurrentChoice(ActionChoice a) {
		if (currentChoice.size() > 0) {
			//currentChoice.remove(0);
		}
		if (a != null) {
			currentChoice.add(a);
		}
		else {
			currentChoice.remove(0);
		}
	}
	
	public void setCurrentChoice(ActionChoice a, boolean frontOfLine) {
		if (currentChoice.size() > 0) {
			//currentChoice.remove(0);
		}
		if (a != null) {
			if (frontOfLine) {
				currentChoice.add(0,a);
			}
			else {
				currentChoice.add(a);
			}
		}
		else {
			currentChoice.remove(0);
		}
	}
	
	public void addCurrentChoice(ActionChoice a) {
		currentChoice.add(a);
	}
	
	@JsonGetter(value="currentChoice")
	public ActionChoice getCurrentChoice() {
		if (currentChoice.size() > 0) {
			return currentChoice.get(0);
		}
		return null;
	}
	
	public void buy(String name) {
		if (!StringUtils.hasText(name)) {
			throw new RuntimeException("no card name passed to buy");
		}
		
		if (!hasBuys()) {
			throw new RuntimeException("no buys left");
		}
		
		Card newCard = game.getBank().tryToBuy(name, treasureAvailable());
		temporaryTreasure -= newCard.getCost();
		temporaryBuys -= 1;
		
		if (newCard.getBuyAction() != null) {
			newCard.getBuyAction().execute(this);
		}
		
		if (newCard.getBuyDestination() == null) {
			gainTo(newCard, bought);
		}
		else {
			switch(newCard.getBuyDestination().getBuyDestination()) {
			case DECK: gainTo(newCard, deck); break;
			case HAND: gainTo(newCard, hand); break;
			case PLAYED: gainTo(newCard, played); break;
			case BOUGHT: gainTo(newCard, bought); break;
			case DISCARD: gainTo(newCard, discard); break;
			}
		}
				
		//TODO: move Haggler code out of here
		for(Card c : played) {
			if (c.getName().equals("Haggler")) {
				(new HagglerAction(this.getGame().getBank(), newCard.getCost())).execute(this);
			}
		}
	}
	
	public void gainTo(Card c, List<Card> destination) {
		//TODO: move card-specific code elsewhere
		boolean traderFound = false;
		for (Card c1 : hand) {
			if (c1.getName().equals("Trader")) {
				traderFound = true;
				break;
			}
		}
		if (traderFound) {
			(new TraderReaction(getGame().getBank(), destination, c)).execute(this);
		}
		else {
			finishGain(c, destination);
		}
	}
	
	public void finishGain(Card c, List<Card> destination) {
		destination.add(0, c);
		
		//TODO: move card-specific code elsewhere
		for(Player p : game.getOtherPlayers(this)) {
			for (Card c1 : p.getHand()) {
				if (c1.getName().equals("Fools Gold") && c.getName().equals("Province")) {
					(new FoolsGoldAction(game.getBank())).execute(p);
				}
			}
		}
		
		if (game.getBank().hasCard("Duchess") && c.getName().equals("Duchy")) {
			(new DuchessGainAction(game.getBank())).execute(this);
		}

		if (c.getGainAction() != null) {
			c.getGainAction().execute(this);
		}		
	}
		
	public void startCleanup() {
		//TODO: remove card-specific logic
		boolean foundScheme = false;
		for (Card c : played) {
			if (c.getName().equals("Scheme")) {
				foundScheme = true;
				(new SchemeAction()).execute(this);
			}
		}
		
		if (!foundScheme) {
			cleanup();
		}
	}
	
	public void cleanup() {
		if (liminal.size() > 0) {
			throw new RuntimeException("Revealed cards were not properly cleaned up");
		}
		
		discard.addAll(bought);
		bought.clear();
		
		discard.addAll(played);
		played.clear();
		
		discard.addAll(hand);
		hand.clear();
		
		//TODO: there may be other situations that could take teh Highway or Haggler out of play that need to be accounted for
		game.clearCardModifiers();
				
		currentChoice.clear();
		temporaryTreasure = 0;
		temporaryBuys = 0;
		temporaryActions = 1;
		throneRoomActions.clear();
		
		for (int i=0; i<5; i++) {
			draw();
		}
		
		game.moveToNextPlayer();
		
		game.testGameOver();
		
	}
	
	@JsonGetter(value = "hasBuys")
	public boolean hasBuys() {
		return numberOfBuys() > 0;
	}
	
	@JsonGetter(value = "numberOfBuys")
	public int numberOfBuys() {
		return 1 + temporaryBuys;
	}
	
	@JsonGetter(value = "treasureAvailable")
	public int treasureAvailable() {
		return temporaryTreasure;
	}
	
	@JsonGetter(value = "hand")
	public List<Card> getHandForJson() {
		ArrayList<Card> sortedHand = new ArrayList<>();
		sortedHand.addAll(hand);
		sortedHand.sort(new HandComparator());		
		
		return sortedHand;
	}
	
	public class HandComparator implements Comparator<Card>{

		@Override
		public int compare(Card c1, Card c2) {
			if (c1.getType() == Card.CardType.ACTION && c2.getType() != Card.CardType.ACTION) return -1;
			if (c2.getType() == Card.CardType.ACTION && c1.getType() != Card.CardType.ACTION) return 1;
			if (c1.getType() == Card.CardType.TREASURE && c2.getType() != Card.CardType.TREASURE) return -1;
			if (c2.getType() == Card.CardType.TREASURE && c1.getType() != Card.CardType.TREASURE) return 1;
			if (c1.getCost() != c2.getCost()) return c1.getCost() - c2.getCost();
			
			return c1.getName().compareTo(c2.getName());
		}
		
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
	
	public enum CardSet{
		DECK,
		HAND,
		PLAYED,
		BOUGHT,
		DISCARD
	}
}
