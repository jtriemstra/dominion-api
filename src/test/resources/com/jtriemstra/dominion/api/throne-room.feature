Feature: Throne room
  
  Scenario: Playing a throne room
    Given I have a Throne Room
    When I play the Throne Room
    Then I should be asked what card to play twice
       