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

public class ThroneRoomCucumberTests extends CucumberTestBase{
	
	CucumberState state = new CucumberState();
	
	@Then("I should be asked what card to play twice")
    public void i_should_be_asked() {
        assertEquals("Choose an action card to play twice", state.player.getCurrentChoice().getPrompt());
    }
}
