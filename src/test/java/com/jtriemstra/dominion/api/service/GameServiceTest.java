package com.jtriemstra.dominion.api.service;

import java.util.HashSet;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.jtriemstra.dominion.api.dto.BankState;
import com.jtriemstra.dominion.api.dto.BankSupply;
import com.jtriemstra.dominion.api.dto.GameState;
import com.jtriemstra.dominion.api.dto.PlayerState;

public class GameServiceTest extends ActionServiceTestBase {
	
	protected GameService gameService;
	
	@Override
	@BeforeEach
	public void init() {
		super.init();
		gameService = new GameService(actionService, playerService);
		bankState.getSupplies().put(ActionService.VILLAGE, new BankSupply(10, ActionService.VILLAGE));		//3
		bankState.getSupplies().put(ActionService.SMITHY, new BankSupply(10, ActionService.SMITHY));		//4
		bankState.getSupplies().put(ActionService.MINE, new BankSupply(10, ActionService.MINE));		//5		
	}
	
	@Test
	public void twoEmptyPilesMeansGameNotOver() {
		bankState.getSupplies().get(ActionService.EMBASSY).setCount(0);
		bankState.getSupplies().get(ActionService.VILLAGE).setCount(0);
		
		Assertions.assertFalse(gameService.isGameOver(gameState));
	}
	
	@Test
	public void threeEmptyPilesMeansGameOver() {
		bankState.getSupplies().get(ActionService.EMBASSY).setCount(0);
		bankState.getSupplies().get(ActionService.VILLAGE).setCount(0);
		bankState.getSupplies().get(ActionService.MINE).setCount(0);
		
		Assertions.assertTrue(gameService.isGameOver(gameState));
	}
	
	@Test
	public void emptyProvincePileMeansGameOver() {
		bankState.getSupplies().get(ActionService.PROVINCE).setCount(0);
		
		Assertions.assertTrue(gameService.isGameOver(gameState));
	}

	@Test
	public void twoEmptyPilesMovesToNextPlayer() {
		bankState.getSupplies().get(ActionService.EMBASSY).setCount(0);
		bankState.getSupplies().get(ActionService.VILLAGE).setCount(0);
		Assertions.assertEquals(0, gameState.getCurrentPlayer());
		
		gameService.endTurn(gameState);
		
		Assertions.assertEquals(1, gameState.getCurrentPlayer());
	}
	
	@Test
	public void threeEmptyPilesSetsNoPlayer() {
		bankState.getSupplies().get(ActionService.EMBASSY).setCount(0);
		bankState.getSupplies().get(ActionService.VILLAGE).setCount(0);
		bankState.getSupplies().get(ActionService.MINE).setCount(0);

		gameService.endTurn(gameState);
		
		Assertions.assertEquals(-1, gameState.getCurrentPlayer());
	}
	
	@Test
	public void emptyProvincePileSetsNoPlayer() {
		bankState.getSupplies().get(ActionService.PROVINCE).setCount(0);

		gameService.endTurn(gameState);
		
		Assertions.assertEquals(-1, gameState.getCurrentPlayer());
	}
}
