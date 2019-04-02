 @ct_dev_regression @tcID_261979
@benton @testRail @ct_dev_api @CPCEndpoint
Feature: C261979 This is a test that verifies the CPC+ Endpoint by retrieving a file by its File ID.

  Scenario Outline: Positive - Get File with 200 response.
    Given User starts QPPCT API test
    When User authenticates the QPPCT API with role=<role>
    When User makes GET request to "/cpc/unprocessed-files"
    Then User receives 200 response code
    Then I keep the JSON response at "0/fileId" as "File_ID"
    When User makes GET request to "/cpc/file/%{File_ID}"
    Then User receives 200 response code

    Examples:
      | role       |
      | CPCPLUSJWT |

  Scenario Outline: Negative - Get File with 404 response.
    Given User starts QPPCT API test
    When User authenticates the QPPCT API with role=<role>
    When User makes GET request to "/cpc/unprocessed-files"
    Then User receives 200 response code
    When User makes GET request to "/cpc/file/invalid"
    Then User receives 404 response code
    And User receives response message "File not found!"

    Examples:
      | role       |
      | CPCPLUSJWT |

#  Scenario Outline: Negative - Get File with 401 response.
#    Given User starts QPPCT API test
#    When User makes GET request to "/cpc/unprocessed-files"
#    Then User receives 401 response code
#    Then User receives response message "Access Denied"
#    When User makes GET request to "/cpc/file/"
#    Then User receives 401 response code
#    And User receives response message "Access Denied"

  Scenario Outline: Negative - Get File with 401 response.
    Given User starts QPPCT API test
    When User authenticates the QPPCT API with role=<role>
    When User makes GET request to "/cpc/unprocessed-files"
    Then User receives 403 response code
    Then User receives response message "Access Denied"
    When User makes GET request to "/cpc/file/"
    Then User receives 403 response code
    And User receives response message "Access Denied"

    Examples:
      | role       |
      | INVALIDJWT |

  Scenario Outline: Negative - Get File with 401 response.
    Given User starts QPPCT API test
    When User authenticates the QPPCT API with role=<role>
    When User makes GET request to "/cpc/unprocessed-files"
    Then User receives 500 response code
    Then User receives response message "Signed JWSs are not supported."
    When User makes GET request to "/cpc/file/"
    Then User receives 500 response code
    And User receives response message "Signed JWSs are not supported."

    Examples:
      | role            |
      | UNAUTHORIZEDJWT |
