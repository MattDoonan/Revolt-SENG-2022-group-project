Feature: User planning a journey
  Description: The purpose of this feature is to test the user experience for the journey page

  Background: The user has logged in and goes to the journey page
    Given I have the app open
    And I am logged out
    And I log in with username: "admin" password: "admin"
    When I navigate to the account screen

  Scenario: (AT_24) A user wants to view the journeys they have made
    Given The user has journeys to view
    When The user is on the accountPage
    Then The user can view a list of previous journeys

  Scenario: (AT_25) A user wants to view the journeys they have made
    Given The user has no previous journeys
    When The user is on the accountPage
    Then The user cant view any journeys

  Scenario: (AT_26) A user wants to add a journey
    Given The user has planned a trip
    When A user saves there planned Journey
    Then The Journey is saved to the user's list of Journeys

  Scenario: (AT_27) A user wants to add previously taken Journey
    Given The user has taken a previous Journey
    When The user makes the previous journey
    Then The previous journey is saved to the database

