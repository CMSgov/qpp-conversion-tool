 @ct_dev_regression @tcID_261978
@benton @testRail @ct_dev_api @CPCEndpoint
Feature: C261978 This is a test that verifies the CPC+ Endpoint by retrieving the list of unprocessed-files.

  Scenario: Negative - Get UnprocessedFiles with 401 response.
    Given User starts QPPCT API test
    When User makes GET request to "/cpc/unprocessed-files"
    Then User receives 403 response code
    And User receives response message "Access Denied"

  Scenario Outline: Positive - Get UnprocessedFiles with 200 response.
    Given User starts QPPCT API test
    When User authenticates the QPPCT API with role=<role>
    When User makes GET request to "/cpc/unprocessed-files"
    Then User receives 200 response code

    Examples:
      | role       |
      | CPCPLUSJWT |

  Scenario Outline: Negative - Get UnprocessedFiles with 401 response.
    Given User starts QPPCT API test
    When User authenticates the QPPCT API with role=<role>
    When User makes GET request to "/cpc/unprocessed-files"
    Then User receives 403 response code
    And User receives response message "Access Denied"

    Examples:
      | role       |
      | INVALIDJWT |

  Scenario Outline: Negative - Get UnprocessedFiles with 401 response.
    Given User starts QPPCT API test
    When User authenticates the QPPCT API with role=<role>
    When User makes GET request to "/cpc/unprocessed-files"
    Then User receives 500 response code
    And User receives response message "Signed JWSs are not supported."

    Examples:
      | role            |
      | UNAUTHORIZEDJWT |
