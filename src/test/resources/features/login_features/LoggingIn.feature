Feature: Logging into an account
  Description: The purpose of this feature is to test the user experience logging into an account

  Background: User has opened the app and gone to log in
    Given I have the app open
    And I am logged out
    When I navigate to the login screen
    Then The login popup appears

  Scenario: (AT_45) A user is logging in correctly to an existing account
    Given I have an account
    When I enter my username: "admin" and password: "admin"
    Then I am successfully logged in

  Scenario: (AT_46) A user is logging in incorrectly to an existing account
    Given I have an account
    When I enter my username: "wrongaccount" and password: "nopassword"
    Then I am informed it was incorrect

  Scenario: (AT_47) A user wants to log in but does not have an account
    Given I do not have an account
    When I choose to sign up
    Then I am redirected to the sign up page

