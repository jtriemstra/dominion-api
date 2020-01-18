Feature: Throne room
  
  Background:
    Given I am a player
    And I have a Throne Room
    And I have 5 cards in my hand
    And I have 1 actions
    
  Scenario: Playing a throne room
    When I play the Throne Room
    Then I should be asked Choose an action card to play twice
    
  Scenario: Playing a throne room and village     
  	Given I have a Village
    When I play the Throne Room
    And I opt for the Village
    Then I should have 5 cards in my hand
    And I should have 4 actions available
    
  Scenario: Playing a throne room and moneylender     
  	Given I have a Moneylender
    When I play the Throne Room
    And I opt for the Moneylender
    Then I should have 1 cards in my hand
    And I should have 0 actions available
    And I should have 6 treasure available
    
  Scenario: Playing a throne room and mine
    Given I have a Mine
    When I play the Throne Room
    And I opt for the Mine
    Then I should be asked Choose a treasure card to trash
    
  Scenario: Playing a throne room and mine 2
    Given I have a Mine
    When I play the Throne Room
    And I opt for the Mine
    And I opt for the Copper
    Then I should be asked Choose a treasure card to gain
    
  Scenario: Playing a throne room and mine 3
    Given I have a Mine
    When I play the Throne Room
    And I opt for the Mine
    And I opt for the Copper
    And I opt for the Silver
    Then I should be asked Choose a treasure card to trash
    
  Scenario: Playing a throne room and mine 4
    Given I have a Mine
    When I play the Throne Room
    And I opt for the Mine
    And I opt for the Copper
    And I opt for the Silver
    And I opt for the Copper
    Then I should be asked Choose a treasure card to gain    
     
  Scenario: Playing a throne room and mine 5
    Given I have a Mine
    When I play the Throne Room
    And I opt for the Mine
    And I opt for the Copper
    And I opt for the Silver
    And I opt for the Copper
    And I opt for the Silver
    Then I should have 3 cards in my hand
    And I should have 0 actions available    
   