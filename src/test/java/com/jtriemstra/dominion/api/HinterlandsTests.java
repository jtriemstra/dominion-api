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
public class HinterlandsTests {
	
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
    public void when_playing_inn_draw_and_discard() {
		Bank realBank = new Bank(Arrays.asList("Inn"));
		Bank mockBank = spy(realBank);
		List<Card> x = new ArrayList<>();
		x.add(mockBank.inn());
		for (int i=0; i<10; i++) { x.add(mockBank.copper());}
		when(mockBank.newDeck()).thenReturn(x);
		
		Game game = new Game(mockBank);
		
		Player player = mockPlayer("test", game);
		
		player.play("Inn");
		assertEquals(6, player.getHand().size());
		assertEquals(2, player.getTemporaryActions());
		assertEquals("Which 2 cards would you like to discard?", player.getCurrentChoice().getPrompt());
		assertEquals(6, player.getCurrentChoice().getOptions().size());
		
		player.finishAction(Arrays.asList("Copper","Copper"));
		assertEquals(4, player.getHand().size());
		
	}
	
	@Test                                                                                         
    public void when_playing_noble_brigand_attack_works() {
		Bank realBank = new Bank(Arrays.asList("Noble Brigand"));
		Bank mockBank = spy(realBank);
		List<Card> x = new ArrayList<>();
		x.add(mockBank.noblebrigand());
		for (int i=0; i<9; i++) { x.add(mockBank.copper());}
		
		List<Card> y = new ArrayList<>();
		for (int i=0; i<5; i++) { y.add(mockBank.copper());}
		y.add(mockBank.silver());
		y.add(mockBank.estate());
		for (int i=0; i<3; i++) { y.add(mockBank.copper());}
		
		when(mockBank.newDeck()).thenReturn(x, y);
		
		Game game = new Game(mockBank);
		
		Player player = mockPlayer("test", game);
		
		Player player2 = mockPlayer("test2", game);
		
		player.play("Noble Brigand");
		assertEquals("Choose a Silver or Gold from each player to steal:", player.getCurrentChoice().getPrompt());
		assertEquals(2, player.getCurrentChoice().getOptions().size());
		assertEquals("test2 : Silver", player.getCurrentChoice().getOptions().get(0));
		
		player.finishAction(Arrays.asList("test2 : Silver"));
		
		assertEquals(1, player.getBought().size());
		assertEquals(1, player.getTemporaryTreasure());
		
		assertEquals(1, player2.getDiscard().size());
		assertContains("Estate", player2.getDiscard());
		assertEquals(3, player2.getDeck().size());
		
		assertNull(player.getCurrentChoice());	
	}
	
	@Test                                                                                         
    public void when_playing_noble_brigand_gain_copper_if_no_treasure() {
		Bank realBank = new Bank(Arrays.asList("Noble Brigand"));
		Bank mockBank = spy(realBank);
		List<Card> x = new ArrayList<>();
		x.add(mockBank.noblebrigand());
		for (int i=0; i<9; i++) { x.add(mockBank.copper());}
		
		List<Card> y = new ArrayList<>();
		for (int i=0; i<5; i++) { y.add(mockBank.copper());}
		y.add(mockBank.estate());
		y.add(mockBank.estate());
		for (int i=0; i<3; i++) { y.add(mockBank.copper());}
		
		when(mockBank.newDeck()).thenReturn(x, y);
		
		Game game = new Game(mockBank);
		
		Player player = mockPlayer("test", game);
		
		Player player2 = mockPlayer("test2", game);
		
		player.play("Noble Brigand");
		assertEquals("Choose a Silver or Gold from each player to steal:", player.getCurrentChoice().getPrompt());
		assertEquals(2, player.getCurrentChoice().getOptions().size());
		assertEquals("test2 : Estate", player.getCurrentChoice().getOptions().get(0));
		
		player.finishAction(new ArrayList<String>());
		
		assertEquals(0, player.getBought().size());
		assertEquals(1, player.getTemporaryTreasure());
		
		assertEquals(3, player2.getDiscard().size());
		assertContains("Copper", player2.getDiscard());
		assertEquals(3, player2.getDeck().size());
		
		assertNull(player.getCurrentChoice());	
	}
	
