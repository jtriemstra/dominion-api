package com.jtriemstra.dominion.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.jtriemstra.dominion.api.service.ActionServiceTestBase.Checked;

@ExtendWith(MockitoExtension.class)
public class ActionServiceHappyTrailsCombosTest extends ActionServiceTestBase  {
	
	@Test
	public void workshopGainsTrailWithFollowupAction() {
		swapHandCards(ActionService.WORKSHOP);
		loadDeck(ActionService.NOMADS);
		stashGameState();

		actionService.turnPlay(gameState, "test", ActionService.WORKSHOP);
		
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(3, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(4, playerState.getHand().size()));
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(0, playerState.getTurn().getActionsAvailable()));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Trail")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(4, playerState.getHand().size()));

		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("PLAY1")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.HAND, () -> Assertions.assertEquals(5, playerState.getHand().size()));
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(1, playerState.getTurn().getActionsAvailable()));
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(0, playerState.getDiscard().size()));
		Assertions.assertEquals(2, playerState.getPlayed().size());
		Assertions.assertEquals("action", playerState.getPhase());
		
		actionService.turnPlay(gameState, "test", ActionService.NOMADS);
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(0, playerState.getTurn().getActionsAvailable()));
	}

	@Test
	public void workshopGainsTrailWithNoAction() {
		swapHandCards(ActionService.WORKSHOP);
		loadDeck(ActionService.SILVER);
		stashGameState();

		actionService.turnPlay(gameState, "test", ActionService.WORKSHOP);
		
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(3, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(4, playerState.getHand().size()));
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(0, playerState.getTurn().getActionsAvailable()));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Trail")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(4, playerState.getHand().size()));

		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("PLAY1")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.HAND, () -> Assertions.assertEquals(5, playerState.getHand().size()));
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(1, playerState.getTurn().getActionsAvailable()));
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(0, playerState.getDiscard().size()));
		Assertions.assertEquals(2, playerState.getPlayed().size());
		Assertions.assertEquals("buy", playerState.getPhase());
	}
	
	@Test
	public void workshopGainsNomads() {
		swapBankSupplies(Map.of(ActionService.DUCHY, ActionService.NOMADS));
		swapHandCards(ActionService.WORKSHOP);
		loadDeck(ActionService.SILVER);
		stashGameState();

		actionService.turnPlay(gameState, "test", ActionService.WORKSHOP);
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Nomads")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(1, playerState.getDiscard().size()));
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(1, playerState.getTurn().getGainedToDiscard().size()));
		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(2, playerState.getTurn().getTreasure()));
	}
	
	@Test
	public void libraryDiscardsTrail() {
		swapHandCards(ActionService.LIBRARY);
		loadDeck(ActionService.ESTATE, ActionService.COPPER, ActionService.SILVER, ActionService.TRAIL, ActionService.SILVER);
		stashGameState();

		actionService.turnPlay(gameState, "test", ActionService.LIBRARY);
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(5, playerState.getHand().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertTrue(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("Trail")));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Trail")));
		actionService.doChoice(gameState, "test");
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(7, playerState.getHand().size()));
		
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("PLAY")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.HAND, () -> Assertions.assertEquals(8, playerState.getHand().size()));
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(1, playerState.getTurn().getActionsAvailable()));
		Assertions.assertEquals("buy", playerState.getPhase());
	}
	
	@Test
	public void libraryDiscardsTwoTrails() {
		swapHandCards(ActionService.LIBRARY);
		loadDeck(ActionService.COPPER, ActionService.COPPER, ActionService.ESTATE, ActionService.COPPER, ActionService.SILVER, ActionService.TRAIL, ActionService.TRAIL);
		stashGameState();

		actionService.turnPlay(gameState, "test", ActionService.LIBRARY);
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(4, playerState.getHand().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertTrue(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("Trail")));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Trail")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.HAND, () -> Assertions.assertEquals(4, playerState.getHand().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertTrue(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("Trail")));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Trail")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(7, playerState.getHand().size()));
		
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("PLAY")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.HAND, () -> Assertions.assertEquals(8, playerState.getHand().size()));
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(1, playerState.getTurn().getActionsAvailable()));
		
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("PLAY")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.HAND, () -> Assertions.assertEquals(9, playerState.getHand().size()));
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(2, playerState.getTurn().getActionsAvailable()));
		
		Assertions.assertEquals("buy", playerState.getPhase());
	}
	
	@Test
	public void libraryDiscardsTwoTrailsAndStaysInActionPhase() {
		swapHandCards(ActionService.LIBRARY);
		loadDeck(ActionService.VILLAGE, ActionService.COPPER, ActionService.ESTATE, ActionService.COPPER, ActionService.SILVER, ActionService.TRAIL, ActionService.TRAIL);
		stashGameState();

		actionService.turnPlay(gameState, "test", ActionService.LIBRARY);
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(4, playerState.getHand().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertTrue(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("Trail")));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Trail")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.HAND, () -> Assertions.assertEquals(4, playerState.getHand().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertTrue(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("Trail")));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Trail")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(7, playerState.getHand().size()));
		
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("PLAY")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.HAND, () -> Assertions.assertEquals(8, playerState.getHand().size()));
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(1, playerState.getTurn().getActionsAvailable()));
		
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("PLAY")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.HAND, () -> Assertions.assertEquals(9, playerState.getHand().size()));
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(2, playerState.getTurn().getActionsAvailable()));
		
		Assertions.assertEquals("action", playerState.getPhase());
	}
	
	@Test
	public void cellarDiscardsTrailAndPlays() {
		swapHandCards(ActionService.CELLAR, ActionService.TRAIL);
		loadDeck(ActionService.VILLAGE, ActionService.COPPER, ActionService.ESTATE, ActionService.COPPER, ActionService.SILVER, ActionService.TRAIL, ActionService.TRAIL);
		stashGameState();

		actionService.turnPlay(gameState, "test", ActionService.CELLAR);

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Trail","Copper")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("PLAY")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.HAND, () -> Assertions.assertEquals(5, playerState.getHand().size()));
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(2, playerState.getTurn().getActionsAvailable()));
		Assertions.assertEquals("action", playerState.getPhase());		
	}
	
	@Test
	public void oasisDiscardsTrailAndPlays() {
		swapHandCards(ActionService.OASIS, ActionService.TRAIL);
		loadDeck(ActionService.VILLAGE, ActionService.COPPER, ActionService.ESTATE, ActionService.COPPER, ActionService.SILVER, ActionService.TRAIL, ActionService.TRAIL);
		stashGameState();

		actionService.turnPlay(gameState, "test", ActionService.OASIS);

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Trail")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("PLAY")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.HAND, () -> Assertions.assertEquals(5, playerState.getHand().size()));
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(2, playerState.getTurn().getActionsAvailable()));
		Assertions.assertEquals("action", playerState.getPhase());	
	}
	
	@Test
	public void berserkerGainsTrailAndPlays() {
		swapHandCards(ActionService.BERSERKER);
		loadDeck(ActionService.VILLAGE);
		stashGameState();

		actionService.turnPlay(gameState, "test", ActionService.BERSERKER);

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Trail")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("PLAY1")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.HAND, () -> Assertions.assertEquals(5, playerState.getHand().size()));
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(1, playerState.getTurn().getActionsAvailable()));
	}
	
	@Test
	public void throneRoomPlusMoneylender() {
		swapHandCards(ActionService.THRONE_ROOM, ActionService.MONEYLENDER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.THRONE_ROOM);
						
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Moneylender")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("YES")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("YES")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(0, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(1, playerState.getHand().size()));
		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(6, playerState.getTurn().getTreasure()));
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(0, playerState.getDiscard().size()));
	}
	
	@Test
	public void throneRoomPlusMoneylenderWithOnlyOneCopper() {
		swapHandCards(ActionService.THRONE_ROOM, ActionService.MONEYLENDER, ActionService.ESTATE, ActionService.ESTATE);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.THRONE_ROOM);
						
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Moneylender")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("YES")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(0, playerState.getTurn().getChoicesAvailable().size()));
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(2, playerState.getHand().size()));
		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(3, playerState.getTurn().getTreasure()));
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(0, playerState.getDiscard().size()));		
	}
	
	@Test
	public void throneRoomPlusHighway() {
		swapHandCards(ActionService.THRONE_ROOM, ActionService.HIGHWAY);
		loadDeck(ActionService.VILLAGE, ActionService.SILVER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.THRONE_ROOM);

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Highway")));
		actionService.doChoice(gameState, "test");
		
		Assertions.assertEquals(3, actionService.getCost(gameState, "test", ActionService.DUCHY));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(5, playerState.getHand().size()));
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(2, playerState.getTurn().getActionsAvailable()));
	}
	
	@Test
	public void throneRoomPlusNomads() {
		swapHandCards(ActionService.THRONE_ROOM, ActionService.NOMADS);
		loadDeck(ActionService.VILLAGE, ActionService.SILVER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.THRONE_ROOM);

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Nomads")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(3, playerState.getHand().size()));
		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(4, playerState.getTurn().getTreasure()));
		doAssertion(Checked.BUY, () -> Assertions.assertEquals(3, playerState.getTurn().getBuys()));		
	}
	
	@Test
	public void throneRoomPlusTrail() {
		swapHandCards(ActionService.THRONE_ROOM, ActionService.TRAIL);
		loadDeck(ActionService.VILLAGE, ActionService.SILVER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.THRONE_ROOM);

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Trail")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(5, playerState.getHand().size()));
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(2, playerState.getTurn().getActionsAvailable()));
	}
	
	@Test
	public void throneRoomPlusOasis() {
		swapHandCards(ActionService.THRONE_ROOM, ActionService.OASIS);
		loadDeck(ActionService.VILLAGE, ActionService.SILVER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.THRONE_ROOM);

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Oasis")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(4, playerState.getHand().size()));
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(1, playerState.getTurn().getActionsAvailable()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.HAND, () -> Assertions.assertEquals(4, playerState.getHand().size()));
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(2, playerState.getTurn().getActionsAvailable()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(2, playerState.getTurn().getTreasure()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(3, playerState.getHand().size()));
	}
	
	@Test
	public void throneRoomPlusBerserker() {
		swapHandCards(ActionService.THRONE_ROOM, ActionService.BERSERKER);
		swapHandCards(playerState2, ActionService.TRAIL);
		loadDeck(ActionService.VILLAGE, ActionService.SILVER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.THRONE_ROOM);

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Berserker")));
		actionService.doChoice(gameState, "test");
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Silver")));
		actionService.doChoice(gameState, "test");

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Silver")));
		actionService.doChoice(gameState, "test");
		
		playerState2.getTurn().setChoicesMade(new ArrayList<>(List.of("Trail","Copper")));
		actionService.doChoice(gameState, "test2");
		
		playerState3.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper","Copper")));
		actionService.doChoice(gameState, "test3");
		
		playerState2.getTurn().setChoicesMade(new ArrayList<>(List.of("PLAY")));
		actionService.doChoice(gameState, "test2");
		
		Assertions.assertEquals(4, playerState2.getHand().size());
		Assertions.assertEquals(1, playerState2.getTurn().getChoicesAvailable().get(0).getMaxChoices());
		Assertions.assertEquals(1, playerState2.getTurn().getChoicesAvailable().get(0).getMinChoices());
		Assertions.assertEquals(0, playerState3.getTurn().getChoicesAvailable().size());
		playerState2.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper")));
		actionService.doChoice(gameState, "test2");
		
		Assertions.assertEquals(0, playerState2.getAttacks().size());
		Assertions.assertEquals(0, playerState3.getAttacks().size());
	}
	
	@Test
	public void throneRoomPlusBerserkerPlayingTrailAndDrawingTrail() {
		swapHandCards(ActionService.THRONE_ROOM, ActionService.BERSERKER);
		swapHandCards(playerState2, ActionService.TRAIL);
		loadDeck(ActionService.VILLAGE, ActionService.SILVER);
		loadDeck(playerState2, ActionService.SILVER, ActionService.TRAIL);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.THRONE_ROOM);

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Berserker")));
		actionService.doChoice(gameState, "test");
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Silver")));
		actionService.doChoice(gameState, "test");

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Silver")));
		actionService.doChoice(gameState, "test");
		
		playerState2.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper","Trail")));
		actionService.doChoice(gameState, "test2");
		
		playerState3.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper","Copper")));
		actionService.doChoice(gameState, "test3");
		
		playerState2.getTurn().setChoicesMade(new ArrayList<>(List.of("PLAY")));
		actionService.doChoice(gameState, "test2");
		
		Assertions.assertEquals(4, playerState2.getHand().size());
		Assertions.assertTrue(playerState2.getHand().getCards().contains(ActionService.TRAIL));
		Assertions.assertEquals(1, playerState2.getAttacks().size());
		Assertions.assertEquals(1, playerState2.getTurn().getChoicesAvailable().get(0).getMaxChoices());
		Assertions.assertEquals(1, playerState2.getTurn().getChoicesAvailable().get(0).getMinChoices());
		Assertions.assertEquals(0, playerState3.getTurn().getChoicesAvailable().size());
		playerState2.getTurn().setChoicesMade(new ArrayList<>(List.of("Trail")));
		actionService.doChoice(gameState, "test2");
		
		playerState2.getTurn().setChoicesMade(new ArrayList<>(List.of("PLAY")));
		actionService.doChoice(gameState, "test2");
		
		Assertions.assertEquals(0, playerState2.getAttacks().size());
		Assertions.assertEquals(0, playerState3.getAttacks().size());
		Assertions.assertEquals(4, playerState2.getHand().size());
		
		//TODO: why does this not repro an error I saw after throne-rooming a berserker?
	}
	
	@Test
	public void throneRoomPlusOasisWithOneTrail() {
		swapHandCards(ActionService.THRONE_ROOM, ActionService.OASIS, ActionService.TRAIL);
		loadDeck(ActionService.SILVER, ActionService.VILLAGE, ActionService.SILVER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.THRONE_ROOM);

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Oasis")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(4, playerState.getHand().size()));
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(1, playerState.getTurn().getActionsAvailable()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.HAND, () -> Assertions.assertEquals(4, playerState.getHand().size()));
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(2, playerState.getTurn().getActionsAvailable()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Trail")));
		actionService.doChoice(gameState, "test");

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("PLAY")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(2, playerState.getTurn().getTreasure()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(4, playerState.getHand().size()));
	}
	
	@Test
	public void throneRoomPlusCellarDrawingTrail() {
		swapHandCards(ActionService.THRONE_ROOM, ActionService.CELLAR);
		loadDeck(ActionService.SILVER, ActionService.TRAIL);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.THRONE_ROOM);

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Cellar")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.HAND, () -> Assertions.assertEquals(3, playerState.getHand().size()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.HAND, () -> Assertions.assertEquals(3, playerState.getHand().size()));
		assertCardsInHand(Map.of("Copper",2,"Trail",1));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Trail")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.HAND, () -> Assertions.assertEquals(4, playerState.getHand().size()));
		assertCardsInHand(Map.of("Copper",2));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("PLAY")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(4, playerState.getHand().size()));
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(3, playerState.getTurn().getActionsAvailable()));
	}
	
	@Test
	public void berserkerAttackDiscardsTrailWhichIsPlayed() {
		swapHandCards(ActionService.BERSERKER);
		swapHandCards(playerState2, ActionService.TRAIL);
		loadDeck(playerState2, ActionService.SILVER);
		
		actionService.turnPlay(gameState, "test", ActionService.BERSERKER);

		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState2.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(5, playerState2.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		
		playerState2.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper","Trail")));
		actionService.doChoice(gameState, "test2");

		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState2.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(2, playerState2.getTurn().getChoicesAvailable().get(0).getOptions().size()));

		playerState2.getTurn().setChoicesMade(new ArrayList<>(List.of("PLAY")));
		actionService.doChoice(gameState, "test2");

		doAssertion(Checked.HAND, () -> Assertions.assertEquals(4, playerState2.getHand().size()));
		Assertions.assertEquals(0, playerState2.getAttacks().size());
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(0, playerState2.getTurn().getChoicesAvailable().size()));
	}
	
	@Test
	public void berserkerAttackDiscardsTrailFirstWhichIsPlayed() {
		swapHandCards(ActionService.BERSERKER);
		swapHandCards(playerState2, ActionService.TRAIL);
		loadDeck(playerState2, ActionService.SILVER);
		
		actionService.turnPlay(gameState, "test", ActionService.BERSERKER);

		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState2.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(5, playerState2.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		
		playerState2.getTurn().setChoicesMade(new ArrayList<>(List.of("Trail","Copper")));
		actionService.doChoice(gameState, "test2");

		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState2.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(2, playerState2.getTurn().getChoicesAvailable().get(0).getOptions().size()));

		playerState2.getTurn().setChoicesMade(new ArrayList<>(List.of("PLAY")));
		actionService.doChoice(gameState, "test2");

		doAssertion(Checked.HAND, () -> Assertions.assertEquals(4, playerState2.getHand().size()));
		Assertions.assertEquals(0, playerState2.getAttacks().size());
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(0, playerState2.getTurn().getChoicesAvailable().size()));
	}
	
	@Test
	public void berserkerAttackDiscardsTrailWhichIsNotPlayed() {
		swapHandCards(ActionService.BERSERKER);
		swapHandCards(playerState2, ActionService.TRAIL);
		loadDeck(playerState2, ActionService.SILVER);
		
		actionService.turnPlay(gameState, "test", ActionService.BERSERKER);

		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState2.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(5, playerState2.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		
		playerState2.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper","Trail")));
		actionService.doChoice(gameState, "test2");

		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState2.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(2, playerState2.getTurn().getChoicesAvailable().get(0).getOptions().size()));

		playerState2.getTurn().setChoicesMade(new ArrayList<>(List.of("DISCARD")));
		actionService.doChoice(gameState, "test2");

		doAssertion(Checked.HAND, () -> Assertions.assertEquals(3, playerState2.getHand().size()));
		Assertions.assertEquals(0, playerState2.getAttacks().size());
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(0, playerState2.getTurn().getChoicesAvailable().size()));
	}
	
	@Test
	public void berserkerAttackDiscardsTrailFirstWhichIsNotPlayed() {
		swapHandCards(ActionService.BERSERKER);
		swapHandCards(playerState2, ActionService.TRAIL);
		loadDeck(playerState2, ActionService.SILVER);
		
		actionService.turnPlay(gameState, "test", ActionService.BERSERKER);

		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState2.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(5, playerState2.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		
		playerState2.getTurn().setChoicesMade(new ArrayList<>(List.of("Trail","Copper")));
		actionService.doChoice(gameState, "test2");

		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState2.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(2, playerState2.getTurn().getChoicesAvailable().get(0).getOptions().size()));

		playerState2.getTurn().setChoicesMade(new ArrayList<>(List.of("DISCARD")));
		actionService.doChoice(gameState, "test2");

		doAssertion(Checked.HAND, () -> Assertions.assertEquals(3, playerState2.getHand().size()));
		Assertions.assertEquals(0, playerState2.getAttacks().size());
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(0, playerState2.getTurn().getChoicesAvailable().size()));
	}
	
	@Test
	public void berserkerAttackDiscardsTrailWhichDrawsTrail() {
		swapHandCards(ActionService.BERSERKER);
		swapHandCards(playerState2, ActionService.TRAIL);
		loadDeck(playerState2, ActionService.TRAIL);
		
		actionService.turnPlay(gameState, "test", ActionService.BERSERKER);

		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState2.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(5, playerState2.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		
		playerState2.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper","Trail")));
		actionService.doChoice(gameState, "test2");

		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState2.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(2, playerState2.getTurn().getChoicesAvailable().get(0).getOptions().size()));

		playerState2.getTurn().setChoicesMade(new ArrayList<>(List.of("PLAY")));
		actionService.doChoice(gameState, "test2");

		doAssertion(Checked.HAND, () -> Assertions.assertEquals(4, playerState2.getHand().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(0, playerState2.getTurn().getChoicesAvailable().size()));
	}
	
	@Test
	public void highwayIntoWorkshopGainBerserkerGainTrailCanUseAction() {
		swapHandCards(ActionService.HIGHWAY);
		loadDeck(ActionService.WORKSHOP, ActionService.WORKSHOP);
		swapBankSupplies(Map.of(ActionService.DUCHY, ActionService.BERSERKER));
		
		actionService.turnPlay(gameState, "test", ActionService.HIGHWAY);
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(1, playerState.getTurn().getActionsAvailable()));
		
		actionService.turnPlay(gameState, "test", ActionService.WORKSHOP);
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(0, playerState.getTurn().getActionsAvailable()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Berserker")));
		actionService.doChoice(gameState, "test");
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Trail")));
		actionService.doChoice(gameState, "test");
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("PLAY1")));
		actionService.doChoice(gameState, "test");
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(1, playerState.getTurn().getActionsAvailable()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(5, playerState.getHand().size()));
		Assertions.assertEquals("action", playerState.getPhase());

		actionService.turnPlay(gameState, "test", ActionService.WORKSHOP);
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(0, playerState.getTurn().getActionsAvailable()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Silver")));
		actionService.doChoice(gameState, "test");
		Assertions.assertEquals("buy", playerState.getPhase());
	}

	@Test
	public void nomadsBuyBerserkerGainTrailCannotUseAction() {
		swapHandCards(ActionService.NOMADS);
		loadDeck(ActionService.WORKSHOP);
		swapBankSupplies(Map.of(ActionService.DUCHY, ActionService.BERSERKER));
		
		actionService.turnPlay(gameState, "test", ActionService.NOMADS);
		Assertions.assertEquals("buy", playerState.getPhase());
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		
		actionService.doBuy(gameState, "test", ActionService.BERSERKER);
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Trail")));
		actionService.doChoice(gameState, "test");
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("PLAY1")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(2, playerState.getHand().size()));
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(1, playerState.getTurn().getActionsAvailable()));

		Assertions.assertThrows(RuntimeException.class, () -> actionService.turnPlay(gameState, "test", ActionService.WORKSHOP));
		
	}
	
	@Test
	public void berserkerGainsNomads() {
		swapHandCards(ActionService.BERSERKER);
		swapBankSupplies(Map.of(ActionService.TRAIL, ActionService.NOMADS));
		actionService.turnPlay(gameState, "test", ActionService.BERSERKER);

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Nomads")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(2, playerState.getTurn().getTreasure()));
	}
	
	@Test
	public void highwayDiscountWorkshopHasMoreChoices() {
		swapHandCards(ActionService.HIGHWAY, ActionService.WORKSHOP);
		loadDeck(ActionService.COPPER);
		
		actionService.turnPlay(gameState, "test", ActionService.HIGHWAY);
		actionService.turnPlay(gameState, "test", ActionService.WORKSHOP);

		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(5, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
	}
	
	@Test
	public void highwayAndNomadsDiscountsTwoCards() {
		swapHandCards(ActionService.HIGHWAY, ActionService.NOMADS);
		loadDeck(ActionService.COPPER);
		swapBankSupplies(Map.of(ActionService.TRAIL, ActionService.THRONE_ROOM));

		actionService.turnPlay(gameState, "test", ActionService.HIGHWAY);
		actionService.turnPlay(gameState, "test", ActionService.NOMADS);
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		
		actionService.doBuy(gameState, "test", ActionService.THRONE_ROOM);
		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(3, playerState.getTurn().getTreasure()));
		actionService.doBuy(gameState, "test", ActionService.THRONE_ROOM);
		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(0, playerState.getTurn().getTreasure()));
	}
	
	@Test
	public void highwayHasNoEffectOnBerserkerGain() {
		swapHandCards(ActionService.HIGHWAY, ActionService.BERSERKER);
		loadDeck(ActionService.COPPER);
		
		actionService.turnPlay(gameState, "test", ActionService.HIGHWAY);
		actionService.turnPlay(gameState, "test", ActionService.BERSERKER);

		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(3, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
	}
}
