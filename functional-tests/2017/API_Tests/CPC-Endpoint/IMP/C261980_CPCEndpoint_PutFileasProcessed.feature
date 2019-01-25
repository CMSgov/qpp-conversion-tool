 @ct_imp_regression @tcID_261980
@benton @testRail  @CPCEndpoint @qppct_imp_api
Feature: C261980 This is a test that verifies the CPC+ Endpoint by updating the status of an unprocessed-file.

  Scenario Outline: Positive - Put File with 200 response.
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

    Examples:
      | role       |
      | CPCPLUSJWT |

  Scenario Outline: Negative - Put File with 404 response.
    Given User starts QPPCT API test
    When User authenticates the QPPCT API with role=<role>
    When User makes GET request to "/cpc/unprocessed-files"
    Then User receives 200 response code
    When User makes PUT request to "/cpc/file/invalid" with:
    """
    """
    Then User receives 404 response code
    And User receives response message "File not found!"

    Examples:
      | role       |
      | CPCPLUSJWT |

  Scenario: Negative - Put File with 401 response.
    Given User starts QPPCT API test
    When User makes GET request to "/cpc/unprocessed-files"
    Then User receives 401 response code
    Then User receives response message "Unauthorized"
    When User makes GET request to "/cpc/file/"
    Then User receives 401 response code
    And User receives response message "Unauthorized"
    When User makes PUT request to "/cpc/file/" with:
    """
    """
    Then User receives 401 response code
    And User receives response message "Unauthorized"

  Scenario Outline: Negative - Put File with 401 response.
    Given User starts QPPCT API test
    When User authenticates the QPPCT API with role=<role>
    When User makes GET request to "/cpc/unprocessed-files"
    Then User receives 401 response code
    Then User receives response message "Unauthorized"
    When User makes GET request to "/cpc/file/"
    Then User receives 401 response code
    And User receives response message "Unauthorized"
    When User makes PUT request to "/cpc/file/" with:
    """
    """
    Then User receives 401 response code
    And User receives response message "Unauthorized"

    Examples:
      | role       |
      | INVALIDJWT |

  Scenario Outline: Negative - Put File with 401 response.
    Given User starts QPPCT API test
    When User authenticates the QPPCT API with role=<role>
    When User makes GET request to "/cpc/unprocessed-files"
    Then User receives 401 response code
    Then User receives response message "Unauthorized"
    When User makes GET request to "/cpc/file/"
    Then User receives 401 response code
    And User receives response message "Unauthorized"
    When User makes PUT request to "/cpc/file/" with:
    """
    """
    Then User receives 401 response code
    And User receives response message "Unauthorized"

    Examples:
      | role            |
      | UNAUTHORIZEDJWT |