	@Test                                                                                         
    public void when_playing_cartographer_can_discard() {
		Bank realBank = new Bank(Arrays.asList("Cartographer"));
		Bank mockBank = spy(realBank);
		List<Card> x = new ArrayList<>();
		x.add(mockBank.cartographer());
		for (int i=0; i<7; i++) { x.add(mockBank.copper());}
		x.add(mockBank.estate());
		x.add(mockBank.estate());
		
		when(mockBank.newDeck()).thenReturn(x);
		
		Game game = new Game(mockBank);
		
		Player player = mockPlayer("test", game);
		
		player.play("Cartographer");
		assertEquals(5, player.getHand().size());
		assertEquals(1, player.getTemporaryActions());
		assertEquals(0, player.getDeck().size());
		assertEquals(4, player.getLiminal().size());
		assertEquals("Choose cards from the deck to discard", player.getCurrentChoice().getPrompt());
		assertEquals(4, player.getCurrentChoice().getOptions().size());
		assertEquals(0, player.getDeck().size());
		
		player.finishAction(Arrays.asList("Estate","Copper"));
		assertEquals(2, player.getDeck().size());
		assertEquals(2, player.getDiscard().size());
		assertEquals(0, player.getLiminal().size());
		assertNull(player.getCurrentChoice());
	}

	@Test                                                                                         
    public void when_playing_mandarin_get_treasure_and_card_to_deck() {
		Bank realBank = new Bank(Arrays.asList("Mandarin"));
		Bank mockBank = spy(realBank);
		List<Card> x = new ArrayList<>();
		x.add(mockBank.mandarin());
		for (int i=0; i<6; i++) { x.add(mockBank.copper());}
		x.add(mockBank.estate());
		x.add(mockBank.estate());
		x.add(mockBank.estate());
		
		when(mockBank.newDeck()).thenReturn(x);
		
		Game game = new Game(mockBank);
		
		Player player = mockPlayer("test", game);
		
		player.play("Copper");
		player.play("Mandarin");
		
		assertEquals(4, player.getTemporaryTreasure());
		assertEquals(0, player.getTemporaryActions());
		assertEquals("Choose a card to put back on your deck", player.getCurrentChoice().getPrompt());
		assertEquals(3, player.getCurrentChoice().getOptions().size());
		
		player.finishAction(Arrays.asList("Copper"));
		
		assertEquals(6, player.getDeck().size());
		
		assertNull(player.getCurrentChoice());
	}
	
	@Test                                                                                         
    public void when_playing_scheme_can_put_action_on_deck() {
		Bank realBank = new Bank(Arrays.asList("Scheme"));
		Bank mockBank = spy(realBank);
		List<Card> x = new ArrayList<>();
		x.add(mockBank.scheme());
		for (int i=0; i<6; i++) { x.add(mockBank.copper());}
		x.add(mockBank.estate());
		x.add(mockBank.estate());
		x.add(mockBank.estate());
		
		when(mockBank.newDeck()).thenReturn(x);
		
		Game game = new Game(mockBank);
		
		Player player = mockPlayer("test", game);
		
		player.play("Scheme");
				
		assertEquals(5, player.getHand().size());
		assertEquals(1, player.getTemporaryActions());
		assertNull(player.getCurrentChoice());
		
		player.startCleanup();
		
		assertEquals("Choose an action card to put on your deck", player.getCurrentChoice().getPrompt());
		assertEquals(1, player.getCurrentChoice().getOptions().size());
		
		player.finishAction(Arrays.asList("Scheme"));
		
		assertEquals(0, player.getDeck().size());
		assertEquals(5, player.getDiscard().size());		
	}
	
	@Test                                                                                         
    public void when_playing_scheme_not_required_to_pick_action_card() {
		Bank realBank = new Bank(Arrays.asList("Scheme"));
		Bank mockBank = spy(realBank);
		List<Card> x = new ArrayList<>();
		x.add(mockBank.scheme());
		for (int i=0; i<6; i++) { x.add(mockBank.copper());}
		x.add(mockBank.estate());
		x.add(mockBank.estate());
		x.add(mockBank.estate());
		
		when(mockBank.newDeck()).thenReturn(x);
		
		Game game = new Game(mockBank);
		
		Player player = mockPlayer("test", game);
		
		player.play("Scheme");
				
		assertEquals(5, player.getHand().size());
		assertEquals(4, player.getDeck().size());
		assertEquals(1, player.getTemporaryActions());
		assertNull(player.getCurrentChoice());
		
		player.startCleanup();
		
		assertEquals("Choose an action card to put on your deck", player.getCurrentChoice().getPrompt());
		assertEquals(1, player.getCurrentChoice().getOptions().size());
		
		player.finishAction(new ArrayList<String>());
		
		assertEquals(5, player.getDeck().size());
		assertEquals(0, player.getDiscard().size());		
	}
	
