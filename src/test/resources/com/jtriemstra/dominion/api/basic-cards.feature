Feature: Basic cards
  
  Background:
    Given I am a player
    
  Scenario: Playing a smithy
    Given I have a Smithy
    And I have 5 cards in my hand
    And I have 1 actions
    When I play the Smithy
    Then I should have 7 cards in my hand
    And I should have a Smithy in my played
    And I should have 0 actions available
    
  Scenario: Playing a festival
    Given I have a Festival
    And I have 5 cards in my hand
    And I have 1 actions
    When I play the Festival
    Then I should have 4 cards in my hand
    And I should have a Festival in my played
    And I should have 2 actions available
    And I should have 2 buys available
    And I should have 2 treasure available
    
  Scenario: Playing a village
    Given I have a Village
    And I have 5 cards in my hand
    And I have 1 actions
    When I play the Village
    Then I should have 5 cards in my hand
    And I should have a Village in my played
    And I should have 2 actions available

  Scenario: Playing a mine
    Given I have a Mine
    And I have 5 cards in my hand
    And I have 1 actions
    When I play the Mine
    Then I should be asked Choose a treasure card to trash
   
  Scenario: Playing a mine 2
    Given I have a Mine
    And I have 5 cards in my hand
    And I have 1 actions
    When I play the Mine
    And I opt for the Copper
    Then I should be asked Choose a treasure card to gain
    
  Scenario: Playing a mine 3
    Given I have a Mine
    And I have 5 cards in my hand
    And I have 1 actions
    When I play the Mine
    And I opt for the Copper
    And I opt for the Silver
    Then I should have 4 cards in my hand