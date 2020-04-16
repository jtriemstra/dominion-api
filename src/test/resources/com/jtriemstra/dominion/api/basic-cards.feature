Feature: Basic cards
  
  Background:
    Given the deck has Smithy,Village,Throne Room,Festival,Moneylender,Mine,Remodel,Library,Cellar,Chancellor
    And I am a player
    And I have 5 cards in my hand
    And I have 1 actions
    
  Scenario: Playing a smithy
    Given I have a Smithy
    When I play the Smithy
    Then I should have 7 cards in my hand
    And I should have a Smithy in my played
    And I should have 0 actions available
    
  Scenario: Playing a festival
    Given I have a Festival
    When I play the Festival
    Then I should have 4 cards in my hand
    And I should have a Festival in my played
    And I should have 2 actions available
    And I should have 2 buys available
    And I should have 2 treasure available
    
  Scenario: Playing a village
    Given I have a Village
    When I play the Village
    Then I should have 5 cards in my hand
    And I should have a Village in my played
    And I should have 2 actions available

  Scenario: Playing a mine
    Given I have a Mine
    When I play the Mine
    Then I should be asked Choose a treasure card to trash
   
  Scenario: Playing a mine 2
    Given I have a Mine
    When I play the Mine
    And I opt for the Copper
    Then I should be asked Choose a treasure card to gain
    
  Scenario: Playing a mine 3
    Given I have a Mine
    When I play the Mine
    And I opt for the Copper
    And I opt for the Silver
    Then I should have 4 cards in my hand

#  Scenario: Playing a mine 4
#    Given I have a Mine
#    When I play the Mine
#    And I opt for the Copper
#    And I opt for the Gold
#    Then I should get an error
    
  Scenario: Playing a remodel
    Given I have a Remodel
    When I play the Remodel
    Then I should be asked Choose a card to trash
    
  Scenario: Playing a remodel 2
    Given I have a Remodel
    When I play the Remodel
    And I opt for the Copper
    And I opt for the Estate
    Then I should have 3 cards in my hand
    And I should have 0 actions available
    And I should have 1 cards in my bought  
    
  Scenario: Playing a library
    Given I have a Library
    And card 0 in the deck is a Copper
    And card 1 in the deck is a Copper
    And card 2 in the deck is a Copper
    When I play the Library
    Then I should have 7 cards in my hand
    
  Scenario: Playing a library 2
    Given I have a Library
    And card 0 in the deck is a Village
    When I play the Library
    Then I should have 5 cards in my hand
    And I should be asked Do you want to discard the action Village?
    
  Scenario: Playing a library 3
    Given I have a Library
    And card 0 in the deck is a Village
    When I play the Library
    And I opt for the Yes
    Then I should have 7 cards in my hand
    And I should have 1 cards in my discard
    
  Scenario: Playing a library 4
    Given I have a Library
    And card 0 in the deck is a Village
    When I play the Library
    And I opt for the No
    Then I should have 7 cards in my hand
    And I should have 0 cards in my discard       
      
  Scenario: Playing a library 5
    Given I have a Library
    And card 0 in the deck is a Village
    And card 1 in the deck is a Village
    And card 2 in the deck is a Village
    When I play the Library
    And I opt for the Yes
    And I opt for the Yes
    And I opt for the Yes
    Then I should have 7 cards in my hand
    And I should have 3 cards in my discard     
  
  Scenario: Playing a cellar
    Given I have a Cellar
    When I play the Cellar
    Then I should be asked Which cards would you like to discard?
    
  Scenario: Playing a cellar 2
    Given I have a Cellar
    When I play the Cellar
    And I opt for the Copper
    Then I should have 4 cards in my hand
    And I should have 1 cards in my discard
    
  Scenario: Playing a cellar 3
    Given I have a Cellar
    When I play the Cellar
    And I opt for the Copper,Copper,Copper
    Then I should have 4 cards in my hand
    And I should have 3 cards in my discard  
    
  Scenario: Playing a chancellor
    Given I have a Chancellor
    When I play the Chancellor
    Then I should be asked Do you want to put your deck into your discard pile?  

  Scenario: Playing a chancellor 2
    Given I have a Chancellor
    When I play the Chancellor
    And I opt for the Yes
    Then I should have 0 cards in my deck
    And I should have 5 cards in my discard
    And I should have 4 cards in my hand
    
  Scenario: Playing a moneylender
    Given I have a Moneylender
    And I have a Copper
    When I play the Moneylender
    Then I should have 3 cards in my hand
    And I should have 3 treasure available