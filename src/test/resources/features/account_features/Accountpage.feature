Feature: User checking there account information
  Description: The purpose of this feature is to test the user experience for the account page

  Background: User has logged into the app and has gone to the account page
    Given I have the app open
    And I am logged out
    And I log in with username: "admin" password: "admin"
    When I navigate to the account screen

  Scenario: (AT_44 1) A user wants to update personal information
    Given I want to edit my information
    When I want to change my account name to "Cool Guy"
    And I want to save my information
    Then My account name has changed to "Cool Guy"

  Scenario: (AT_44 2) A user wants to update personal information
    Given I want to edit my information
    When I want to change my account email to "newEmail@gmail.com"
    And I want to save my information
    Then My account email has changed to "newEmail@gmail.com"

  Scenario: (AT_44 3) A user wants to update personal information
    Given I want to edit my information
    When I want to change my password to "1234"
    And I want to save my information
    Then I logout of the app
    Then I log in with username: "admin" password: "1234"
    And I am successfully logged in

