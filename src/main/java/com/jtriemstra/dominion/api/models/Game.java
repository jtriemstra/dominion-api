package com.jtriemstra.dominion.api.models;

import java.util.*;

import lombok.Data;

public class Game {

	private List<Player> players = new ArrayList<>();
	private Bank bank;
	
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
}