	@Test                                                                                         
    public void when_playing_illgottengains_get_extra_copper() {
		Bank realBank = new Bank(Arrays.asList("Ill-Gotten Gains"));
		Bank mockBank = spy(realBank);
		List<Card> x = new ArrayList<>();
		x.add(mockBank.illgottengains());
		for (int i=0; i<6; i++) { x.add(mockBank.copper());}
		x.add(mockBank.estate());
		x.add(mockBank.estate());
		x.add(mockBank.estate());
		
		when(mockBank.newDeck()).thenReturn(x);
		
		Game game = new Game(mockBank);
		
		Player player = mockPlayer("test", game);
		
		player.play("Ill-Gotten Gains");
		assertEquals(1, player.getTemporaryTreasure());
		assertEquals("Would you like to gain a Copper?", player.getCurrentChoice().getPrompt());
		
		player.finishAction(Arrays.asList("Yes"));
		
		assertEquals(5, player.getHand().size());
		assertNull(player.getCurrentChoice());		
	}
	
	@Test                                                                                         
    public void when_playing_fools_gold_treasure_is_correct() {
		Bank realBank = new Bank(Arrays.asList("Fools Gold"));
		Bank mockBank = spy(realBank);
		List<Card> x = new ArrayList<>();
		x.add(mockBank.foolsgold());
		x.add(mockBank.foolsgold());
		x.add(mockBank.foolsgold());
		for (int i=0; i<4; i++) { x.add(mockBank.copper());}
		x.add(mockBank.estate());
		x.add(mockBank.estate());
		x.add(mockBank.estate());
		
		when(mockBank.newDeck()).thenReturn(x);
		
		Game game = new Game(mockBank);
		
		Player player = mockPlayer("test", game);
		
		player.play("Fools Gold");
		assertEquals(1, player.getTemporaryTreasure());
		player.play("Fools Gold");
		assertEquals(5, player.getTemporaryTreasure());
		player.play("Fools Gold");
		assertEquals(9, player.getTemporaryTreasure());
		
		assertNull(player.getCurrentChoice());		
	}
	
	@Test                                                                                         
    public void when_fools_gold_in_hand_can_discard() {
		Bank realBank = new Bank(Arrays.asList("Fools Gold"));
		Bank mockBank = spy(realBank);
		List<Card> x = new ArrayList<>();
		x.add(mockBank.foolsgold());
		x.add(mockBank.foolsgold());
		x.add(mockBank.foolsgold());
		for (int i=0; i<4; i++) { x.add(mockBank.copper());}
		x.add(mockBank.estate());
		x.add(mockBank.estate());
		x.add(mockBank.estate());
		
		List<Card> y = new ArrayList<>();
		y.add(mockBank.gold());
		y.add(mockBank.gold());
		y.add(mockBank.gold());
		for (int i=0; i<7; i++) { y.add(mockBank.copper());}
		
		when(mockBank.newDeck()).thenReturn(y, x);
		
		Game game = new Game(mockBank);
		
		Player player = mockPlayer("test", game);
		
		Player player2 = mockPlayer("test2", game);
		
		player.play("Gold");
		player.play("Gold");		
		player.play("Gold");
		assertEquals(9, player.getTemporaryTreasure());
		
		player.buy("Province");
		assertNull(player.getCurrentChoice());
		
		assertEquals("Would you like to trash this Fools Gold to gain a real gold to deck?", player2.getCurrentChoice().getPrompt());
		player2.finishAction(Arrays.asList("Yes"));
		assertEquals(4, player2.getHand().size());
		assertEquals(6, player2.getDeck().size());
		assertEquals("Gold", player2.getDeck().get(0).getName());
		
		assertEquals("Would you like to trash this Fools Gold to gain a real gold to deck?", player2.getCurrentChoice().getPrompt());
		player2.finishAction(Arrays.asList("Yes"));
		assertEquals("Would you like to trash this Fools Gold to gain a real gold to deck?", player2.getCurrentChoice().getPrompt());
		player2.finishAction(Arrays.asList("No"));
		assertEquals(3, player2.getHand().size());
		assertEquals(7, player2.getDeck().size());
		
		
		assertNull(player2.getCurrentChoice());
	}
	
