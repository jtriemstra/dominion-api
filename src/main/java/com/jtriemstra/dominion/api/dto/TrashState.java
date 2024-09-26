package com.jtriemstra.dominion.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrashState implements CardDestination {
	private int dummyVar;
	
	@Override
	public void add(String cardName) {
		
	}

	@Override
	@JsonIgnore
	public String getKey() {
		return "Trash";
	}
}
