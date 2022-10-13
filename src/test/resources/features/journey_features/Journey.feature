Feature: User planning a journey
  Description: The purpose of this feature is to test the user experience for the journey page

  Background: The user has logged in and goes to the journey page
    Given I have the app open
    And I am logged out
    And I log in with username: "admin" password: "admin"
    When I navigate to the journey screen