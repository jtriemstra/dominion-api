package com.jtriemstra.dominion.api.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class PlayedState implements CardSource, CardDestination {
	private List<String> cards;
	
	public PlayedState() {
		cards = new ArrayList<>();
	}
	
	@Override
	public void add(String cardName) {
		cards.add(cardName);
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
	@JsonIgnore
	public String getKey() {
		return "Played";
	}
	
	@Override
	public int size() {
		return cards.size();
	}

}
