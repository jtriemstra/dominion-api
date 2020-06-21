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
		
		cleanupStages.add(new CleanupAction());
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
		
		if (currentChoice.size() == 0) {
			//TODO: make this a more general "move through stages" idea
			if (isCleaningUp) {
				cleanup();
			}
			else if (buyStages.size() > 0) {
				doNextBuyStage();
			}
		}
	}
	
	//TODO: update this interface to reflect that there's a queue under here
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
		
		buyStages.add(new BuyAction(name));
		
		doNextBuyStage();		
	}
	
	public void gainTo(Card c, List<Card> destination, int quantity) {
		for (int i=0; i<quantity; i++){
			buyStages.add(0, new TraderCheckAction(c));
			buyStages.add(1, new GainAction(c, destination));
		}
		
		doNextBuyStage();
	}
	
	public void gainTo(Card c, List<Card> destination) {
		gainTo(c, destination, 1);
	}
	
	@JsonIgnore private List<CardAction> buyStages = new ArrayList<>();
		
	public void doNextBuyStage() {
		//TODO: unify buy stages, cleanup stages, and actions
		if (buyStages.size() > 0 ) {
			CardAction currentAction = buyStages.remove(0);
			currentAction.execute(this);
		}
	}
	
	public class BuyAction extends CardAction {
		public Card cardToBuy;
		private String cardNameToBuy;
		
		public BuyAction(String cardName) {
			cardNameToBuy = cardName;
		}
		
		public void execute(Player p) {
			cardToBuy = game.getBank().tryToBuy(cardNameToBuy, treasureAvailable());
			temporaryTreasure -= cardToBuy.getCost();
			temporaryBuys -= 1;
			
			if (cardToBuy.getBuyAction() != null) {
				buyStages.add(0, cardToBuy.getBuyAction());
			}
			buyStages.add(new HagglerCheckAction(cardToBuy));
			buyStages.add(new TraderCheckAction(cardToBuy));
			buyStages.add(new GainAction(cardToBuy));
			
			doNextBuyStage();
		}
	}
	
	public class HagglerCheckAction extends CardAction {
		private Card newCard;
		
		public HagglerCheckAction(Card c) {
			this.newCard = c;
		}
		
		public void execute(Player p) {
			boolean hagglerFound = false;
			for (Card c1 : played) {
				if (c1.getName().equals("Haggler")) {
					hagglerFound = true;
					break;
				}
			}
			if (hagglerFound) {
				buyStages.add(0, new HagglerAction(getGame().getBank(), newCard.getCost()));
			}
			
			doNextBuyStage();
		}
	}
	
	public class TraderCheckAction extends CardAction {
		private Card newCard;
		
		public TraderCheckAction(Card c) {
			this.newCard = c;
		}
		
		public void execute(Player p) {
			boolean traderFound = false;
			for (Card c1 : hand) {
				if (c1.getName().equals("Trader")) {
					traderFound = true;
					break;
				}
			}
			if (traderFound) {
				buyStages.add(0, new TraderReaction(getGame().getBank(), newCard));
			}
			
			doNextBuyStage();
		}
	}
	
	public class GainAction extends CardAction {
		private Card newCard;
		
		private List<Card> destination;
		
		public GainAction(Card c) {
			this.newCard = c;
		}
		
		public GainAction(Card c, List<Card> destination) {
			this.newCard = c;
			this.destination = destination;
		}
		
		public void setNewCard(Card c) {
			this.newCard = c;
		}
		
		public void execute(Player p) {
			if (newCard.getBuyDestination() != null) {
				switch(newCard.getBuyDestination().getBuyDestination()) {
				case DECK: destination = deck; break;
				case HAND: destination = hand; break;
				case PLAYED: destination = played; break;
				case BOUGHT: destination = bought; break;
				case DISCARD: destination = discard; break;
				default: destination = bought; break;
				}
				
			}
			else {
				if (destination == null) {
					destination = bought;
				}
			}
			
			destination.add(0, newCard);
			
			if (newCard.getGainAction() != null) {
				buyStages.add(0, newCard.getGainAction());
			}
			
			doNextBuyStage();
		}
	}
	
		
	@JsonIgnore private List<CardAction> cleanupStages = new ArrayList<>();
	@JsonIgnore private boolean isCleaningUp = false;
	
	public void addCleanupStage(int index, CardAction ca) {
		cleanupStages.add(index, ca);
	}
	
	public void cleanup() {
		isCleaningUp = true;
		cleanupStages.remove(0).execute(this);
	}
	
	public class CleanupAction extends CardAction {
		//TODO: revisit this Player parameter - it's there to match the CardAction base class, but a lot of this functionality makes more sense as private to Player class
		public void execute(Player p) {
			if (liminal.size() > 0) {
				throw new RuntimeException("Revealed cards were not properly cleaned up");
			}
			
			discard.addAll(bought);
			bought.clear();
			
			discard.addAll(played);
			played.clear();
			
			discard.addAll(hand);
			hand.clear();
			
			cleanupStages.clear();
			cleanupStages.add(new CleanupAction());
			
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
			
			isCleaningUp = false;
			
			game.moveToNextPlayer();
			
			game.testGameOver();			
		}
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
