package com.jtriemstra.dominion.api;

import java.util.Arrays;

import org.junit.Ignore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import com.jtriemstra.dominion.api.models.*;

import lombok.extern.slf4j.Slf4j;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyList;

import java.util.*;

@Slf4j
public class AttackTests {
	
	private void assertContains(String name, List<Card> cardSet) {
		boolean cardFound = false;
		for (Card c : cardSet) {
			if (name.equals(c.getName())) {
				cardFound = true;
			}
		}
		
		assertTrue(cardFound);
	}
	
	private void assertNotContains(String name, List<Card> cardSet) {
		boolean cardFound = false;
		for (Card c : cardSet) {
			if (name.equals(c.getName())) {
				cardFound = true;
			}
		}
		
		assertTrue(!cardFound);
	}
	
	private Player mockPlayer(String name, Game game) {
		Player realPlayer = new Player(name);
		Player player = spy(realPlayer);
		when(player.shuffle(anyList())).thenAnswer(i -> i.getArguments()[0]);
		player.init(game);
		game.addPlayer(player);
		
		return player;
	}
	
	@Test                                                                                         
    public void when_playing_spy_moat_blocks() {
		Bank realBank = new Bank(Arrays.asList("Spy", "Moat"));
		Bank mockBank = spy(realBank);
		List<Card> x = new ArrayList<>();
		x.add(mockBank.spy());
		for (int i=0; i<9; i++) { x.add(mockBank.copper());}
		
		List<Card> y = new ArrayList<>();
		y.add(mockBank.moat());
		for (int i=0; i<9; i++) { y.add(mockBank.copper());}
		
		when(mockBank.newDeck()).thenReturn(x, y);
		
		Game game = new Game(mockBank);
		
		Player player = mockPlayer("test", game);
		Player player2 = mockPlayer("test2", game);
		
		player.play("Spy");
		assertEquals("Choose cards for opponents to discard", player.getCurrentChoice().getPrompt());
		assertEquals(1, player.getCurrentChoice().getOptions().size());
		assertEquals("test : Copper", player.getCurrentChoice().getOptions().get(0));
		
		player.finishAction(Arrays.asList("test : Copper"));
		
		assertEquals(1, player.getDiscard().size());
		assertEquals(0, player2.getDiscard().size());
		assertEquals(3, player.getDeck().size());
		
		assertNull(player.getCurrentChoice());	
	}
	
	@Test                                                                                         
    public void when_playing_spy_attack_works() {
		Bank realBank = new Bank(Arrays.asList("Spy"));
		Bank mockBank = spy(realBank);
		List<Card> x = new ArrayList<>();
		x.add(mockBank.spy());
		for (int i=0; i<9; i++) { x.add(mockBank.copper());}
		
		List<Card> y = new ArrayList<>();
		y.add(mockBank.estate());
		for (int i=0; i<4; i++) { y.add(mockBank.copper());}
		y.add(mockBank.silver());
		for (int i=0; i<4; i++) { y.add(mockBank.copper());}
		
		when(mockBank.newDeck()).thenReturn(x, y);
		
		Game game = new Game(mockBank);
		
		Player player = mockPlayer("test", game);
		Player player2 = mockPlayer("test2", game);
		
		player.play("Spy");
		assertEquals("Choose cards for opponents to discard", player.getCurrentChoice().getPrompt());
		assertEquals(2, player.getCurrentChoice().getOptions().size());
		assertEquals("test : Copper", player.getCurrentChoice().getOptions().get(0));
		assertEquals("test2 : Silver", player.getCurrentChoice().getOptions().get(1));
		
		player.finishAction(Arrays.asList("test2 : Silver"));
		
		assertEquals(0, player.getDiscard().size());
		assertEquals(1, player2.getDiscard().size());
		assertEquals(4, player.getDeck().size());
		assertEquals(4, player2.getDeck().size());
		
		assertNull(player.getCurrentChoice());	
	}
}
