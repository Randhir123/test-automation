@IdVerify
Feature: Perform face match

  Background:
    Given caller presents a valid OAuth2 token
    And mock is enabled on the IdVerify Service
    And product id is "bea087ed-379a-4952-8173-67cd18b4471d"

  Scenario: Perform face match with valid face image
    Given a person with valid face image
      | nik       | 3174082212800007 |
      | phone_no  | 081234567890     |
      | email     | john@doe.com     |
    And the match threshold is set to 1.0
    When request is submitted for face match
    Then verify that the HTTP response is 200
    And a transaction id is returned
    And verify that the response has location header with a status url
    When status is checked
    Then verify that the HTTP response is 200
    And face match status is "success"
    And face match result is returned