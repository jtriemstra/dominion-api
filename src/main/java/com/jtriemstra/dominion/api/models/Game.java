package com.jtriemstra.dominion.api.models;

import java.util.*;

import lombok.Data;

@Data
public class Game {

	private List<Player> players = new ArrayList<>();
	private Bank bank;
	private String currentPlayer;
	
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
}
