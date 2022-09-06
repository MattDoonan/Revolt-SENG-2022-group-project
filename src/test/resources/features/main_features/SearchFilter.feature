Feature: Searching and filtering chargers
  Scenario: User inputs a search on a list of chargers, and the query is valid
    Given The user is on the main page showing all chargers
    When The user inputs a valid query "Dunedin" and clicks Go
    Then The user gets shown the chargers that best match their query "Dunedin"
