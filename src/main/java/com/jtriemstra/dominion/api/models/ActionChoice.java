package com.jtriemstra.dominion.api.models;

import java.util.List;

public abstract class ActionChoice {
	public abstract String getPrompt();
	public abstract List<String> getOptions();
	public void doOptions(Player player, List<String> options) {}
	//TODO: max and min choice settings?
}
