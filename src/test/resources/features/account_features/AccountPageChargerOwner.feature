Feature: User checking there account information
  Description: The purpose of this feature is to test the Charger owner user experience for the account page

  Background: User has logged into the app and has gone to the account page
    Given I have the app open
    And I am logged out
    And I log in with username: "MrTestOwner" password: "qwerty"
    When I navigate to the account screen

  Scenario: (AT_35) A user wants to edit a chargers details
    Given The user owns a charger
    When The user edits the charger details
    Then The charger details are saved

  Scenario: (AT_36) A user wants to delete a charger
    Given The user owns a charger
    When The user clicks delete charger
    Then The charger details are deleted

  Scenario: (AT_37) A user wants to view a charger they own
    Given The user owns no chargers
    Then The table is empty

  Scenario: (AT_50) A user wants to delete there account
    Given I navigate to the account screen
    When The user confirms to delete their account
    Then The users account has been deleted as well as chargers and vehicles

