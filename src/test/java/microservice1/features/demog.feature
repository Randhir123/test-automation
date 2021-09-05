@IdVerify
Feature: Perform full demographic verification of a person with Portal

  Background:
    Given caller presents a valid OAuth2 token
    And mock is enabled on the IdVerify Service
    And product id is "bea087ed-379a-4952-8173-67cd18b4471d"

  Scenario: Perform full demographic verification with valid credentials
    Given a person with full demographic details
      | nik                 | 3174082212800007 |
      | full_name           | Romi Gunawan     |
      | dob                 | 22/12/1988       |
      | phone_no            | 081234567890     |
      | email               | john@doe.com     |
      | mother_maiden_name  |                  |
      | family_card_no      | 1234567890       |
      | address             | SARIMANIS 13 TOWN HOUSE B3 |
      | village             | Sarijadi         |
      | district            | Sukasari         |
      | city                | KOTA BANDUNG     |
      | province            | Jawa Barat       |
    And the match threshold is set to 1.0
    When request is submitted for full demog verification
    Then verify that the HTTP response is 200
    And a transaction id is returned