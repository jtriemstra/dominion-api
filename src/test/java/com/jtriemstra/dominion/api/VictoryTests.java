package com.jtriemstra.dominion.api;

import  java.util.Arrays;
import org.junit.jupiter.api.Test;

import com.jtriemstra.dominion.api.models.Bank;
import com.jtriemstra.dominion.api.models.Player;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class VictoryTests {
	@Test                                                                                         
    public void when_gardens_in_deck_score_correctly() {
		Player p = new Player("test");
		Bank b = new Bank(Arrays.asList("Gardens", "Smithy"));
		p.getBought().add(b.province());
		for (int i=0; i<5; i++) p.getHand().add(b.gold());
		p.getPlayed().add(b.smithy());
		for (int i=0;i<5;i++) {
			p.getDiscard().add(b.gardens());
		}
		
		int points = p.getPoints();
		assertEquals(11, points);
	}
	
	@Test                                                                                         
    public void when_silkroad_in_deck_score_correctly() {
		Player p = new Player("test");
		Bank b = new Bank(Arrays.asList("Silk Road", "Smithy"));
		p.getBought().add(b.province());
		for (int i=0; i<5; i++) p.getHand().add(b.gold());
		p.getPlayed().add(b.smithy());
		p.getDeck().add(b.silkroad());
		p.getDeck().add(b.silkroad());
		for (int i=0;i<5;i++) {
			p.getDiscard().add(b.estate());
		}
		
		int points = p.getPoints();
		assertEquals(15, points);
	}
}