	@Test                                                                                         
    public void when_discarding_tunnel_gain_gold() {
		Bank realBank = new Bank(Arrays.asList("Tunnel", "Oasis"));
		Bank mockBank = spy(realBank);
		List<Card> x = new ArrayList<>();
		x.add(mockBank.oasis());
		x.add(mockBank.tunnel());
		for (int i=0; i<5; i++) { x.add(mockBank.copper());}
		x.add(mockBank.estate());
		x.add(mockBank.estate());
		x.add(mockBank.estate());
		
		when(mockBank.newDeck()).thenReturn(x);
		
		Game game = new Game(mockBank);
		
		Player player = mockPlayer("test", game);
		
		player.play("Oasis");
		player.finishAction(Arrays.asList("Tunnel"));
		
		assertEquals("Do you want to gain a gold for discarding the Tunnel?", player.getCurrentChoice().getPrompt());
		player.finishAction(Arrays.asList("Yes"));
		
		assertEquals(1, player.getBought().size());
		assertEquals(1, player.getDiscard().size());
		assertNotContains("Tunnel", player.getHand());	
		assertNull(player.getCurrentChoice());		
	}
	
	@Test                                                                                         
    public void when_discarding_tunnel_decline_gold() {
		Bank realBank = new Bank(Arrays.asList("Tunnel", "Oasis"));
		Bank mockBank = spy(realBank);
		List<Card> x = new ArrayList<>();
		x.add(mockBank.oasis());
		x.add(mockBank.tunnel());
		for (int i=0; i<5; i++) { x.add(mockBank.copper());}
		x.add(mockBank.estate());
		x.add(mockBank.estate());
		x.add(mockBank.estate());
		
		when(mockBank.newDeck()).thenReturn(x);
		
		Game game = new Game(mockBank);
		
		Player player = mockPlayer("test", game);
		
		player.play("Oasis");
		player.finishAction(Arrays.asList("Tunnel"));
		
		assertEquals("Do you want to gain a gold for discarding the Tunnel?", player.getCurrentChoice().getPrompt());
		player.finishAction(Arrays.asList("No"));
		
		assertEquals(0, player.getBought().size());
		assertEquals(1, player.getDiscard().size());
		assertNotContains("Tunnel", player.getHand());	
		assertNull(player.getCurrentChoice());		
	}
	
	@Test                                                                                         
    public void when_playing_jackofalltrades_no_discard_no_trash_correct() {
		Bank realBank = new Bank(Arrays.asList("Jack of All Trades"));
		Bank mockBank = spy(realBank);
		List<Card> x = new ArrayList<>();
		x.add(mockBank.jackofalltrades());
		for (int i=0; i<6; i++) { x.add(mockBank.copper());}
		x.add(mockBank.estate());
		x.add(mockBank.estate());
		x.add(mockBank.estate());
		
		when(mockBank.newDeck()).thenReturn(x);
		
		Game game = new Game(mockBank);
		
		Player player = mockPlayer("test", game);
		
		player.play("Copper");
		player.play("Copper");
		player.play("Jack of All Trades");
		assertEquals("Silver", player.getBought().get(0).getName());
		assertEquals("Would you like to discard:", player.getCurrentChoice().getPrompt());
		assertEquals(4, player.getDeck().size());
		assertEquals(1, player.getLiminal().size());
		
		player.finishAction(new ArrayList<String>());
		
		assertEquals(0, player.getLiminal().size());
		assertEquals(5, player.getHand().size());
		assertEquals(2, player.getDeck().size());
		assertEquals("Would you like to trash one:", player.getCurrentChoice().getPrompt());
		assertEquals(1, player.getCurrentChoice().getOptions().size());
		player.finishAction(new ArrayList<String>());
		assertNull(player.getCurrentChoice());
		assertEquals(2, player.getDeck().size());
		assertEquals(5, player.getHand().size());
	}
	
