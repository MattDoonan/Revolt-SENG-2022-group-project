Feature: Signing Up for an Account
  Description: The purpose of this feature is to test for when the user is creating a new account

  Background: The user has gone to login but does not have an account so tries to make one
    Given I have the app open
    And I am logged out
    And the login screen is not open
    When I navigate to the login screen
    And there is an option to sign up
    Then I choose to sign up
    And I am redirected to the sign up page

  Scenario: (AT_19) A user wants to sign up to the app with a non-existing account
    Given account with username: "user" does not exist
    When I enter my details username: "user" email: "user@example.com" password: "password"
    And I click the sign up button
    Then an account with username: "user" and email: "user@example.com" is created
    And I am successfully logged in

  Scenario: (AT_19.5) A user wants to sign up with an invalid email
    Given account with username: "user" does not exist
    And email: "invalidEmail" is invalid
    When I enter my details username: "user" email: "invalidEmail" password: "password"
    And I click the sign up button
    Then I am informed my email is invalid
    And an account with username: "user" is not created

  Scenario: (AT_20) A user tries to sign up to the app with an existing account
    Given account with username: "user" email: "user@example.com" password: "password" exists
    When I enter my details username: "user" email: "user@example.com" password: "password"
    And I click the sign up button
    Then I am informed that my account exists