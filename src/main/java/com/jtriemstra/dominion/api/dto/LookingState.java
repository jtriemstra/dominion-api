package com.jtriemstra.dominion.api.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class LookingState implements CardSource, CardDestination {
	private List<String> cards;
	
	public LookingState() {
		cards = new ArrayList<>();
	}

	@Override
	public void add(String cardName) {
		cards.add(cardName);
	}

	@Override
	public String remove() {
		if (cards.size() == 1) {
			return cards.remove(0);
		} else {
			throw new RuntimeException("Invalid remove call");
		}
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
	@JsonIgnore
	public String getKey() {
		return "Looking";
	}
	
	@Override
	public int size() {
		return cards.size();
	}

}
