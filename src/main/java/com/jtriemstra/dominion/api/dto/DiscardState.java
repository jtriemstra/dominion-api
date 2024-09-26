package com.jtriemstra.dominion.api.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class DiscardState implements CardSource, CardDestination {
	private List<String> cards;

	public DiscardState() {
		cards = new ArrayList<>();
	}

	@Override
	public String remove() {
		return cards.remove(0);
	}

	@Override
	public String remove(String cardName) {
		cards.remove(cardName);
		return cardName;
	}
	
	@Override
	public void add(String cardName) {
		cards.add(0, cardName);		
	}

	@Override
	@JsonIgnore
	public String getKey() {
		return "Discard";
	}
	
	@Override
	public int size() {
		return cards.size();
	}
	
}
