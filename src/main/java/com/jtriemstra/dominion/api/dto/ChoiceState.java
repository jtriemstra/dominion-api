package com.jtriemstra.dominion.api.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
	@Builder.Default
	Map<String, Object> additionalData = new HashMap<>();
	@Builder.Default
	List<String> ids = new ArrayList<>();
	
	public ChoiceState() {
		options = new ArrayList<>();
	}
	
	public void addAll(List<String> in) {
		options.addAll(in);
		for (String s : in) {
			ids.add(UUID.randomUUID().toString());
		}
	}
	
	public void clear() {
		options.clear();
		minChoices = 0;
		maxChoices = 0;
		additionalData.clear();
	}
}
