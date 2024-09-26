package com.jtriemstra.dominion.api.dto;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class BankState {
	private Map<String, BankSupply> supplies;
	
	public BankState() {
		supplies = new HashMap<>();
	}
}
