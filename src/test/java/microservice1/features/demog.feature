@MyTag
Feature: Perform demographic verification of a person

  Background:
    Given caller presents a valid access token
    And product id is "prod1"

  Scenario: Perform demographic verification with valid credentials
    Given a person with full demographic details
      | adhar_id            | 123456789        |
      | full_name           | John Doe         |
      | dob                 | 31/12/1990       |
      | phone_no            | 999999999999     |
      | email               | john@doe.com     |
    And the match threshold is set to 1.0
    When request is submitted for demographic verification
    Then verify that the HTTP response is 200
    And a transaction id is returned
