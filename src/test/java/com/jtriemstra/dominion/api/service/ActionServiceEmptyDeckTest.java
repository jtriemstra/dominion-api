package com.jtriemstra.dominion.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ActionServiceEmptyDeckTest extends ActionServiceTestBase {
	
	@Test
	public void adventurer() {
		swapHandCards(ActionService.ADVENTURER);
		loadDiscard(ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.ADVENTURER);
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("")));
		actionService.doChoice(gameState, "test");
		
		Assertions.assertEquals(6, playerState.getHand().size());
		Assertions.assertEquals(0, playerState.getDiscard().size());
		Assertions.assertEquals(4, playerState.getDeck().size());
	}

	@Test
	public void bandit() {
		swapHandCards(ActionService.BANDIT);
		loadDiscard(ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER);
		loadDiscard(this.playerState2, ActionService.COPPER, ActionService.COPPER);
		loadDiscard(this.playerState3, ActionService.COPPER, ActionService.COPPER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.BANDIT);
				
		Assertions.assertEquals(4, playerState.getHand().size());
		Assertions.assertEquals(7, playerState.getDiscard().size());
		Assertions.assertEquals(0, playerState.getDeck().size());
		Assertions.assertEquals(0, playerState.getRevealing().size());
		Assertions.assertEquals(2, playerState2.getRevealing().size());
		Assertions.assertEquals(2, playerState3.getRevealing().size());
		Assertions.assertEquals(0, playerState2.getDeck().size());
		Assertions.assertEquals(0, playerState3.getDeck().size());
		Assertions.assertEquals(0, playerState2.getDiscard().size());
		Assertions.assertEquals(0, playerState3.getDiscard().size());
	}

	
	@Test
	public void cartographer() {
		swapHandCards(ActionService.CARTOGRAPHER);
		loadDiscard(ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.CARTOGRAPHER);
				
		Assertions.assertEquals(5, playerState.getHand().size());
		Assertions.assertEquals(0, playerState.getDiscard().size());
		Assertions.assertEquals(1, playerState.getDeck().size());
		Assertions.assertEquals(4, playerState.getLooking().size());
	}

	@Test
	public void cellar() {
		swapHandCards(ActionService.CELLAR);
		loadDiscard(ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.CELLAR);
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper","Copper")));
		actionService.doChoice(gameState, "test");
		
		Assertions.assertEquals(4, playerState.getHand().size());
		Assertions.assertEquals(0, playerState.getDiscard().size());
		Assertions.assertEquals(6, playerState.getDeck().size());
	}

	@Test
	public void chancellor() {
		swapHandCards(ActionService.CHANCELLOR);
		loadDiscard(ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.CHANCELLOR);
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("YES")));
		actionService.doChoice(gameState, "test");
		
		Assertions.assertEquals(4, playerState.getHand().size());
		Assertions.assertEquals(6, playerState.getDiscard().size());
		Assertions.assertEquals(0, playerState.getDeck().size());
	}

	@Test
	public void councilRoom() {
		swapHandCards(ActionService.COUNCIL_ROOM);
		loadDiscard(this.playerState2, ActionService.COPPER);
		loadDiscard(this.playerState3, ActionService.COPPER);
		loadDiscard(ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.COUNCIL_ROOM);
		
		Assertions.assertEquals(8, playerState.getHand().size());
		Assertions.assertEquals(0, playerState.getDiscard().size());
		Assertions.assertEquals(2, playerState.getDeck().size());
	}

	@Test
	public void crossroads() {
		swapHandCards(ActionService.CROSSROADS, ActionService.ESTATE, ActionService.ESTATE);
		loadDiscard(ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.CROSSROADS);
				
		Assertions.assertEquals(6, playerState.getHand().size());
		Assertions.assertEquals(0, playerState.getDiscard().size());
		Assertions.assertEquals(4, playerState.getDeck().size());
	}
	
	@Test
	public void duchess() {
		swapHandCards(ActionService.DUCHESS);
		loadDiscard(ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER);
		loadDiscard(this.playerState2, ActionService.COPPER);
		loadDiscard(this.playerState3, ActionService.COPPER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.DUCHESS);
				
		Assertions.assertEquals(4, playerState.getHand().size());
		Assertions.assertEquals(0, playerState.getDiscard().size());
		Assertions.assertEquals(5, playerState.getDeck().size());
		Assertions.assertEquals(1, playerState.getLooking().size());
	}

	@Test
	public void embassy() {
		swapHandCards(ActionService.EMBASSY);
		loadDiscard(ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.EMBASSY);
				
		Assertions.assertEquals(9, playerState.getHand().size());
		Assertions.assertEquals(0, playerState.getDiscard().size());
		Assertions.assertEquals(1, playerState.getDeck().size());
	}

	@Test
	public void guardDog() {
		swapHandCards(ActionService.GUARD_DOG);
		loadDiscard(ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.GUARD_DOG);
				
		Assertions.assertEquals(6, playerState.getHand().size());
		Assertions.assertEquals(0, playerState.getDiscard().size());
		Assertions.assertEquals(4, playerState.getDeck().size());
	}

	@Test
	public void harbinger() {
		swapHandCards(ActionService.HARBINGER);
		loadDiscard(ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.HARBINGER);
				
		Assertions.assertEquals(5, playerState.getHand().size());
		Assertions.assertEquals(0, playerState.getDiscard().size());
		Assertions.assertEquals(5, playerState.getDeck().size());
	}

	@Test
	public void highway() {
		swapHandCards(ActionService.HIGHWAY);
		loadDiscard(ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.HIGHWAY);
				
		Assertions.assertEquals(5, playerState.getHand().size());
		Assertions.assertEquals(0, playerState.getDiscard().size());
		Assertions.assertEquals(5, playerState.getDeck().size());
	}

	@Test
	public void inn() {
		swapHandCards(ActionService.INN);
		loadDiscard(ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.INN);
				
		Assertions.assertEquals(6, playerState.getHand().size());
		Assertions.assertEquals(0, playerState.getDiscard().size());
		Assertions.assertEquals(4, playerState.getDeck().size());
	}

	@Test
	public void jackOfAllTrades() {
		swapHandCards(ActionService.JACK_OF_ALL_TRADES);
		loadDiscard(ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.JACK_OF_ALL_TRADES);
				
		Assertions.assertEquals(4, playerState.getHand().size());
		Assertions.assertEquals(0, playerState.getDiscard().size());
		Assertions.assertEquals(6, playerState.getDeck().size());
		Assertions.assertEquals(1, playerState.getLooking().size());
	}

	@Test
	public void laboratory() {
		swapHandCards(ActionService.LABORATORY);
		loadDiscard(ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.LABORATORY);
				
		Assertions.assertEquals(6, playerState.getHand().size());
		Assertions.assertEquals(0, playerState.getDiscard().size());
		Assertions.assertEquals(4, playerState.getDeck().size());
	}

	@Test
	public void library() {
		swapHandCards(ActionService.LIBRARY);
		loadDiscard(ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.LIBRARY);
				
		Assertions.assertEquals(7, playerState.getHand().size());
		Assertions.assertEquals(0, playerState.getDiscard().size());
		Assertions.assertEquals(3, playerState.getDeck().size());
	}

	@Test
	public void margrave() {
		swapHandCards(ActionService.MARGRAVE);
		loadDiscard(ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER);
		loadDiscard(this.playerState2, ActionService.COPPER);
		loadDiscard(this.playerState3, ActionService.COPPER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.MARGRAVE);
				
		Assertions.assertEquals(7, playerState.getHand().size());
		Assertions.assertEquals(0, playerState.getDiscard().size());
		Assertions.assertEquals(3, playerState.getDeck().size());
	}

	@Test
	public void market() {
		swapHandCards(ActionService.MARKET);
		loadDiscard(ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.MARKET);
				
		Assertions.assertEquals(5, playerState.getHand().size());
		Assertions.assertEquals(0, playerState.getDiscard().size());
		Assertions.assertEquals(5, playerState.getDeck().size());
	}

	@Test
	public void merchant() {
		swapHandCards(ActionService.MERCHANT);
		loadDiscard(ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.MERCHANT);
				
		Assertions.assertEquals(5, playerState.getHand().size());
		Assertions.assertEquals(0, playerState.getDiscard().size());
		Assertions.assertEquals(5, playerState.getDeck().size());
	}

	@Test
	public void moat() {
		swapHandCards(ActionService.MOAT);
		loadDiscard(ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.MOAT);
				
		Assertions.assertEquals(6, playerState.getHand().size());
		Assertions.assertEquals(0, playerState.getDiscard().size());
		Assertions.assertEquals(4, playerState.getDeck().size());
	}

	@Test
	public void nobleBrigand() {
		swapHandCards(ActionService.NOBLE_BRIGAND);
		loadDiscard(ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER);
		loadDiscard(this.playerState2, ActionService.COPPER, ActionService.COPPER);
		loadDiscard(this.playerState3, ActionService.COPPER, ActionService.COPPER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.NOBLE_BRIGAND);
				
		Assertions.assertEquals(4, playerState.getHand().size());
		Assertions.assertEquals(6, playerState.getDiscard().size());
		Assertions.assertEquals(0, playerState.getDeck().size());
	}

	@Test
	public void oasis() {
		swapHandCards(ActionService.OASIS);
		loadDiscard(ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.OASIS);
				
		Assertions.assertEquals(5, playerState.getHand().size());
		Assertions.assertEquals(0, playerState.getDiscard().size());
		Assertions.assertEquals(5, playerState.getDeck().size());
	}

	@Test
	public void oracle() {
		swapHandCards(ActionService.ORACLE);
		loadDiscard(ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER);
		loadDiscard(this.playerState2, ActionService.COPPER, ActionService.COPPER);
		loadDiscard(this.playerState3, ActionService.COPPER, ActionService.COPPER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.ORACLE);
				
		Assertions.assertEquals(4, playerState.getHand().size());
		Assertions.assertEquals(0, playerState.getDiscard().size());
		Assertions.assertEquals(4, playerState.getDeck().size());
		Assertions.assertEquals(2, playerState.getRevealing().size());
		Assertions.assertEquals(2, playerState2.getRevealing().size());
		Assertions.assertEquals(2, playerState3.getRevealing().size());
		Assertions.assertEquals(0, playerState2.getDeck().size());
		Assertions.assertEquals(0, playerState3.getDeck().size());
		Assertions.assertEquals(0, playerState2.getDiscard().size());
		Assertions.assertEquals(0, playerState3.getDiscard().size());
	}

	@Test
	public void poacher() {
		swapHandCards(ActionService.POACHER);
		loadDiscard(ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.POACHER);
				
		Assertions.assertEquals(5, playerState.getHand().size());
		Assertions.assertEquals(0, playerState.getDiscard().size());
		Assertions.assertEquals(5, playerState.getDeck().size());
	}

	@Test
	public void scheme() {
		swapHandCards(ActionService.SCHEME);
		loadDiscard(ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.SCHEME);
				
		Assertions.assertEquals(5, playerState.getHand().size());
		Assertions.assertEquals(0, playerState.getDiscard().size());
		Assertions.assertEquals(5, playerState.getDeck().size());
	}

	@Test
	public void sentry() {
		swapHandCards(ActionService.SENTRY);
		loadDiscard(ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.SENTRY);
				
		Assertions.assertEquals(5, playerState.getHand().size());
		Assertions.assertEquals(0, playerState.getDiscard().size());
		Assertions.assertEquals(3, playerState.getDeck().size());
		Assertions.assertEquals(2, playerState.getLooking().size());
	}

	@Test
	public void smithy() {
		swapHandCards(ActionService.SMITHY);
		loadDiscard(ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.SMITHY);
				
		Assertions.assertEquals(7, playerState.getHand().size());
		Assertions.assertEquals(0, playerState.getDiscard().size());
		Assertions.assertEquals(3, playerState.getDeck().size());
	}

	@Test
	public void spiceMerchant() {
		swapHandCards(ActionService.SPICE_MERCHANT);
		loadDiscard(ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.SPICE_MERCHANT);
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper")));
		actionService.doChoice(gameState, "test");
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("CARDS")));
		actionService.doChoice(gameState, "test");
				
		Assertions.assertEquals(5, playerState.getHand().size());
		Assertions.assertEquals(0, playerState.getDiscard().size());
		Assertions.assertEquals(4, playerState.getDeck().size());
	}

	@Test
	public void stables() {
		swapHandCards(ActionService.STABLES);
		loadDiscard(ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.STABLES);
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper")));
		actionService.doChoice(gameState, "test");
				
		Assertions.assertEquals(6, playerState.getHand().size());
		Assertions.assertEquals(0, playerState.getDiscard().size());
		Assertions.assertEquals(4, playerState.getDeck().size());
	}

	@Test
	public void spy() {
		swapHandCards(ActionService.SPY);
		loadDiscard(ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER);
		loadDiscard(this.playerState2, ActionService.COPPER);
		loadDiscard(this.playerState3, ActionService.COPPER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.SPY);
				
		Assertions.assertEquals(5, playerState.getHand().size());
		Assertions.assertEquals(0, playerState.getDiscard().size());
		Assertions.assertEquals(4, playerState.getDeck().size());
		Assertions.assertEquals(1, playerState.getRevealing().size());
	}

	@Test
	public void spy2() {
		swapHandCards(ActionService.SPY);
		loadDeck(ActionService.COPPER);
		loadDiscard(ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER);
		loadDiscard(this.playerState2, ActionService.COPPER);
		loadDiscard(this.playerState3, ActionService.COPPER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.SPY);
				
		Assertions.assertEquals(5, playerState.getHand().size());
		Assertions.assertEquals(0, playerState.getDiscard().size());
		Assertions.assertEquals(4, playerState.getDeck().size());
		Assertions.assertEquals(1, playerState.getRevealing().size());
	}

	@Test
	public void trail() {
		swapHandCards(ActionService.TRAIL);
		loadDiscard(ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.TRAIL);
				
		Assertions.assertEquals(5, playerState.getHand().size());
		Assertions.assertEquals(0, playerState.getDiscard().size());
		Assertions.assertEquals(5, playerState.getDeck().size());
	}
	
	@Test
	public void vassal() {
		swapHandCards(ActionService.VASSAL);
		loadDiscard(ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.VASSAL);
				
		Assertions.assertEquals(4, playerState.getHand().size());
		Assertions.assertEquals(0, playerState.getDiscard().size());
		Assertions.assertEquals(5, playerState.getDeck().size());
		Assertions.assertEquals(1, playerState.getLooking().size());
	}

	@Test
	public void village() {
		swapHandCards(ActionService.VILLAGE);
		loadDiscard(ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.VILLAGE);
				
		Assertions.assertEquals(5, playerState.getHand().size());
		Assertions.assertEquals(0, playerState.getDiscard().size());
		Assertions.assertEquals(5, playerState.getDeck().size());
	}

	@Test
	public void wheelwright() {
		swapHandCards(ActionService.WHEELWRIGHT);
		loadDiscard(ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.WHEELWRIGHT);
				
		Assertions.assertEquals(5, playerState.getHand().size());
		Assertions.assertEquals(0, playerState.getDiscard().size());
		Assertions.assertEquals(5, playerState.getDeck().size());
	}

	@Test
	public void witch() {
		swapBankSupplies(Map.of(ActionService.GOLD, ActionService.CURSE));
		swapHandCards(ActionService.WITCH);
		loadDiscard(ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.WITCH);
				
		Assertions.assertEquals(6, playerState.getHand().size());
		Assertions.assertEquals(0, playerState.getDiscard().size());
		Assertions.assertEquals(4, playerState.getDeck().size());
	}

	@Test
	public void witchsHut() {
		swapHandCards(ActionService.WITCHS_HUT);
		loadDiscard(ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER, ActionService.COPPER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.WITCHS_HUT);
				
		Assertions.assertEquals(8, playerState.getHand().size());
		Assertions.assertEquals(0, playerState.getDiscard().size());
		Assertions.assertEquals(2, playerState.getDeck().size());
	}
}
