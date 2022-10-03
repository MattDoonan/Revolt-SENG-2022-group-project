Feature: User checking there account information
  Description: The purpose of this feature is to test the user experience for the account page

  Background: User has logged into the app and has gone to the account page
    Given I have the app open
    And I am logged out
    And I log in with username: "admin" password: "admin"
    When I navigate to the account screen

  Scenario: (AT_44) A user wants to update personal infomation
    Given I want to edit my information
    When I want to change my account name to "Cool Guy"
    And I want to save my information