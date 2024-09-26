package com.jtriemstra.dominion.api.dto;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jtriemstra.dominion.api.models.Card;
import com.jtriemstra.dominion.api.models.Player.HandComparator;

import lombok.Data;

@Data
public class HandState implements CardSource, CardDestination {
	private List<String> cards;

	public HandState() {
		cards = new ArrayList<>();
	}
		
	@Override
	public String remove() {
		return "";
	}

	@Override
	public String remove(String cardName) {
		int foundIndex = -1;
		for (int i=0; i<cards.size(); i++) {
			if (cards.get(i).equals(cardName)) {
				foundIndex = i;
				break;
			}
		}
		return cards.remove(foundIndex);
	}
	
	@Override
	public void add(String cardName) {
		cards.add(cardName);
	}
	
	public int size() {
		return cards.size();
	}
	
	public boolean hasVictory() {
		return cards.stream().anyMatch(n -> CardData.cardInfo.get(n).isVictory());
	}
	
	public List<String> attackReactions() {
		return cards.stream().filter(c -> CardData.cardInfo.get(c).isAttackReaction()).collect(Collectors.toList());

	}

	@Override
	@JsonIgnore
	public String getKey() {
		return "Hand";
	}
}	
