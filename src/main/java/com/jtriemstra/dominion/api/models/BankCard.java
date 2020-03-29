package com.jtriemstra.dominion.api.models;

import lombok.Data;

@Data
public class BankCard {
	private Card card;
	private int quantity;
	
	public BankCard(Card card, int quantity) {
		this.card = card;
		this.quantity = quantity;
	}
}
