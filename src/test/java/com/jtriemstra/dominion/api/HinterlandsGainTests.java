package com.jtriemstra.dominion.api;

import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import com.jtriemstra.dominion.api.models.*;

import lombok.extern.slf4j.Slf4j;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.*;

@Slf4j
public class HinterlandsGainTests {
	
	private void assertContains(String name, List<Card> cardSet) {
		boolean cardFound = false;
		for (Card c : cardSet) {
			if (name.equals(c.getName())) {
				cardFound = true;
			}
		}
		
		assertTrue(cardFound);
	}
	
	@Test                                                                                         
    public void embassy() {
		Bank realBank = new Bank(Arrays.asList("Embassy"));
		Bank mockBank = spy(realBank);
		List<Card> x = new ArrayList<>();		
		for (int i=0; i<10; i++) { x.add(mockBank.copper());}
		when(mockBank.newDeck()).thenReturn(x);
		
		Game game = new Game(mockBank);
		
		Player player = new Player("test");
		player.init(game);
		game.addPlayer(player);
		
		Player player2 = new Player("test2");
		player2.init(game);
		game.addPlayer(player2);
		
		player.play("Copper");
		player.play("Copper");
		player.play("Copper");
		player.play("Copper");
		player.play("Copper");
		
		player.buy("Embassy");
		
		assertContains("Embassy", player.getBought());
		assertContains("Silver", player2.getDiscard());
	}
	
	@Test                                                                                         
    public void cache() {
		Bank realBank = new Bank(Arrays.asList("Cache"));
		Bank mockBank = spy(realBank);
		List<Card> x = new ArrayList<>();		
		for (int i=0; i<10; i++) { x.add(mockBank.copper());}
		when(mockBank.newDeck()).thenReturn(x);
		
		Game game = new Game(mockBank);
		
		Player player = new Player("test");
		player.init(game);
		game.addPlayer(player);
		
		player.play("Copper");
		player.play("Copper");
		player.play("Copper");
		player.play("Copper");
		player.play("Copper");
		
		player.buy("Cache");
		
		assertEquals(3, player.getBought().size());		
	}
	
	@Test                                                                                         
    public void borderVillage() {
		Bank realBank = new Bank(Arrays.asList("Border Village", "Nomad Camp"));
		Bank mockBank = spy(realBank);
		List<Card> x = new ArrayList<>();		
		for (int i=0; i<10; i++) { x.add(mockBank.silver());}
		when(mockBank.newDeck()).thenReturn(x);
		
		Game game = new Game(mockBank);
		
		Player player = new Player("test");
		player.init(game);
		game.addPlayer(player);
		
		player.play("Silver");
		player.play("Silver");
		player.play("Silver");
		player.play("Silver");
		player.play("Silver");
		
		player.buy("Border Village");
		assertTrue(player.getCurrentChoice().getOptions().contains("Nomad Camp"));
		assertTrue(!player.getCurrentChoice().getOptions().contains("Gold"));
		player.finishAction(Arrays.asList("Nomad Camp"));
		
		assertEquals(2, player.getBought().size());		
	}
	
	@Test                                                                                         
    public void nomadCamp() {
		Bank realBank = new Bank(Arrays.asList("Nomad Camp"));
		Bank mockBank = spy(realBank);
		List<Card> x = new ArrayList<>();		
		for (int i=0; i<10; i++) { x.add(mockBank.silver());}
		when(mockBank.newDeck()).thenReturn(x);
		
		Game game = new Game(mockBank);
		
		Player player = new Player("test");
		player.init(game);
		game.addPlayer(player);
		
		player.play("Silver");
		player.play("Silver");
		player.play("Silver");
		player.play("Silver");
		player.play("Silver");
		
		player.buy("Nomad Camp");
		
		assertEquals(0, player.getBought().size());		
		assertEquals(6, player.getDeck().size());		
	}
	
	@Test                                                                                         
    public void mandarin() {
		Bank realBank = new Bank(Arrays.asList("Mandarin"));
		Bank mockBank = spy(realBank);
		List<Card> x = new ArrayList<>();		
		for (int i=0; i<10; i++) { x.add(mockBank.silver());}
		when(mockBank.newDeck()).thenReturn(x);
		
		Game game = new Game(mockBank);
		
		Player player = new Player("test");
		player.init(game);
		game.addPlayer(player);
		
		player.play("Silver");
		player.play("Silver");
		player.play("Silver");
		player.buy("Mandarin");
		
		assertEquals(0, player.getPlayed().size());		
		assertEquals(8, player.getDeck().size());		
	}
}
