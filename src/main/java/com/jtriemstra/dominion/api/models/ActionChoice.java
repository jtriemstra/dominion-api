package com.jtriemstra.dominion.api.models;

import java.util.List;

public abstract class ActionChoice {
	public abstract String getPrompt();
	public abstract List<String> getOptions();
	public abstract void doOptions(Player player, List<String> options);
	public abstract int getMinOptions();
	public abstract int getMaxOptions();
}
