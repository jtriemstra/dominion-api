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
public class ActionServiceAttackReactionTest extends ActionServiceTestBase {


	@Test
	public void guardDogCanBePlayedOnMilitia() {
		swapHandCards(playerState2, ActionService.GUARD_DOG);
		swapHandCards(ActionService.MILITIA);
		loadDeck(playerState2, ActionService.GOLD, ActionService.GOLD, ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.MILITIA);
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(2, playerState2.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		Assertions.assertEquals(ActionService.ATTACK_REACTION, playerState2.getTurn().getChoicesAvailable().get(0).getFollowUpAction());
		Assertions.assertTrue(playerState2.getTurn().getChoicesAvailable().get(0).getOptions().stream().anyMatch(o -> o.getText().equals("Guard Dog")));
		Assertions.assertTrue(playerState2.getTurn().getChoicesAvailable().get(0).getOptions().stream().anyMatch(o -> o.getText().equals("No")));
		
		playerState2.getTurn().setChoicesMade(new ArrayList<>(List.of("Guard Dog")));
		actionService.doChoice(gameState, "test2");
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(6, playerState2.getHand().size()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(2, playerState2.getHand().getCards().stream().filter(c -> c.equals("Gold")).count()));
	}

	@Test
	public void guardDogCanBeSkippedOnMilitia() {
		swapHandCards(playerState2, ActionService.GUARD_DOG);
		swapHandCards(ActionService.MILITIA);
		loadDeck(playerState2, ActionService.GOLD, ActionService.GOLD, ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.MILITIA);
		
		playerState2.getTurn().setChoicesMade(new ArrayList<>(List.of("No")));
		actionService.doChoice(gameState, "test2");
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(5, playerState2.getHand().size()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(1, playerState2.getHand().getCards().stream().filter(c -> c.equals("Guard Dog")).count()));

		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(5, playerState2.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		Assertions.assertEquals(ActionService.MILITIA1, playerState2.getTurn().getChoicesAvailable().get(0).getFollowUpAction());
	}
	
	@Test
	public void guardDogCausesMilitiaToTakeEffectOnSmallHand() {
		swapHandCards(playerState2, ActionService.GUARD_DOG);
		playerState2.getHand().getCards().remove(ActionService.COPPER);
		playerState2.getHand().getCards().remove(ActionService.COPPER);
		swapHandCards(ActionService.MILITIA);
		loadDeck(playerState2, ActionService.GOLD, ActionService.GOLD, ActionService.SILVER, ActionService.SILVER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.MILITIA);
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(2, playerState2.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		Assertions.assertEquals(ActionService.ATTACK_REACTION, playerState2.getTurn().getChoicesAvailable().get(0).getFollowUpAction());
		Assertions.assertTrue(playerState2.getTurn().getChoicesAvailable().get(0).getOptions().stream().anyMatch(o -> o.getText().equals("Guard Dog")));
		Assertions.assertTrue(playerState2.getTurn().getChoicesAvailable().get(0).getOptions().stream().anyMatch(o -> o.getText().equals("No")));
		
		playerState2.getTurn().setChoicesMade(new ArrayList<>(List.of("Guard Dog")));
		actionService.doChoice(gameState, "test2");
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(6, playerState2.getHand().size()));
		doAssertion(Checked.HAND, () -> assertCardsInHand(playerState2, Map.of(ActionService.COPPER, 2, ActionService.SILVER, 2, ActionService.GOLD, 2)));
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(6, playerState2.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(3, playerState2.getTurn().getChoicesAvailable().get(0).getMinChoices()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(3, playerState2.getTurn().getChoicesAvailable().get(0).getMaxChoices()));
	}
	
	@Test
	public void militiaCanBeBlockedByMoat() {
		swapHandCards(playerState3, ActionService.MOAT);
		swapHandCards(ActionService.MILITIA);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.MILITIA);
		
		Assertions.assertEquals(2, playerState3.getTurn().getChoicesAvailable().get(0).getOptions().size());
		Assertions.assertTrue(playerState3.getTurn().getChoicesAvailable().get(0).getOptions().stream().anyMatch(o -> o.getText().equals("Moat")));
		Assertions.assertTrue(playerState3.getTurn().getChoicesAvailable().get(0).getOptions().stream().anyMatch(o -> o.getText().equals("No")));
		Assertions.assertEquals(5, playerState2.getTurn().getChoicesAvailable().get(0).getOptions().size());
		
		playerState3.getTurn().setChoicesMade(new ArrayList<>(List.of("Moat")));
		actionService.doChoice(gameState, "test3");
		
		Assertions.assertEquals(0, playerState3.getTurn().getChoicesAvailable().size());
	}
	
	@Test
	public void militiaCanBeAllowedByMoat() {
		swapHandCards(playerState3, ActionService.MOAT);
		swapHandCards(ActionService.MILITIA);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.MILITIA);
		
		playerState3.getTurn().setChoicesMade(new ArrayList<>(List.of("No")));
		actionService.doChoice(gameState, "test3");
		
		Assertions.assertEquals(5, playerState3.getTurn().getChoicesAvailable().get(0).getOptions().size());
		Assertions.assertEquals(ActionService.MILITIA1, playerState3.getTurn().getChoicesAvailable().get(0).getFollowUpAction());
	}
	
	@Test
	public void militiaCanTriggerTunnel() {
		swapHandCards(playerState3, ActionService.TUNNEL);
		swapHandCards(ActionService.MILITIA);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.MILITIA);
		
		Assertions.assertEquals(5, playerState3.getTurn().getChoicesAvailable().get(0).getOptions().size());
		
		playerState3.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper","Tunnel")));
		actionService.doChoice(gameState, "test3");
		
		Assertions.assertEquals(1, playerState3.getTurn().getChoicesAvailable().size());
		
		playerState3.getTurn().setChoicesMade(new ArrayList<>(List.of("YES")));
		actionService.doChoice(gameState, "test3");
		
		Assertions.assertEquals(0, playerState3.getTurn().getChoicesAvailable().size());
		Assertions.assertEquals(3, playerState3.getHand().size());
		Assertions.assertEquals(3, playerState3.getDiscard().getCards().size());
		Assertions.assertTrue(playerState3.getDiscard().getCards().contains("Gold"));
	}

