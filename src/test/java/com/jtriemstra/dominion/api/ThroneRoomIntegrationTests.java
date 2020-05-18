package com.jtriemstra.dominion.api;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import com.jtriemstra.dominion.api.models.*;

import lombok.extern.slf4j.Slf4j;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.*;

@Slf4j
public class ThroneRoomIntegrationTests {

	private Player mockPlayer(String name, Game game) {
		Player realPlayer = new Player(name);
		Player player = spy(realPlayer);
		when(player.shuffle(anyList())).thenAnswer(i -> i.getArguments()[0]);
		player.init(game);
		game.addPlayer(player);
		
		return player;
	}
	
	@Test                                                                                         
    public void simpleActionVillage() {
		
		Bank mockBank = spy(Bank.class);
		List<Card> x = new ArrayList<>();
		x.add(mockBank.village());
		x.add(mockBank.throneroom());
		for (int i=0; i<8; i++) { x.add(mockBank.copper());}
		when(mockBank.newDeck()).thenReturn(x);
		
		Game game = new Game(mockBank);
		
		Player player = mockPlayer("test", game);
		
		player.play("Throne Room");
		player.finishAction(Arrays.asList("Village"));
		
		assertEquals(4, player.numberOfActions());
		assertEquals(5, player.getHand().size());
		assertEquals(true, player.hasBuys());
		assertEquals(true, player.hasActions());
		assertEquals(null, player.getCurrentChoice());
		assertEquals(0, player.getDiscard().size());
		assertEquals(2, player.getPlayed().size());
	}
	
	@Test                                                                                         
    public void simpleActionSmithy() {
		Bank mockBank = spy(Bank.class);
		List<Card> x = new ArrayList<>();
		x.add(mockBank.smithy());
		x.add(mockBank.throneroom());
		for (int i=0; i<15; i++) { x.add(mockBank.copper());}
		when(mockBank.newDeck()).thenReturn(x);
		
		Game game = new Game(mockBank);
		
		Player player = mockPlayer("test", game);
		
		player.play("Throne Room");
		player.finishAction(Arrays.asList("Smithy"));
		
		assertEquals(0, player.getTemporaryActions());
		assertEquals(9, player.getHand().size());
		assertEquals(true, player.hasBuys());
		assertEquals(false, player.hasActions());
		assertEquals(null, player.getCurrentChoice());
		assertEquals(0, player.getDiscard().size());
		assertEquals(2, player.getPlayed().size());
	}
	
	@Test
	public void complexActionWorkshop() {
		log.info("starting complexActionWorkshop");
		Bank mockBank = spy(Bank.class);
		List<Card> x = new ArrayList<>();
		x.add(mockBank.workshop());
		x.add(mockBank.throneroom());
		for (int i=0; i<8; i++) { x.add(mockBank.copper());}
		when(mockBank.newDeck()).thenReturn(x);
		
		Game game = new Game(mockBank);
		
		Player player = mockPlayer("test", game);
		
		player.play("Throne Room");
		assertEquals("Choose an action card to play twice", player.getCurrentChoice().getPrompt());
		player.finishAction(Arrays.asList("Workshop"));
		assertEquals("Choose a card costing up to 4", player.getCurrentChoice().getPrompt());
		player.finishAction(Arrays.asList("Silver"));
		assertEquals("Choose a card costing up to 4", player.getCurrentChoice().getPrompt());
		player.finishAction(Arrays.asList("Silver"));
		
		assertEquals(0, player.getTemporaryActions());
		assertEquals(3, player.getHand().size());
		assertEquals(true, player.hasBuys());
		assertEquals(false, player.hasActions());
		assertEquals(null, player.getCurrentChoice());
		assertEquals(2, player.getBought().size());
		assertEquals(0, player.getDiscard().size());
		assertEquals(2, player.getPlayed().size());
	}
	
}
