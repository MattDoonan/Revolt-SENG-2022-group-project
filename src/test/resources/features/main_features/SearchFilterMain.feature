Feature: Searching/Filter Chargers on Home Screen
  Description: The purpose of this feature is to test the searching/filtering interactions on the map view

  Background: (AT_1) User is on the home view
    Given I have the app open
    When I navigate to the home view
    Then chargers are available

  Scenario: (AT_2) User inputs a search on a list of chargers, and the query is valid
    Given There is no current input given
    When The user inputs a valid query "Dunedin"
    Then The user gets the chargers that best match their query "Dunedin"

  Scenario: (AT_3) User inputs a search for chargers with no valid query
    Given There is no current input given
    When The user inputs an invalid query "xyxwqzgrezw"
    Then No results are listed

  Scenario: (AT_4) User filters for only free charger stations
    Given There is no current input given
    When The user filters for no charging cost
    Then The list of chargers found have no charging cost