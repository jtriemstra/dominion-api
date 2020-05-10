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
import static org.mockito.ArgumentMatchers.anyList;
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

	private Player mockPlayer(String name, Game game) {
		Player realPlayer = new Player(name);
		Player player = spy(realPlayer);
		when(player.shuffle(anyList())).thenAnswer(i -> i.getArguments()[0]);
		player.init(game);
		game.addPlayer(player);
		
		return player;
	}
	
	@Test                                                                                         
    public void embassy() {
		Bank realBank = new Bank(Arrays.asList("Embassy"));
		Bank mockBank = spy(realBank);
		List<Card> x = new ArrayList<>();		
		for (int i=0; i<10; i++) { x.add(mockBank.copper());}
		when(mockBank.newDeck()).thenReturn(x);
		
		Game game = new Game(mockBank);
		
		Player player = mockPlayer("test", game);
		
		Player player2 = mockPlayer("test2", game);
		
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
    public void inn() {
		Bank realBank = new Bank(Arrays.asList("Inn","Village"));
		Bank mockBank = spy(realBank);
		List<Card> x = new ArrayList<>();		
		x.add(mockBank.village());
		for (int i=0; i<14; i++) { x.add(mockBank.copper());}
		when(mockBank.newDeck()).thenReturn(x);
		
		Game game = new Game(mockBank);
		
		Player player = mockPlayer("test", game);
		
		player.play("Village");
		player.cleanup();
		player.play("Copper");
		player.play("Copper");
		player.play("Copper");
		player.play("Copper");
		player.play("Copper");
		
		player.buy("Inn");
		
		assertEquals("Which action cards would you like to put in your deck?", player.getCurrentChoice().getPrompt());
		assertEquals(2, player.getCurrentChoice().getOptions().size());
		
		player.finishAction(Arrays.asList("Village","Inn"));
		player.cleanup();
		
		assertEquals(1, player.getDeck().size());
		assertEquals(5, player.getHand().size());
		assertEquals(10, player.getDiscard().size());
	}
	
	@Test                                                                                         
    public void cache() {
		Bank realBank = new Bank(Arrays.asList("Cache"));
		Bank mockBank = spy(realBank);
		List<Card> x = new ArrayList<>();		
		for (int i=0; i<10; i++) { x.add(mockBank.copper());}
		when(mockBank.newDeck()).thenReturn(x);
		
		Game game = new Game(mockBank);
		
		Player player = mockPlayer("test", game);
		
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
		
		Player player = mockPlayer("test", game);
		
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
		
		Player player = mockPlayer("test", game);
		
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
		
		Player player = mockPlayer("test", game);
		
		player.play("Silver");
		player.play("Silver");
		player.play("Silver");
		player.buy("Mandarin");
		
		assertEquals(0, player.getPlayed().size());		
		assertEquals(8, player.getDeck().size());		
	}
	
	@Test                                                                                         
    public void farmland() {
		Bank realBank = new Bank(Arrays.asList("Farmland", "Nomad Camp", "Border Village"));
		Bank mockBank = spy(realBank);
		List<Card> x = new ArrayList<>();
		x.add(mockBank.estate());
		for (int i=0; i<9; i++) { x.add(mockBank.silver());}
		when(mockBank.newDeck()).thenReturn(x);
		
		Game game = new Game(mockBank);
		
		Player player = mockPlayer("test", game);
		
		player.play("Silver");
		player.play("Silver");
		player.play("Silver");
		player.buy("Farmland");
		
		assertEquals(3, player.getPlayed().size());		
		assertEquals("Choose a card to trash (Farmland)", player.getCurrentChoice().getPrompt());
		
		player.finishAction(Arrays.asList("Estate"));
		
		assertEquals("Choose a card to gain from Farmland", player.getCurrentChoice().getPrompt());
		assertEquals(1, player.getCurrentChoice().getOptions().size());
		
		player.finishAction(Arrays.asList("Nomad Camp"));
		
		assertNull(player.getCurrentChoice());
	}
	
	@Test                                                                                         
    public void when_buy_farmland_with_empty_hand_no_choice_required() {
		Bank realBank = new Bank(Arrays.asList("Farmland"));
		Bank mockBank = spy(realBank);
		List<Card> x = new ArrayList<>();
		x.add(mockBank.silver());
		for (int i=0; i<9; i++) { x.add(mockBank.copper());}
		when(mockBank.newDeck()).thenReturn(x);
		
		Game game = new Game(mockBank);
		
		Player player = mockPlayer("test", game);
		
		player.play("Silver");
		player.play("Copper");
		player.play("Copper");
		player.play("Copper");
		player.play("Copper");
		player.buy("Farmland");
		
		assertEquals(5, player.getPlayed().size());		
		assertEquals("Choose a card to trash (Farmland)", player.getCurrentChoice().getPrompt());
		assertEquals(0, player.getCurrentChoice().getOptions().size());
		
		player.finishAction(new ArrayList<String>());
		
		assertNull(player.getCurrentChoice());
	}
	
	@Test                                                                                         
    public void farmlandWithHighway() {
		Bank realBank = new Bank(Arrays.asList("Farmland", "Nomad Camp", "Border Village", "Highway"));
		Bank mockBank = spy(realBank);
		List<Card> x = new ArrayList<>();
		x.add(mockBank.estate());
		x.add(mockBank.highway());
		for (int i=0; i<8; i++) { x.add(mockBank.silver());}
		when(mockBank.newDeck()).thenReturn(x);
		
		Game game = new Game(mockBank);
		
		Player player = mockPlayer("test", game);
		
		player.play("Highway");
		player.play("Silver");
		player.play("Silver");
		player.play("Silver");
		player.buy("Farmland");
			
		assertEquals(4, player.getPlayed().size());		
		assertEquals("Choose a card to trash (Farmland)", player.getCurrentChoice().getPrompt());
		
		player.finishAction(Arrays.asList("Estate"));
		
		assertEquals("Choose a card to gain from Farmland", player.getCurrentChoice().getPrompt());
		assertEquals(1, player.getCurrentChoice().getOptions().size());
		
		player.finishAction(Arrays.asList("Nomad Camp"));
		
		assertNull(player.getCurrentChoice());
	}
	
	@Test                                                                                         
    public void haggler() {
		Bank realBank = new Bank(Arrays.asList("Farmland", "Nomad Camp", "Border Village", "Haggler", "Province"));
		Bank mockBank = spy(realBank);
		List<Card> x = new ArrayList<>();
		x.add(mockBank.haggler());
		x.add(mockBank.gold());
		x.add(mockBank.gold());
		
		for (int i=0; i<9; i++) { x.add(mockBank.silver());}
		when(mockBank.newDeck()).thenReturn(x);
		
		Game game = new Game(mockBank);
		
		Player player = mockPlayer("test", game);
		
		player.play("Gold");
		player.play("Gold");
		player.play("Haggler");
		player.buy("Province");
				
		assertEquals("Choose a card to gain from Haggler", player.getCurrentChoice().getPrompt());
		assertEquals(6, player.getCurrentChoice().getOptions().size());
		
		player.finishAction(Arrays.asList("Gold"));
		
		assertNull(player.getCurrentChoice());
	}
	
	@Test
	public void developAndFarmland() {
		Bank realBank = new Bank(Arrays.asList("Farmland", "Nomad Camp", "Border Village", "Develop", "Embassy"));
		Bank mockBank = spy(realBank);
		List<Card> x = new ArrayList<>();
		x.add(mockBank.develop());
		x.add(mockBank.embassy());
		x.add(mockBank.gold());
		x.add(mockBank.gold());
		for (int i=0; i<9; i++) { x.add(mockBank.silver());}
		when(mockBank.newDeck()).thenReturn(x);
		
		Game game = new Game(mockBank);
		
		Player player = mockPlayer("test", game);
		
		player.play("Develop");
				
		assertEquals("Choose a card to trash", player.getCurrentChoice().getPrompt());
		assertEquals(4, player.getCurrentChoice().getOptions().size());
		
		player.finishAction(Arrays.asList("Embassy"));
		
		assertEquals("Choose a card to gain", player.getCurrentChoice().getPrompt());
		assertEquals(4, player.getCurrentChoice().getOptions().size());
		
		player.finishAction(Arrays.asList("Farmland"));
		
		assertEquals("Choose a card to gain", player.getCurrentChoice().getPrompt());
		assertEquals(1, player.getCurrentChoice().getOptions().size());
		
		player.finishAction(Arrays.asList("Nomad Camp"));
		
		assertNull(player.getCurrentChoice());
	}
	
	@Test
	public void developAndIllGotten() {
		Bank realBank = new Bank(Arrays.asList("Ill-Gotten Gains", "Nomad Camp", "Border Village", "Develop", "Embassy"));
		Bank mockBank = spy(realBank);
		List<Card> x = new ArrayList<>();
		x.add(mockBank.develop());
		x.add(mockBank.nomadcamp());
		x.add(mockBank.gold());
		x.add(mockBank.gold());
		for (int i=0; i<9; i++) { x.add(mockBank.silver());}
		when(mockBank.newDeck()).thenReturn(x);
		
		Game game = new Game(mockBank);
		
		Player player = mockPlayer("test", game);
		
		Player player2 = mockPlayer("test2", game);
		
		player.play("Develop");
				
		assertEquals("Choose a card to trash", player.getCurrentChoice().getPrompt());
		assertEquals(4, player.getCurrentChoice().getOptions().size());
		
		player.finishAction(Arrays.asList("Nomad Camp"));
		
		assertEquals("Choose a card to gain", player.getCurrentChoice().getPrompt());
		assertEquals(5, player.getCurrentChoice().getOptions().size());
		assertEquals(0, player2.getDiscard().size());
		
		player.finishAction(Arrays.asList("Ill-Gotten Gains"));
		
		assertEquals(1, player2.getDiscard().size());
		assertEquals("Curse", player2.getDiscard().get(0).getName());
		
		assertEquals("Choose a card to gain", player.getCurrentChoice().getPrompt());
		assertEquals(2, player.getCurrentChoice().getOptions().size());
		
		player.finishAction(Arrays.asList("Silver"));
		
		assertNull(player.getCurrentChoice());
		
	}
	
	@Test
	public void traderAndCache() {
		Bank realBank = new Bank(Arrays.asList("Cache", "Trader"));
		Bank mockBank = spy(realBank);
		List<Card> x = new ArrayList<>();
		x.add(mockBank.trader());
		x.add(mockBank.gold());
		x.add(mockBank.gold());
		for (int i=0; i<9; i++) { x.add(mockBank.silver());}
		when(mockBank.newDeck()).thenReturn(x);
		
		Game game = new Game(mockBank);
		
		Player player = mockPlayer("test", game);
		
		player.play("Gold");
		player.play("Gold");
		player.buy("Cache");
		
		assertEquals(0, player.getBought().size());
		assertEquals(0, player.getDiscard().size());
				
		assertEquals("Would you like to gain a silver instead of the regular card Cache", player.getCurrentChoice().getPrompt());
		player.finishAction(Arrays.asList("No"));
		
		assertEquals(1, player.getBought().size());
		assertEquals("Would you like to gain a silver instead of the regular card Copper", player.getCurrentChoice().getPrompt());
		player.finishAction(Arrays.asList("Yes"));
		assertEquals(2, player.getBought().size());
		
		assertEquals("Would you like to gain a silver instead of the regular card Copper", player.getCurrentChoice().getPrompt());
		player.finishAction(Arrays.asList("Yes"));
		assertEquals(3, player.getBought().size());
		assertEquals("Silver", player.getBought().get(0).getName());
		assertEquals("Silver", player.getBought().get(1).getName());
				
		assertNull(player.getCurrentChoice());
	}
	
	@Test
	public void traderAndIllGottenGains() {
		Bank realBank = new Bank(Arrays.asList("Ill-Gotten Gains", "Trader"));
		Bank mockBank = spy(realBank);
		List<Card> x = new ArrayList<>();
		x.add(mockBank.trader());
		x.add(mockBank.illgottengains());
		x.add(mockBank.gold());
		x.add(mockBank.gold());
		for (int i=0; i<9; i++) { x.add(mockBank.copper());}
		when(mockBank.newDeck()).thenReturn(x);
		
		Game game = new Game(mockBank);
		
		Player player = mockPlayer("test", game);
		
		player.play("Ill-Gotten Gains");
		assertEquals("Would you like to gain a Copper?", player.getCurrentChoice().getPrompt());
		player.finishAction(Arrays.asList("Yes"));
				
		assertEquals("Would you like to gain a silver instead of the regular card Copper", player.getCurrentChoice().getPrompt());
		player.finishAction(Arrays.asList("Yes"));
		
		assertEquals(0, player.getBought().size());
		assertEquals(5, player.getHand().size());
		assertEquals(true, player.getHand().stream().anyMatch(c -> c.getName().equals("Silver")));
				
		assertNull(player.getCurrentChoice());
	}
	
	@Test
	public void traderAndCacheTakeFirstSilver() {
		Bank realBank = new Bank(Arrays.asList("Cache", "Trader"));
		Bank mockBank = spy(realBank);
		List<Card> x = new ArrayList<>();
		x.add(mockBank.trader());
		x.add(mockBank.gold());
		x.add(mockBank.gold());
		for (int i=0; i<9; i++) { x.add(mockBank.silver());}
		when(mockBank.newDeck()).thenReturn(x);
		
		Game game = new Game(mockBank);
		
		Player player = mockPlayer("test", game);
		
		player.play("Gold");
		player.play("Gold");
		player.buy("Cache");
		
		assertEquals(0, player.getBought().size());
		assertEquals(0, player.getDiscard().size());
				
		assertEquals("Would you like to gain a silver instead of the regular card Cache", player.getCurrentChoice().getPrompt());
		player.finishAction(Arrays.asList("Yes"));
		
		assertEquals(1, player.getBought().size());
		assertEquals("Silver", player.getBought().get(0).getName());
				
		assertNull(player.getCurrentChoice());
	}
	
	@Test
	public void borderVillageTraderAndCache() {
		Bank realBank = new Bank(Arrays.asList("Cache", "Trader", "Border Village"));
		Bank mockBank = spy(realBank);
		List<Card> x = new ArrayList<>();
		x.add(mockBank.trader());
		x.add(mockBank.gold());
		x.add(mockBank.gold());
		for (int i=0; i<9; i++) { x.add(mockBank.silver());}
		when(mockBank.newDeck()).thenReturn(x);
		
		Game game = new Game(mockBank);
		
		Player player = mockPlayer("test", game);
		
		player.play("Gold");
		player.play("Gold");
		player.buy("Border Village");
		
		assertEquals(0, player.getBought().size());
		assertEquals(0, player.getDiscard().size());
				
		assertEquals("Would you like to gain a silver instead of the regular card Border Village", player.getCurrentChoice().getPrompt());
		
		player.finishAction(Arrays.asList("No"));
		
		assertEquals(1, player.getBought().size());
		
		assertEquals("Choose an extra card to gain", player.getCurrentChoice().getPrompt());
		assertEquals(7, player.getCurrentChoice().getOptions().size());
		
		player.finishAction(Arrays.asList("Cache"));
		
		assertEquals("Would you like to gain a silver instead of the regular card Cache", player.getCurrentChoice().getPrompt());
		player.finishAction(Arrays.asList("No"));
		
		assertEquals("Would you like to gain a silver instead of the regular card Copper", player.getCurrentChoice().getPrompt());
		player.finishAction(Arrays.asList("Yes"));
		
		assertEquals("Would you like to gain a silver instead of the regular card Copper", player.getCurrentChoice().getPrompt());
		player.finishAction(Arrays.asList("Yes"));
		
		assertEquals(4, player.getBought().size());
		assertEquals("Silver", player.getBought().get(0).getName());
		assertEquals("Silver", player.getBought().get(1).getName());
		assertEquals("Cache", player.getBought().get(2).getName());
				
		assertNull(player.getCurrentChoice());
	}
	
	@Test
	public void farmlandTraderAndCache() {
		Bank realBank = new Bank(Arrays.asList("Cache", "Trader", "Farmland"));
		Bank mockBank = spy(realBank);
		List<Card> x = new ArrayList<>();
		x.add(mockBank.trader());
		x.add(mockBank.gold());
		x.add(mockBank.gold());
		for (int i=0; i<9; i++) { x.add(mockBank.silver());}
		when(mockBank.newDeck()).thenReturn(x);
		
		Game game = new Game(mockBank);
		
		Player player = mockPlayer("test", game);
		
		player.play("Gold");
		player.play("Gold");
		player.buy("Farmland");
		
		assertEquals(0, player.getBought().size());
		assertEquals(0, player.getDiscard().size());
		
		assertEquals("Choose a card to trash (Farmland)", player.getCurrentChoice().getPrompt());
		assertEquals(3, player.getCurrentChoice().getOptions().size());
		
		player.finishAction(Arrays.asList("Silver"));
		assertEquals(2, player.getHand().size());
		
		assertEquals("Choose a card to gain from Farmland", player.getCurrentChoice().getPrompt());
		assertEquals(2, player.getCurrentChoice().getOptions().size());
		
		player.finishAction(Arrays.asList("Cache"));
		
		assertEquals("Would you like to gain a silver instead of the regular card Farmland", player.getCurrentChoice().getPrompt());
		
		player.finishAction(Arrays.asList("No"));
		
		assertEquals(1, player.getBought().size());
		
		assertEquals("Would you like to gain a silver instead of the regular card Cache", player.getCurrentChoice().getPrompt());
		player.finishAction(Arrays.asList("No"));
		
		assertEquals("Would you like to gain a silver instead of the regular card Copper", player.getCurrentChoice().getPrompt());
		player.finishAction(Arrays.asList("Yes"));
		
		assertEquals("Would you like to gain a silver instead of the regular card Copper", player.getCurrentChoice().getPrompt());
		player.finishAction(Arrays.asList("No"));
		
		assertEquals(4, player.getBought().size());
		assertEquals("Copper", player.getBought().get(0).getName());
		assertEquals("Silver", player.getBought().get(1).getName());
		assertEquals("Cache", player.getBought().get(2).getName());
		assertEquals("Farmland", player.getBought().get(3).getName());
				
		assertNull(player.getCurrentChoice());
	}
	
	@Test
	public void hagglerFarmlandTraderAndCache() {
		Bank realBank = new Bank(Arrays.asList("Cache", "Trader", "Haggler", "Farmland"));
		Bank mockBank = spy(realBank);
		List<Card> x = new ArrayList<>();
		x.add(mockBank.trader());
		x.add(mockBank.haggler());
		x.add(mockBank.gold());
		x.add(mockBank.gold());
		for (int i=0; i<9; i++) { x.add(mockBank.silver());}
		when(mockBank.newDeck()).thenReturn(x);
		
		Game game = new Game(mockBank);
		
		Player player = mockPlayer("test", game);
		
		player.play("Haggler");
		player.play("Gold");
		player.play("Gold");
		player.buy("Farmland");
		
		assertEquals(0, player.getBought().size());
		assertEquals(0, player.getDiscard().size());
		
		assertEquals("Choose a card to trash (Farmland)", player.getCurrentChoice().getPrompt());
		assertEquals(2, player.getCurrentChoice().getOptions().size());
		
		player.finishAction(Arrays.asList("Silver"));
		assertEquals(1, player.getHand().size());
		
		assertEquals("Choose a card to gain from Farmland", player.getCurrentChoice().getPrompt());
		player.finishAction(Arrays.asList("Cache"));
		
		assertEquals("Would you like to gain a silver instead of the regular card Farmland", player.getCurrentChoice().getPrompt());
		player.finishAction(Arrays.asList("No"));
		
		assertEquals("Choose a card to gain from Haggler", player.getCurrentChoice().getPrompt());
		assertEquals(5, player.getCurrentChoice().getOptions().size());
		player.finishAction(Arrays.asList("Cache"));
		
		assertEquals("Would you like to gain a silver instead of the regular card Cache", player.getCurrentChoice().getPrompt());
		player.finishAction(Arrays.asList("No"));
		
		assertEquals("Would you like to gain a silver instead of the regular card Cache", player.getCurrentChoice().getPrompt());
		player.finishAction(Arrays.asList("No"));
		
		assertEquals("Would you like to gain a silver instead of the regular card Copper", player.getCurrentChoice().getPrompt());
		player.finishAction(Arrays.asList("Yes"));
				
		assertEquals("Would you like to gain a silver instead of the regular card Copper", player.getCurrentChoice().getPrompt());
		player.finishAction(Arrays.asList("Yes"));
		
		assertEquals("Would you like to gain a silver instead of the regular card Copper", player.getCurrentChoice().getPrompt());
		player.finishAction(Arrays.asList("Yes"));
				
		assertEquals("Would you like to gain a silver instead of the regular card Copper", player.getCurrentChoice().getPrompt());
		player.finishAction(Arrays.asList("Yes"));
		
		
		assertEquals(7, player.getBought().size());
		assertEquals("Silver", player.getBought().get(0).getName());
		assertEquals("Silver", player.getBought().get(1).getName());
		assertEquals("Silver", player.getBought().get(2).getName());
		assertEquals("Silver", player.getBought().get(3).getName());
		assertEquals("Cache", player.getBought().get(4).getName());
		assertEquals("Cache", player.getBought().get(5).getName());
		assertEquals("Farmland", player.getBought().get(6).getName());
				
		assertNull(player.getCurrentChoice());
	}
	
	@Test
	public void developTraderAndCache() {
		Bank realBank = new Bank(Arrays.asList("Cache", "Trader", "Develop"));
		Bank mockBank = spy(realBank);
		List<Card> x = new ArrayList<>();
		x.add(mockBank.trader());
		x.add(mockBank.trader());
		x.add(mockBank.develop());
		x.add(mockBank.gold());
		x.add(mockBank.gold());
		for (int i=0; i<9; i++) { x.add(mockBank.silver());}
		when(mockBank.newDeck()).thenReturn(x);
		
		Game game = new Game(mockBank);
		
		Player player = mockPlayer("test", game);
		
		player.play("Develop");
		
		assertEquals(0, player.getBought().size());
		assertEquals(0, player.getDiscard().size());
		
		assertEquals("Choose a card to trash", player.getCurrentChoice().getPrompt());
		assertEquals(4, player.getCurrentChoice().getOptions().size());
		
		player.finishAction(Arrays.asList("Trader"));
		assertEquals(3, player.getHand().size());
		
		assertEquals("Choose a card to gain", player.getCurrentChoice().getPrompt());
		assertEquals(4, player.getCurrentChoice().getOptions().size());
		player.finishAction(Arrays.asList("Cache"));
		
		assertEquals("Would you like to gain a silver instead of the regular card Cache", player.getCurrentChoice().getPrompt());
		player.finishAction(Arrays.asList("No"));
		
		assertEquals("Choose a card to gain", player.getCurrentChoice().getPrompt());
		assertEquals(2, player.getCurrentChoice().getOptions().size());
		player.finishAction(Arrays.asList("Silver"));
		
		assertEquals("Would you like to gain a silver instead of the regular card Copper", player.getCurrentChoice().getPrompt());
		player.finishAction(Arrays.asList("Yes"));
				
		assertEquals("Would you like to gain a silver instead of the regular card Copper", player.getCurrentChoice().getPrompt());
		player.finishAction(Arrays.asList("Yes"));
		
		assertEquals("Would you like to gain a silver instead of the regular card Silver", player.getCurrentChoice().getPrompt());
		player.finishAction(Arrays.asList("No"));
		
		assertEquals(2, player.getBought().size());
		assertEquals("Silver", player.getBought().get(0).getName());
		assertEquals("Silver", player.getBought().get(1).getName());
		assertEquals(11, player.getDeck().size());
		assertEquals("Silver", player.getDeck().get(0).getName());
		assertEquals("Cache", player.getDeck().get(1).getName());
		
				
		assertNull(player.getCurrentChoice());
	}

	@Test
	public void developBorderVillageDuchess() {
		Bank realBank = new Bank(Arrays.asList("Border Village", "Duchess", "Develop"));
		Bank mockBank = spy(realBank);
		List<Card> x = new ArrayList<>();
		x.add(mockBank.haggler());
		x.add(mockBank.develop());
		x.add(mockBank.gold());
		x.add(mockBank.gold());
		for (int i=0; i<9; i++) { x.add(mockBank.silver());}
		when(mockBank.newDeck()).thenReturn(x);
		
		Game game = new Game(mockBank);
		
		Player player = mockPlayer("test", game);
		
		player.play("Develop");
		
		assertEquals(0, player.getBought().size());
		assertEquals(0, player.getDiscard().size());
		
		assertEquals("Choose a card to trash", player.getCurrentChoice().getPrompt());
		assertEquals(4, player.getCurrentChoice().getOptions().size());
		
		player.finishAction(Arrays.asList("Haggler"));
		assertEquals(3, player.getHand().size());
		
		assertEquals("Choose a card to gain", player.getCurrentChoice().getPrompt());
		assertEquals(2, player.getCurrentChoice().getOptions().size());
		player.finishAction(Arrays.asList("Border Village"));
		
		assertEquals("Choose an extra card to gain", player.getCurrentChoice().getPrompt());
		assertEquals(7, player.getCurrentChoice().getOptions().size());
		player.finishAction(Arrays.asList("Duchy"));
		
		assertEquals("Choose a card to gain", player.getCurrentChoice().getPrompt());
		assertEquals(0, player.getCurrentChoice().getOptions().size());
		player.finishAction(new ArrayList<String>());
		
		assertEquals("Would you like to gain a Duchess", player.getCurrentChoice().getPrompt());
		player.finishAction(Arrays.asList("Yes"));
		
		assertEquals(2, player.getBought().size());
		assertEquals("Border Village", player.getDeck().get(0).getName());
				
		assertNull(player.getCurrentChoice());
	}
}
