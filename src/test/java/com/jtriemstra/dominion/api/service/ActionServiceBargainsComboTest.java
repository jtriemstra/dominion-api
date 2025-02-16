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
public class ActionServiceBargainsComboTest extends ActionServiceTestBase {
	
	@Test
	public void wheelwrightDiscardsTrailToPlayItThenGainAnotherAndPlayIt() {
		swapHandCards(ActionService.WHEELWRIGHT, ActionService.TRAIL);
		loadDeck(ActionService.SILVER, ActionService.SILVER, ActionService.SILVER);
		stashGameState();

		actionService.turnPlay(gameState, "test", ActionService.WHEELWRIGHT);
		
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(5 + 1, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(5, playerState.getHand().size()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Trail")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(ActionService.TRAIL1, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("PLAY")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(5, playerState.getHand().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(ActionService.WHEELWRIGHT2, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Trail")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(ActionService.TRAIL1, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("PLAY1")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(6, playerState.getHand().size()));
	}
	
	@Test
	public void wheelwrightWithTraderCanGainSilver() {
		swapBankSupplies(Map.of(ActionService.DUCHY, ActionService.SCHEME));
		swapHandCards(ActionService.WHEELWRIGHT, ActionService.TRADER, ActionService.SCHEME);
		loadDeck(ActionService.SILVER, ActionService.SILVER, ActionService.SILVER);
		stashGameState();

		actionService.turnPlay(gameState, "test", ActionService.WHEELWRIGHT);
		
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(5 + 1, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(5, playerState.getHand().size()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Scheme")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(ActionService.WHEELWRIGHT2, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Scheme")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(ActionService.TRADER1, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Silver")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals("Silver", playerState.getDiscard().getCards().get(0)));

	}
	
	@Test
	public void wheelwrightWithTraderCanGainOriginalChoice() {
		swapBankSupplies(Map.of(ActionService.DUCHY, ActionService.SCHEME));
		swapHandCards(ActionService.WHEELWRIGHT, ActionService.TRADER, ActionService.SCHEME);
		loadDeck(ActionService.SILVER, ActionService.SILVER, ActionService.SILVER);
		stashGameState();

		actionService.turnPlay(gameState, "test", ActionService.WHEELWRIGHT);
		
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(5 + 1, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(5, playerState.getHand().size()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Scheme")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(ActionService.WHEELWRIGHT2, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Scheme")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(ActionService.TRADER1, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Scheme")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals("Scheme", playerState.getDiscard().getCards().get(0)));

	}
	
	@Test
	public void withTraderGainingBorderVillageCanGainSilver() {
		swapHandCards(ActionService.GOLD, ActionService.TRADER, ActionService.GOLD);
		loadDeck(ActionService.SILVER, ActionService.SILVER, ActionService.SILVER);
		stashGameState();

		actionService.turnPlay(gameState, "test", ActionService.GOLD);
		actionService.turnPlay(gameState, "test", ActionService.GOLD);
		actionService.doBuy(gameState, "test", ActionService.BORDER_VILLAGE);
		
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(ActionService.TRADER1, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Border Village")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(ActionService.BORDER_VILLAGE2, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(ActionService.TRADER1, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Silver")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals("Silver", playerState.getDiscard().getCards().get(0)));
	}
	
	@Test
	public void withTraderGainingTrailAndSwappingSilverThenCantPlayTrail() {
		swapHandCards(ActionService.GOLD, ActionService.TRADER);
		loadDeck(ActionService.SILVER, ActionService.SILVER, ActionService.SILVER);
		stashGameState();

		actionService.turnPlay(gameState, "test", ActionService.GOLD);
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		actionService.doBuy(gameState, "test", ActionService.TRAIL);
		
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(ActionService.TRADER1, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Silver")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(0, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals("Silver", playerState.getDiscard().getCards().get(0)));
	}
	
	@Test
	public void withTraderAndHagglerCanGainTwoSilver() {
		swapBankSupplies(Map.of(ActionService.DUCHY, ActionService.ESTATE));
		swapHandCards(ActionService.HAGGLER, ActionService.TRADER);
		loadDeck(ActionService.SILVER, ActionService.SILVER, ActionService.SILVER);
		stashGameState();

		actionService.turnPlay(gameState, "test", ActionService.HAGGLER);
		actionService.doBuy(gameState, "test", ActionService.ESTATE);
		
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(ActionService.HAGGLER2, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(ActionService.TRADER1, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Silver")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(ActionService.TRADER1, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Silver")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals("Silver", playerState.getDiscard().getCards().get(0)));
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals("Silver", playerState.getDiscard().getCards().get(1)));
	}
	
	@Test
	public void withHagglerBuyingBorderVillageGainsTwoExtra() {
		swapHandCards(ActionService.HAGGLER, ActionService.GOLD);
		loadDeck(ActionService.SILVER, ActionService.SILVER, ActionService.SILVER);
		stashGameState();

		actionService.turnPlay(gameState, "test", ActionService.HAGGLER);
		actionService.turnPlay(gameState, "test", ActionService.GOLD);
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		actionService.doBuy(gameState, "test", ActionService.BORDER_VILLAGE);
		
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(ActionService.HAGGLER2, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Silver")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(ActionService.BORDER_VILLAGE2, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Silver")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals("Silver", playerState.getDiscard().getCards().get(0)));
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals("Border Village", playerState.getDiscard().getCards().get(1)));
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals("Silver", playerState.getDiscard().getCards().get(2)));
	}
	
	@Test
	public void withHagglerWheelwrightGainDoesNotTriggerExtraGain() {
		swapBankSupplies(Map.of(ActionService.DUCHY, ActionService.SCHEME));
		swapHandCards(ActionService.HAGGLER, ActionService.WHEELWRIGHT, ActionService.SILVER);
		loadDeck(ActionService.COPPER);
		stashGameState();
		
		playerState.getTurn().setActionsAvailable(2);
		actionService.turnPlay(gameState, "test", ActionService.HAGGLER);
		actionService.turnPlay(gameState, "test", ActionService.WHEELWRIGHT);
		
		
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(ActionService.WHEELWRIGHT1, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Silver")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(ActionService.WHEELWRIGHT2, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Scheme")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(0, playerState.getTurn().getChoicesAvailable().size()));
	}
	
