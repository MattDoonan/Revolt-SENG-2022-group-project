@searching
Feature: Searching and filtering chargers
  Scenario: User inputs a search on a list of chargers, and the query is valid
    Given There is no current input given
    When The user inputs a valid query "Dunedin"
    Then The user gets the chargers that best match their query "Dunedin"

  Scenario: User inputs a search for chargers with no valid query
    Given There is no current input given
    When The user inputs an invalid query "xyxwqzgrezw"
    Then No results are listed