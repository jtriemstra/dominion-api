package com.jtriemstra.dominion.api.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class DeckState implements CardDestination, CardSource {
	private List<String> cards;
	private Boolean dummy;

	public DeckState() {
		cards = new ArrayList<>();
		dummy = false;
	}
	
	@Override
	public String remove() {
		return cards.remove(0);
	}

	@Override
	public String remove(String cardName) {
		throw new RuntimeException("Invalid remove call");
	}

	@Override
	public void add(String cardName) {
		cards.add(0, cardName);
	}

	@Override
	@JsonIgnore
	public String getKey() {
		return "Deck";
	}
	
	@Override
	public int size() {
		return cards.size();
	}
}
