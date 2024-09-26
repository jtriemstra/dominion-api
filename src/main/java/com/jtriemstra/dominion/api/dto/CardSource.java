package com.jtriemstra.dominion.api.dto;

public interface CardSource {
	String remove();
	String remove(String cardName);
	int size();
}
