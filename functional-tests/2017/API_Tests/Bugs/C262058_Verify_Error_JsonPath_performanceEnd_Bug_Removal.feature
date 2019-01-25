@tcID_262058
@rubin
Feature: C262058 This is a test that verifies that QPPA JsonPath error bug has been fixed.

  Scenario Outline: Negative -- Verify the expected error JsonPath is "$.measurementSet[0].performanceEnd"
    Given User starts QPPA API test
    When  User authenticates the QPPA API with role=<role>
    And   User does not have any measurement-sets posted
    When  User makes POST request to "/api/submissions/submissions" with <file_path>/Bad_Performance_End.<format> in <format> format
    Then  User receives 422 response code
    And   the JSON response at "error/details/0/path" should not be "$.measurementSets.performanceEnd"
    And   the JSON response at "error/details/0/path" should be "$.measurementSet[0].performanceEnd"

    Examples:
      | role         | file_path        | format |
      | AmbSurgGroup | QPPA/Submissions | json   |
