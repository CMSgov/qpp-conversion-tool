@ct_ui
Feature: This is a test to check whether the Simple UI page can be accessed

  Scenario Outline: Upload file to the Simple UI and Download Conversion
    Given User opens a browser with directory "<directory>"
    And   User navigates to the Conversion Tool Simple UI page
    And   User uploads a <file> file to the Simple UI
    Then  User receives Successful Upload message
    And   User closes browser
    When  User authenticates the QPPA API with role=<role>
    And   User does not have any measurement-sets posted
    And   User makes POST request to "/api/submissions/submissions" with <file_path>/valid-QRDA-III-latest-EligGroup.<format> in <format> format
    Then  User receives 201 response code
    And   I keep the JSON response at "data/submission/id" as "Submission_ID"
    When  User makes GET request to "/api/submissions/submissions/%{Submission_ID}/score"
    Then  User receives 200 response code
    And   The system deletes the downloaded file at <download_path>

    Examples:
      | directory                                                        | file                                                        | role      | file_path              | format | download_path                                                |
      | /features/step_definitions/API/Data Files/Integration/QPPCT/2017 | /Integration/QPPCT/2017/valid-QRDA-III-latest-EligGroup.xml | EligGroup | Integration/QPPCT/2017 | json   | /Integration/QPPCT/2017/valid-QRDA-III-latest-EligGroup.json |