	@Test                                                                                         
    public void when_playing_multiple_jackofalltrades_liminal_correct() {
		Bank realBank = new Bank(Arrays.asList("Jack of All Trades", "Crossroads"));
		Bank mockBank = spy(realBank);
		List<Card> x = new ArrayList<>();
		x.add(mockBank.jackofalltrades());
		x.add(mockBank.jackofalltrades());
		x.add(mockBank.crossroads());
		for (int i=0; i<6; i++) { x.add(mockBank.copper());}
		x.add(mockBank.estate());
		
		when(mockBank.newDeck()).thenReturn(x);
		
		Game game = new Game(mockBank);
		
		Player player = mockPlayer("test", game);
		
		player.play("Crossroads");
		
		player.play("Jack of All Trades");
		assertEquals("Silver", player.getBought().get(0).getName());
		assertEquals("Would you like to discard:", player.getCurrentChoice().getPrompt());
		assertEquals(4, player.getDeck().size());
		assertEquals(1, player.getLiminal().size());
		
		player.finishAction(new ArrayList<String>());
		
		assertEquals(0, player.getLiminal().size());
		assertEquals(5, player.getHand().size());
		assertEquals(3, player.getDeck().size());
		assertEquals("Would you like to trash one:", player.getCurrentChoice().getPrompt());
		assertEquals(1, player.getCurrentChoice().getOptions().size());
		player.finishAction(new ArrayList<String>());
		assertNull(player.getCurrentChoice());
		assertEquals(3, player.getDeck().size());
		assertEquals(5, player.getHand().size());
		
		player.play("Jack of All Trades");
		assertEquals("Silver", player.getBought().get(1).getName());
		assertEquals("Would you like to discard:", player.getCurrentChoice().getPrompt());
		assertEquals(2, player.getDeck().size());
		assertEquals(1, player.getLiminal().size());
		
		player.finishAction(new ArrayList<String>());
		
		assertEquals(0, player.getLiminal().size());
		assertEquals(5, player.getHand().size());
		assertEquals(2, player.getDeck().size());
		assertEquals("Would you like to trash one:", player.getCurrentChoice().getPrompt());
		assertEquals(0, player.getCurrentChoice().getOptions().size());
		player.finishAction(new ArrayList<String>());
		assertNull(player.getCurrentChoice());
		assertEquals(2, player.getDeck().size());
		assertEquals(5, player.getHand().size());
		
	}
	
	@Test                                                                                         
    public void when_playing_jackofalltrades_with_discard_counts_match() {
		Bank realBank = new Bank(Arrays.asList("Jack of All Trades"));
		Bank mockBank = spy(realBank);
		List<Card> x = new ArrayList<>();
		x.add(mockBank.jackofalltrades());
		for (int i=0; i<6; i++) { x.add(mockBank.copper());}
		x.add(mockBank.estate());
		x.add(mockBank.estate());
		x.add(mockBank.estate());
		
		when(mockBank.newDeck()).thenReturn(x);
		
		Game game = new Game(mockBank);
		
		Player player = mockPlayer("test", game);
		
		player.play("Copper");
		player.play("Copper");
		player.play("Jack of All Trades");
		assertEquals("Silver", player.getBought().get(0).getName());
		assertEquals("Would you like to discard:", player.getCurrentChoice().getPrompt());
		assertEquals(4, player.getDeck().size());
		assertEquals(1, player.getLiminal().size());
		
		List<String> options = new ArrayList<String>();
		options.add("Estate");
		player.finishAction(options);
		
		assertEquals(5, player.getHand().size());
		assertEquals(1, player.getDeck().size());
		assertEquals(1, player.getDiscard().size());
		assertEquals("Would you like to trash one:", player.getCurrentChoice().getPrompt());
		assertEquals(2, player.getCurrentChoice().getOptions().size());
		player.finishAction(new ArrayList<String>());
		assertNull(player.getCurrentChoice());
		assertEquals(1, player.getDeck().size());
		assertEquals(5, player.getHand().size());
	}
	
	@Test                                                                                         
    public void when_playing_jackofalltrades_with_discard_and_trash_counts_match() {
		Bank realBank = new Bank(Arrays.asList("Jack of All Trades"));
		Bank mockBank = spy(realBank);
		List<Card> x = new ArrayList<>();
		x.add(mockBank.jackofalltrades());
		for (int i=0; i<6; i++) { x.add(mockBank.copper());}
		x.add(mockBank.estate());
		x.add(mockBank.estate());
		x.add(mockBank.estate());
		
		when(mockBank.newDeck()).thenReturn(x);
		
		Game game = new Game(mockBank);
		
		Player player = mockPlayer("test", game);
		
		player.play("Copper");
		player.play("Copper");
		player.play("Jack of All Trades");
		assertEquals("Silver", player.getBought().get(0).getName());
		assertEquals("Would you like to discard:", player.getCurrentChoice().getPrompt());
		assertEquals(4, player.getDeck().size());
		assertEquals(1, player.getLiminal().size());
		
		List<String> options = new ArrayList<String>();
		options.add("Estate");
		player.finishAction(options);
		
		assertEquals(5, player.getHand().size());
		assertEquals(1, player.getDeck().size());
		assertEquals(1, player.getDiscard().size());
		assertEquals("Would you like to trash one:", player.getCurrentChoice().getPrompt());
		assertEquals(2, player.getCurrentChoice().getOptions().size());
		
		player.finishAction(options);
		
		assertNull(player.getCurrentChoice());
		assertEquals(1, player.getDeck().size());
		assertEquals(4, player.getHand().size());
	}
	
