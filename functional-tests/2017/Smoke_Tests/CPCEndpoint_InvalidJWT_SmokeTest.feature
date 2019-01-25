@tcID_
@benton  @testRail @qppct_smoke @CI @CPCEndpoint
Feature: C261981 This is a test that verifies the base functionality of the CPC+ Endpoint.

  Scenario Outline: Negative - Put File with 401 response.
    Given User starts QPPCT API test
    When User authenticates the QPPCT API with role=<role>
    When User makes GET request to "/cpc/unprocessed-files"
    Then User receives 403 response code
    Then User receives response message "Access Denied"
    When User makes GET request to "/cpc/file/"
    Then User receives 403 response code
    And User receives response message "Access Denied"
    When User makes PUT request to "/cpc/file/" with:
    """
    """
    Then User receives 403 response code
    And User receives response message "Access Denied"

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
