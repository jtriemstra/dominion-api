package com.jtriemstra.dominion.api.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jtriemstra.dominion.api.service.Executable;

import lombok.Data;

@Data
public class TurnState {
	private boolean active;
	private boolean skipActions;
	private int buys;
	private int treasure;
	private int actionsAvailable;
	private List<List<String>> actionsTaken;
	private List<String> gainedToDiscard;
	private String gainDestination;
	private List<String> repeatedAction;
	private List<String> gainReactions;
	
	private List<ChoiceState> choicesAvailable;
	private List<String> choicesMade;
	
	private boolean isCleanup;
	private List<String> cleanupActions;
	
	private List<String> buyActions;
	private String buying;
	private List<String> costFunctions;
	
	private Map<String, List<String>> playActions;
	
	public TurnState() {
		this.actionsTaken = new ArrayList<>();
		this.gainedToDiscard = new ArrayList<>();
		this.choicesAvailable = new ArrayList<>();
		this.choicesMade = new ArrayList<>();
		this.cleanupActions = new ArrayList<>();
		this.buyActions = new ArrayList<>();
		this.costFunctions = new ArrayList<>();
		this.playActions = new HashMap<>();
		this.gainReactions = new ArrayList<>();
		this.repeatedAction = new ArrayList<>();
		this.skipActions = false;
	}
	
	public void pushRepeatedAction(String s) {
		repeatedAction.add(0, s);
	}
	
	public String popRepeatedAction() {
		if (repeatedAction.size() > 0) {
			return repeatedAction.remove(0);
		}
		return null;
	}
	
	public String peekRepeatedAction() {
		if (repeatedAction.size() > 0) {
			return repeatedAction.get(0);
		}
		return null;
	}
	
}
