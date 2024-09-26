package com.jtriemstra.dominion.api.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PlayerStateTest {
	@Test
	public void sortHand() {
		PlayerState p = new PlayerState("test");
		p.getHand().add("Copper");
		p.getHand().add("Copper");
		p.getHand().add("Copper");
		p.getHand().add("Copper");
		p.getHand().add("Harbinger");
		
		Assertions.assertEquals("Harbinger", p.getHandForJson().get(0));
	}
	

	@Test
	public void sortHand2() {
		PlayerState p = new PlayerState("test");
		p.getHand().add("Copper");
		p.getHand().add("Copper");
		p.getHand().add("Copper");
		p.getHand().add("Copper");
		p.getHand().add("Cellar");
		
		Assertions.assertEquals("Cellar", p.getHandForJson().get(0));
	}
	@Test
	public void sortHand3() {
		PlayerState p = new PlayerState("test");
		p.getHand().add("Copper");
		p.getHand().add("Copper");
		p.getHand().add("Harbinger");
		p.getHand().add("Copper");
		p.getHand().add("Copper");
		
		Assertions.assertEquals("Harbinger", p.getHandForJson().get(0));
	}
}
