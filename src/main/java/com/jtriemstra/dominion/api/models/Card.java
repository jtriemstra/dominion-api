package com.jtriemstra.dominion.api.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Card {
	@NonNull private int cost;
	@NonNull private String name;
	@NonNull private int victoryPoints;
	@NonNull private int additionalActions;
	@NonNull private CardType type;
	@NonNull private int additionalBuys;
	@NonNull private int treasure;
	@NonNull private int additionalCards;
	
	@JsonIgnore private CardAction specialAction;
	@JsonIgnore private VictoryFunction victoryFunction;
	@JsonIgnore private CardAction gainAction;
	@JsonIgnore private CardAction buyAction;
	@JsonIgnore private BuyDestination buyDestination;
	@JsonIgnore private CardAction discardAction;
	@JsonIgnore private TreasureFunction treasureFunction;

	@JsonGetter(value="cost")
	public int getCost() {
		List<String> cardModifiers = Game.currentModifiers();
		int reducedCost = cost;
		
		if (cardModifiers != null) {
			for(String s : cardModifiers) {
				// TODO: move Highway specific info out of here, and into a Highway card class 
				if (reducedCost > 0 && s.equals("Highway")) {
					reducedCost--;
				}
			}
		}
		
		return reducedCost;
	}
		
	public String toString() {
		return name;
	}
	
	public boolean equals(Card c) {
		return c.name.equals(this.name);
	}
		
	public enum CardType {
		TREASURE,
		VICTORY,
		ACTION,
		REACTION,
		ATTACK
	}
}
