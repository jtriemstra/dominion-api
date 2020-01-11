package com.jtriemstra.dominion.api;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.Assert.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import com.jtriemstra.dominion.api.models.Bank;
import com.jtriemstra.dominion.api.models.Card;
import com.jtriemstra.dominion.api.models.Game;
import com.jtriemstra.dominion.api.models.Player;

public class BasicCucumberTests extends CucumberTestBase{
	
	
	
	@Given("I am a player")
	public void i_am_a_player() {
		List<Card> x = new ArrayList<>();
		x.add(getBank().village());
		x.add(getBank().smithy());
		x.add(getBank().festival());
		x.add(getBank().throneroom());
		for (int i=0; i<8; i++) { x.add(getBank().copper());}
		
		when(getBank().newDeck()).thenReturn(x);
		
		state.player = new Player();
		state.player.init(state.game);
	}
	
	@Given("I have a {}")
    public void i_have_this_card(String cardName) {
		for(Card c : getPlayer().getHand()) {
			if (c.getName().equals(cardName)) {
				return;
			}
		}
		
		throw new RuntimeException("hand is missing the specified card");
	}
    
	@Given("I have {} cards in my hand")
    public void i_have_n_cards(int numberOfCards) {
		assertEquals(numberOfCards, getPlayer().getHand().size());
	}
	
	@Given("I have {} actions")
    public void i_have_n_actions(int numberOfActions) {
		assertEquals(numberOfActions, getPlayer().getTemporaryActions() + 1);
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
