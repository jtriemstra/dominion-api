package com.jtriemstra.dominion.api;

import static org.junit.Assert.assertEquals;

import com.jtriemstra.dominion.api.models.Player;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AttackCucumberSteps extends CucumberTestBase{

	@Given("the other player has a {}")
    public void the_other_player_this_card(String cardName) {
		Player otherPlayer = getGame().getOtherPlayers(getPlayer()).get(0);
		
		int firstCopperIndex = 0;
		while (!otherPlayer.getHand().get(firstCopperIndex).getName().equals("Copper")) {
			firstCopperIndex++;
		}
		
		otherPlayer.getHand().remove(firstCopperIndex);
		otherPlayer.getHand().add(firstCopperIndex, getBank().getByName(cardName));
	}
	
	@Then("the other players deck should start with {}")
    public void other_players_deck_should_start_with(String cardNames) {
		Player otherPlayer = getGame().getOtherPlayers(getPlayer()).get(0);
		
		String[] multipleCardNames = cardNames.split(",");
    	for (int i=0; i<multipleCardNames.length; i++) {
    		assertEquals(multipleCardNames[i], otherPlayer.getDeck().get(i).getName());
    	}
    }
}
