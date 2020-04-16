package com.jtriemstra.dominion.api;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.Assert.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.util.Arrays;

import com.jtriemstra.dominion.api.models.Bank;
import com.jtriemstra.dominion.api.models.Card;
import com.jtriemstra.dominion.api.models.Game;
import com.jtriemstra.dominion.api.models.Player;

public class ThroneRoomCucumberTests extends CucumberTestBase{
	
	CucumberState state = new CucumberState();
	
	@Then("I should be asked {}")
    public void i_should_be_asked(String optionPrompt) {
        assertEquals(optionPrompt, state.player.getCurrentChoice().getPrompt());
    }
	
	@Then("options should include {}")
    public void options_should_include(String options) {
		String[] multipleOptions = options.split(",");
		for (String s : state.player.getCurrentChoice().getOptions()) {
			System.out.println(s);
		}
		for (String s : multipleOptions) {
			System.out.println(s);
			assertTrue(state.player.getCurrentChoice().getOptions().contains(s));
		}
    }
	
	@When("I opt for the {}")
    public void i_opt_for(String optionName) {
		assertNotNull(getPlayer().getCurrentChoice());
		String[] multipleOptionNames = optionName.split(",");
        getPlayer().finishAction(java.util.Arrays.asList(multipleOptionNames));
    }
}
