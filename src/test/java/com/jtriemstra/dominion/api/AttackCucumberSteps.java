package com.jtriemstra.dominion.api;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.assertj.core.util.Arrays;

import com.jtriemstra.dominion.api.models.Player;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AttackCucumberSteps extends CucumberTestBase{

	@Given("the other players hand is {}")
    public void the_other_player_this_card(String cardNames) {
		Player otherPlayer = getGame().getOtherPlayers(getPlayer()).get(0);
		otherPlayer.getHand().clear();
		
		String[] multipleCardNames = cardNames.split(",");
    	for (int i=0; i<multipleCardNames.length; i++) {
    		otherPlayer.getHand().add(getBank().getByName(multipleCardNames[i]));
    	}
	}
	
	@Then("the other players deck should start with {}")
    public void other_players_deck_should_start_with(String cardNames) {
		Player otherPlayer = getGame().getOtherPlayers(getPlayer()).get(0);
		
		String[] multipleCardNames = cardNames.split(",");
    	for (int i=0; i<multipleCardNames.length; i++) {
    		assertEquals(multipleCardNames[i], otherPlayer.getDeck().get(i).getName());
    	}
    }
	
	@Then("the other player should have {} cards in hand")
	public void other_player_should_have_n_cards(int numberOfCards) {
		Player otherPlayer = getGame().getOtherPlayers(getPlayer()).get(0);
		assertEquals(numberOfCards, otherPlayer.getHand().size());
	}
	
	@When("the other player opts for the {}")
	public void other_player_action(String cardNames) {
		Player otherPlayer = getGame().getOtherPlayers(getPlayer()).get(0);
		String[] multipleCardNames = cardNames.split(",");
		
		otherPlayer.finishAction(new ArrayList( Arrays.asList( multipleCardNames) ));
		
		
	}
}
