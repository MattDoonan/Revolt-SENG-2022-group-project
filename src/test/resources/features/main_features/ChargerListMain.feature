Feature: Browsing the Charger list on Home Screen
  Description: The purpose of this feature is to test the capability of the charger list

  Background: (AT_1) User is on the home view
    Given I have the app open
    When I navigate to the home view
    Then chargers are available

  Scenario: : (AT_9) User wants to see more info about the charger
    Given No charger has been selected
    When The user selects a charger
    Then More info of charger displayed

