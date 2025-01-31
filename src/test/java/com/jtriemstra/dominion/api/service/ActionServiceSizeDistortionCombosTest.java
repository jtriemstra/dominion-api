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
public class ActionServiceSizeDistortionCombosTest extends ActionServiceTestBase {

	@Test
	public void throneRoomPlusWitch() {
		swapBankSupplies(Map.of(ActionService.DUCHY, ActionService.CURSE));
		swapHandCards(ActionService.THRONE_ROOM, ActionService.WITCH);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.SILVER, ActionService.SILVER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.THRONE_ROOM);
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		Assertions.assertTrue(playerState.getTurn().getChoicesAvailable().get(0).getOptions().stream().anyMatch(o -> o.getText().equals("Witch")));
				
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Witch")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(7, playerState.getHand().size()));
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.COPPER, 3, ActionService.SILVER, 2, ActionService.GOLD, 2)));
		
		Assertions.assertEquals(2, playerState2.getDiscard().getCards().size());
		Assertions.assertEquals(2, playerState3.getDiscard().getCards().size());
	}
	
	@Test
	public void throneRoomPlusFestival() {
		swapHandCards(ActionService.THRONE_ROOM, ActionService.FESTIVAL);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.THRONE_ROOM);
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		Assertions.assertTrue(playerState.getTurn().getChoicesAvailable().get(0).getOptions().stream().anyMatch(o -> o.getText().equals("Festival")));
				
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Festival")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(3, playerState.getHand().size()));
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(4, playerState.getTurn().getActionsAvailable()));
		doAssertion(Checked.BUY, () -> Assertions.assertEquals(3, playerState.getTurn().getBuys()));
		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(4, playerState.getTurn().getTreasure()));
	}
	
	@Test
	public void throneRoomPlusArtisan() {
		swapHandCards(ActionService.THRONE_ROOM, ActionService.ARTISAN);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.THRONE_ROOM);

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Artisan")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(5, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		Assertions.assertEquals(ActionService.ARTISAN1, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction());
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Duchy")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(4, playerState.getHand().size()));
		doAssertion(Checked.HAND, () -> Assertions.assertTrue(playerState.getHand().getCards().contains("Duchy")));
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(4, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		Assertions.assertEquals(ActionService.ARTISAN2, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction());

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Duchy")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.HAND, () -> Assertions.assertEquals(3, playerState.getHand().size()));
		doAssertion(Checked.HAND, () -> Assertions.assertFalse(playerState.getHand().getCards().contains("Duchy")));
		doAssertion(Checked.HAND, () -> Assertions.assertTrue(playerState.getDeck().getCards().contains("Duchy")));

		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(5, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		Assertions.assertEquals(ActionService.ARTISAN1, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction());

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Embassy")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.HAND, () -> Assertions.assertEquals(4, playerState.getHand().size()));
		doAssertion(Checked.HAND, () -> Assertions.assertTrue(playerState.getHand().getCards().contains("Embassy")));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.HAND, () -> Assertions.assertEquals(3, playerState.getHand().size()));
		doAssertion(Checked.HAND, () -> Assertions.assertTrue(playerState.getHand().getCards().contains("Embassy")));
		
		Assertions.assertEquals(0, playerState.getTurn().getActionsAvailable());
		Assertions.assertEquals(0, playerState.getTurn().getChoicesAvailable().size());
	}
	
	@Test
	public void throneRoomPlusChapel() {
		swapHandCards(ActionService.THRONE_ROOM, ActionService.CHAPEL);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.THRONE_ROOM);

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Chapel")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(3, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper","Copper")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(0, playerState.getHand().size()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(0, playerState.getDiscard().getCards().size()));
	}
	
	@Test
	public void throneRoomPlusWorkshop() {
		swapHandCards(ActionService.THRONE_ROOM, ActionService.WORKSHOP);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.THRONE_ROOM);

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Workshop")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(3, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Silver")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(3, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Silver")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(3, playerState.getHand().size()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(2, playerState.getDiscard().getCards().size()));
	}
	
	@Test
	public void throneRoomPlusBureaucrat() {
		swapHandCards(ActionService.THRONE_ROOM, ActionService.BUREAUCRAT);
		swapHandCards(playerState2, ActionService.ESTATE, ActionService.ESTATE);
		swapHandCards(playerState3, ActionService.ESTATE);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.THRONE_ROOM);

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Bureaucrat")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.DECK, () -> Assertions.assertEquals(2, playerState.getDeck().getCards().size()));

		Assertions.assertEquals(2, playerState2.getAttacks().getAttacks().size());
		Assertions.assertEquals(2, playerState3.getAttacks().getAttacks().size());
		
		Assertions.assertEquals(1, playerState2.getTurn().getChoicesAvailable().size());
		Assertions.assertEquals(2, playerState2.getTurn().getChoicesAvailable().get(0).getOptions().size());
		Assertions.assertEquals(1, playerState3.getTurn().getChoicesAvailable().get(0).getOptions().size());
		
		playerState2.getTurn().setChoicesMade(new ArrayList<>(List.of("Estate")));
		actionService.doChoice(gameState, "test2");
		
		playerState3.getTurn().setChoicesMade(new ArrayList<>(List.of("Estate")));
		actionService.doChoice(gameState, "test3");

		Assertions.assertEquals(1, playerState2.getAttacks().getAttacks().size());
		Assertions.assertEquals(0, playerState3.getAttacks().getAttacks().size());
		
		doAssertion(Checked.DECK, () -> Assertions.assertEquals(2, playerState.getDeck().getCards().size()));
		Assertions.assertEquals(1, playerState2.getTurn().getChoicesAvailable().get(0).getOptions().size());
		Assertions.assertEquals(0, playerState3.getTurn().getChoicesAvailable().size());
		
		playerState2.getTurn().setChoicesMade(new ArrayList<>(List.of("Estate")));
		actionService.doChoice(gameState, "test2");
	}

	@Test
	public void twoBureaucrats() {
		playerState.getTurn().setActionsAvailable(2);
		swapHandCards(ActionService.BUREAUCRAT, ActionService.BUREAUCRAT);
		swapHandCards(playerState2, ActionService.ESTATE, ActionService.ESTATE);
		swapHandCards(playerState3, ActionService.ESTATE);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.BUREAUCRAT);
		
		doAssertion(Checked.DECK, () -> Assertions.assertEquals(1, playerState.getDeck().getCards().size()));
		
		Assertions.assertEquals(1, playerState2.getTurn().getChoicesAvailable().size());
		Assertions.assertEquals(2, playerState2.getTurn().getChoicesAvailable().get(0).getOptions().size());
		Assertions.assertEquals(1, playerState3.getTurn().getChoicesAvailable().get(0).getOptions().size());
		
		actionService.turnPlay(gameState, "test", ActionService.BUREAUCRAT);
		
		doAssertion(Checked.DECK, () -> Assertions.assertEquals(2, playerState.getDeck().getCards().size()));
		
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
	
	@Test
	public void throneRoomPlusSentry() {
		swapHandCards(ActionService.THRONE_ROOM, ActionService.SENTRY);
		loadDeck(ActionService.ESTATE, ActionService.SILVER, ActionService.SILVER, ActionService.DUCHY, ActionService.WITCH);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.THRONE_ROOM);

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Sentry")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(4, playerState.getHand().size()));
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.COPPER, 3, ActionService.WITCH, 1)));
		Assertions.assertTrue(playerState.getTurn().getChoicesAvailable().get(0).getOptions().stream().anyMatch(o -> o.getText().equals(("DISCARD : Duchy"))));
		Assertions.assertTrue(playerState.getTurn().getChoicesAvailable().get(0).getOptions().stream().anyMatch(o -> o.getText().equals("DECK : Silver")));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("DISCARD : Duchy")));
		actionService.doChoice(gameState, "test");
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("DECK : Silver")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.HAND, () -> Assertions.assertEquals(5, playerState.getHand().size()));
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.COPPER, 3, ActionService.WITCH, 1, ActionService.SILVER, 1)));
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(2, playerState.getTurn().getActionsAvailable()));
	}
	
	@Test
	public void throneRoomPlusBandit() {
		swapHandCards(ActionService.THRONE_ROOM, ActionService.BANDIT);
		loadDeck(playerState2, ActionService.ESTATE, ActionService.COPPER, ActionService.SILVER, ActionService.DUCHY);
		loadDeck(playerState3, ActionService.GOLD, ActionService.COPPER, ActionService.SILVER, ActionService.DUCHY);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.THRONE_ROOM);

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Bandit")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(2, playerState.getDiscard().getCards().size()));
 
		Assertions.assertEquals(2, playerState2.getAttacks().size());
		Assertions.assertEquals(1, playerState2.getTurn().getChoicesAvailable().size());
		Assertions.assertEquals(1, playerState2.getTurn().getChoicesAvailable().get(0).getOptions().size());
		Assertions.assertEquals(1, playerState3.getTurn().getChoicesAvailable().size());
		Assertions.assertEquals(1, playerState3.getTurn().getChoicesAvailable().get(0).getOptions().size());
		
		playerState2.getTurn().setChoicesMade(new ArrayList<>(List.of("Silver")));
		actionService.doChoice(gameState, "test2");
		playerState3.getTurn().setChoicesMade(new ArrayList<>(List.of("Silver")));
		actionService.doChoice(gameState, "test3");
		
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(2, playerState.getDiscard().getCards().size()));
		Assertions.assertEquals(1, playerState2.getTurn().getChoicesAvailable().size());
		Assertions.assertEquals(0, playerState2.getTurn().getChoicesAvailable().get(0).getOptions().size());
		Assertions.assertEquals(1, playerState3.getTurn().getChoicesAvailable().size());
		Assertions.assertEquals(1, playerState3.getTurn().getChoicesAvailable().get(0).getOptions().size());
		
		playerState2.getTurn().setChoicesMade(new ArrayList<>(List.of("")));
		actionService.doChoice(gameState, "test2");
		playerState3.getTurn().setChoicesMade(new ArrayList<>(List.of("Gold")));
		actionService.doChoice(gameState, "test3");
		
		Assertions.assertEquals(3, playerState2.getDiscard().getCards().size());
		Assertions.assertEquals(2, playerState3.getDiscard().getCards().size());
	}
	
	@Test
	public void twoBandits() {
		playerState.getTurn().setActionsAvailable(2);
		swapHandCards(ActionService.BANDIT, ActionService.BANDIT);
		loadDeck(playerState2, ActionService.ESTATE, ActionService.COPPER, ActionService.SILVER, ActionService.DUCHY);
		loadDeck(playerState3, ActionService.GOLD, ActionService.COPPER, ActionService.SILVER, ActionService.DUCHY);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.BANDIT);
		
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(1, playerState.getDiscard().getCards().size()));

		actionService.turnPlay(gameState, "test", ActionService.BANDIT);
		
		Assertions.assertEquals(2, playerState2.getAttacks().size());
		Assertions.assertEquals(1, playerState2.getTurn().getChoicesAvailable().size());
		Assertions.assertEquals(1, playerState2.getTurn().getChoicesAvailable().get(0).getOptions().size());
		Assertions.assertEquals(1, playerState3.getTurn().getChoicesAvailable().size());
		Assertions.assertEquals(1, playerState3.getTurn().getChoicesAvailable().get(0).getOptions().size());
		
		playerState2.getTurn().setChoicesMade(new ArrayList<>(List.of("Silver")));
		actionService.doChoice(gameState, "test2");
		playerState3.getTurn().setChoicesMade(new ArrayList<>(List.of("Silver")));
		actionService.doChoice(gameState, "test3");
		
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(2, playerState.getDiscard().getCards().size()));
		Assertions.assertEquals(1, playerState2.getTurn().getChoicesAvailable().size());
		Assertions.assertEquals(0, playerState2.getTurn().getChoicesAvailable().get(0).getOptions().size());
		Assertions.assertEquals(1, playerState3.getTurn().getChoicesAvailable().size());
		Assertions.assertEquals(1, playerState3.getTurn().getChoicesAvailable().get(0).getOptions().size());
		
		playerState2.getTurn().setChoicesMade(new ArrayList<>(List.of("")));
		actionService.doChoice(gameState, "test2");
		playerState3.getTurn().setChoicesMade(new ArrayList<>(List.of("Gold")));
		actionService.doChoice(gameState, "test3");
		
		Assertions.assertEquals(3, playerState2.getDiscard().getCards().size());
		Assertions.assertEquals(2, playerState3.getDiscard().getCards().size());
		
		Assertions.assertEquals(0, playerState2.getTurn().getChoicesAvailable().size());
		Assertions.assertEquals(0, playerState2.getAttacks().size());
	}
	
	@Test
	public void festivalThroneRoomArtisanGainAction() {
		swapBankSupplies(Map.of(ActionService.DUCHY, ActionService.SENTRY, ActionService.BORDER_VILLAGE, ActionService.BANDIT));
		swapHandCards(ActionService.THRONE_ROOM, ActionService.FESTIVAL, ActionService.ARTISAN);
		loadDeck(ActionService.GOLD);
		loadDeck(playerState2, ActionService.COPPER, ActionService.COPPER);
		loadDeck(playerState3, ActionService.COPPER, ActionService.COPPER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.FESTIVAL);
		actionService.turnPlay(gameState, "test", ActionService.THRONE_ROOM);
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Artisan")));
		actionService.doChoice(gameState, "test");
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Sentry")));
		actionService.doChoice(gameState, "test");
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper")));
		actionService.doChoice(gameState, "test");
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Bandit")));
		actionService.doChoice(gameState, "test");
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper")));
		actionService.doChoice(gameState, "test");
		actionService.turnPlay(gameState, "test", ActionService.SENTRY);
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("DISCARD : Copper")));
		actionService.doChoice(gameState, "test");
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("DECK : Gold")));
		actionService.doChoice(gameState, "test");
		actionService.turnPlay(gameState, "test", ActionService.BANDIT);
		
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(2, playerState.getDiscard().getCards().size()));
	}

	@Test
	public void twoBureaucrat() {
		playerState.getTurn().setActionsAvailable(2);
		swapHandCards(ActionService.BUREAUCRAT, ActionService.BUREAUCRAT);
		swapHandCards(playerState2, ActionService.ESTATE, ActionService.ESTATE);
		swapHandCards(playerState3, ActionService.ESTATE);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.BUREAUCRAT);
		
		// first BUREAUCRAT triggers a choice
		Assertions.assertEquals(1, playerState2.getTurn().getChoicesAvailable().size());
		Assertions.assertEquals(2, playerState2.getTurn().getChoicesAvailable().get(0).getOptions().size());
		Assertions.assertEquals(1, playerState3.getTurn().getChoicesAvailable().get(0).getOptions().size());
		
		actionService.turnPlay(gameState, "test", ActionService.BUREAUCRAT);
		
		// second BUREAUCRAT doesn't impact the choices immediately
		Assertions.assertEquals(2, playerState2.getTurn().getChoicesAvailable().get(0).getOptions().size());
		Assertions.assertEquals(1, playerState2.getTurn().getChoicesAvailable().size());
		Assertions.assertEquals(2, playerState2.getAttacks().size());
		Assertions.assertEquals(2, playerState3.getAttacks().size());	

		playerState2.getTurn().setChoicesMade(new ArrayList<>(List.of("Estate")));
		actionService.doChoice(gameState, "test2");

		playerState3.getTurn().setChoicesMade(new ArrayList<>(List.of("Estate")));
		actionService.doChoice(gameState, "test3");
		
		Assertions.assertEquals(1, playerState2.getAttacks().size());
		// player 3 auto-moves past the attack because they have no reaction cards in hand. a reaction card should trigger the attack possibility
		Assertions.assertEquals(0, playerState3.getAttacks().size());
		Assertions.assertEquals(1, playerState2.getTurn().getChoicesAvailable().size());
		Assertions.assertEquals(1, playerState2.getTurn().getChoicesAvailable().get(0).getOptions().size());
		Assertions.assertEquals(1, playerState2.getTurn().getChoicesAvailable().get(0).getMinChoices());
		Assertions.assertEquals(1, playerState2.getTurn().getChoicesAvailable().get(0).getMaxChoices());

		playerState2.getTurn().setChoicesMade(new ArrayList<>(List.of("Estate")));
		actionService.doChoice(gameState, "test2");

		Assertions.assertEquals(0, playerState2.getAttacks().size());
		Assertions.assertEquals(0, playerState2.getTurn().getChoicesAvailable().size());
	}

	@Test
	public void dualThroneRoomPlusOneBureaucrat() {
		swapHandCards(ActionService.THRONE_ROOM, ActionService.THRONE_ROOM, ActionService.BUREAUCRAT);
		swapHandCards(playerState2, ActionService.ESTATE, ActionService.ESTATE);
		swapHandCards(playerState3, ActionService.ESTATE);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.CELLAR, ActionService.ESTATE);
		loadDiscard(ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.THRONE_ROOM);
						
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Throne Room")));
		actionService.doChoice(gameState, "test");
		
		// first play of second throne room = 2 Bureaus
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Bureaucrat")));
		actionService.doChoice(gameState, "test");		
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(2, playerState.getHand().size()));
		doAssertion(Checked.DECK, () -> Assertions.assertEquals(6, playerState.getDeck().size()));
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(0, playerState.getTurn().getActionsAvailable()));
		
		// no second TR choice with no action cards left
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(0, playerState.getTurn().getChoicesAvailable().size()));

		Assertions.assertEquals(2, playerState2.getAttacks().getAttacks().size());
		Assertions.assertEquals(2, playerState3.getAttacks().getAttacks().size());
	}

	@Test
	public void dualThroneRoomPlusDualBureaucrat() {
		swapHandCards(ActionService.THRONE_ROOM, ActionService.THRONE_ROOM, ActionService.BUREAUCRAT, ActionService.BUREAUCRAT);
		swapHandCards(playerState2, ActionService.ESTATE, ActionService.ESTATE);
		swapHandCards(playerState3, ActionService.ESTATE);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.CELLAR, ActionService.ESTATE);
		loadDiscard(ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.THRONE_ROOM);
						
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Throne Room")));
		actionService.doChoice(gameState, "test");
		
		// first play of second throne room = 2 Bureaus
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Bureaucrat")));
		actionService.doChoice(gameState, "test");		
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(2, playerState.getHand().size()));
		doAssertion(Checked.DECK, () -> Assertions.assertEquals(6, playerState.getDeck().size()));
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));

		// second play of second throne room = 2 Bureaus
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Bureaucrat")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(1, playerState.getHand().size()));
		doAssertion(Checked.DECK, () -> Assertions.assertEquals(8, playerState.getDeck().size()));
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(0, playerState.getTurn().getChoicesAvailable().size()));
		
		Assertions.assertEquals(4, playerState2.getAttacks().getAttacks().size());
		Assertions.assertEquals(4, playerState3.getAttacks().getAttacks().size());
	}

	@Test
	public void throneRoomPlusBureaucratSkipsAttackWhenNoVictoryCards() {
		swapHandCards(ActionService.THRONE_ROOM, ActionService.BUREAUCRAT);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.THRONE_ROOM);

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Bureaucrat")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.DECK, () -> Assertions.assertEquals(2, playerState.getDeck().getCards().size()));

		Assertions.assertEquals(0, playerState2.getAttacks().getAttacks().size());
		Assertions.assertEquals(0, playerState3.getAttacks().getAttacks().size());
		
		Assertions.assertEquals(0, playerState2.getTurn().getChoicesAvailable().size());
		Assertions.assertEquals(0, playerState3.getTurn().getChoicesAvailable().size());

	}
}
