package com.jtriemstra.dominion.api.dto;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jtriemstra.dominion.api.models.ActionChoice;
import com.jtriemstra.dominion.api.models.Card;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor // support deserialization in tests
public class PlayerState {
	private String name;
	private TurnState turn;
	private DeckState deck;
	private DiscardState discard;
	private HandState hand;
	private LookingState looking;
	private PlayedState played;
	private AsideState aside;
	private RevealingState revealing;
	private AttacksState attacks;
	private int points;
	private Set<String> buyableBankCards;
	
	public PlayerState(String name) {
		this.name = name;
		this.turn=new TurnState();
		this.deck=new DeckState();
		this.discard = new DiscardState();
		this.hand = new HandState();
		this.looking = new LookingState();
		this.played = new PlayedState();
		this.aside = new AsideState();
		this.revealing = new RevealingState();
		this.attacks = new AttacksState();
		this.buyableBankCards = new HashSet<>();
	}
	
	@JsonGetter(value = "numberOfActions")
	public int numberOfActions() {
		
		return turn.getActionsAvailable();
	}
	
	@JsonGetter(value = "hasActions")
	public boolean hasActions() {
		if (played.getCards().size() == 0) return true;
		
		return numberOfActions() > 0;
	}
	
	@JsonGetter(value="currentChoice")
	public ChoiceState getCurrentChoice() {
		if (turn.getChoicesAvailable().size() > 0) {
			return turn.getChoicesAvailable().get(0);
		}
		return null;
	}
	@JsonGetter(value = "hasBuys")
	public boolean hasBuys() {
		return numberOfBuys() > 0;
	}
	
	@JsonGetter(value = "numberOfBuys")
	public int numberOfBuys() {
		return turn.getBuys();
	}
	
	@JsonGetter(value = "treasureAvailable")
	public int treasureAvailable() {
		return turn.getTreasure();
	}
	
	@JsonGetter(value = "hand")
	public List<String> getHandForJson() {
		ArrayList<String> sortedHand = new ArrayList<>();
		sortedHand.addAll(hand.getCards());
		sortedHand.sort(new HandComparator());		
		
		return sortedHand;
	}
	
	@JsonGetter(value = "deck")
	public List<String> getDeckForJson() {
		return deck.getCards();
	}

	@JsonGetter(value = "discard")
	public List<String> getDiscardForJson() {
		return discard.getCards();
	}

	@JsonGetter(value = "looking")
	public List<String> getLookingForJson() {
		return looking.getCards();
	}

	@JsonGetter(value = "played")
	public List<String> getPlayedForJson() {
		return played.getCards();
	}
	@JsonGetter(value = "bought")
	public List<String> bought() {
		return turn.getGainedToDiscard();
	}
	@JsonGetter(value = "phase")
	public String getPhase() {
		if (!getTurn().isSkipActions() 
				&& getTurn().getActionsAvailable() > 0 
				&& hand.getCards().stream().filter(s -> CardData.cardInfo.get(s).isAction()).count() > 0
				&& played.getCards().stream().filter(s -> CardData.cardInfo.get(s).isTreasure()).count() == 0){
			return "action";
		} else if (getTurn().getBuys() > 0) {
			return "buy";
		} else {
			return "cleanup";
		}
	}
	
	public class HandComparator implements Comparator<String>{

		@Override
		public int compare(String name1, String name2) {
			CardData c1 = CardData.cardInfo.get(name1);
			CardData c2 = CardData.cardInfo.get(name2);
			
			if (c1.isAction() && !c2.isAction()) return -1;
			if (c2.isAction() && !c1.isAction()) return 1;
			if (c1.isTreasure() && !c2.isTreasure()) return -1;
			if (c2.isTreasure() && !c1.isTreasure()) return 1;
			if (c1.getCost() != c2.getCost()) return c1.getCost() - c2.getCost();
			
			return c1.getName().compareTo(c2.getName());
		}
		
	}
}
