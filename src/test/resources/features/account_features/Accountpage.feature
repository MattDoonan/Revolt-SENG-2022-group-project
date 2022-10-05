Feature: User checking there account information
  Description: The purpose of this feature is to test the user experience for the account page

  Background: User has logged into the app and has gone to the account page
    Given I have the app open
    And I am logged out
    And I log in with username: "admin" password: "admin"
    When I navigate to the account screen

  Scenario: (AT_43 1) A user wants to update personal information
    Given I want to edit my information
    When I want to change my account name to "Cool Guy"
    And I want to save my information
    Then My account name has changed to "Cool Guy"

  Scenario: (AT_43 2) A user wants to update personal information
    Given I want to edit my information
    When I want to change my account email to "newEmail@gmail.com"
    And I want to save my information
    Then My account email has changed to "newEmail@gmail.com"

  Scenario: (AT_43 3) A user wants to update personal information
    Given I want to edit my information
    When I want to change my password to "1234"
    And I want to save my information
    Then I logout of the app
    Then I log in with username: "admin" password: "1234"
    And I am successfully logged in

  Scenario: (AT_44) A user wants to update personal information
    Given I want to edit my information
    When I want to change my account name to "MrTest"
    And I want to save my information
    Then My account name has changed to "admin"

  Scenario: (AT_6) Admin or charger owner filters a list of shown chargers
    Given There are chargers in the presentation
    When The user clicks on the carparks table header
    Then The list of chargers is sorted by carparks

  Scenario: (AT_7) Admin or charger owners hides info they do not need
    Given There are chargers in the presentation
    When The user click select columns and deselects 'Show address' and 'Show owner'
    Then The fields disappear

  Scenario: (AT_8) Admin or charger owner wants to un-hide a field they want
    Given There are chargers in the presentation
    Given The time limit is hidden
    When The user click select columns and selects show time limit
    Then The time limit field appears

  Scenario: (AT_51) A user 'Charger Owner' permissions wants to register a charger
    Given The user has a charger they would like to add to the app
    When the user inputs the charger’s details, and clicks the ‘add charger’ button
    Then The charger is added to the table

  Scenario: (AT_58) Admin wants to change a users permissions
    Given There is sufficient reason to change a user’s status
    When A user upgrades a user permission
    Then The user now has access to different functionality of the app

  Scenario: (AT_61) Admin wants to delete a user
    Given There is sufficient reason to change a user’s status
    When The admin deletes an account
    Then The account is deleted