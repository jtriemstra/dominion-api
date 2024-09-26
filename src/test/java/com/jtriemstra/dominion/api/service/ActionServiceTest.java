package com.jtriemstra.dominion.api.service;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtriemstra.dominion.api.dto.BankState;
import com.jtriemstra.dominion.api.dto.BankSupply;
import com.jtriemstra.dominion.api.dto.GameState;
import com.jtriemstra.dominion.api.dto.PlayerState;

import lombok.SneakyThrows;

@ExtendWith(MockitoExtension.class)
public class ActionServiceTest extends ActionServiceTestBase {
	
	@Test
	public void moatAddsCards() {
		swapHandCards(ActionService.MOAT);
		loadDeck(ActionService.GOLD, ActionService.SILVER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.MOAT);
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(6, playerState.getHand().size()));
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.GOLD, 1, ActionService.SILVER, 1)));
		doAssertion(Checked.DECK, () -> Assertions.assertEquals(0, playerState.getDeck().getCards().size()));
	}
	
	@Test
	public void villageAddsCorrectValues() {
		swapHandCards(ActionService.VILLAGE);
		loadDeck(ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.VILLAGE);
		
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(2, playerState.getTurn().getActionsAvailable()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(5, playerState.getHand().size()));
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.GOLD, 1)));
		doAssertion(Checked.DECK, () -> Assertions.assertEquals(0, playerState.getDeck().getCards().size()));
	}
	
	@Test
	public void drawReloadsDeck() {
		loadDeck(ActionService.GOLD);
		playerState.getDiscard().add(ActionService.ESTATE);
		playerState.getDiscard().add(ActionService.ESTATE);
		playerState.getDiscard().add(ActionService.ESTATE);
		stashGameState();
		
		actionService.defaultDraw(gameState, "test");
		actionService.defaultDraw(gameState, "test");
		actionService.defaultDraw(gameState, "test");
		
		Assertions.assertEquals(8, playerState.getHand().size());
		assertCardsInHand(Map.of(ActionService.GOLD, 1, ActionService.SILVER, 0, ActionService.ESTATE, 2));
		Assertions.assertEquals(1, playerState.getDeck().getCards().size());
		Assertions.assertEquals(0, playerState.getDiscard().getCards().size());
	}
	
	@Test
	public void weaverCanPlayOnDiscard() {
		swapHandCards(ActionService.OASIS, ActionService.WEAVER);
		loadDeck(ActionService.ESTATE);  // just to meet the draw requirement
		stashGameState();

		actionService.turnPlay(gameState, "test", ActionService.OASIS);
		playerState.getTurn().getChoicesMade().add(ActionService.WEAVER);
		actionService.doChoice(gameState, "test");
		
		Assertions.assertEquals(ActionService.WEAVER1, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction());
		Assertions.assertTrue(playerState.getTurn().getChoicesAvailable().get(0).getOptions().stream().anyMatch(s -> s.equals("PLAY")));
		
		playerState.getTurn().getChoicesMade().clear();
		playerState.getTurn().getChoicesMade().add("PLAY");
		actionService.doChoice(gameState, "test");
		Assertions.assertEquals(ActionService.WEAVER2, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction());
		Assertions.assertTrue(playerState.getTurn().getChoicesAvailable().get(0).getOptions().stream().anyMatch(s -> s.equals("2 Silver")));
	}
	
	@Test
	public void embassyGainSharesSilvers() {
		playerState.getTurn().setTreasure(5);
		actionService.doBuy(gameState, "test", "Embassy");
		stashGameState();
		
		Assertions.assertTrue(playerState.getDiscard().getCards().contains(ActionService.EMBASSY));
		Assertions.assertTrue(playerState2.getDiscard().getCards().contains(ActionService.SILVER));
		Assertions.assertTrue(playerState3.getDiscard().getCards().contains(ActionService.SILVER));
	}
	
	@Test
	public void borderVillageHagglerEmbassyCombo() {
		swapHandCards(ActionService.HAGGLER);
		
		actionService.turnPlay(gameState, "test", ActionService.HAGGLER);
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		
		actionService.doBuy(gameState, "test", "Border Village");
		Assertions.assertTrue(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("Embassy"));
		Assertions.assertFalse(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("Gold"));
		
		playerState.getTurn().getChoicesMade().add("Embassy");
		actionService.doChoice(gameState, "test");
		
		Assertions.assertTrue(playerState.getDiscard().getCards().contains(ActionService.EMBASSY));
		Assertions.assertTrue(playerState2.getDiscard().getCards().contains(ActionService.SILVER));
		Assertions.assertTrue(playerState3.getDiscard().getCards().contains(ActionService.SILVER));

		Assertions.assertTrue(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("Embassy"));
		Assertions.assertFalse(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("Gold"));

		playerState.getTurn().getChoicesMade().add("Silver");
		actionService.doChoice(gameState, "test");

		Assertions.assertTrue(playerState.getDiscard().getCards().contains(ActionService.SILVER));
		Assertions.assertTrue(playerState.getDiscard().getCards().contains(ActionService.BORDER_VILLAGE));
	}

	@Test
	public void goldAddsCorrectValues() {
		swapHandCards(ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.GOLD);
		
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(1, playerState.getTurn().getActionsAvailable()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(4, playerState.getHand().size()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(3, playerState.getTurn().getTreasure()));
	}

	@Test
	public void silverAddsCorrectValues() {
		swapHandCards(ActionService.SILVER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.SILVER);

		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(1, playerState.getTurn().getActionsAvailable()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(4, playerState.getHand().size()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(2, playerState.getTurn().getTreasure()));
	}

	@Test
	public void copperAddsCorrectValues() {
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.COPPER);

		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(1, playerState.getTurn().getActionsAvailable()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(4, playerState.getHand().size()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(1, playerState.getTurn().getTreasure()));
	}

	@Test
	public void smithyAddsCorrectValues() {
		swapHandCards(ActionService.SMITHY);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.SMITHY);

		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(0, playerState.getTurn().getActionsAvailable()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(7, playerState.getHand().size()));
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.GOLD, 3, ActionService.COPPER, 4)));
		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(0, playerState.getTurn().getTreasure()));
	}

	@Test
	public void festivalAddsCorrectValues() {
		swapHandCards(ActionService.FESTIVAL);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.FESTIVAL);

		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(2, playerState.getTurn().getActionsAvailable()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(4, playerState.getHand().size()));
		doAssertion(Checked.BUY, () -> Assertions.assertEquals(2, playerState.getTurn().getBuys()));
		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(2, playerState.getTurn().getTreasure()));
	}

	@Test
	public void woodcutterAddsCorrectValues() {
		swapHandCards(ActionService.WOODCUTTER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.WOODCUTTER);
		
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(0, playerState.getTurn().getActionsAvailable()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(4, playerState.getHand().size()));
		doAssertion(Checked.BUY, () -> Assertions.assertEquals(2, playerState.getTurn().getBuys()));
		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(2, playerState.getTurn().getTreasure()));
	}

	@Test
	public void laboratoryAddsCorrectValues() {
		swapHandCards(ActionService.LABORATORY);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.LABORATORY);
		
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(1, playerState.getTurn().getActionsAvailable()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(6, playerState.getHand().size()));
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.GOLD, 2, ActionService.COPPER, 4)));
	}

	@Test
	public void marketAddsCorrectValues() {
		swapHandCards(ActionService.MARKET);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.MARKET);
		
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(1, playerState.getTurn().getActionsAvailable()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(5, playerState.getHand().size()));
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.GOLD, 1, ActionService.COPPER, 4)));
		doAssertion(Checked.BUY, () -> Assertions.assertEquals(2, playerState.getTurn().getBuys()));
		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(1, playerState.getTurn().getTreasure()));
	}
	
	@Test
	public void remodelAddsCorrectChoices() {
		swapHandCards(ActionService.REMODEL);
		swapHandCards(ActionService.SILVER);		
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.REMODEL);
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertTrue(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("Silver")));
		doAssertion(Checked.CHOICES, () -> Assertions.assertTrue(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("Copper")));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Silver")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.CHOICES, () -> Assertions.assertFalse(playerState.getHand().getCards().contains("Silver")));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(3, playerState.getHand().size()));
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertTrue(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("Silver")));
		doAssertion(Checked.CHOICES, () -> Assertions.assertTrue(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("Embassy")));
		doAssertion(Checked.CHOICES, () -> Assertions.assertFalse(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("Gold")));
		doAssertion(Checked.CHOICES, () -> Assertions.assertFalse(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("Border Village")));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Embassy")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.DISCARD, () -> Assertions.assertTrue(playerState.getDiscard().getCards().contains("Embassy")));
	}
	
	@Test
	public void militiaAddsTreasureAndAttack() {
		// set up a player who doesn't need to discard
		playerState3.getHand().remove(ActionService.COPPER);
		playerState3.getHand().remove(ActionService.COPPER);
		swapHandCards(ActionService.MILITIA);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.MILITIA);
		
		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(2, playerState.getTurn().getTreasure()));
		
		Assertions.assertEquals(5, playerState2.getTurn().getChoicesAvailable().get(0).getOptions().size());
		Assertions.assertEquals(1, playerState3.getTurn().getChoicesAvailable().size());
		Assertions.assertEquals(0, playerState3.getTurn().getChoicesAvailable().get(0).getMaxChoices());
		Assertions.assertEquals(0, playerState3.getTurn().getChoicesAvailable().get(0).getMinChoices());
		
		playerState2.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper","Copper")));
		actionService.doChoice(gameState, "test2");

		Assertions.assertEquals(0, playerState2.getTurn().getChoicesAvailable().size());
		Assertions.assertTrue(playerState2.getDiscard().getCards().contains("Copper"));
		Assertions.assertEquals(2, playerState2.getDiscard().getCards().size());
	}

	@Test
	public void bureaucratGainsSilverAndAttack() {
		// set up a player who needs to discard
		playerState3.getHand().remove(ActionService.COPPER);
		playerState3.getHand().add(ActionService.ESTATE);
		swapHandCards(ActionService.BUREAUCRAT);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.BUREAUCRAT);
		
		doAssertion(Checked.DECK, () -> Assertions.assertEquals(1, playerState.getDeck().getCards().size()));
		doAssertion(Checked.DECK, () -> Assertions.assertEquals("Silver", playerState.getDeck().getCards().get(0)));
		
		Assertions.assertEquals(1, playerState3.getTurn().getChoicesAvailable().get(0).getOptions().size());
		Assertions.assertEquals(0, playerState2.getTurn().getChoicesAvailable().size());
	}
	
	@Test
	public void vassalAddsTreasureAndAction() {
		swapHandCards(ActionService.VASSAL);
		loadDeck(ActionService.VILLAGE, ActionService.VILLAGE, ActionService.VILLAGE);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.VASSAL);
		
		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(2, playerState.getTurn().getTreasure()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertTrue(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("PLAY")));
		doAssertion(Checked.CHOICES, () -> Assertions.assertTrue(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("DISCARD")));			
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(ActionService.VASSAL1, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));
	}

	@Test
	public void vassalAddsTreasureAndNotAction() {
		swapHandCards(ActionService.VASSAL);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.VASSAL);
		
		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(2, playerState.getTurn().getTreasure()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertTrue(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("DISCARD")));		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(ActionService.VASSAL1, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));
	}

	public void playingWeaverGivesChoice() {
		swapHandCards(ActionService.WEAVER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.WEAVER);
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(3, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertTrue(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("2 Silver")));
		doAssertion(Checked.CHOICES, () -> Assertions.assertTrue(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("Silver")));
		doAssertion(Checked.CHOICES, () -> Assertions.assertTrue(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("Copper")));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(ActionService.WEAVER2, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));
	}

	public void discardingWeaverNoChoice() {
		swapHandCards(ActionService.WEAVER);
		stashGameState();
		
		actionService.cleanup(gameState, "test");
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(0, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
	}

	public void discardingWeaverGivesChoice() {
		swapHandCards(ActionService.WEAVER);
		stashGameState();
		
		actionService.discard(gameState, "test", gameState.getPlayers().get("test").getHand(), ActionService.WEAVER);
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertTrue(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("PLAY")));
		doAssertion(Checked.CHOICES, () -> Assertions.assertTrue(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("DISCARD")));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(ActionService.WEAVER1, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));
	}

	@Test
	public void schemeAddsCorrectValues() {
		swapHandCards(ActionService.SCHEME);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.SCHEME);
		
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(1, playerState.getTurn().getActionsAvailable()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(5, playerState.getHand().size()));
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.GOLD, 1, ActionService.COPPER, 4)));
	}

	@Test
	public void schemeWithNoOtherActionsGivesAChoice() {
		swapHandCards(ActionService.SCHEME);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.SCHEME);
		actionService.cleanup(gameState, "test");

		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertTrue(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("Scheme")));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(ActionService.SCHEME2, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));
	}

	@Test
	public void schemeWithOtherActionsGivesMultipleChoice() {
		swapHandCards(ActionService.SCHEME, ActionService.VILLAGE);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.SCHEME);
		actionService.turnPlay(gameState, "test", ActionService.VILLAGE);
		actionService.cleanup(gameState, "test");

		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertTrue(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("Scheme")));
		doAssertion(Checked.CHOICES, () -> Assertions.assertTrue(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("Village")));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(ActionService.SCHEME2, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));
	}

	@Test
	public void hagglerAddsCorrectValues() {
		swapHandCards(ActionService.HAGGLER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.HAGGLER);
		
		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(2, playerState.getTurn().getTreasure()));
	}

	@Test
	public void hagglerAddsGainChoice() {
		swapHandCards(ActionService.HAGGLER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.HAGGLER);
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		
		actionService.doBuy(gameState, "test", ActionService.BORDER_VILLAGE);
		
		Assertions.assertEquals(0, playerState.getTurn().getGainedToDiscard().size());
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(ActionService.HAGGLER2, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertTrue(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("Embassy")));
		doAssertion(Checked.CHOICES, () -> Assertions.assertTrue(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("Silver")));
		doAssertion(Checked.CHOICES, () -> Assertions.assertFalse(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("Duchy")));
		doAssertion(Checked.CHOICES, () -> Assertions.assertFalse(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("Gold")));
	}

	@Test
	public void hagglerGainsTwoCards() {
		swapHandCards(ActionService.HAGGLER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.HAGGLER);
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		
		actionService.doBuy(gameState, "test", ActionService.BORDER_VILLAGE);
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Embassy")));
		
		actionService.doChoice(gameState, "test");

		Assertions.assertEquals(2, playerState.getDiscard().getCards().size());
		Assertions.assertEquals(2, playerState.getTurn().getGainedToDiscard().size());
		Assertions.assertTrue(playerState.getTurn().getGainedToDiscard().contains("Embassy"));
		Assertions.assertTrue(playerState.getTurn().getGainedToDiscard().contains("Border Village"));
	}

	@Test
	public void guardDogAddsTwoCards() {
		swapHandCards(ActionService.GUARD_DOG);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.GUARD_DOG);
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(6, playerState.getHand().size()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(2, playerState.getHand().getCards().stream().filter(c -> c.equals("Gold")).count()));
	}

	@Test
	public void guardDogAddsFourCards() {
		swapHandCards(ActionService.GUARD_DOG);
		playerState.getHand().remove("Copper");
		playerState.getHand().remove("Copper");
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.GOLD, ActionService.SILVER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.GUARD_DOG);
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(6, playerState.getHand().size()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(3, playerState.getHand().getCards().stream().filter(c -> c.equals("Gold")).count()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(1, playerState.getHand().getCards().stream().filter(c -> c.equals("Silver")).count()));
	}

	@Test
	public void oasisAddsCorrectValues() {
		swapHandCards(ActionService.OASIS);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.OASIS);
		
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(1, playerState.getTurn().getActionsAvailable()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(5, playerState.getHand().size()));
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.GOLD, 1, ActionService.COPPER, 4)));
		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(1, playerState.getTurn().getTreasure()));

		Assertions.assertEquals(5, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size());
	}

	@Test
	public void oasisDoesDiscard() {
		swapHandCards(ActionService.OASIS);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.OASIS);
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper")));
		actionService.doChoice(gameState, "test");		
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(4, playerState.getHand().size()));
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.GOLD, 1, ActionService.COPPER, 3)));
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(1, playerState.getDiscard().getCards().size()));
	}

	@Test
	public void embassyAddsCorrectValues() {
		swapHandCards(ActionService.EMBASSY);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.GOLD, ActionService.SILVER, ActionService.SILVER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.EMBASSY);
		
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(0, playerState.getTurn().getActionsAvailable()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(9, playerState.getHand().size()));
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.GOLD, 3, ActionService.COPPER, 4, ActionService.SILVER, 2)));
		
		Assertions.assertEquals(9, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size());
	}

	@Test
	public void embassyDoesDiscard() {
		swapHandCards(ActionService.EMBASSY);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.GOLD, ActionService.SILVER, ActionService.SILVER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.EMBASSY);
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper", "Copper", "Copper")));
		actionService.doChoice(gameState, "test");		
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(6, playerState.getHand().size()));
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.GOLD, 3, ActionService.COPPER, 1, ActionService.SILVER, 2)));
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(3, playerState.getDiscard().getCards().size()));
	}
	
	// TODO: look at non-buy gains
	@Test
	public void embassyGainDoesAddSilvers() {
		stashGameState();
		
		for (int i=1; i<=5; i++) { actionService.turnPlay(gameState, "test", ActionService.COPPER);}
		
		actionService.doBuy(gameState, "test", "Embassy");
		
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(1, playerState.getDiscard().getCards().size()));
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals("Embassy", playerState.getDiscard().getCards().get(0)));
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(1, playerState2.getDiscard().getCards().size()));
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals("Silver", playerState2.getDiscard().getCards().get(0)));
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(1, playerState3.getDiscard().getCards().size()));
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals("Silver", playerState3.getDiscard().getCards().get(0)));
	}

	@Test
	public void borderVillageAddsCorrectValues() {
		swapHandCards(ActionService.BORDER_VILLAGE);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.BORDER_VILLAGE);
		
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(2, playerState.getTurn().getActionsAvailable()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(5, playerState.getHand().size()));
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.GOLD, 1, ActionService.COPPER, 4)));		
	}

	@Test
	public void borderVillageGainDoesExtraGain() {
		swapHandCards(ActionService.SILVER);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.GOLD);
		stashGameState();

		for (int i=1; i<=4; i++) { actionService.turnPlay(gameState, "test", ActionService.COPPER);}
		actionService.turnPlay(gameState, "test", ActionService.SILVER);
		actionService.doBuy(gameState, "test", "Border Village");

		Assertions.assertEquals(5, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size());
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Duchy")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(2, playerState.getDiscard().getCards().size()));
	}
	
	@Test
	public void throneRoomDoesNoAction() {
		swapHandCards(ActionService.THRONE_ROOM);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.THRONE_ROOM);

		Assertions.assertEquals(0, playerState.getTurn().getChoicesAvailable().size());	
	}

	@Test
	public void throneRoomRepeatsVillage() {
		swapHandCards(ActionService.THRONE_ROOM, ActionService.VILLAGE);
		loadDeck(ActionService.GOLD, ActionService.SILVER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.THRONE_ROOM);

		Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size());
		Assertions.assertEquals(ActionService.THRONE_ROOM2, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction());

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Village")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(4, playerState.getTurn().getActionsAvailable()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(5, playerState.getHand().size()));
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.GOLD, 1, ActionService.COPPER, 3, ActionService.SILVER, 1)));		
	}

	@Test
	public void throneRoomRepeatsMarket() {
		swapHandCards(ActionService.THRONE_ROOM, ActionService.MARKET);
		loadDeck(ActionService.GOLD, ActionService.SILVER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.THRONE_ROOM);

		Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size());
		Assertions.assertEquals(ActionService.THRONE_ROOM2, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction());

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Market")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(2, playerState.getTurn().getActionsAvailable()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(5, playerState.getHand().size()));
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.GOLD, 1, ActionService.COPPER, 3, ActionService.SILVER, 1)));
		doAssertion(Checked.BUY, () -> Assertions.assertEquals(3, playerState.getTurn().getBuys()));
		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(2, playerState.getTurn().getTreasure()));
	}
	
	@Test
	public void throneRoomRepeatsRemodel() {
		swapHandCards(ActionService.THRONE_ROOM, ActionService.REMODEL, ActionService.SILVER);
		loadDeck(ActionService.GOLD, ActionService.SILVER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.THRONE_ROOM);
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Remodel")));
		actionService.doChoice(gameState, "test");
				
		doAssertion(Checked.CHOICES, () -> Assertions.assertTrue(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("Silver")));
		doAssertion(Checked.CHOICES, () -> Assertions.assertTrue(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("Copper")));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Silver")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.CHOICES, () -> Assertions.assertTrue(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("Silver")));
		doAssertion(Checked.CHOICES, () -> Assertions.assertTrue(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("Embassy")));
		doAssertion(Checked.CHOICES, () -> Assertions.assertFalse(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("Gold")));
		doAssertion(Checked.CHOICES, () -> Assertions.assertFalse(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("Border Village")));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Embassy")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.CHOICES, () -> Assertions.assertTrue(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("Copper")));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.CHOICES, () -> Assertions.assertTrue(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("Copper")));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(1, playerState.getHand().size()));
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(2, playerState.getDiscard().getCards().size()));
	}

	@Test
	public void highwayAddsCorrectValuesAndDiscount() {
		swapHandCards(ActionService.HIGHWAY);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.HIGHWAY);
		
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(1, playerState.getTurn().getActionsAvailable()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(5, playerState.getHand().size()));
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.GOLD, 1, ActionService.COPPER, 4)));
		
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		
		actionService.doBuy(gameState, "test", "Duchy");
		
		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(0, playerState.getTurn().getTreasure()));
	}
	
	@Test
	public void trailAddsCorrectValues() {
		swapHandCards(ActionService.TRAIL);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.TRAIL);
		
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(1, playerState.getTurn().getActionsAvailable()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(5, playerState.getHand().size()));
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.GOLD, 1, ActionService.COPPER, 4)));		
	}

	public void discardingTrailGivesChoice() {
		swapHandCards(ActionService.TRAIL);
		stashGameState();
		
		actionService.discard(gameState, "test", gameState.getPlayers().get("test").getHand(), ActionService.TRAIL);
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertTrue(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("PLAY")));
		doAssertion(Checked.CHOICES, () -> Assertions.assertTrue(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("DISCARD")));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(ActionService.TRAIL1, playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction()));
	}

	public void discardingTrailCanPlay() {
		swapHandCards(ActionService.TRAIL);
		stashGameState();
		
		actionService.discard(gameState, "test", gameState.getPlayers().get("test").getHand(), ActionService.TRAIL);
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("PLAY")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(1, playerState.getTurn().getActionsAvailable()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(5, playerState.getHand().size()));
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.GOLD, 1, ActionService.COPPER, 4)));		
	}
	
	@Test
	public void gainingTrailCanPlay() {
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.GOLD);
		stashGameState();

		for (int i=1; i<=4; i++) { actionService.turnPlay(gameState, "test", ActionService.COPPER);}
		actionService.doBuy(gameState, "test", "Trail");

		Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size());
		Assertions.assertTrue(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("PLAY1"));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("PLAY1")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(2, playerState.getTurn().getActionsAvailable()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(2, playerState.getHand().size()));
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.GOLD, 1, ActionService.COPPER, 1)));		
	}
	
	@Test
	public void trashingTrailCanPlay() {
		swapHandCards(ActionService.TRAIL, ActionService.REMODEL);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.REMODEL);

		Assertions.assertTrue(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains(ActionService.TRAIL));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Trail")));
		actionService.doChoice(gameState, "test");

		// TODO: is this the right order? does it matter?
		Assertions.assertEquals("Trail1", playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction());
		Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().size());
		Assertions.assertTrue(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("PLAY"));
		Assertions.assertTrue(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("TRASH"));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("PLAY")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(1, playerState.getTurn().getActionsAvailable()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(4, playerState.getHand().size()));
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.GOLD, 1, ActionService.COPPER, 3)));	
		
		Assertions.assertEquals("Remodel2", playerState.getTurn().getChoicesAvailable().get(0).getFollowUpAction());
		Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size());
		Assertions.assertTrue(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("Gold"));
		Assertions.assertTrue(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("Duchy"));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Gold")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(2, playerState.getDiscard().getCards().size()));
		doAssertion(Checked.DISCARD, () -> Assertions.assertTrue(playerState.getDiscard().getCards().contains("Gold")));
		doAssertion(Checked.DISCARD, () -> Assertions.assertTrue(playerState.getDiscard().getCards().contains("Trail")));
	}
	
	@Test
	public void discardingTunnelCanGainGold() {
		swapHandCards(ActionService.TUNNEL);
		stashGameState();
		
		actionService.discard(gameState, "test", gameState.getPlayers().get("test").getHand(), ActionService.TUNNEL);
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertTrue(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("YES")));
		doAssertion(Checked.CHOICES, () -> Assertions.assertTrue(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("NO")));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("YES")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(2, playerState.getDiscard().getCards().size()));
		doAssertion(Checked.DISCARD, () -> Assertions.assertTrue(playerState.getDiscard().getCards().contains("Gold")));
	}

	@Test
	public void discardingTunnelCanSkipGold() {
		swapHandCards(ActionService.TUNNEL);
		stashGameState();
		
		actionService.discard(gameState, "test", gameState.getPlayers().get("test").getHand(), ActionService.TUNNEL);
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertTrue(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("YES")));
		doAssertion(Checked.CHOICES, () -> Assertions.assertTrue(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("NO")));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("NO")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(1, playerState.getDiscard().getCards().size()));
	}
	
	@Test
	public void foolsGoldAddsCorrectValues() {
		swapHandCards(ActionService.FOOLS_GOLD, ActionService.FOOLS_GOLD, ActionService.FOOLS_GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.FOOLS_GOLD);
		
		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(1, playerState.getTurn().getTreasure()));

		actionService.turnPlay(gameState, "test", ActionService.FOOLS_GOLD);
		
		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(5, playerState.getTurn().getTreasure()));

		actionService.turnPlay(gameState, "test", ActionService.FOOLS_GOLD);
		
		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(9, playerState.getTurn().getTreasure()));
	}
	
	@Test
	public void spiceMerchantCardsChoiceAddsCorrectValues() {
		swapHandCards(ActionService.SPICE_MERCHANT, ActionService.ESTATE);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.SPICE_MERCHANT);
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(3, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertTrue(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("CARDS")));
		doAssertion(Checked.CHOICES, () -> Assertions.assertTrue(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("BUY")));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("CARDS")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(1, playerState.getTurn().getActionsAvailable()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(5, playerState.getHand().size()));
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.GOLD, 2, ActionService.COPPER, 2)));
	}

	@Test
	public void spiceMerchantBuyChoiceAddsCorrectValues() {
		swapHandCards(ActionService.SPICE_MERCHANT);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.SPICE_MERCHANT);
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(4, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertTrue(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("CARDS")));
		doAssertion(Checked.CHOICES, () -> Assertions.assertTrue(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("BUY")));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("BUY")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.BUY, () -> Assertions.assertEquals(2, playerState.getTurn().getBuys()));
		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(2, playerState.getTurn().getTreasure()));		
	}
	
	@Test
	public void stablesAddsCorrectValues() {
		swapHandCards(ActionService.STABLES, ActionService.ESTATE);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.STABLES);
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(3, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(1, playerState.getDiscard().getCards().size()));

		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(1, playerState.getTurn().getActionsAvailable()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(6, playerState.getHand().size()));
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.GOLD, 3, ActionService.COPPER, 2)));
	}

	@Test
	public void wheelwrightAddsCorrectValuesForCostTwo() {
		swapHandCards(ActionService.WHEELWRIGHT, ActionService.ESTATE);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.WHEELWRIGHT);
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(5, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));

		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(1, playerState.getTurn().getActionsAvailable()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(5, playerState.getHand().size()));
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.GOLD, 1, ActionService.COPPER, 3)));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Estate")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(1, playerState.getDiscard().getCards().size()));

		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(0, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
	}

	@Test
	public void wheelwrightAddsCorrectValuesForCostSix() {
		swapHandCards(ActionService.WHEELWRIGHT, ActionService.ESTATE);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.WHEELWRIGHT);
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(5, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));

		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(1, playerState.getTurn().getActionsAvailable()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(5, playerState.getHand().size()));
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.GOLD, 1, ActionService.COPPER, 3)));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Gold")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(1, playerState.getDiscard().getCards().size()));

		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(3, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertTrue(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("Border Village")));
		doAssertion(Checked.CHOICES, () -> Assertions.assertFalse(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("Silver")));
	}
	
	@Test
	public void soukAddsCorrectValuesForNormalHand() {
		swapHandCards(ActionService.SOUK);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.SOUK);
		doAssertion(Checked.BUY, () -> Assertions.assertEquals(2, playerState.getTurn().getBuys()));
		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(3, playerState.getTurn().getTreasure()));			
	}

	@Test
	public void soukAddsCorrectValuesForSmallHand() {
		swapHandCards(ActionService.SOUK);
		playerState.getHand().remove("Copper");
		playerState.getHand().remove("Copper");
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.SOUK);
		doAssertion(Checked.BUY, () -> Assertions.assertEquals(2, playerState.getTurn().getBuys()));
		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(5, playerState.getTurn().getTreasure()));			
	}
	
	@Test
	public void gainingSoukCanTrashEmpty() {
		stashGameState();
		for (int i=1; i<=5; i++) {
			actionService.turnPlay(gameState, "test", ActionService.COPPER);	
		}
		actionService.doBuy(gameState, "test", ActionService.SOUK);
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(0, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
	}

	@Test
	public void gainingSoukCanTrash() {
		swapHandCards(ActionService.GOLD, ActionService.SILVER);
		stashGameState();
		actionService.turnPlay(gameState, "test", ActionService.SILVER);	
		actionService.turnPlay(gameState, "test", ActionService.GOLD);
		
		actionService.doBuy(gameState, "test", ActionService.SOUK);
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(3, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper", "Copper")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(1, playerState.getHand().size()));
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.COPPER, 1)));
	}
	
	@Test
	public void nomadsAddsCorrectValues() {
		swapHandCards(ActionService.NOMADS);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.NOMADS);
		
		doAssertion(Checked.BUY, () -> Assertions.assertEquals(2, playerState.getTurn().getBuys()));
		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(2, playerState.getTurn().getTreasure()));		
	}
	
	@Test
	public void gainingNomadsAddsTreasure() {
		stashGameState();
		for (int i=1; i<=4; i++) {
			actionService.turnPlay(gameState, "test", ActionService.COPPER);	
		}
		actionService.doBuy(gameState, "test", ActionService.NOMADS);
		

		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(2, playerState.getTurn().getTreasure()));	
	}
	
	@Test
	public void trashingNomadsAddsTreasure() {
		swapHandCards(ActionService.NOMADS);
		stashGameState();
		
		actionService.doTrash(gameState, "test", ActionService.NOMADS, playerState.getHand());
		

		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(2, playerState.getTurn().getTreasure()));	
	}
	
	@Test
	public void gainingFarmlandCreatesReplacement() {
		swapBankSupplies(Map.of(ActionService.BORDER_VILLAGE, ActionService.FARMLAND));
		swapHandCards(ActionService.SILVER, ActionService.SILVER, ActionService.SILVER);
		stashGameState();
		actionService.turnPlay(gameState, "test", ActionService.SILVER);
		actionService.turnPlay(gameState, "test", ActionService.SILVER);

		actionService.doBuy(gameState, "test", ActionService.FARMLAND);

		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(3, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertTrue(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("Silver")));
		doAssertion(Checked.CHOICES, () -> Assertions.assertTrue(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("Copper")));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Silver")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertTrue(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("Embassy")));
		doAssertion(Checked.CHOICES, () -> Assertions.assertTrue(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("Duchy")));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Embassy")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(2, playerState.getDiscard().getCards().size()));
	}

	@Test
	public void artisanGainsToHandAndDeck() {
		swapHandCards(ActionService.ARTISAN);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.ARTISAN);
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(5, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertTrue(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("Duchy")));
		doAssertion(Checked.CHOICES, () -> Assertions.assertTrue(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("Embassy")));
		doAssertion(Checked.CHOICES, () -> Assertions.assertFalse(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("Gold")));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Embassy")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(5, playerState.getHand().size()));
		doAssertion(Checked.HAND, () -> Assertions.assertTrue(playerState.getHand().getCards().contains("Embassy")));
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(5, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertFalse(playerState.getTurn().getChoicesAvailable().get(0).getOptions().contains("Duchy")));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Embassy")));
		actionService.doChoice(gameState, "test");		

		doAssertion(Checked.HAND, () -> Assertions.assertEquals(4, playerState.getHand().size()));
		doAssertion(Checked.HAND, () -> Assertions.assertFalse(playerState.getHand().getCards().contains("Embassy")));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(1, playerState.getDeck().getCards().size()));
		doAssertion(Checked.HAND, () -> Assertions.assertTrue(playerState.getDeck().getCards().contains("Embassy")));
	}
	
	@Test
	public void poacherAddsCorrectValuesWithNoDiscard() {
		swapHandCards(ActionService.POACHER);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.POACHER);
		
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(1, playerState.getTurn().getActionsAvailable()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(5, playerState.getHand().size()));
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.GOLD, 1, ActionService.COPPER, 4)));
		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(1, playerState.getTurn().getTreasure()));
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(0, playerState.getTurn().getChoicesAvailable().size()));
	}

	@Test
	public void poacherAddsCorrectValuesWithDiscard() {
		swapHandCards(ActionService.POACHER);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.GOLD);
		bankState.getSupplies().get(ActionService.TRAIL).setCount(0);
		stashGameState();		
		
		actionService.turnPlay(gameState, "test", ActionService.POACHER);
		
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(1, playerState.getTurn().getActionsAvailable()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(5, playerState.getHand().size()));
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.GOLD, 1, ActionService.COPPER, 4)));
		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(1, playerState.getTurn().getTreasure()));
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(5, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().get(0).getMinChoices()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().get(0).getMaxChoices()));
	}

	@Test
	public void merchantAddsCorrectValues() {
		swapHandCards(ActionService.MERCHANT, ActionService.SILVER, ActionService.SILVER);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.MERCHANT);
		
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(1, playerState.getTurn().getActionsAvailable()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(5, playerState.getHand().size()));
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.GOLD, 1, ActionService.COPPER, 2)));
		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(0, playerState.getTurn().getTreasure()));
		
		actionService.turnPlay(gameState, "test", ActionService.SILVER);
		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(3, playerState.getTurn().getTreasure()));

		actionService.turnPlay(gameState, "test", ActionService.SILVER);
		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(5, playerState.getTurn().getTreasure()));
	}
	
	@Test
	public void unplayedMerchantDoesntChangeSilver() {
		swapHandCards(ActionService.MERCHANT, ActionService.SILVER, ActionService.SILVER);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.GOLD);
		stashGameState();

		actionService.turnPlay(gameState, "test", ActionService.SILVER);
		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(2, playerState.getTurn().getTreasure()));
		
	}

	@Test
	public void harbingerAddsCorrectValuesWithNoDiscard() {
		swapHandCards(ActionService.HARBINGER);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.HARBINGER);
		
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(1, playerState.getTurn().getActionsAvailable()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(5, playerState.getHand().size()));
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.GOLD, 1, ActionService.COPPER, 4)));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(0, playerState.getTurn().getChoicesAvailable().size()));
	}

	@Test
	public void harbingerAddsCorrectValuesWithDiscard() {
		swapHandCards(ActionService.HARBINGER);
		playerState.getDiscard().getCards().add("Estate");
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.HARBINGER);
		
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(1, playerState.getTurn().getActionsAvailable()));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(5, playerState.getHand().size()));
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.GOLD, 1, ActionService.COPPER, 4)));
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Estate")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(0, playerState.getDiscard().getCards().size()));
	}
	
	@Test
	public void chapelAddsCorrectChoices() {
		swapHandCards(ActionService.CHAPEL);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.CHAPEL);
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(4, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper", "Copper")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(2, playerState.getHand().size()));
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(0, playerState.getDiscard().getCards().size()));
	}

	@Test
	public void chancellorAddsCorrectValuesAndChoices() {
		swapHandCards(ActionService.CHANCELLOR);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.CHANCELLOR);

		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(2, playerState.getTurn().getTreasure()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("YES")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.DECK, () -> Assertions.assertEquals(0, playerState.getDeck().getCards().size()));
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(3, playerState.getDiscard().getCards().size()));
	}

	@Test
	public void chancellorCanLeaveDeckAlone() {
		swapHandCards(ActionService.CHANCELLOR);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.CHANCELLOR);
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("NO")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.DECK, () -> Assertions.assertEquals(3, playerState.getDeck().getCards().size()));
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(0, playerState.getDiscard().getCards().size()));
	}
	
	@Test
	public void mineAddsCorrectChoices() {
		swapHandCards(ActionService.MINE);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.MINE);
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(4, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(3, playerState.getHand().size()));
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(0, playerState.getDiscard().getCards().size()));
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Silver")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(4, playerState.getHand().size()));
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(0, playerState.getDiscard().getCards().size()));
	}

	@Test
	public void traderCanGainNoSilver() {
		swapHandCards(ActionService.TRADER);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.TRADER);
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(4, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(0, playerState.getDiscard().getCards().size()));
		
	}

	@Test
	public void traderCanGainFiveSilver() {
		swapHandCards(ActionService.TRADER, ActionService.EMBASSY);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.TRADER);
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(4, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Embassy")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(5, playerState.getDiscard().getCards().size()));
		
	}
	
	@Test
	public void traderCanGainSilverInsteadOfCopper() {
		swapHandCards(ActionService.TRADER);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.GOLD);
		stashGameState();
		
		actionService.doBuy(gameState, "test", "Copper");
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Silver")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(1, playerState.getDiscard().getCards().size()));
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals("Silver", playerState.getDiscard().getCards().get(0)));
	}
	
	@Test
	public void cellarAddsActionAndCanDraw() {
		swapHandCards(ActionService.CELLAR);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.CELLAR);
		
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(1, playerState.getTurn().getActionsAvailable()));
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(4, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper","Copper")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(2, playerState.getDiscard().getCards().size()));
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.GOLD, 2, ActionService.COPPER, 2)));
	} 
	
	@Test
	public void workshopCanGainCard() {
		swapHandCards(ActionService.WORKSHOP);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.WORKSHOP);
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(3, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Silver")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(1, playerState.getDiscard().getCards().size()));
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(ActionService.SILVER, playerState.getDiscard().getCards().get(0)));
	}
	
	@Test
	public void feastTrashesAndCanGainCard() {
		swapHandCards(ActionService.FEAST);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.FEAST);
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(5, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Embassy")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(1, playerState.getDiscard().getCards().size()));
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(ActionService.EMBASSY, playerState.getDiscard().getCards().get(0)));
	}
	
	@Test
	public void moneylenderTrashesForTreasure() {
		swapHandCards(ActionService.MONEYLENDER);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.MONEYLENDER);
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("YES")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(3, playerState.getTurn().getTreasure()));
		
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.COPPER, 3)));
	}
	
	@Test
	public void moneylenderCanSkipTrash() {
		swapHandCards(ActionService.MONEYLENDER);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.MONEYLENDER);
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("NO")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(0, playerState.getTurn().getTreasure()));
		
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.COPPER, 4)));
	}
	
	@Test
	public void councilRoomAddsCardsBuyAndOtherPlayers() {
		swapHandCards(ActionService.COUNCIL_ROOM);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.SILVER, ActionService.SILVER);
		playerState2.getDeck().add(ActionService.SILVER);
		playerState3.getDeck().add(ActionService.SILVER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.COUNCIL_ROOM);
	
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.COPPER, 4, ActionService.SILVER, 2, ActionService.GOLD, 2)));
		doAssertion(Checked.BUY, () -> Assertions.assertEquals(2, playerState.getTurn().getBuys()));
		
		Assertions.assertEquals(6, playerState2.getHand().size());
		Assertions.assertEquals(6, playerState3.getHand().size());
	}
	
	@Test
	public void libraryDrawsTreasureCards() {
		swapHandCards(ActionService.LIBRARY);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.SILVER, ActionService.SILVER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.LIBRARY);
		
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.COPPER, 4, ActionService.SILVER, 2, ActionService.GOLD, 1)));
	}

	@Test
	public void libraryDrawsTreasureCardsAndSkipsAction() {
		swapHandCards(ActionService.LIBRARY);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.SILVER, ActionService.EMBASSY);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.LIBRARY);
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of(ActionService.EMBASSY)));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.COPPER, 4, ActionService.SILVER, 1, ActionService.GOLD, 2)));
		
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(1, playerState.getDiscard().getCards().size()));
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(ActionService.EMBASSY, playerState.getDiscard().getCards().get(0)));
	}
	
	@Test
	public void libraryCanMoveDiscardBackToDeck() {
		swapHandCards(ActionService.LIBRARY);
		loadDeck(ActionService.GOLD);
		playerState.getDiscard().add(ActionService.SILVER);
		playerState.getDiscard().add(ActionService.COPPER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.LIBRARY);
		
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.COPPER, 5, ActionService.SILVER, 1, ActionService.GOLD, 1)));
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(0, playerState.getDiscard().getCards().size()));
	}
	
	@Test
	public void illGottenGainsCanGainCopper() {
		swapHandCards(ActionService.ILL_GOTTEN_GAINS);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.SILVER, ActionService.EMBASSY);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.ILL_GOTTEN_GAINS);
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("YES")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.COPPER, 5)));
	}
	
	@Test
	public void illGottenGainsCanSkipCopper() {
		swapHandCards(ActionService.ILL_GOTTEN_GAINS);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.SILVER, ActionService.EMBASSY);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.ILL_GOTTEN_GAINS);
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("NO")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.COPPER, 4)));
	}
	
	@Test
	public void gainingIllGottenGainsDealsCurse() {
		swapBankSupplies(Map.of(ActionService.TRAIL, ActionService.ILL_GOTTEN_GAINS, ActionService.DUCHY, ActionService.CURSE));
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		
		actionService.doBuy(gameState, "test", ActionService.ILL_GOTTEN_GAINS);
		
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(1, playerState.getDiscard().getCards().size()));
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(ActionService.ILL_GOTTEN_GAINS, playerState.getDiscard().getCards().get(0)));
		
		Assertions.assertEquals(1, playerState2.getDiscard().getCards().size());
		Assertions.assertEquals(ActionService.CURSE, playerState2.getDiscard().getCards().get(0));
		
		Assertions.assertEquals(1, playerState3.getDiscard().getCards().size());
		Assertions.assertEquals(ActionService.CURSE, playerState3.getDiscard().getCards().get(0));
	}
	
	@Test
	public void nomadCampAddsBuyTreasure() {
		swapHandCards(ActionService.NOMAD_CAMP);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.SILVER, ActionService.EMBASSY);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.NOMAD_CAMP);
		
		doAssertion(Checked.BUY, () -> Assertions.assertEquals(2, playerState.getTurn().getBuys()));
		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(2, playerState.getTurn().getTreasure()));
	}

	@Test
	public void nomadCampGainsToDeck() {
		swapBankSupplies(Map.of(ActionService.TRAIL, ActionService.NOMAD_CAMP));
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.SILVER, ActionService.EMBASSY);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		
		actionService.doBuy(gameState, "test", ActionService.NOMAD_CAMP);
		
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(0, playerState.getDiscard().getCards().size()));
		doAssertion(Checked.DECK, () -> Assertions.assertEquals(5, playerState.getDeck().getCards().size()));
	}

	@Test
	public void margraveAddsBuyCardsAndAttacks() {
		swapHandCards(ActionService.MARGRAVE);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.SILVER, ActionService.EMBASSY);
		playerState2.getDeck().add(ActionService.SILVER);
		playerState3.getDeck().add(ActionService.SILVER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.MARGRAVE);
		
		doAssertion(Checked.BUY, () -> Assertions.assertEquals(2, playerState.getTurn().getBuys()));
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.COPPER, 4, ActionService.EMBASSY, 1, ActionService.SILVER, 1, ActionService.GOLD, 1)));
		
		Assertions.assertEquals(6, playerState2.getHand().getCards().size());
		Assertions.assertEquals(6, playerState3.getHand().getCards().size());
		
		Assertions.assertEquals(6,  playerState2.getTurn().getChoicesAvailable().get(0).getOptions().size());
		
		playerState2.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper", "Copper", "Copper")));
		actionService.doChoice(gameState, "test2");
		
		Assertions.assertEquals(3, playerState2.getHand().getCards().size());
	}
	
	@Test
	public void innAddsCardsActionsDiscard() {
		swapHandCards(ActionService.INN);
		loadDeck(ActionService.GOLD, ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.INN);
		
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.COPPER, 4, ActionService.GOLD, 2)));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(6, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));		
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper", "Copper")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.COPPER, 2, ActionService.GOLD, 2)));
	}
	
	@Test
	public void innGainAllowsShuffleToDeck() {
		swapBankSupplies(Map.of(ActionService.TRAIL, ActionService.INN));
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.SILVER, ActionService.EMBASSY);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		
		actionService.doBuy(gameState, "test", ActionService.INN);
		
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(1, playerState.getDiscard().getCards().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Inn")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(0, playerState.getDiscard().getCards().size()));
		doAssertion(Checked.DECK, () -> Assertions.assertEquals(5, playerState.getDeck().getCards().size()));
	}
	
	@Test
	public void mandarinAddsTreasureAndChoice() {
		swapHandCards(ActionService.MANDARIN);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.SILVER, ActionService.EMBASSY);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.MANDARIN);
		
		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(3, playerState.getTurn().getTreasure()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(4, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.DECK, () -> Assertions.assertEquals(5, playerState.getDeck().getCards().size()));
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.COPPER, 3)));
	}

	@Test
	public void mandarinGainReturnsTreasureToDeck() {
		swapBankSupplies(Map.of(ActionService.TRAIL, ActionService.MANDARIN));
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.SILVER, ActionService.EMBASSY);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		
		actionService.doBuy(gameState, "test", ActionService.MANDARIN);
		
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(1, playerState.getDiscard().getCards().size()));
		doAssertion(Checked.DECK, () -> Assertions.assertEquals(9, playerState.getDeck().getCards().size()));
	}
	
	@Test
	public void jackOfAllTradesAddsStuff() {
		swapHandCards(ActionService.JACK_OF_ALL_TRADES);
		loadDeck(ActionService.SILVER, ActionService.EMBASSY);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.JACK_OF_ALL_TRADES);
		
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(1, playerState.getDiscard().getCards().size()));
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals("Silver", playerState.getDiscard().getCards().get(0)));
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("DISCARD")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(2, playerState.getDiscard().getCards().size()));
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.COPPER, 4, ActionService.SILVER, 1)));
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(0, playerState.getTurn().getChoicesAvailable().size()));
	}

	@Test
	public void jackOfAllTradesAddsStuffNoDiscardAndTrash() {
		swapHandCards(ActionService.JACK_OF_ALL_TRADES);
		loadDeck(ActionService.SILVER, ActionService.EMBASSY);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.JACK_OF_ALL_TRADES);
		
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(1, playerState.getDiscard().getCards().size()));
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals("Silver", playerState.getDiscard().getCards().get(0)));
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("RETURN")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(1, playerState.getDiscard().getCards().size()));
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.COPPER, 4, ActionService.EMBASSY, 1)));
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of(ActionService.EMBASSY)));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(1, playerState.getDiscard().getCards().size()));
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.COPPER, 4)));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(4, playerState.getHand().getCards().size()));
	}

	@Test
	public void jackOfAllTradesAddsStuffNoDiscardNoTrash() {
		swapHandCards(ActionService.JACK_OF_ALL_TRADES);
		loadDeck(ActionService.SILVER, ActionService.EMBASSY);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.JACK_OF_ALL_TRADES);
		
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(1, playerState.getDiscard().getCards().size()));
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals("Silver", playerState.getDiscard().getCards().get(0)));
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("RETURN")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(1, playerState.getDiscard().getCards().size()));
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.COPPER, 4, ActionService.EMBASSY, 1)));
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("None")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(1, playerState.getDiscard().getCards().size()));
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.COPPER, 4, ActionService.EMBASSY, 1)));
	}
	
	@Test
	public void nobleBrigandAddsTreasureAndAttackNoTreasure() {
		swapHandCards(ActionService.NOBLE_BRIGAND);
		loadDeck(playerState2, ActionService.DUCHY, ActionService.DUCHY);
		loadDeck(playerState3, ActionService.DUCHY, ActionService.DUCHY);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.NOBLE_BRIGAND);
		
		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(1, playerState.getTurn().getTreasure()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(0, playerState.getTurn().getChoicesAvailable().size()));
		Assertions.assertEquals(1, playerState2.getDiscard().getCards().size());
		Assertions.assertEquals(1, playerState3.getDiscard().getCards().size());
	}

	@Test
	public void nobleBrigandAddsTreasureAndAttackOnlyCopper() {
		swapHandCards(ActionService.NOBLE_BRIGAND);
		loadDeck(playerState2, ActionService.COPPER, ActionService.COPPER);
		loadDeck(playerState3, ActionService.COPPER, ActionService.COPPER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.NOBLE_BRIGAND);
		
		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(1, playerState.getTurn().getTreasure()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(0, playerState.getTurn().getChoicesAvailable().size()));
		Assertions.assertEquals(0, playerState2.getDiscard().getCards().size());
		Assertions.assertEquals(0, playerState3.getDiscard().getCards().size());
	}
	
	@Test
	public void nobleBrigandAddsTreasureAndAttackSilverAndGold() {
		swapHandCards(ActionService.NOBLE_BRIGAND);
		loadDeck(playerState2, ActionService.SILVER, ActionService.GOLD);
		loadDeck(playerState3, ActionService.SILVER, ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.NOBLE_BRIGAND);
		
		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(1, playerState.getTurn().getTreasure()));
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().size()));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("test2 : Gold")));
		actionService.doChoice(gameState, "test");
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("test3 : Discard both")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(1, playerState.getDiscard().getCards().size()));
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals("Gold", playerState.getDiscard().getCards().get(0)));
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(1, playerState2.getDiscard().getCards().size()));
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(2, playerState3.getDiscard().getCards().size()));
	}

	@Test
	public void nobleBrigandCanMoveDiscardToDeck() {
		swapHandCards(ActionService.NOBLE_BRIGAND);
		playerState2.getDiscard().getCards().add(ActionService.DUCHY);
		loadDeck(playerState2, ActionService.DUCHY);
		loadDeck(playerState3, ActionService.DUCHY, ActionService.DUCHY);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.NOBLE_BRIGAND);
		
		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(1, playerState.getTurn().getTreasure()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(0, playerState.getTurn().getChoicesAvailable().size()));
		Assertions.assertEquals(1, playerState2.getDiscard().getCards().size());
		Assertions.assertEquals(1, playerState3.getDiscard().getCards().size());
	}
	
	@Test
	public void adventurerDrawsTwoTreasure() {
		swapHandCards(ActionService.ADVENTURER);
		loadDeck(ActionService.GOLD, ActionService.GOLD, ActionService.SILVER, ActionService.EMBASSY);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.ADVENTURER);
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(1, playerState.getDiscard().getCards().size()));
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.COPPER, 4, ActionService.GOLD, 1, ActionService.SILVER, 1)));
	}

	@Test
	public void adventurerCanShuffleDiscard() {
		swapHandCards(ActionService.ADVENTURER);
		loadDeck(ActionService.GOLD);
		loadDiscard(ActionService.SILVER, ActionService.EMBASSY);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.ADVENTURER);
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.COPPER, 4, ActionService.GOLD, 1, ActionService.SILVER, 1)));
		Assertions.assertTrue(playerState.getDeck().getCards().size() == 1 || playerState.getDiscard().getCards().size() == 1);
	}
	
	@Test
	public void witchDrawsAndAttacks() {
		swapBankSupplies(Map.of(ActionService.TRAIL, ActionService.CURSE));
		swapHandCards(ActionService.WITCH);
		loadDeck(ActionService.GOLD, ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.WITCH);
		
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.COPPER, 4, ActionService.GOLD, 2)));
		Assertions.assertEquals(ActionService.CURSE, playerState2.getDiscard().getCards().get(0));
	}
	
	@Test
	public void berserkerGainsCardAndAttacks() {
		swapHandCards(ActionService.BERSERKER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.BERSERKER);

		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(3, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Silver")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(1, playerState.getDiscard().getCards().size()));
		
		Assertions.assertEquals(1, playerState2.getTurn().getChoicesAvailable().size());
		Assertions.assertEquals(5, playerState2.getTurn().getChoicesAvailable().get(0).getOptions().size());
		
		playerState2.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper", "Copper")));
		actionService.doChoice(gameState, "test2");
		
		Assertions.assertEquals(0, playerState2.getTurn().getChoicesAvailable().size());
		Assertions.assertEquals(3, playerState2.getHand().size());
	}

	@Test
	public void berserkerGainWithNoActionNormal() {
		swapBankSupplies(Map.of(ActionService.TRAIL, ActionService.BERSERKER));
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		
		actionService.doBuy(gameState, "test", ActionService.BERSERKER);
		
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(1, playerState.getDiscard().getCards().size()));
		Assertions.assertEquals(5, playerState.getPlayed().getCards().size()); // the coppers
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(0, playerState.getTurn().getChoicesAvailable().size()));
	}

	@Test
	public void berserkerGainWithActionPlays() {
		swapBankSupplies(Map.of(ActionService.TRAIL, ActionService.BERSERKER));
		swapHandCards(ActionService.VILLAGE, ActionService.SILVER, ActionService.SILVER);
		loadDeck(ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.VILLAGE);
		actionService.turnPlay(gameState, "test", ActionService.SILVER);
		actionService.turnPlay(gameState, "test", ActionService.SILVER);
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		
		actionService.doBuy(gameState, "test", ActionService.BERSERKER);
		
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(0, playerState.getDiscard().getCards().size()));
		Assertions.assertEquals(5, playerState.getPlayed().getCards().size()); 
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
	}

	@Test
	public void crossroadsWithNoVictory() {
		swapHandCards(ActionService.CROSSROADS);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.CROSSROADS);
		
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.COPPER, 4)));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(4, playerState.getHand().size()));
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(3, playerState.getTurn().getActionsAvailable()));
	}

	@Test
	public void crossroadsWithVictory() {
		swapHandCards(ActionService.CROSSROADS, ActionService.DUCHY, ActionService.DUCHY);
		loadDeck(ActionService.GOLD, ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.CROSSROADS);
		
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.COPPER, 2, ActionService.GOLD, 2)));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(6, playerState.getHand().size()));
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(3, playerState.getTurn().getActionsAvailable()));
	}

	@Test
	public void multipleCrossroadsAddsActionsOnce() {
		swapHandCards(ActionService.CROSSROADS, ActionService.CROSSROADS, ActionService.DUCHY);
		loadDeck(ActionService.GOLD, ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.CROSSROADS);
		actionService.turnPlay(gameState, "test", ActionService.CROSSROADS);
		
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.COPPER, 2, ActionService.GOLD, 2)));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(5, playerState.getHand().size()));
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(2, playerState.getTurn().getActionsAvailable()));
	}
	
	@Test
	public void developGainsCorrectly() {
		swapHandCards(ActionService.DEVELOP, ActionService.DUCHY);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.DEVELOP);
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(4, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Duchy")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(0, playerState.getDiscard().getCards().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(3, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Gold")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(0, playerState.getDiscard().getCards().size()));
		doAssertion(Checked.DECK, () -> Assertions.assertEquals(1, playerState.getDeck().getCards().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Trail")));
		actionService.doChoice(gameState, "test");
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(2, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("GAIN")));
		actionService.doChoice(gameState, "test");
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(0, playerState.getDiscard().getCards().size()));
		doAssertion(Checked.DECK, () -> Assertions.assertEquals(2, playerState.getDeck().getCards().size()));
		
	}
	
	@Test
	public void sentryAddsCardActionAndChoice() {
		swapHandCards(ActionService.SENTRY);
		loadDeck(ActionService.SILVER, ActionService.DUCHY, ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.SENTRY);
		
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.COPPER, 4, ActionService.GOLD, 1)));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(5, playerState.getHand().size()));
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(1, playerState.getTurn().getActionsAvailable()));
		Assertions.assertEquals(2, playerState.getLooking().getCards().size());
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(6, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		doAssertion(Checked.DECK, () -> Assertions.assertEquals(0, playerState.getDeck().getCards().size()));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("DECK : Silver")));
		actionService.doChoice(gameState, "test");
		
		Assertions.assertEquals(1, playerState.getLooking().getCards().size());
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(3, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		doAssertion(Checked.DECK, () -> Assertions.assertEquals(1, playerState.getDeck().getCards().size()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("DISCARD : Duchy")));
		actionService.doChoice(gameState, "test");
		
		Assertions.assertEquals(0, playerState.getLooking().getCards().size());
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(0, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.DECK, () -> Assertions.assertEquals(1, playerState.getDeck().getCards().size()));
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(1, playerState.getDiscard().getCards().size()));
	}
	
	@Test
	public void witchsHutAddsCardsAndChoice() {
		swapHandCards(ActionService.WITCHS_HUT);
		loadDeck(ActionService.SILVER, ActionService.SILVER, ActionService.GOLD, ActionService.GOLD);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.WITCHS_HUT);

		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.COPPER, 4, ActionService.SILVER, 2, ActionService.GOLD, 2)));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(8, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Copper","Copper")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(2, playerState.getDiscard().getCards().size()));
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.COPPER, 2, ActionService.SILVER, 2, ActionService.GOLD, 2)));
	}

	@Test
	public void witchsHutAddsCardsAndAttack() {
		swapHandCards(ActionService.WITCHS_HUT);
		swapBankSupplies(Map.of(ActionService.DUCHY, ActionService.CURSE));
		loadDeck(ActionService.SILVER, ActionService.SILVER, ActionService.EMBASSY, ActionService.EMBASSY);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.WITCHS_HUT);
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Embassy","Embassy")));
		actionService.doChoice(gameState, "test");

		Assertions.assertEquals(1, playerState2.getDiscard().getCards().size());
		Assertions.assertEquals("Curse", playerState2.getDiscard().getCards().get(0));
		Assertions.assertEquals(1, playerState3.getDiscard().getCards().size());
		Assertions.assertEquals("Curse", playerState3.getDiscard().getCards().get(0));
	}
	
	@Test
	public void cartographerAddsCardActionAndChoice() {
		swapHandCards(ActionService.CARTOGRAPHER);
		loadDeck(ActionService.SILVER, ActionService.DUCHY, ActionService.GOLD, ActionService.GOLD, ActionService.SILVER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.CARTOGRAPHER);
		
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.COPPER, 4, ActionService.SILVER, 1)));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(5, playerState.getHand().size()));
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(1, playerState.getTurn().getActionsAvailable()));
		
		Assertions.assertEquals(4, playerState.getLooking().getCards().size());
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(8, playerState.getTurn().getChoicesAvailable().get(0).getOptions().size()));
		doAssertion(Checked.DECK, () -> Assertions.assertEquals(0, playerState.getDeck().getCards().size()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("DISCARD : Duchy")));
		actionService.doChoice(gameState, "test");
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("DECK : Silver")));
		actionService.doChoice(gameState, "test");
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("DECK : Gold")));
		actionService.doChoice(gameState, "test");
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("DECK : Gold")));
		actionService.doChoice(gameState, "test");

		doAssertion(Checked.DECK, () -> Assertions.assertEquals(3, playerState.getDeck().getCards().size()));
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(1, playerState.getDiscard().getCards().size()));
	}
	
	@Test
	public void oracleAddsMultipleChoices() {
		swapHandCards(ActionService.ORACLE);
		loadDeck(ActionService.SILVER, ActionService.DUCHY);
		loadDeck(playerState2, ActionService.GOLD, ActionService.EMBASSY);
		loadDeck(playerState3, ActionService.COPPER, ActionService.ESTATE);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.ORACLE);
		
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(3, playerState.getTurn().getChoicesAvailable().size()));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("Duchy")));
		actionService.doChoice(gameState, "test");
		doAssertion(Checked.DECK, () -> Assertions.assertEquals(2, playerState.getDeck().getCards().size()));
		Assertions.assertEquals(0, playerState.getRevealing().getCards().size());

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("DISCARD : test2")));
		actionService.doChoice(gameState, "test");
		Assertions.assertEquals(2, playerState2.getDiscard().getCards().size());
		Assertions.assertEquals(0, playerState2.getTurn().getChoicesAvailable().size());

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("DECK : test3")));
		actionService.doChoice(gameState, "test");
		Assertions.assertEquals(0, playerState3.getDiscard().getCards().size());
		Assertions.assertEquals(1, playerState3.getTurn().getChoicesAvailable().size());
		Assertions.assertEquals(2, playerState3.getTurn().getChoicesAvailable().get(0).getOptions().size());
		
		playerState3.getTurn().setChoicesMade(new ArrayList<>(List.of("Estate")));
		actionService.doChoice(gameState, "test3");
		Assertions.assertEquals(2, playerState3.getDeck().getCards().size());
	}
	
	@Test
	public void banditGainsGoldAndChoices() {
		swapHandCards(ActionService.BANDIT);
		loadDeck(playerState2, ActionService.GOLD, ActionService.EMBASSY);
		loadDeck(playerState3, ActionService.COPPER, ActionService.ESTATE);
		stashGameState();

		actionService.turnPlay(gameState, "test", ActionService.BANDIT);
		
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(1, playerState.getDiscard().getCards().size()));
		doAssertion(Checked.DISCARD, () -> Assertions.assertEquals(ActionService.GOLD, playerState.getDiscard().getCards().get(0)));
		
		Assertions.assertEquals(0, playerState.getTurn().getChoicesAvailable().size());
		Assertions.assertEquals(1, playerState2.getTurn().getChoicesAvailable().size());
		Assertions.assertEquals(1, playerState2.getTurn().getChoicesAvailable().get(0).getOptions().size());
		Assertions.assertEquals(1, playerState3.getTurn().getChoicesAvailable().size());
		Assertions.assertEquals(0, playerState3.getTurn().getChoicesAvailable().get(0).getOptions().size());
		
		playerState2.getTurn().setChoicesMade(new ArrayList<>(List.of("Gold")));
		actionService.doChoice(gameState, "test2");
		Assertions.assertEquals(0, playerState2.getDeck().getCards().size());
		Assertions.assertEquals(1, playerState2.getDiscard().getCards().size());
		Assertions.assertEquals(0, playerState2.getRevealing().getCards().size());
		Assertions.assertEquals("Embassy", playerState2.getDiscard().getCards().get(0));

		playerState3.getTurn().setChoicesMade(new ArrayList<>(List.of("")));
		actionService.doChoice(gameState, "test3");
		Assertions.assertEquals(0, playerState3.getDeck().getCards().size());
		Assertions.assertEquals(2, playerState3.getDiscard().getCards().size());
		Assertions.assertEquals(0, playerState3.getRevealing().getCards().size());
	}
	
	@Test
	public void spyAddsCardActionAndAttack() {
		swapHandCards(ActionService.SPY);
		loadDeck(ActionService.SILVER, ActionService.DUCHY);
		loadDeck(playerState2, ActionService.GOLD);
		loadDeck(playerState3, ActionService.COPPER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.SPY);
		
		doAssertion(Checked.HAND, () -> assertCardsInHand(Map.of(ActionService.COPPER, 4, ActionService.DUCHY, 1)));
		doAssertion(Checked.HAND, () -> Assertions.assertEquals(5, playerState.getHand().size()));
		doAssertion(Checked.ACTIONS, () -> Assertions.assertEquals(1, playerState.getTurn().getActionsAvailable()));

		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(3, playerState.getTurn().getChoicesAvailable().size()));
		
		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("DECK")));
		actionService.doChoice(gameState, "test");
		Assertions.assertEquals(1, playerState.getDeck().getCards().size());
		Assertions.assertEquals(0, playerState.getDiscard().getCards().size());
		Assertions.assertEquals(0, playerState.getRevealing().getCards().size());

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("DISCARD : test2")));
		actionService.doChoice(gameState, "test");
		Assertions.assertEquals(0, playerState2.getDeck().getCards().size());
		Assertions.assertEquals(1, playerState2.getDiscard().getCards().size());
		Assertions.assertEquals(0, playerState2.getRevealing().getCards().size());

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("DECK : test3")));
		actionService.doChoice(gameState, "test");
		Assertions.assertEquals(1, playerState3.getDeck().getCards().size());
		Assertions.assertEquals(0, playerState3.getDiscard().getCards().size());
		Assertions.assertEquals(0, playerState3.getRevealing().getCards().size());
	}

	@Test
	public void cauldronAddsBuyTreasure() {
		swapHandCards(ActionService.CAULDRON);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.CAULDRON);

		doAssertion(Checked.HAND, () -> Assertions.assertEquals(4, playerState.getHand().size()));
		doAssertion(Checked.BUY, () -> Assertions.assertEquals(2, playerState.getTurn().getBuys()));
		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(2, playerState.getTurn().getTreasure()));
	}

	@Test
	public void cauldronAddsAttack() {
		swapHandCards(ActionService.GOLD,ActionService.CAULDRON,ActionService.MARKET,ActionService.GOLD,ActionService.GOLD);
		loadDeck(ActionService.SILVER, ActionService.SILVER);
		swapBankSupplies(Map.of(ActionService.EMBASSY, ActionService.CHAPEL, ActionService.DUCHY, ActionService.CURSE));
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.MARKET);
		actionService.turnPlay(gameState, "test", ActionService.CAULDRON);
		actionService.turnPlay(gameState, "test", ActionService.GOLD);
		actionService.turnPlay(gameState, "test", ActionService.GOLD);
		actionService.turnPlay(gameState, "test", ActionService.GOLD);

		actionService.doBuy(gameState, "test", "Chapel");
		
		Assertions.assertEquals(0, playerState2.getDiscard().getCards().size());
		Assertions.assertEquals(0, playerState3.getDiscard().getCards().size());

		actionService.doBuy(gameState, "test", "Chapel");

		Assertions.assertEquals(0, playerState2.getDiscard().getCards().size());
		Assertions.assertEquals(0, playerState3.getDiscard().getCards().size());

		actionService.doBuy(gameState, "test", "Chapel");

		Assertions.assertEquals(1, playerState2.getDiscard().getCards().size());
		Assertions.assertEquals(1, playerState3.getDiscard().getCards().size());
		Assertions.assertEquals("Curse", playerState2.getDiscard().getCards().get(0));
	}
	
	@Test
	public void duchessAddsTreasureAndLook() {
		swapHandCards(ActionService.DUCHESS);
		loadDeck(ActionService.SILVER, ActionService.DUCHY);
		loadDeck(playerState2, ActionService.GOLD);
		loadDeck(playerState3, ActionService.COPPER);
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.DUCHESS);

		doAssertion(Checked.TREASURE, () -> Assertions.assertEquals(2, playerState.getTurn().getTreasure()));

		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState2.getTurn().getChoicesAvailable().size()));
		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState3.getTurn().getChoicesAvailable().size()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("DISCARD")));
		actionService.doChoice(gameState, "test");
		playerState2.getTurn().setChoicesMade(new ArrayList<>(List.of("DECK")));
		actionService.doChoice(gameState, "test2");
		playerState3.getTurn().setChoicesMade(new ArrayList<>(List.of("DISCARD")));
		actionService.doChoice(gameState, "test3");
		
		Assertions.assertEquals(1, playerState.getDeck().getCards().size());
		Assertions.assertEquals(1, playerState.getDiscard().getCards().size());
		Assertions.assertEquals(1, playerState2.getDeck().getCards().size());
		Assertions.assertEquals(0, playerState2.getDiscard().getCards().size());
		Assertions.assertEquals(0, playerState3.getDeck().getCards().size());
		Assertions.assertEquals(1, playerState3.getDiscard().getCards().size());
	}
	
	@Test
	public void whenDuchessAvailableGainingDuchyGivesOption() {
		swapBankSupplies(Map.of(ActionService.EMBASSY, ActionService.DUCHESS));
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		
		actionService.doBuy(gameState, "test", "Duchy");

		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(1, playerState.getTurn().getChoicesAvailable().size()));

		playerState.getTurn().setChoicesMade(new ArrayList<>(List.of("YES")));
		actionService.doChoice(gameState, "test");
		Assertions.assertEquals(2, playerState.getDiscard().getCards().size());
	}

	@Test
	public void whenDuchessNotAvailableGainingDuchyGivesNoOption() {
		
		stashGameState();
		
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		actionService.turnPlay(gameState, "test", ActionService.COPPER);
		
		actionService.doBuy(gameState, "test", "Duchy");

		doAssertion(Checked.CHOICES, () -> Assertions.assertEquals(0, playerState.getTurn().getChoicesAvailable().size()));
	}
	
	public interface CheckedAssertion {
		public void doAssert();
	}
}

