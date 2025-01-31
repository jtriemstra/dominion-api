package com.jtriemstra.dominion.api.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class ChoiceStateOption {
	private String text;
	private String id;
	
	public ChoiceStateOption(String text) {
		this.text = text;
		this.id = UUID.randomUUID().toString();
	}
}
