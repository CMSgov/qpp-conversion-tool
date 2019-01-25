@tcID_261981
@benton  @testRail @qppct_smoke @CI @CPCEndpoint
Feature: C261981 This is a test that verifies the base functionality of the CPC+ Endpoint.

  Scenario Outline: Positive - Put File with 200 response.
    Given User starts QPPCT API test
    When User authenticates the QPPCT API with role=<role>
    When User makes GET request to "/cpc/unprocessed-files"
    Then User receives 200 response code
    Then I keep the JSON response at "0/fileId" as "File_ID"
#    When User makes GET request to "/cpc/file/%{File_ID}"
#    Then User receives 200 response code

    Examples:
      | role       |
      | CPCPLUSJWT |

  Scenario Outline: Negative - Get and Put File with 404 response.
    Given User starts QPPCT API test
    When User authenticates the QPPCT API with role=<role>
    When User makes GET request to "/cpc/file/invalid"
    Then User receives 404 response code
    Then User receives response message "File not found!"
    When User makes PUT request to "/cpc/file/invalid" with:
    """

    """
    Then User receives 404 response code
    And User receives response message "File not found!"

    Examples:
      | role       |
      | CPCPLUSJWT |
