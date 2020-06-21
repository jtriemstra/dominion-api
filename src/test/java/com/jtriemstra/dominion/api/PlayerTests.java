package com.jtriemstra.dominion.api;

import org.junit.jupiter.api.Test;
import com.jtriemstra.dominion.api.models.*;
import com.jtriemstra.dominion.api.models.Player.CleanupAction;

import lombok.extern.slf4j.Slf4j;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.*;

@Slf4j
public class PlayerTests {
	private Player mockPlayer(String name, Game game) {
		Player realPlayer = new Player(name);
		Player player = spy(realPlayer);
		when(player.shuffle(anyList())).thenAnswer(i -> i.getArguments()[0]);
		player.init(game);
		game.addPlayer(player);
		
		return player;
	}
	
	@Test                                                                                         
    public void when_cleanup_then_cards_reset_correctly() {
		Bank realBank = new Bank(Arrays.asList("Inn","Village"));
		Bank mockBank = spy(realBank);
		List<Card> x = new ArrayList<>();		
		x.add(mockBank.village());
		for (int i=0; i<14; i++) { x.add(mockBank.copper());}
		when(mockBank.newDeck()).thenReturn(x);
		
		Game game = new Game(mockBank);
		
		Player player = mockPlayer("test", game);
		
		player.play("Village");
		player.play("Copper");
		player.play("Copper");
		player.play("Copper");
		player.buy("Village");
		
		player.cleanup();
		
		assertEquals(7, player.getDiscard().size());
		assertEquals(5, player.getHand().size());
		assertEquals(0, player.getBought().size());
	}
	
	
	
}
