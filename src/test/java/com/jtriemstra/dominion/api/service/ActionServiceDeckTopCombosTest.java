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
public class ActionServiceDeckTopCombosTest extends ActionServiceTestBase {

	@Test
	public void vassalIntoMoneylender() {
		
		swapHandCards(ActionService.ESTATE, ActionService.VASSAL);
		loadDeck(ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.MONEYLENDER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.VASSAL);
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		Assertions.assertTrue(playerState.getLooking().getCards().contains("Moneylender"));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("PLAY")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("YES")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(0, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(5, playerState.getTurn().getTreasure()));
		Assertions.assertEquals(0, playerState.getLooking().size());
		 
	}
	
	@Test
	public void vassalIntoArtisan() {
		
		swapHandCards(ActionService.ESTATE, ActionService.VASSAL);
		loadDeck(ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.ARTISAN);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.VASSAL);
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		Assertions.assertTrue(playerState.getLooking().getCards().contains("Artisan"));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("PLAY")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(5, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Duchy")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(5, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(0, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(2, playerState.getTurn().getTreasure()));
		Assertions.assertEquals(0, playerState.getLooking().size());
	}
	
	@Test
	public void vassalIntoHarbinger() {
		
		swapHandCards(ActionService.ESTATE, ActionService.VASSAL);
		loadDeck(ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.HARBINGER);
		this.loadDiscard(ActionService.SILVER, ActionService.ESTATE);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.VASSAL);
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		Assertions.assertTrue(playerState.getLooking().getCards().contains("Harbinger"));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("PLAY")));
		actionService.doChoice(gameState, "test");
		
		Assertions.assertEquals(0, playerState.getLooking().size());
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(5, playerState.getHand().size()));
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(1, playerState.getTurn().getActionsAvailable()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Silver")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(1, playerState.getDiscard().size()));
		doAssertion(Checked.DECK, () -> Assertions.assertEquals(3, playerState.getDeck().size()));
	}
	

	@Test
	public void vassalIntoSentry() {
		
		swapHandCards(ActionService.ESTATE, ActionService.VASSAL);
		loadDeck(ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.SENTRY);
		this.loadDiscard(ActionService.SILVER, ActionService.ESTATE);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.VASSAL);
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		Assertions.assertTrue(playerState.getLooking().getCards().contains("Sentry"));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("PLAY")));
		actionService.doChoice(gameState, "test");
		
		Assertions.assertEquals(2, playerState.getLooking().size());
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(5, playerState.getHand().size()));
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(1, playerState.getTurn().getActionsAvailable()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(6, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("DISCARD : Copper")));
		actionService.doChoice(gameState, "test");

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("TRASH : Copper")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(3, playerState.getDiscard().size()));
		doAssertion(Checked.DECK, () -> Assertions.assertEquals(0, playerState.getDeck().size()));
	}
	
	@Test
	public void bureaucratFollowedByCouncilRoom() {
		swapHandCards(ActionService.BUREAUCRAT, ActionService.COUNCIL_ROOM);
		loadDeck(ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER);
		loadDeck(playerState2, ActionService.COPPER);
		loadDeck(playerState3, ActionService.COPPER);
		swapHandCards(playerState2, ActionService.ESTATE, ActionService.ESTATE);
		swapHandCards(playerState3, ActionService.ESTATE);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.BUREAUCRAT);
		
		//started with 4, gained 1
		doAssertion(Checked.DECK, () -> Assertions.assertEquals(5, playerState.getDeck().getCards().size()));
		
		Assertions.assertEquals(1, playerState2.getTurn().getChoicesAvailable().size());
		Assertions.assertEquals(2, playerState2.getTurn().getChoicesAvailable().get(0).getOptions().size());
		Assertions.assertEquals(1, playerState3.getTurn().getChoicesAvailable().get(0).getOptions().size());
		
		actionService.turnPlay(gameState, "test", ActionService.COUNCIL_ROOM);
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(7, playerState.getHand().getCards().size()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(5, playerState2.getHand().getCards().size()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(5, playerState3.getHand().getCards().size()));
		
		playerState2.getTurn().setChoicesMade(new ArrayList<>(List.of("Estate")));
		actionService.doChoice(gameState, "test2");
		
		playerState3.getTurn().setChoicesMade(new ArrayList<>(List.of("Estate")));
		actionService.doChoice(gameState, "test3");

		doAssertion(Checked.DECK, () -> Assertions.assertEquals(2, playerState.getDeck().getCards().size()));
		Assertions.assertEquals(1, playerState2.getTurn().getChoicesAvailable().get(0).getOptions().size());
		Assertions.assertEquals(0, playerState3.getTurn().getChoicesAvailable().size());
		
		playerState2.getTurn().setChoicesMade(new ArrayList<>(List.of("Estate")));
		actionService.doChoice(gameState, "test2");
		
		Assertions.assertEquals(0, playerState2.getTurn().getChoicesAvailable().size());
		Assertions.assertEquals(0, playerState2.getAttacks().size());
	}
}
