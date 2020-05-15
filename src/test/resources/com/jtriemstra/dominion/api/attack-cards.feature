Feature: Attack cards
  
  Background:
    Given the deck has Smithy,Village,Militia,Moat,Witch,Bureaucrat,Spy,Festival,Market,Woodcutter
    And I am a player
    And there are 2 other players
    And I have 5 cards in my hand
    And I have 1 actions
    
  Scenario: Playing a bureaucrat
    Given my hand is Bureaucrat,Copper,Copper,Copper,Copper
    And the other players hand is Estate,Copper,Copper,Copper,Copper
    When I play the Bureaucrat
    Then my deck should start with Silver
    And the other players deck should start with Estate
    And the other player should have 4 cards in hand
    
  Scenario: Playing a bureaucrat 2
    Given my hand is Bureaucrat,Copper,Copper,Copper,Copper
    And the other players hand is Copper,Copper,Copper,Copper,Copper
    When I play the Bureaucrat
    Then my deck should start with Silver
    And the other players deck should start with Copper
    And the other player should have 5 cards in hand    
    
  Scenario: Playing a militia
    Given I have a Militia
    And the other players hand is Copper,Copper,Copper,Copper,Copper
    When I play the Militia
    And the other player opts for the Copper,Copper
    Then I should have 2 treasure available
    And the other player should have 2 cards in discard
    And the other player should have 3 cards in hand
    
  Scenario: Playing a Militia 2
  	Given I have a Militia
    And the other players hand is Moat,Copper,Copper,Copper,Copper
    When I play the Militia
    Then the other player should have no active choice
    And the other player should have 5 cards in hand
      
    