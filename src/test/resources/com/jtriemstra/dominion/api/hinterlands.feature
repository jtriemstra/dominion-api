Feature: Basic cards
  
  Background:
    Given the deck has Oasis,Margrave,Stables,Develop,Crossroads,Spice Merchant,Silk Road,Embassy,Border Village,Nomad Camp
    And I am a player
    And there are 2 other players
    And I have 5 cards in my hand
    And I have 1 actions
    
  Scenario: Playing a oasis
    Given I have a Oasis
    When I play the Oasis
    Then I should be asked Which card would you like to discard?
  
  Scenario: Playing a oasis 2
    Given I have a Oasis
    When I play the Oasis
    And I opt for the Copper
    Then I should have 4 cards in my hand
    And I should have a Oasis in my played
    And I should have a Copper in my discard
    And I should have 1 actions available
    And I should have 1 treasure available
      
  Scenario: Playing a crossroads
    Given I have a Crossroads
    And I have a Estate
    When I play the Crossroads
    Then I should have 5 cards in my hand
    And I should have 3 actions available
    
  Scenario: Playing a crossroads 2
  	Given I have a Crossroads
    And I have a Estate
    And I have a Estate
    When I play the Crossroads
    Then I should have 6 cards in my hand
    And I should have 3 actions available
    
  Scenario: Playing a crossroads 3
  	Given I have a Crossroads
  	And I have a Crossroads
    And I have a Estate
    And I have a Estate
    When I play the Crossroads
    And I play the Crossroads
    Then I should have 7 cards in my hand
    And I should have 2 actions available
    
  Scenario: Playing a develop
    Given I have a Develop
    When I play the Develop
    Then I should have 4 cards in my hand
    And I should be asked Choose a card to trash
    
  Scenario: Playing a develop 2
    Given I have a Develop
    And I have a Oasis
    When I play the Develop
    And I opt for the Oasis
    Then I should be asked Choose a card to gain
    And options should include Crossroads,Silk Road,Spice Merchant
    
  Scenario: Playing a stables
    Given I have a Stables
    And I have a Copper
    When I play the Stables
    And I opt for the Copper
    Then I should have 6 cards in my hand
    And I should have 1 actions available
    
  Scenario: Playing a spice merchant
    Given I have a Spice Merchant
    And I have a Copper
    When I play the Spice Merchant
    And I opt for the Copper
    Then I should be asked Choose one:
    
  Scenario: Playing a spice merchant 2
    Given I have a Spice Merchant
    And I have a Copper
    When I play the Spice Merchant
    And I opt for the Copper
    And I opt for the 2 Cards; 1 Action
    Then I should have 5 cards in my hand
    And I should have 1 actions available
  
  Scenario: Playing a spice merchant 3
    Given I have a Spice Merchant
    And I have a Copper
    When I play the Spice Merchant
    And I opt for the Copper
    And I opt for the 1 Buy; 2 Treasure
    Then I should have 3 cards in my hand
    And I should have 0 actions available  
    And I should have 2 buys available
    And I should have 2 treasure available
    
  Scenario: Playing a margrave
    Given my hand is Margrave,Copper,Copper,Copper,Copper
    And the other players hand is Copper,Copper,Copper,Copper,Copper
    When I play the Margrave
    Then I should have 7 cards in my hand
    And I should have 2 buys available
    And the other player should have 6 cards in hand   

#TODO finish the attack  

  Scenario: Playing a embassy
  	Given my hand is Embassy,Copper,Copper,Copper,Copper
    When I play the Embassy
    Then I should have 9 cards in my hand
    And I should be asked Which cards would you like to discard?
   
  Scenario: Playing a embassy 2
  	Given my hand is Embassy,Copper,Copper,Copper,Copper
    When I play the Embassy
    And I opt for the Copper,Copper,Copper
    Then I should have 6 cards in my hand
    
  Scenario: Playing a border village
    Given I have a Border Village
    When I play the Border Village
    Then I should have 5 cards in my hand
    And I should have 2 actions available  
    And I should have 1 buys available
    And I should have 0 treasure available
    
  Scenario: Playing a nomad camp
    Given I have a Nomad Camp
    When I play the Nomad Camp
    Then I should have 4 cards in my hand
    And I should have 0 actions available  
    And I should have 2 buys available
    And I should have 2 treasure available