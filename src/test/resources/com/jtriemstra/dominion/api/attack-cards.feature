Feature: Attack cards
  
  Background:
    Given the deck has Smithy,Village,Militia,Council Room,Witch,Bureaucrat,Spy,Festival,Market,Woodcutter
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