package com.jtriemstra.dominion.api;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import com.jtriemstra.dominion.api.models.*;

import lombok.extern.slf4j.Slf4j;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.*;

@Slf4j
public class GameTests {
	
	
	@Test                                                                                         
    public void getOtherPlayers() {
		
				
		Game game = new Game(new Bank());
		
		Player player = new Player();
		player.init(game);
		game.getPlayers().add(player);
		
		Player otherPlayer = new Player();
		otherPlayer.init(game);
		game.getPlayers().add(otherPlayer);
		
		List<Player> others = game.getOtherPlayers(player);
		assertEquals(1, others.size());
		assertEquals(otherPlayer, others.get(0));
	}
	
	
	
}
