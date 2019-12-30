package com.jtriemstra.dominion.api.models;

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
	
	private CardAction specialAction;
	
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
