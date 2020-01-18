Feature: Attack cards
  
  Background:
    Given the deck has Smithy,Village,Militia,Council Room,Witch,Bureaucrat,Spy,Festival,Market,Woodcutter
    And I am a player
    And I have 5 cards in my hand
    And I have 1 actions
    
  Scenario: Playing a bureaucrat
    Given I have a Bureaucrat
    And the other player has a Estate
    When I play the Bureaucrat
    Then my deck should start with Silver
    And the other players deck should start with Estate