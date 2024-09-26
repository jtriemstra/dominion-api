package com.jtriemstra.dominion.api.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class AsideState implements CardDestination, CardSource {
	private List<String> cards;

	public AsideState() {
		cards = new ArrayList<>();
	}

	@Override
	public String remove() {
		return cards.remove(0);
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
		cards.add(0, cardName);		
	}

	@Override
	@JsonIgnore
	public String getKey() {
		return "Aside";
	}
	
	@Override
	public int size() {
		return cards.size();
	}

}
