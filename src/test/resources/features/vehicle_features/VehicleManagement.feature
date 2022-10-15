Feature: Managing a users vehicles
  Description: This purpose of this feature is to test the user interactions with their vehicles

  Background: (AT_40) A user is logged in and on the vehicle screen
    Given I have the app open
    And I am logged out
    And I log in with username: "admin" password: "admin"
    And I have no vehicles
    When I navigate to the vehicle screen
    Then I am shown an empty garage

  Scenario: (AT_41) A user wants to register a valid vehicle on the app
    Given I click add new vehicle
    When I provide vehicle make: "Tesla" model: "Y" maxrange: "300" connector type: "Type 1 Socketed"
    Then I can see the vehicle in the garage

  Scenario: (AT_42) A user wants to register an invalid vehicle on the app
    Given I click add new vehicle
    When I provide vehicle make: "" model: "" maxrange: "" connector type: "Type 1 Socketed"
    Then I am informed my input is invalid

  # Scenario: (AT_39) A user wants to delete a vehicle
  #   Given I have a vehicle in the garage
  #   When I click delete vehicle
  #   And I confirm the action
  #   Then I can no longer see the vehicle

  Scenario: (AT_38) A user wants to edit an existing vehicle
    Given I have a vehicle in the garage
    When I edit the vehicle
    And change the make to "NewMake"
    Then the vehicle's make is now "NewMake"