	@Test                                                                                         
    public void when_playing_highway_silver_is_cheaper() {
		Bank realBank = new Bank(Arrays.asList("Highway"));
		Bank mockBank = spy(realBank);
		List<Card> x = new ArrayList<>();
		x.add(mockBank.highway());
		x.add(mockBank.estate());
		x.add(mockBank.estate());
		x.add(mockBank.estate());
		for (int i=0; i<6; i++) { x.add(mockBank.copper());}
		
		when(mockBank.newDeck()).thenReturn(x);
		
		Game game = new Game(mockBank);
		
		Player player = mockPlayer("test", game);
		
		player.play("Highway");
		player.play("Copper");
		player.play("Copper");
		
		assertEquals(3, player.getHand().size());
		player.buy("Silver");		
	}
	
	@Test                                                                                         
    public void when_playing_trader_get_silvers() {
		Bank realBank = new Bank(Arrays.asList("Trader"));
		Bank mockBank = spy(realBank);
		List<Card> x = new ArrayList<>();
		x.add(mockBank.trader());
		x.add(mockBank.estate());
		x.add(mockBank.estate());
		x.add(mockBank.estate());
		for (int i=0; i<6; i++) { x.add(mockBank.copper());}
		
		when(mockBank.newDeck()).thenReturn(x);
		
		Game game = new Game(mockBank);
		
		Player player = mockPlayer("test", game);
		
		player.play("Trader");
		player.finishAction(Arrays.asList("Estate"));
		
		assertEquals(2, player.getBought().size());
	}
	
	@Test                                                                                         
    public void when_playing_trader_with_highway_get_less_silvers() {
		Bank realBank = new Bank(Arrays.asList("Trader","Highway"));
		Bank mockBank = spy(realBank);
		List<Card> x = new ArrayList<>();
		x.add(mockBank.trader());
		x.add(mockBank.highway());
		x.add(mockBank.estate());
		x.add(mockBank.estate());
		x.add(mockBank.estate());
		for (int i=0; i<5; i++) { x.add(mockBank.copper());}
		
		when(mockBank.newDeck()).thenReturn(x);
		
		Game game = new Game(mockBank);
		
		Player player = mockPlayer("test", game);
		
		player.play("Highway");
		player.play("Trader");
		player.finishAction(Arrays.asList("Estate"));
		
		assertEquals(1, player.getBought().size());
	}
	
	@Test                                                                                         
    public void when_playing_oracle_action_correct() {
		Bank realBank = new Bank(Arrays.asList("Oracle"));
		Bank mockBank = spy(realBank);
		List<Card> x = new ArrayList<>();
		x.add(mockBank.oracle());
		for (int i=0; i<6; i++) { x.add(mockBank.copper());}
		x.add(mockBank.estate());
		x.add(mockBank.estate());
		x.add(mockBank.estate());
		
		List<Card> y = new ArrayList<>();
		y.add(mockBank.oracle());
		for (int i=0; i<6; i++) { y.add(mockBank.copper());}
		y.add(mockBank.estate());
		y.add(mockBank.estate());
		y.add(mockBank.estate());
		
		when(mockBank.newDeck()).thenReturn(x, y);
		
		Game game = new Game(mockBank);
		
		Player player = mockPlayer("test", game);
		assertEquals(5, player.getDeck().size());
		
		Player player2 = mockPlayer("test2", game);
		
		assertEquals(5, player.getDeck().size());
		assertEquals(5, player2.getDeck().size());
		
		player.play("Oracle");
		
		assertEquals(3, player.getDeck().size());
		assertEquals("Choose cards to discard:", player.getCurrentChoice().getPrompt());
		assertEquals(4, player.getCurrentChoice().getOptions().size());
		assertEquals("test : Copper", player.getCurrentChoice().getOptions().get(0));
		assertEquals("test2 : Copper", player.getCurrentChoice().getOptions().get(2));
		assertEquals(3, player.getDeck().size());
		
		player.finishAction(Arrays.asList("test2 : Copper","test2 : Copper"));
		
		assertEquals(3, player.getDeck().size());
		assertEquals(6, player.getHand().size());
		assertEquals(3, player2.getDeck().size());
		assertEquals(2, player2.getDiscard().size());
		
		assertNull(player.getCurrentChoice());
	}
}
