package com.jtriemstra.dominion.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jtriemstra.dominion.api.service.ActionServiceTestBase.Checked;

@ExtendWith(MockitoExtension.class)
public class ActionServiceFirstGameCombosTest extends ActionServiceTestBase {
	
	@Test
	public void twoMilitiaWithNoMoat() {
		swapHandCards(ActionService.MILITIA, ActionService.MILITIA);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.MILITIA);
		
		// first militia triggers a choice
		Assertions.assertEquals(5, playerState2.getTurn().getChoicesAvailable().get(0).getOptions().size());
		Assertions.assertEquals(1, playerState2.getTurn().getChoicesAvailable().size());
		Assertions.assertEquals(2, playerState2.getTurn().getChoicesAvailable().get(0).getMinChoices());
		Assertions.assertEquals(2, playerState2.getTurn().getChoicesAvailable().get(0).getMaxChoices());
		Assertions.assertEquals(5, playerState2.getHand().size());
		
		actionService.turnPlay(gameState, "test", ActionService.MILITIA);
		
		// second militia doesn't impact the choices immediately
		Assertions.assertEquals(5, playerState2.getTurn().getChoicesAvailable().get(0).getOptions().size());
		Assertions.assertEquals(1, playerState2.getTurn().getChoicesAvailable().size());
		Assertions.assertEquals(2, playerState2.getAttacks().size());		

		playerState2.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper","Copper")));
		actionService.doChoice(gameState, "test2");
		
		Assertions.assertEquals(1, playerState2.getAttacks().size());
		Assertions.assertEquals(1, playerState2.getTurn().getChoicesAvailable().size());
		Assertions.assertEquals(3, playerState2.getTurn().getChoicesAvailable().get(0).getOptions().size());
		Assertions.assertEquals(0, playerState2.getTurn().getChoicesAvailable().get(0).getMinChoices());
		Assertions.assertEquals(0, playerState2.getTurn().getChoicesAvailable().get(0).getMaxChoices());
	}

	@Test
	public void twoMilitiaWithMoat() {
		swapHandCards(playerState2, ActionService.MOAT);
		swapHandCards(ActionService.MILITIA, ActionService.MILITIA);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.MILITIA);
		
		// first militia triggers a reaction choice
		Assertions.assertEquals(2, playerState2.getTurn().getChoicesAvailable().get(0).getOptions().size());
		Assertions.assertEquals(1, playerState2.getTurn().getChoicesAvailable().size());
				
		actionService.turnPlay(gameState, "test", ActionService.MILITIA);
		
		// second militia doesn't impact the choices immediately
		Assertions.assertEquals(2, playerState2.getTurn().getChoicesAvailable().get(0).getOptions().size());
		Assertions.assertEquals(1, playerState2.getTurn().getChoicesAvailable().size());	

		playerState2.getTurn().setChoicesMade(new ArrayList<>(List.of("Moat")));
		actionService.doChoice(gameState, "test2");
		
		Assertions.assertEquals(1, playerState2.getAttacks().size());
		Assertions.assertEquals(2, playerState2.getTurn().getChoicesAvailable().get(0).getOptions().size());
		Assertions.assertEquals(1, playerState2.getTurn().getChoicesAvailable().size());	

		playerState2.getTurn().setChoicesMade(new ArrayList<>(List.of("Moat")));
		actionService.doChoice(gameState, "test2");

		Assertions.assertEquals(0, playerState2.getAttacks().size());
		Assertions.assertEquals(0, playerState2.getTurn().getChoicesAvailable().size());	
		
	}

	@Test
	public void twoMilitiaWithMoatButSecondAttackGoesThrough() {
		swapHandCards(playerState2, ActionService.MOAT);
		swapHandCards(ActionService.MILITIA, ActionService.MILITIA);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.MILITIA);
		
		// first militia triggers a reaction choice
		Assertions.assertEquals(2, playerState2.getTurn().getChoicesAvailable().get(0).getOptions().size());
		Assertions.assertEquals(1, playerState2.getTurn().getChoicesAvailable().size());
				
		actionService.turnPlay(gameState, "test", ActionService.MILITIA);
		
		// second militia doesn't impact the choices immediately
		Assertions.assertEquals(2, playerState2.getTurn().getChoicesAvailable().get(0).getOptions().size());
		Assertions.assertEquals(1, playerState2.getTurn().getChoicesAvailable().size());	

		playerState2.getTurn().setChoicesMade(new ArrayList<>(List.of("Moat")));
		actionService.doChoice(gameState, "test2");
		
		Assertions.assertEquals(1, playerState2.getAttacks().size());
		Assertions.assertEquals(2, playerState2.getTurn().getChoicesAvailable().get(0).getOptions().size());
		Assertions.assertEquals(1, playerState2.getTurn().getChoicesAvailable().size());	

		playerState2.getTurn().setChoicesMade(new ArrayList<>(List.of("No")));
		actionService.doChoice(gameState, "test2");

		Assertions.assertEquals(5, playerState2.getTurn().getChoicesAvailable().get(0).getOptions().size());
		Assertions.assertEquals(1, playerState2.getTurn().getChoicesAvailable().size());
		
	}

	@Test
	public void twoMilitiaWithMoatButFirstAttackGoesThrough() {
		swapHandCards(playerState2, ActionService.MOAT);
		swapHandCards(ActionService.MILITIA, ActionService.MILITIA);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.MILITIA);
		
		// first militia triggers a reaction choice
		Assertions.assertEquals(2, playerState2.getTurn().getChoicesAvailable().get(0).getOptions().size());
		Assertions.assertEquals(1, playerState2.getTurn().getChoicesAvailable().size());
				
		actionService.turnPlay(gameState, "test", ActionService.MILITIA);
		
		// second militia doesn't impact the choices immediately
		Assertions.assertEquals(2, playerState2.getTurn().getChoicesAvailable().get(0).getOptions().size());
		Assertions.assertEquals(1, playerState2.getTurn().getChoicesAvailable().size());	

		playerState2.getTurn().setChoicesMade(new ArrayList<>(List.of("No")));
		actionService.doChoice(gameState, "test2");
		
		Assertions.assertEquals(2, playerState2.getAttacks().size());
		Assertions.assertEquals(5, playerState2.getTurn().getChoicesAvailable().get(0).getOptions().size());
		Assertions.assertEquals(1, playerState2.getTurn().getChoicesAvailable().size());

		playerState2.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper","Copper")));
		actionService.doChoice(gameState, "test2");		
		
		Assertions.assertEquals(2, playerState2.getTurn().getChoicesAvailable().get(0).getOptions().size());
		Assertions.assertEquals(1, playerState2.getTurn().getChoicesAvailable().size());	

		playerState2.getTurn().setChoicesMade(new ArrayList<>(List.of("Moat")));
		actionService.doChoice(gameState, "test2");

		Assertions.assertEquals(0, playerState2.getTurn().getChoicesAvailable().size());
		Assertions.assertEquals(0, playerState2.getAttacks().size());
		
	}
}
