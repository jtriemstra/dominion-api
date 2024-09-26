package com.jtriemstra.dominion.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class V3BankCard {
	private String name;
	private int quantity;
}
