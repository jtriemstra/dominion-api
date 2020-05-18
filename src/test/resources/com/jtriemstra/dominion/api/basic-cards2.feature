Feature: Basic cards
  
  Background:
    Given the deck has Council Room,Laboratory,Market,Feast,Chapel,Woodcutter,Workshop,Adventurer,Moat
    And I am a player
    And I have 5 cards in my hand
    And I have 1 actions
    
  Scenario: Playing a workshop
    Given I have a Workshop
    When I play the Workshop
    And I opt for the Feast
    Then I should have 4 cards in my hand
    And I should have 0 actions available
    And I should have a Feast in my bought

  Scenario: Playing a woodcutter
    Given I have a Woodcutter
    When I play the Woodcutter
    Then I should have 4 cards in my hand
    And I should have 0 actions available
    And I should have 2 buys available
    And I should have 2 treasure available
    
  Scenario: Playing a chapel
    Given I have a Chapel
    When I play the Chapel
    And I opt for the Copper,Copper
    Then I should have 2 cards in my hand
    
  Scenario: Playing a feast
    Given I have a Feast
    When I play the Feast
    And I opt for the Market
    Then I should have 4 cards in my hand
    And I should have a Market in my bought
    
  Scenario: Playing a market
    Given I have a Market
    When I play the Market
    Then I should have 5 cards in my hand
    And I should have 1 actions available
    And I should have 2 buys available
    And I should have 1 treasure available
    
  Scenario: Playing a laboratory
    Given I have a Laboratory
    When I play the Laboratory
    Then I should have 6 cards in my hand
    And I should have 1 actions available
    And I should have 0 treasure available
    
  Scenario: Playing a council room
    Given I have a Council Room
    And there are 1 other players
    When I play the Council Room
    Then I should have 8 cards in my hand
    And I should have 2 buys available
    And the other player should have 6 cards in hand  
    
  Scenario: Playing a moat
    Given I have a Moat
    When I play the Moat
    Then I should have 6 cards in my hand
    
  Scenario: Playing an adventurer
    Given I have a Adventurer
    And my deck starts with Feast,Feast,Silver,Gold
    When I play the Adventurer
    Then I should have 6 cards in my hand
    And I should have 2 cards in my discard