	@Test
	public void militiaCanSkipTunnel() {
		swapHandCards(playerState3, ActionService.TUNNEL);
		swapHandCards(ActionService.MILITIA);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.MILITIA);
		
		Assertions.assertEquals(5, playerState3.getTurn().getChoicesAvailable().get(0).getOptions().size());
		
		playerState3.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper","Tunnel")));
		actionService.doChoice(gameState, "test3");
		
		Assertions.assertEquals(1, playerState3.getTurn().getChoicesAvailable().size());
		
		playerState3.getTurn().setChoicesMade(new ArrayList<>(List.of("NO")));
		actionService.doChoice(gameState, "test3");
		
		Assertions.assertEquals(0, playerState3.getTurn().getChoicesAvailable().size());
		Assertions.assertEquals(3, playerState3.getHand().size());
		Assertions.assertEquals(2, playerState3.getDiscard().getCards().size());
		Assertions.assertFalse(playerState3.getDiscard().getCards().contains("Gold"));
	}

	@Test
	public void militiaCanTriggerWeaver() {
		swapHandCards(playerState3, ActionService.WEAVER);
		swapHandCards(ActionService.MILITIA);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.MILITIA);
		
		Assertions.assertEquals(5, playerState3.getTurn().getChoicesAvailable().get(0).getOptions().size());
		
		playerState3.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper","Weaver")));
		actionService.doChoice(gameState, "test3");
		
		Assertions.assertEquals(1, playerState3.getTurn().getChoicesAvailable().size());
		
		playerState3.getTurn().setChoicesMade(new ArrayList<>(List.of("PLAY")));
		actionService.doChoice(gameState, "test3");

		Assertions.assertEquals(1, playerState3.getTurn().getChoicesAvailable().size());

		playerState3.getTurn().setChoicesMade(new ArrayList<>(List.of("2 Silver")));
		actionService.doChoice(gameState, "test3");
		
		Assertions.assertEquals(0, playerState3.getTurn().getChoicesAvailable().size());
		Assertions.assertEquals(3, playerState3.getHand().size());
		Assertions.assertEquals(4, playerState3.getDiscard().getCards().size());
		Assertions.assertTrue(playerState3.getDiscard().getCards().contains("Silver"));
		Assertions.assertTrue(playerState3.getDiscard().getCards().contains("Weaver"));
	}
	
	@Test
	public void militiaCanSkipWeaver() {
		swapHandCards(playerState3, ActionService.WEAVER);
		swapHandCards(ActionService.MILITIA);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.MILITIA);
		
		Assertions.assertEquals(5, playerState3.getTurn().getChoicesAvailable().get(0).getOptions().size());
		
		playerState3.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper","Weaver")));
		actionService.doChoice(gameState, "test3");
		
		Assertions.assertEquals(1, playerState3.getTurn().getChoicesAvailable().size());
		
		playerState3.getTurn().setChoicesMade(new ArrayList<>(List.of("DISCARD")));
		actionService.doChoice(gameState, "test3");

		Assertions.assertEquals(0, playerState3.getTurn().getChoicesAvailable().size());
		
		Assertions.assertEquals(3, playerState3.getHand().size());
		Assertions.assertEquals(2, playerState3.getDiscard().getCards().size());
		Assertions.assertTrue(playerState3.getDiscard().getCards().contains("Weaver"));
	}

	@Test
	public void militiaCanTriggerTrail() {
		swapHandCards(playerState3, ActionService.TRAIL);
		swapHandCards(ActionService.MILITIA);
		loadDeck(playerState3, ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.MILITIA);
		
		Assertions.assertEquals(5, playerState3.getTurn().getChoicesAvailable().get(0).getOptions().size());
		
		playerState3.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper","Trail")));
		actionService.doChoice(gameState, "test3");
		
		Assertions.assertEquals(1, playerState3.getTurn().getChoicesAvailable().size());
		
		playerState3.getTurn().setChoicesMade(new ArrayList<>(List.of("PLAY")));
		actionService.doChoice(gameState, "test3");
		
		Assertions.assertEquals(4, playerState3.getHand().size());
		Assertions.assertEquals(2, playerState3.getDiscard().getCards().size());		
	}
	
	@Test
	public void militiaCanSkipTrail() {
		swapHandCards(playerState3, ActionService.TRAIL);
		swapHandCards(ActionService.MILITIA);
		loadDeck(ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.MILITIA);
		
		Assertions.assertEquals(5, playerState3.getTurn().getChoicesAvailable().get(0).getOptions().size());
		
		playerState3.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper","Trail")));
		actionService.doChoice(gameState, "test3");
		
		Assertions.assertEquals(1, playerState3.getTurn().getChoicesAvailable().size());
		
		playerState3.getTurn().setChoicesMade(new ArrayList<>(List.of("DISCARD")));
		actionService.doChoice(gameState, "test3");

		Assertions.assertEquals(0, playerState3.getTurn().getChoicesAvailable().size());
		
		Assertions.assertEquals(3, playerState3.getHand().size());
		Assertions.assertEquals(2, playerState3.getDiscard().getCards().size());
	}
	
	@Test
	public void bureaucratCanBeBlockedByMoat() {
		swapHandCards(playerState3, ActionService.MOAT, ActionService.ESTATE);
		swapHandCards(ActionService.BUREAUCRAT);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.BUREAUCRAT);
		
		Assertions.assertEquals(2, playerState3.getTurn().getChoicesAvailable().get(0).getOptions().size());
		Assertions.assertTrue(playerState3.getTurn().getChoicesAvailable().get(0).getOptions().stream().anyMatch(o -> o.getText().equals("Moat")));
		Assertions.assertTrue(playerState3.getTurn().getChoicesAvailable().get(0).getOptions().stream().anyMatch(o -> o.getText().equals("No")));
		
		playerState3.getTurn().setChoicesMade(new ArrayList<>(List.of("Moat")));
		actionService.doChoice(gameState, "test3");
		
		Assertions.assertEquals(0, playerState3.getTurn().getChoicesAvailable().size());

		Assertions.assertEquals(0, playerState3.getDeck().getCards().size());
		Assertions.assertEquals(5, playerState3.getHand().getCards().size());
	}
	
	@Test
	public void bureaucratCanBeAllowedByMoat() {
		swapHandCards(playerState3, ActionService.MOAT, ActionService.ESTATE);
		swapHandCards(ActionService.BUREAUCRAT);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.BUREAUCRAT);
		
		playerState3.getTurn().setChoicesMade(new ArrayList<>(List.of("No")));
		actionService.doChoice(gameState, "test3");
		
		Assertions.assertEquals(1, playerState3.getTurn().getChoicesAvailable().get(0).getOptions().size());
		Assertions.assertEquals(ActionService.BUREAUCRAT1, playerState3.getTurn().getChoicesAvailable().get(0).getFollowUpAction());
		
		playerState3.getTurn().setChoicesMade(new ArrayList<>(List.of("Estate")));
		actionService.doChoice(gameState, "test3");
		
		Assertions.assertEquals(1, playerState3.getDeck().getCards().size());
		Assertions.assertEquals(4, playerState3.getHand().getCards().size());
	}

	@Test
	public void guardDogCanBePlayedOnBureaucrat() {
		swapHandCards(playerState2, ActionService.GUARD_DOG);
		swapHandCards(ActionService.BUREAUCRAT);
		loadDeck(playerState2, ActionService.GOLD, ActionService.ESTATE);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.BUREAUCRAT);
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(2, playerState2.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		Assertions.assertEquals(ActionService.ATTACK_REACTION, playerState2.getTurn().getChoicesAvailable().get(0).getFollowUpAction());
		Assertions.assertTrue(playerState2.getTurn().getChoicesAvailable().get(0).getOptions().stream().anyMatch(o -> o.getText().equals("Guard Dog")));
		Assertions.assertTrue(playerState2.getTurn().getChoicesAvailable().get(0).getOptions().stream().anyMatch(o -> o.getText().equals("No")));
		
		playerState2.getTurn().setChoicesMade(new ArrayList<>(List.of("Guard Dog")));
		actionService.doChoice(gameState, "test2");
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(6, playerState2.getHand().size()));
		doAssertion(Checked.HAND, () -> assertCardsInHand(playerState2, Map.of(ActionService.COPPER, 4, ActionService.ESTATE, 1, ActionService.GOLD, 1)));

		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState2.getTurn().getChoicesAvailable().get(0).getOptions().size()));

		playerState2.getTurn().setChoicesMade(new ArrayList<>(List.of("Estate")));
		actionService.doChoice(gameState, "test2");
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(5, playerState2.getHand().size()));
		doAssertion(Checked.HAND, () -> assertCardsInHand(playerState2, Map.of(ActionService.COPPER, 4, ActionService.GOLD, 1)));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(1, playerState2.getDeck().getCards().size()));
	}

	@Test
	public void guardDogCanBeSkippedOnBureaucrat() {
		swapHandCards(playerState2, ActionService.GUARD_DOG, ActionService.ESTATE);
		swapHandCards(ActionService.BUREAUCRAT);
		loadDeck(playerState2, ActionService.GOLD, ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.BUREAUCRAT);
		
		playerState2.getTurn().setChoicesMade(new ArrayList<>(List.of("No")));
		actionService.doChoice(gameState, "test2");
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(5, playerState2.getHand().size()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(1, playerState2.getHand().getCards().stream().filter(c -> c.equals("Guard Dog")).count()));

		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState2.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		Assertions.assertEquals(ActionService.BUREAUCRAT1, playerState2.getTurn().getChoicesAvailable().get(0).getFollowUpAction());
	}

	@Test
	public void margraveCanBeBlockedByMoat() {
		swapHandCards(playerState3, ActionService.MOAT, ActionService.ESTATE);
		swapHandCards(ActionService.MARGRAVE);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.GOLD);
		loadDeck(playerState2, ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.MARGRAVE);
		
		Assertions.assertEquals(2, playerState3.getTurn().getChoicesAvailable().get(0).getOptions().size());
		Assertions.assertTrue(playerState3.getTurn().getChoicesAvailable().get(0).getOptions().stream().anyMatch(o -> o.getText().equals("Moat")));
		Assertions.assertTrue(playerState3.getTurn().getChoicesAvailable().get(0).getOptions().stream().anyMatch(o -> o.getText().equals("No")));
		
		playerState3.getTurn().setChoicesMade(new ArrayList<>(List.of("Moat")));
		actionService.doChoice(gameState, "test3");
		
		Assertions.assertEquals(0, playerState3.getTurn().getChoicesAvailable().size());

		Assertions.assertEquals(0, playerState3.getDeck().getCards().size());
		Assertions.assertEquals(5, playerState3.getHand().getCards().size());
	}
	
	@Test
	public void margraveCanBeAllowedByMoat() {
		swapHandCards(playerState3, ActionService.MOAT, ActionService.ESTATE);
		swapHandCards(ActionService.MARGRAVE);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.GOLD);
		loadDeck(playerState2, ActionService.GOLD);
		loadDeck(playerState3, ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.MARGRAVE);
		
		playerState3.getTurn().setChoicesMade(new ArrayList<>(List.of("No")));
		actionService.doChoice(gameState, "test3");
		
		Assertions.assertEquals(6, playerState3.getTurn().getChoicesAvailable().get(0).getOptions().size());
		Assertions.assertEquals(ActionService.MARGRAVE2, playerState3.getTurn().getChoicesAvailable().get(0).getFollowUpAction());
		
		playerState3.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper","Estate","Copper")));
		actionService.doChoice(gameState, "test3");
		
		Assertions.assertEquals(0, playerState3.getDeck().getCards().size());
		Assertions.assertEquals(3, playerState3.getHand().getCards().size());
		Assertions.assertEquals(3, playerState3.getDiscard().getCards().size());
	}
	
	@Test
	public void guardDogCanBePlayedOnMargrave() {
		swapHandCards(playerState2, ActionService.GUARD_DOG);
		swapHandCards(ActionService.MARGRAVE);
		loadDeck(playerState2, ActionService.GOLD, ActionService.GOLD, ActionService.GOLD);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.GOLD);
		loadDeck(playerState3, ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.MARGRAVE);
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(2, playerState2.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		Assertions.assertEquals(ActionService.ATTACK_REACTION, playerState2.getTurn().getChoicesAvailable().get(0).getFollowUpAction());
		Assertions.assertTrue(playerState2.getTurn().getChoicesAvailable().get(0).getOptions().stream().anyMatch(o -> o.getText().equals("Guard Dog")));
		Assertions.assertTrue(playerState2.getTurn().getChoicesAvailable().get(0).getOptions().stream().anyMatch(o -> o.getText().equals("No")));
		
		playerState2.getTurn().setChoicesMade(new ArrayList<>(List.of("Guard Dog")));
		actionService.doChoice(gameState, "test2");
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(7, playerState2.getHand().size()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(3, playerState2.getHand().getCards().stream().filter(c -> c.equals("Gold")).count()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(7, playerState2.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(4, playerState2.getTurn().getChoicesAvailable().get(0).getMinChoices()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(4, playerState2.getTurn().getChoicesAvailable().get(0).getMaxChoices()));
	}

	@Test
	public void guardDogCanBeSkippedOnMargrave() {
		swapHandCards(playerState2, ActionService.GUARD_DOG);
		swapHandCards(ActionService.MARGRAVE);
		loadDeck(playerState2, ActionService.GOLD);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.GOLD);
		loadDeck(playerState3, ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.MARGRAVE);
		
		playerState2.getTurn().setChoicesMade(new ArrayList<>(List.of("No")));
		actionService.doChoice(gameState, "test2");
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(6, playerState2.getHand().size()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(1, playerState2.getHand().getCards().stream().filter(c -> c.equals("Guard Dog")).count()));

		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(6, playerState2.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		Assertions.assertEquals(ActionService.MARGRAVE2, playerState2.getTurn().getChoicesAvailable().get(0).getFollowUpAction());
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(3, playerState2.getTurn().getChoicesAvailable().get(0).getMinChoices()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(3, playerState2.getTurn().getChoicesAvailable().get(0).getMaxChoices()));
	}
	

	@Test
	public void militiaTriggersMoatOnSmallHand() {
		swapHandCards(playerState2, ActionService.MOAT);
		playerState2.getHand().getCards().remove(ActionService.COPPER);
		playerState2.getHand().getCards().remove(ActionService.COPPER);
		swapHandCards(ActionService.MILITIA);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.MILITIA);
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(2, playerState2.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		Assertions.assertEquals(ActionService.ATTACK_REACTION, playerState2.getTurn().getChoicesAvailable().get(0).getFollowUpAction());
		Assertions.assertTrue(playerState2.getTurn().getChoicesAvailable().get(0).getOptions().stream().anyMatch(o -> o.getText().equals("Moat")));
		Assertions.assertTrue(playerState2.getTurn().getChoicesAvailable().get(0).getOptions().stream().anyMatch(o -> o.getText().equals("No")));
		
	}
}
