package com.jtriemstra.dominion.api.service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtriemstra.dominion.api.dto.BankState;
import com.jtriemstra.dominion.api.dto.BankSupply;
import com.jtriemstra.dominion.api.dto.GameState;
import com.jtriemstra.dominion.api.dto.PlayerState;
import com.jtriemstra.dominion.api.service.ActionServiceTest.CheckedAssertion;

import lombok.SneakyThrows;

public class ActionServiceTestBase {
	protected ActionService actionService;
	protected PlayerService playerService;
	protected GameState gameState;
	protected BankState bankState;
	protected PlayerState playerState;
	protected PlayerState playerState2;
	protected PlayerState playerState3;
	protected GameState stashedGameState;
	protected ObjectMapper objectMapper = new ObjectMapper();
	public enum Checked {
		ACTIONS,
		BUY,
		DECK,
		DISCARD,
		CHOICES,
		HAND,
		TREASURE
	}
	protected Set<Checked> checked;
		
	@BeforeEach
	public void init() {
		checked = new HashSet<>();
		stashedGameState = null;
		playerService = new PlayerService();
		actionService = new ActionService(playerService, Mockito.mock(NotificationService.class));
		actionService.init();
		bankState = new BankState();
		gameState = new GameState(bankState);
		playerState = new PlayerState("test");
		playerState2 = new PlayerState("test2");
		playerState3 = new PlayerState("test3");
		gameState.getPlayers().put("test", playerState);
		gameState.getPlayers().put("test2", playerState2);
		gameState.getPlayers().put("test3", playerState3);
		gameState.getPlayerNames().addAll(List.of("test", "test2", "test3"));
		startTurn();
		
		bankState.getSupplies().put(ActionService.EMBASSY, new BankSupply(10, ActionService.EMBASSY)); //5
		bankState.getSupplies().put(ActionService.COPPER, new BankSupply(60, ActionService.COPPER));
		bankState.getSupplies().put(ActionService.SILVER, new BankSupply(40, ActionService.SILVER));
		bankState.getSupplies().put(ActionService.GOLD, new BankSupply(30, ActionService.GOLD));
		bankState.getSupplies().put(ActionService.DUCHY, new BankSupply(12, ActionService.DUCHY));
		bankState.getSupplies().put(ActionService.BORDER_VILLAGE, new BankSupply(10, ActionService.BORDER_VILLAGE)); //6
		bankState.getSupplies().put(ActionService.TRAIL, new BankSupply(10, ActionService.TRAIL));		//4
		bankState.getSupplies().put(ActionService.PROVINCE, new BankSupply(10, ActionService.PROVINCE));		//8
	}
	
	protected void startTurn() {
		playerState.getHand().add(ActionService.COPPER);
		playerState.getHand().add(ActionService.COPPER);
		playerState.getHand().add(ActionService.COPPER);
		playerState.getHand().add(ActionService.COPPER);
		playerState.getHand().add(ActionService.COPPER);
		
		playerState2.getHand().add(ActionService.COPPER);
		playerState2.getHand().add(ActionService.COPPER);
		playerState2.getHand().add(ActionService.COPPER);
		playerState2.getHand().add(ActionService.COPPER);
		playerState2.getHand().add(ActionService.COPPER);

		playerState3.getHand().add(ActionService.COPPER);
		playerState3.getHand().add(ActionService.COPPER);
		playerState3.getHand().add(ActionService.COPPER);
		playerState3.getHand().add(ActionService.COPPER);
		playerState3.getHand().add(ActionService.COPPER);
		
		playerState.getTurn().setActionsAvailable(1);
		playerState.getTurn().setActive(true);
		playerState.getTurn().setBuys(1);
		playerState.getTurn().setTreasure(0);
		playerState.getTurn().getGainedToDiscard().clear();
	}
	
	protected void swapHandCards(String... cards) {
		swapHandCards(playerState, cards);
	}
	
	protected void swapHandCards(PlayerState player, String... cards) {
		for (String card : cards) {
			player.getHand().getCards().remove(0);
			player.getHand().add(card);
		}
	}
	
	protected void swapBankSupplies(Map<String, String> swaps) {
		for (String old : swaps.keySet()) {
			bankState.getSupplies().remove(old);
			bankState.getSupplies().put(swaps.get(old), new BankSupply(10, swaps.get(old)));
		}
	}
	
	protected void assertCardsInHand(Map<String, Integer> expected) {
		for (String s : expected.keySet()) {
			Assertions.assertEquals(expected.get(s).intValue(), (int) playerState.getHand().getCards().stream().filter(c -> c.equals(s)).count());
		}
	}

	protected void assertCardsInHand(PlayerState player, Map<String, Integer> expected) {
		for (String s : expected.keySet()) {
			Assertions.assertEquals(expected.get(s).intValue(), (int) player.getHand().getCards().stream().filter(c -> c.equals(s)).count());
		}
	}
	
	protected void doAssertion(Checked checked, CheckedAssertion check) {
		this.checked.add(checked);
		check.doAssert();
	}
	
	@SneakyThrows
	protected void stashGameState() {
		String s = objectMapper.writeValueAsString(gameState);
		//stashedGameState = objectMapper.readValue(s, GameState.class);
	}
	
	protected void loadDeck(String... cards) {
		for (String card : cards) {
			playerState.getDeck().add(card);
		}
	}
	
	protected void loadDeck(PlayerState aPlayer, String... cards) {
		for (String card : cards) {
			aPlayer.getDeck().add(card);
		}
	}

	protected void loadDiscard(String... cards) {
		for (String card : cards) {
			playerState.getDiscard().add(card);
		}
	}

	protected void loadDiscard(PlayerState aPlayer, String... cards) {
		for (String card : cards) {
			aPlayer.getDiscard().add(card);
		}
	}
}
