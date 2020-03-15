package com.jtriemstra.dominion.api.models;

import java.util.*;

import lombok.Data;

public class Game {

	private List<Player> players = new ArrayList<>();
	private Bank bank;
	private int currentPlayer;
	
	public Game() {
		bank = new Bank();
	}
	
	public Game(Bank bank) {
		this.bank = bank;
	}
	
	public List<Player> getOtherPlayers(Player thisPlayer){
		List<Player> others = new ArrayList<>();
		for(Player p : players) {
			if (p != thisPlayer) {
				others.add(p);
			}
		}
		
		return others;
	}
	
	public Bank getBank() {
		return bank;
	}
	
	public void addPlayer(Player player) {
		for (Player p : players) {
			if (player.getName().equals(p.getName())) {
				throw new RuntimeException("a player with this name already exists");
			}
		}
		
		players.add(player);
		
		if (players.size() == 1) {
			currentPlayer = 0;
		}
	}
	
	public Player getPlayer(String name) {
		for (Player p : players) {
			if (name.equals(p.getName())) {
				return p;
			}
		}
		
		throw new RuntimeException("player not found with specified name");
	}
	
	public int getPlayerCount() {
		return players.size();
	}
	
	public Player getCurrentPlayer() {
		if (currentPlayer >= players.size()) {
			throw new RuntimeException("server state has gotten out of sync. current player is greater than number of players");
		}
		return players.get(currentPlayer);
	}
	
	public void moveToNextPlayer() {
		if (currentPlayer == players.size() - 1) {
			currentPlayer = 0;
		}
		else {
			currentPlayer++;
		}
	}
	
	public String[] getPlayerNames() {
		String[] names = new String[players.size()];
		
		for (int i=0; i<players.size(); i++) {
			names[i] = players.get(i).getName();
		}
		
		return names;
	}
	
	public int getCurrentPlayerIndex() {
		if (currentPlayer >= players.size()) {
			throw new RuntimeException("server state has gotten out of sync. current player is greater than number of players");
		}
		return currentPlayer;
	}
}
