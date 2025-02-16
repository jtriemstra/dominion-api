package com.jtriemstra.dominion.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.jtriemstra.dominion.api.service.ActionServiceTestBase.Checked;

public class ActionServiceSleightOfHandCombosTest extends ActionServiceTestBase {
	
	@Test
	public void throneRoomPlusCellar() {
		swapHandCards(ActionService.THRONE_ROOM, ActionService.CELLAR);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.ESTATE, ActionService.ESTATE);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.THRONE_ROOM);
						
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Cellar")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(1, playerState.getTurn().getActionsAvailable()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(3, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper","Copper")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(3, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Estate","Estate")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(0, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(3, playerState.getHand().size()));
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.GOLD, 2, ActionService.COPPER, 1)));
	}
	
	@Test
	public void throneRoomPlusCouncilRoom() {
		swapHandCards(ActionService.THRONE_ROOM, ActionService.COUNCIL_ROOM);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.ESTATE, ActionService.ESTATE, ActionService.GOLD, ActionService.GOLD, ActionService.ESTATE, ActionService.ESTATE);
		loadDeck(playerState2, ActionService.GOLD, ActionService.GOLD);
		loadDeck(playerState3, ActionService.GOLD, ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.THRONE_ROOM);
						
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Council Room")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(11, playerState.getHand().size()));
		Assertions.assertEquals(7, playerState2.getHand().size());
		Assertions.assertEquals(7, playerState3.getHand().size());
	}
	
	@Test
	public void throneRoomPlusFestival() {
		swapHandCards(ActionService.THRONE_ROOM, ActionService.FESTIVAL);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.THRONE_ROOM);
						
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Festival")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(4, playerState.getTurn().getActionsAvailable()));
		doAssertion(Checked.BUY, () -> Assertions.assertEquals(3, playerState.getTurn().getBuys()));
		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(4, playerState.getTurn().getTreasure()));
	}
	
	@Test
	public void throneRoomPlusLibrary() {
		swapHandCards(ActionService.THRONE_ROOM, ActionService.LIBRARY);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.ESTATE, ActionService.ESTATE);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.THRONE_ROOM);
						
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Library")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(7, playerState.getHand().size()));
	}
	
	@Test
	public void throneRoomPlusHarbingerWithNoDiscard() {
		swapHandCards(ActionService.THRONE_ROOM, ActionService.HARBINGER);
		loadDeck(ActionService.ESTATE, ActionService.ESTATE);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.THRONE_ROOM);
						
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Harbinger")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(5, playerState.getHand().size()));
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(2, playerState.getTurn().getActionsAvailable()));
	}

	@Test
	public void throneRoomPlusHarbingerWithDiscard() {
		swapHandCards(ActionService.THRONE_ROOM, ActionService.HARBINGER);
		playerState.getDiscard().getCards().add("Estate");
		playerState.getDiscard().getCards().add("Silver");
		loadDeck(ActionService.ESTATE, ActionService.ESTATE);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.THRONE_ROOM);
						
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Harbinger")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(4, playerState.getHand().size()));
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(1, playerState.getTurn().getActionsAvailable()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(2 + 1, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Silver")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.DECK, () -> Assertions.assertEquals(1, playerState.getDeck().getCards().size()));
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(1, playerState.getDiscard().getCards().size()));

		doAssertion(Checked.HAND, () -> Assertions.assertEquals(5, playerState.getHand().size()));
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(2, playerState.getTurn().getActionsAvailable()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1 + 1, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Estate")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.DECK, () -> Assertions.assertEquals(2, playerState.getDeck().getCards().size()));
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(0, playerState.getDiscard().getCards().size()));
	}
	
	@Test
	public void throneRoomPlusMilitia() {
		swapHandCards(ActionService.THRONE_ROOM, ActionService.MILITIA);
		loadDeck(ActionService.ESTATE, ActionService.ESTATE);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.THRONE_ROOM);
						
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Militia")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(4, playerState.getTurn().getTreasure()));

		playerState2.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper","Copper")));
		actionService.doChoice(gameState, "test2");
		playerState3.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper","Copper")));
		actionService.doChoice(gameState, "test3");

		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(4, playerState.getTurn().getTreasure()));
	}
	
	@Test
	public void throneRoomPlusPoacherWithNoDiscards() {
		swapHandCards(ActionService.THRONE_ROOM, ActionService.POACHER);
		loadDeck(ActionService.ESTATE, ActionService.ESTATE);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.THRONE_ROOM);
						
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Poacher")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(2, playerState.getTurn().getTreasure()));
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(2, playerState.getTurn().getActionsAvailable()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(5, playerState.getHand().size()));
	}

	@Test
	public void throneRoomPlusPoacherWithDiscard() {
		swapHandCards(ActionService.THRONE_ROOM, ActionService.POACHER);
		this.bankState.getSupplies().get(ActionService.DUCHY).setCount(0);
		loadDeck(ActionService.ESTATE, ActionService.ESTATE);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.THRONE_ROOM);
						
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Poacher")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(1, playerState.getTurn().getTreasure()));
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(1, playerState.getTurn().getActionsAvailable()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(4, playerState.getHand().size()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Estate")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(2, playerState.getTurn().getTreasure()));
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(2, playerState.getTurn().getActionsAvailable()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(4, playerState.getHand().size()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Estate")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.HAND, () -> Assertions.assertEquals(3, playerState.getHand().size()));
	}
	
	@Test
	public void throneRoomPlusSmithy() {
		swapHandCards(ActionService.THRONE_ROOM, ActionService.SMITHY);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.ESTATE, ActionService.ESTATE, ActionService.GOLD, ActionService.GOLD, ActionService.ESTATE, ActionService.ESTATE);
		loadDeck(playerState2, ActionService.GOLD, ActionService.GOLD);
		loadDeck(playerState3, ActionService.GOLD, ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.THRONE_ROOM);
						
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Smithy")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(9, playerState.getHand().size()));
	}

	@Test
	public void cellarPlusHarbinger() {
		swapHandCards(ActionService.CELLAR, ActionService.HARBINGER);
		loadDeck(ActionService.GOLD, ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.CELLAR);
						
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(4, playerState.getHand().size()));
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.COPPER, 2, ActionService.GOLD, 1)));

		actionService.turnPlay(gameState, "test", ActionService.HARBINGER);

		doAssertion(Checked.HAND, () -> Assertions.assertEquals(4, playerState.getHand().size()));
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.COPPER, 2, ActionService.GOLD, 2)));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(0, playerState.getDiscard().getCards().size()));
		doAssertion(Checked.DECK, () -> Assertions.assertEquals(1, playerState.getDeck().getCards().size()));
	}

	@Test
	public void harbingerPlusCellar() {
		swapHandCards(ActionService.CELLAR, ActionService.HARBINGER);
		loadDeck(ActionService.GOLD, ActionService.GOLD);
		playerState.getDiscard().getCards().add(ActionService.SILVER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.HARBINGER);
						
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Silver")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(5, playerState.getHand().size()));
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.COPPER, 3, ActionService.GOLD, 1)));
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(0, playerState.getDiscard().getCards().size()));
		doAssertion(Checked.DECK, () -> Assertions.assertEquals(2, playerState.getDeck().getCards().size()));

		actionService.turnPlay(gameState, "test", ActionService.CELLAR);
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(1, playerState.getDiscard().getCards().size()));
		doAssertion(Checked.DECK, () -> Assertions.assertEquals(1, playerState.getDeck().getCards().size()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(4, playerState.getHand().size()));
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.COPPER, 2, ActionService.GOLD, 1, ActionService.SILVER, 1)));
	}

	@Test
	public void dualThroneRoomPlusCellar() {
		swapHandCards(ActionService.THRONE_ROOM, ActionService.THRONE_ROOM, ActionService.CELLAR);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.ESTATE, ActionService.ESTATE);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.THRONE_ROOM);
						
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Throne Room")));
		actionService.doChoice(gameState, "test");
		
		// first play of second throne room = 2 Cellars
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Cellar")));
		actionService.doChoice(gameState, "test");		
		
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(1, playerState.getTurn().getActionsAvailable()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Estate")));
		actionService.doChoice(gameState, "test");
		
		// second play of second throne room = no cellar left, no action choice
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(0, playerState.getTurn().getChoicesAvailable().size()));
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(2, playerState.getHand().size()));
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.ESTATE, 1, ActionService.COPPER, 1)));
	}

	@Test
	public void dualThroneRoomPlusCellarAndSmithy() {
		swapHandCards(ActionService.THRONE_ROOM, ActionService.THRONE_ROOM, ActionService.CELLAR);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.SMITHY, ActionService.ESTATE);
		loadDiscard(ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.THRONE_ROOM);
						
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Throne Room")));
		actionService.doChoice(gameState, "test");
		
		// first play of second throne room = 2 Cellars
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Cellar")));
		actionService.doChoice(gameState, "test");		
		
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(1, playerState.getTurn().getActionsAvailable()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Estate")));
		actionService.doChoice(gameState, "test");
		
		// second play of second throne room = Smithy
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Smithy")));
		actionService.doChoice(gameState, "test");	
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(7, playerState.getHand().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(0, playerState.getTurn().getChoicesAvailable().size()));
	}

	@Test
	public void dualThroneRoomPlusSmithyAndCellar() {
		swapHandCards(ActionService.THRONE_ROOM, ActionService.THRONE_ROOM, ActionService.SMITHY);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.CELLAR, ActionService.ESTATE);
		loadDiscard(ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.THRONE_ROOM);
						
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Throne Room")));
		actionService.doChoice(gameState, "test");
		
		// first play of second throne room = 2 Smithys
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Smithy")));
		actionService.doChoice(gameState, "test");		
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(8, playerState.getHand().size()));
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(0, playerState.getTurn().getActionsAvailable()));
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));

		// second play of second throne room = Cellar
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Cellar")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(7, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Estate","Copper")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(2, playerState.getTurn().getActionsAvailable()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(7, playerState.getHand().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));

		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(7, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(2, playerState.getTurn().getActionsAvailable()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(7, playerState.getHand().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(0, playerState.getTurn().getChoicesAvailable().size()));
	}
	
	@Test
	public void dualThroneRoomPlusDualFestival() {
		swapHandCards(ActionService.THRONE_ROOM, ActionService.THRONE_ROOM, ActionService.FESTIVAL, ActionService.FESTIVAL);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.CELLAR, ActionService.ESTATE);
		loadDiscard(ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.THRONE_ROOM);
						
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Throne Room")));
		actionService.doChoice(gameState, "test");
		
		// first play of second throne room = 2 Festivals
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Festival")));
		actionService.doChoice(gameState, "test");		
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(2, playerState.getHand().size()));
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(4, playerState.getTurn().getActionsAvailable()));
		doAssertion(Checked.BUY, () -> Assertions.assertEquals(3, playerState.getTurn().getBuys()));
		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(4, playerState.getTurn().getTreasure()));
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));

		// second play of second throne room = Festival
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Festival")));
		actionService.doChoice(gameState, "test");
		

		doAssertion(Checked.HAND, () -> Assertions.assertEquals(1, playerState.getHand().size()));
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(8, playerState.getTurn().getActionsAvailable()));
		doAssertion(Checked.BUY, () -> Assertions.assertEquals(5, playerState.getTurn().getBuys()));
		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(8, playerState.getTurn().getTreasure()));
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(0, playerState.getTurn().getChoicesAvailable().size()));
	}
	
	@Test
	public void militiaThenThroneRoomToMilitia() {
		playerState.getTurn().setActionsAvailable(2);
		swapHandCards(ActionService.THRONE_ROOM, ActionService.MILITIA, ActionService.MILITIA);
		loadDeck(ActionService.ESTATE, ActionService.ESTATE);
		stashGameState();

		actionService.turnPlay(gameState, "test", ActionService.MILITIA);
		
		// first militia triggers a choice and treasure
		Assertions.assertEquals(5, playerState2.getTurn().getChoicesAvailable().get(0).getOptions().size());
		Assertions.assertEquals(1, playerState2.getTurn().getChoicesAvailable().size());
		Assertions.assertEquals(2, playerState2.getTurn().getChoicesAvailable().get(0).getMinChoices());
		Assertions.assertEquals(2, playerState2.getTurn().getChoicesAvailable().get(0).getMaxChoices());
		Assertions.assertEquals(5, playerState2.getHand().size());
		Assertions.assertEquals(1, playerState2.getAttacks().size());
		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(2, playerState.getTurn().getTreasure()));
		
		actionService.turnPlay(gameState, "test", ActionService.THRONE_ROOM);
						
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Militia")));
		actionService.doChoice(gameState, "test");
		
		// second militia gains treasure immediately but doesn't change target choices
		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(6, playerState.getTurn().getTreasure())); 
		Assertions.assertEquals(5, playerState2.getTurn().getChoicesAvailable().get(0).getOptions().size());
		Assertions.assertEquals(1, playerState2.getTurn().getChoicesAvailable().size());
		Assertions.assertEquals(3, playerState2.getAttacks().size()); 		

		// target responds to first militia, which doesn't trigger third yet
		playerState2.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper","Copper")));
		actionService.doChoice(gameState, "test2");
		playerState3.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper","Copper")));
		actionService.doChoice(gameState, "test3");

		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(6, playerState.getTurn().getTreasure())); 
		Assertions.assertEquals(0, playerState2.getTurn().getChoicesAvailable().size());
		Assertions.assertEquals(0, playerState2.getAttacks().size());
		Assertions.assertEquals(0, playerState3.getTurn().getChoicesAvailable().size());
		Assertions.assertEquals(0, playerState3.getAttacks().size());
	}
	
	@Test
	public void throneRoomToMilitiaThenCouncilRoom() {
		playerState.getTurn().setActionsAvailable(2);
		swapHandCards(ActionService.THRONE_ROOM, ActionService.MILITIA, ActionService.COUNCIL_ROOM);
		loadDeck(ActionService.ESTATE, ActionService.ESTATE, ActionService.ESTATE, ActionService.ESTATE);
		loadDeck(playerState2, ActionService.GOLD, ActionService.GOLD);
		loadDeck(playerState3, ActionService.GOLD, ActionService.GOLD);
		stashGameState();

		actionService.turnPlay(gameState, "test", ActionService.THRONE_ROOM);
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Militia")));
		actionService.doChoice(gameState, "test");
		
		actionService.turnPlay(gameState, "test", ActionService.COUNCIL_ROOM);
		
		// TODO: there's more to test here, but this shows the current ordering issue of council room
		
		Assertions.assertEquals(5, playerState2.getHand().size());
	}
}
