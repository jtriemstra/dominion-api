package com.jtriemstra.dominion.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor // support deserialization in tests
public class BankSupply implements CardSource {
	private int count;
	private String name;
	
	@Override
	public String remove() {
		this.count -= 1;
		return name;
	}
	
	@Override
	public String remove(String cardName) {
		throw new RuntimeException("Invalid remove call");
	}
	
	@Override
	public int size() {
		return count;
	}
}
