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
		players.add(player);
	}
	
	public Player getPlayer(String name) {
		return players.get(0);
	}
	
	public int getPlayerCount() {
		return players.size();
	}
}
