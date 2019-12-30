package com.jtriemstra.dominion.api.models;

import java.util.*;

import lombok.Data;

@Data
public class Game {

	private List<Player> players = new ArrayList<>();
	
	private String currentPlayer;
	
	
}
