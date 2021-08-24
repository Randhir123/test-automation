@IdVerify
Feature: Perform demographic verification of a person with Dukcapil

  Background:
    Given caller presents a valid OAuth2 token
    And mock is enabled on the IdVerify Service
    And product id is "bea087ed-379a-4952-8173-67cd18b4471d"

  Scenario: Perform demographic verification with valid credentials
    Given a person with demographic details
      | nik       | 3174082212800007 |
      | full_name | Romi Gunawan     |
      | dob       | 22/12/1988       |
      | phone_no  | 081234567890     |
      | email     | john@doe.com     |
    And the match threshold is set to 1.0
    When request is submitted for demog verification
    Then verify that the HTTP response is 200
    And a transaction id is returned