package com.jtriemstra.dominion.api.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ChoiceState {
	@Builder.Default
	List<String> options = new ArrayList<>();
	int minChoices;
	int maxChoices;
	String text;
	String followUpAction;
	
	public ChoiceState() {
		options = new ArrayList<>();
	}
	
	public void addAll(List<String> in) {
		options.addAll(in);
	}
	
	public void clear() {
		options.clear();
		minChoices = 0;
		maxChoices = 0;
	}
}