	@Test
	public void withTraderAndHagglerBuyingBorderVillageCreatesManyChoices() {
		// buy border village triggers haggler gain
		// haggler gain triggers trader
		// trader resolves to one gained card
		// border village buy becomes gain, triggers trader
		// skip trader to gain the border village, triggers BV gain
		// BV gain triggers trader
		swapHandCards(ActionService.HAGGLER, ActionService.GOLD, ActionService.TRADER);
		loadDeck(ActionService.SILVER, ActionService.SILVER, ActionService.SILVER);
		stashGameState();

		actionService.turnPlay(gameState, "test", ActionService.HAGGLER);
		actionService.turnPlay(gameState, "test", ActionService.GOLD);
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		actionService.doBuy(gameState, "test", ActionService.BORDER_VILLAGE);
		
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(ActionService.HAGGLER2, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper")));
		actionService.doChoice(gameState, "test");
		
		// the TRADER choice for both the Haggler-gained copper and the bought BV get queued up. They shouldn't impact each other, so this is OK
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(ActionService.TRADER1, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Silver")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(ActionService.TRADER1, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Border Village")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(ActionService.BORDER_VILLAGE2, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Duchy")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(ActionService.TRADER1, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Silver")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals("Silver", playerState.getDiscard().getCards().get(0)));
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals("Border Village", playerState.getDiscard().getCards().get(1)));
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals("Silver", playerState.getDiscard().getCards().get(2)));
	}
	
	@Test
	public void withTraderAndHagglerBuyingBorderVillageTakingSilverInsteadOfTrailCantPlayTrail() {
		swapHandCards(ActionService.HAGGLER, ActionService.GOLD, ActionService.TRADER);
		loadDeck(ActionService.SILVER, ActionService.SILVER, ActionService.SILVER);
		stashGameState();

		actionService.turnPlay(gameState, "test", ActionService.HAGGLER);
		actionService.turnPlay(gameState, "test", ActionService.GOLD);
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		actionService.doBuy(gameState, "test", ActionService.BORDER_VILLAGE);
		
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(ActionService.HAGGLER2, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Trail")));
		actionService.doChoice(gameState, "test");
		
		// the TRADER choice for both the Haggler-gained Trail and the bought BV get queued up. They shouldn't impact each other, so this is OK
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(ActionService.TRADER1, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Silver")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(ActionService.TRADER1, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Border Village")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(ActionService.BORDER_VILLAGE2, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Trail")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(ActionService.TRADER1, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Silver")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals("Silver", playerState.getDiscard().getCards().get(0)));
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals("Border Village", playerState.getDiscard().getCards().get(1)));
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals("Silver", playerState.getDiscard().getCards().get(2)));
	}
	
	@Test
	public void withTraderAndHagglerBuyingBorderVillageTakingTrailCanPlayTrail() {
		swapHandCards(ActionService.HAGGLER, ActionService.GOLD, ActionService.TRADER);
		loadDeck(ActionService.SILVER, ActionService.SILVER, ActionService.SILVER);
		stashGameState();

		actionService.turnPlay(gameState, "test", ActionService.HAGGLER);
		actionService.turnPlay(gameState, "test", ActionService.GOLD);
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		actionService.doBuy(gameState, "test", ActionService.BORDER_VILLAGE);
		
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(ActionService.HAGGLER2, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Trail")));
		actionService.doChoice(gameState, "test");
		
		// the TRADER choice for both the Haggler-gained Trail and the bought BV get queued up. They shouldn't impact each other, so this is OK
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(ActionService.TRADER1, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Trail")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(ActionService.TRAIL1, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("PLAY1")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(3, playerState.getHand().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(ActionService.TRADER1, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Border Village")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(ActionService.BORDER_VILLAGE2, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Trail")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(ActionService.TRADER1, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Trail")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(ActionService.TRAIL1, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("PLAY1")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(4, playerState.getHand().size()));
		
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals("Border Village", playerState.getDiscard().getCards().get(0)));
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(1, playerState.getDiscard().size()));
	}
	
	@Test
	public void wheelwrightDiscardingTrailCanPlayTrailAndGainTrail() {
		swapHandCards(ActionService.WHEELWRIGHT, ActionService.TRAIL);
		loadDeck(ActionService.SILVER, ActionService.SILVER, ActionService.SILVER);
		stashGameState();

		actionService.turnPlay(gameState, "test", ActionService.WHEELWRIGHT);
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(5, playerState.getHand().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(ActionService.WHEELWRIGHT1, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Trail")));
		actionService.doChoice(gameState, "test");
		
		// the Trail discard and Wheelwright play both get queued up
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(ActionService.TRAIL1, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("PLAY")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(5, playerState.getHand().size()));
		
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(ActionService.WHEELWRIGHT2, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Trail")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(ActionService.TRAIL1, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("PLAY1")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(6, playerState.getHand().size()));
	}
	
	@Test
	public void wheelwrightGainingTrailCanPlayTrail() {
		swapHandCards(ActionService.WHEELWRIGHT, ActionService.DUCHY);
		loadDeck(ActionService.SILVER, ActionService.SILVER, ActionService.SILVER);
		stashGameState();

		actionService.turnPlay(gameState, "test", ActionService.WHEELWRIGHT);
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(5, playerState.getHand().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(ActionService.WHEELWRIGHT1, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Duchy")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(4, playerState.getHand().size()));
		
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(ActionService.WHEELWRIGHT2, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Trail")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(ActionService.TRAIL1, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("PLAY1")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(5, playerState.getHand().size()));
	}
	
	@Test
	public void withTraderBuyingFoolsGoldCanGainSilver() {
		swapBankSupplies(Map.of(ActionService.DUCHY, ActionService.FOOLS_GOLD));
		swapHandCards(ActionService.TRADER);
		loadDeck(ActionService.SILVER, ActionService.SILVER, ActionService.SILVER);
		stashGameState();

		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		actionService.turnPlay(gameState, "test", ActionService.COPPER);

		actionService.doBuy(gameState, "test", ActionService.FOOLS_GOLD);

		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(ActionService.TRADER1, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Silver")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals("Silver", playerState.getDiscard().getCards().get(0)));
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(1, playerState.getDiscard().size()));
	}
	
	@Test
	public void withTraderTheFoolsGoldReactionCanGainSilverInsteadOfGold() {
		swapHandCards(playerState2, ActionService.FOOLS_GOLD);
		swapHandCards(ActionService.GOLD, ActionService.GOLD, ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.GOLD);
		actionService.turnPlay(gameState, "test", ActionService.GOLD);
		actionService.turnPlay(gameState, "test", ActionService.GOLD);
		actionService.doBuy(gameState, "test", ActionService.PROVINCE);
		
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState2.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(ActionService.TRADER1, playerState2.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));
		
		throw new RuntimeException("the reaction isn't finished yet");
	}

	@Test
	public void withTraderTheCauldronReactionCanGainSilverInsteadOfCurse() {
		swapBankSupplies(Map.of(ActionService.DUCHY, ActionService.SCHEME, ActionService.PROVINCE, ActionService.CURSE));
		swapHandCards(playerState2, ActionService.TRADER);
		swapHandCards(ActionService.CAULDRON, ActionService.GOLD, ActionService.HAGGLER);
		loadDeck(ActionService.SILVER, ActionService.SILVER, ActionService.SILVER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.HAGGLER);
		actionService.turnPlay(gameState, "test", ActionService.GOLD);
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		actionService.turnPlay(gameState, "test", ActionService.CAULDRON);
		actionService.doBuy(gameState, "test", ActionService.BORDER_VILLAGE);
		
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(ActionService.HAGGLER2, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Scheme")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(ActionService.BORDER_VILLAGE2, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Scheme")));
		actionService.doChoice(gameState, "test");

		Assertions.assertEquals(1, playerState3.getDiscard().size());
		Assertions.assertEquals("Curse", playerState3.getDiscard().getCards().get(0));
		Assertions.assertEquals(0, playerState2.getDiscard().size());
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState2.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(ActionService.TRADER1, playerState2.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));
		
		playerState2.getTurn().setChoicesMade(new ArrayList<>(List.of("Silver")));
		actionService.doChoice(gameState, "test2");
		Assertions.assertEquals(1, playerState2.getDiscard().size());
	}
	
	@Test
	public void hagglerGainingTrailCanPlayTrailToNoEffect() {
		swapBankSupplies(Map.of(ActionService.DUCHY, ActionService.TRAIL));
		swapHandCards(ActionService.GOLD, ActionService.GOLD, ActionService.HAGGLER);
		loadDeck(ActionService.SILVER, ActionService.SILVER, ActionService.SILVER);
		stashGameState();

		actionService.turnPlay(gameState, "test", ActionService.HAGGLER);
		actionService.turnPlay(gameState, "test", ActionService.GOLD);
		actionService.turnPlay(gameState, "test", ActionService.GOLD);
		actionService.doBuy(gameState, "test", ActionService.GOLD);
		
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(ActionService.HAGGLER2, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Trail")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(ActionService.TRAIL1, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("PLAY1")));
		actionService.doChoice(gameState, "test");
		
		Assertions.assertEquals(3, playerState.getHand().size());
		Assertions.assertEquals(1, playerState.getTurn().getActionsAvailable());
		Assertions.assertEquals("cleanup", playerState.getPhase());
	}
	
	@Test
	public void borderVillageGainingSoukTriggersTrash() {
		swapBankSupplies(Map.of(ActionService.DUCHY, ActionService.SOUK));
		swapHandCards(ActionService.GOLD, ActionService.GOLD);
		loadDeck(ActionService.SILVER, ActionService.SILVER, ActionService.SILVER);
		stashGameState();

		actionService.turnPlay(gameState, "test", ActionService.GOLD);
		actionService.turnPlay(gameState, "test", ActionService.GOLD);
		actionService.doBuy(gameState, "test", ActionService.BORDER_VILLAGE);
		
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(ActionService.BORDER_VILLAGE2, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Souk")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(ActionService.SOUK1, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(0, playerState.getTurn().getChoicesAvailable().get(0).getMinChoices()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().get(0).getMaxChoices()));
		
	}
	
	@Test
	public void wheelwrightDiscardingGoldGainingSoukTriggersTrash() {
		swapBankSupplies(Map.of(ActionService.DUCHY, ActionService.SOUK));
		swapHandCards(ActionService.WHEELWRIGHT, ActionService.GOLD);
		loadDeck(ActionService.SILVER, ActionService.SILVER, ActionService.SILVER);
		stashGameState();

		actionService.turnPlay(gameState, "test", ActionService.WHEELWRIGHT);
		
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(ActionService.WHEELWRIGHT1, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Gold")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(ActionService.WHEELWRIGHT2, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Souk")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(ActionService.SOUK1, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(0, playerState.getTurn().getChoicesAvailable().get(0).getMinChoices()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().get(0).getMaxChoices()));
		
	}
	
	@Test
	public void hagglerGainingSoukTriggersTrash() {
		swapBankSupplies(Map.of(ActionService.DUCHY, ActionService.SOUK));
		swapHandCards(ActionService.HAGGLER, ActionService.GOLD, ActionService.GOLD);
		loadDeck(ActionService.SILVER, ActionService.SILVER, ActionService.SILVER);
		stashGameState();

		actionService.turnPlay(gameState, "test", ActionService.HAGGLER);
		actionService.turnPlay(gameState, "test", ActionService.GOLD);
		actionService.turnPlay(gameState, "test", ActionService.GOLD);

		actionService.doBuy(gameState, "test", ActionService.GOLD);
		
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(ActionService.HAGGLER2, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Souk")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(ActionService.SOUK1, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(0, playerState.getTurn().getChoicesAvailable().get(0).getMinChoices()));
		doAssertion(Checked.CHOICES, ()-> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().get(0).getMaxChoices()));

	}
}
