package com.jtriemstra.dominion.api.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.jtriemstra.dominion.api.dto.BankState;
import com.jtriemstra.dominion.api.dto.GameState;
import com.jtriemstra.dominion.api.dto.PlayerState;

public class PlayerServiceTest {

	protected PlayerService playerService;
	protected PlayerState playerState;
	protected GameState gameState;
	protected BankState bankState;
	
	@BeforeEach
	public void init() {
		bankState = new BankState();
		gameState = new GameState(bankState);
		playerService = new PlayerService();
		playerState = new PlayerState("test");
		gameState.getPlayers().put("test", playerState);		
	}
	
	@Test
	public void zeroCardsMeansZeroPoints() {
		gameState.setCurrentPlayer(-1);
		playerService.calculatePoints(gameState, "test");
		Assertions.assertEquals(0, playerState.getPoints());
	}
	
	@Test
	public void treasureCardsMeansZeroPoints() {
		gameState.setCurrentPlayer(-1);
		playerState.getDeck().add(ActionService.COPPER);
		playerState.getDeck().add(ActionService.SILVER);
		playerState.getDeck().add(ActionService.GOLD);
		playerService.calculatePoints(gameState, "test");
		Assertions.assertEquals(0, playerState.getPoints());
	}
	
	@Test
	public void basicVictoryCardsMeansPoints() {
		gameState.setCurrentPlayer(-1);
		playerState.getDeck().add(ActionService.ESTATE);
		playerState.getDeck().add(ActionService.DUCHY);
		playerState.getDeck().add(ActionService.DUCHY);
		playerState.getDeck().add(ActionService.PROVINCE);
		playerService.calculatePoints(gameState, "test");
		Assertions.assertEquals(13, playerState.getPoints());
	}
	
	@Test
	public void gardensWorthZeroWithLessThan10() {
		gameState.setCurrentPlayer(-1);
		playerState.getDeck().add(ActionService.ESTATE);
		playerState.getDeck().add(ActionService.DUCHY);
		playerState.getDeck().add(ActionService.DUCHY);
		playerState.getDeck().add(ActionService.PROVINCE);
		playerState.getDeck().add(ActionService.GARDENS);
		playerService.calculatePoints(gameState, "test");
		Assertions.assertEquals(13, playerState.getPoints());
	}
}
