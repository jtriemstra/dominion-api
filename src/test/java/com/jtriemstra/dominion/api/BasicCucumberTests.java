package com.jtriemstra.dominion.api;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;

import static org.junit.Assert.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import com.jtriemstra.dominion.api.models.Bank;
import com.jtriemstra.dominion.api.models.Card;
import com.jtriemstra.dominion.api.models.Game;
import com.jtriemstra.dominion.api.models.Player;

@Slf4j
public class BasicCucumberTests extends CucumberTestBase{
	
	@Before
	public void setup() {
		log.info("calling setup");
		List<Card> x = new ArrayList<>();
		for (int i=0; i<10; i++) { x.add(state.mockBank.copper());}
		
		when(state.mockBank.newDeck()).thenReturn(x);

	}
	
	@Given("I am a player")
	public void i_am_a_player() {
		log.info("calling init");
		state.player = new Player();
		state.player.init(state.game);
	}
	
	@Given("I have a {}")
    public void i_have_this_card(String cardName) {
		int firstCopperIndex = 0;
		while (!getPlayer().getHand().get(firstCopperIndex).getName().equals("Copper")) {
			firstCopperIndex++;
		}
		
		getPlayer().getHand().remove(firstCopperIndex);
		getPlayer().getHand().add(firstCopperIndex, getBank().getByName(cardName));
	}
    
	@Given("I have {} cards in my hand")
    public void i_have_n_cards(int numberOfCards) {
		assertEquals(numberOfCards, getPlayer().getHand().size());
	}
	
	@Given("I have {} actions")
    public void i_have_n_actions(int numberOfActions) {
		assertEquals(numberOfActions, getPlayer().getTemporaryActions() + 1);
	}
	
	@Given("card {} in the deck is a {}")
	public void card_n_is_x(int cardIndex, String cardName) {
		getPlayer().getDeck().add(cardIndex, getBank().getByName(cardName));
	}
	
    @When("I play the {}")
    public void i_play_the_card(String cardName) {
    	getPlayer().play(cardName);
    }

    @Then("I should have a {} in my played")
    public void i_should_have_played(String cardName) {
    	boolean foundCard = false;
    	
        for(Card c : getPlayer().getPlayed()) {
        	if (cardName.equals(c.getName())) {
        		foundCard = true;
        	}
        }
        
        assertEquals(true, foundCard);
    }
    @Then("I should have {} cards in my {}")
    public void i_should_have_cards(int cardCount, String source) {
        switch(source) {
        case "hand":
        	assertEquals(cardCount, getPlayer().getHand().size());
        	break;
        case "played":
        	assertEquals(cardCount, getPlayer().getPlayed().size());
        	break;
        case "discard":
        	assertEquals(cardCount, getPlayer().getDiscard().size());
        	break;
        default:
        	throw new RuntimeException("invalid source");
        }
    }
    @Then("I should have {} {} available")
    public void i_should_have_available(int itemCount, String itemName) {
        switch(itemName) {
        case "actions":
        	assertEquals(itemCount, getPlayer().getTemporaryActions());
        	break;
        case "buys":
        	assertEquals(itemCount, getPlayer().getTemporaryBuys() + 1);
        	break;
        case "treasure":
        	assertEquals(itemCount, getPlayer().getTemporaryTreasure());
        	break;
        default:
        	throw new RuntimeException("invalid source");
        }
    }
}
