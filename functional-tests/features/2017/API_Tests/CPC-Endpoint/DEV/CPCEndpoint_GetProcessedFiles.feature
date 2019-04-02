 @ct_dev_regression @tcID_
@benton @testRail @ct_dev_api @CPCEndpoint
Feature: This is a test that verifies the CPC+ Endpoint by retrieving the list of unprocessed-files.

  Scenario: Negative - Get ProcessedFiles with 401 response.
    Given User starts QPPCT API test
    And   User has no authorization
    And   the user is not authenticated
    When  User makes GET request to "/cpc/qpp/"
    Then  User receives 403 response code
    And   User receives response message "Access Denied"

  Scenario Outline: Positive - Get UnprocessedFiles with 200 response.
    Given User starts QPPCT API test
    When User authenticates the QPPCT API with role=<role>
    When User makes GET request to "/cpc/unprocessed-files"
    Then User receives 200 response code
    Then I keep the JSON response at "0/fileId" as "File_ID"
    When User makes GET request to "/cpc/file/%{File_ID}"
    Then User receives 200 response code
    When User makes PUT request to "/cpc/file/%{File_ID}" with:
    """
    """
    Then User receives 200 response code
    And User receives response message "The file was found and will be updated as processed."
    When User makes GET request to "/cpc/qpp/%{File_ID}"
    Then User receives 500 response code

    Examples:
      | role       |
      | CPCPLUSJWT |

  Scenario Outline: Negative - Get UnprocessedFiles with 200 response.
    Given User starts QPPCT API test
    When User authenticates the QPPCT API with role=<role>
    When User makes GET request to "/cpc/qpp/invalid"
    Then User receives 404 response code
    And User receives response message "File not found!"

    Examples:
      | role       |
      | CPCPLUSJWT |

  Scenario Outline: Negative - Get UnprocessedFiles with 401 response.
    Given User starts QPPCT API test
    When User authenticates the QPPCT API with role=<role>
    When User makes GET request to "/cpc/qpp/"
    Then User receives 403 response code
    And User receives response message "Access Denied"

    Examples:
      | role       |
      | INVALIDJWT |

  Scenario Outline: Negative - Get UnprocessedFiles with 401 response.
    Given User starts QPPCT API test
    When User authenticates the QPPCT API with role=<role>
    When User makes GET request to "/cpc/qpp/"
    Then User receives 500 response code
    And User receives response message "Signed JWSs are not supported."

    Examples:
      | role            |
      | UNAUTHORIZEDJWT |
