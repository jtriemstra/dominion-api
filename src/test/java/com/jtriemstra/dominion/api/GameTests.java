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
public class GameTests {
	private Player mockPlayer(String name, Game game) {
		Player realPlayer = new Player(name);
		Player player = spy(realPlayer);
		when(player.shuffle(anyList())).thenAnswer(i -> i.getArguments()[0]);
		player.init(game);
		game.addPlayer(player);
		
		return player;
	}
	
	@Test                                                                                         
    public void getOtherPlayers() {
		
				
		Game game = new Game(new Bank());
		
		Player player = mockPlayer("test", game);
		
		Player otherPlayer = mockPlayer("test1", game);
		
		List<Player> others = game.getOtherPlayers(player);
		assertEquals(1, others.size());
		assertEquals(otherPlayer, others.get(0));
	}
	
	
	
}
