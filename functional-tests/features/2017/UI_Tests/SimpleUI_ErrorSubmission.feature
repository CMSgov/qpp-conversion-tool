@ct_ui
Feature: This is a test to check whether the Simple UI page can be accessed

  Scenario Outline: Upload file to the Simple UI and Download Conversion
    Given User opens a browser with directory "<directory>"
    And User navigates to the Conversion Tool Simple UI page
    And User uploads a <file> file to the Simple UI
    And User downloads error response
    And The system checks for the error file at <download_path>
    And The system deletes the downloaded file at <download_path>

    Examples:
      | directory                                                        | file                                                                   | download_path                     |
      | /features/step_definitions/API/Data Files/Integration/QPPCT/2017 | /Integration/QPPCT/2017/valid-QRDA-III-latest_MIPS_FakeProgramName.xml | Integration/QPPCT/2017/error.json |