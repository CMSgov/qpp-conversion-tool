@ct_ui
Feature: This is a test to check whether the Simple UI page can be accessed

  Scenario Outline: Upload file to the Simple UI and Download Conversion
    Given User opens a browser with directory "<directory>"
    And User navigates to the Conversion Tool Simple UI page
    And User uploads a <file1> file to the Simple UI
    And   User receives Successful Upload message
    And User uploads another <file2> file to the Simple UI
    And   User receives Successful Upload message
    And   The system deletes the downloaded file at <download_path1>
    And   The system deletes the downloaded file at <download_path2>

    Examples:
      | directory                                                        | file1                                             | file2                                            | download_path1                                     | download_path2                                         |
      | /features/step_definitions/API/Data Files/Integration/QPPCT/2017 | /Integration/QPPCT/2017/valid-QRDA-III-latest.xml | /Integration/QPPCT/2017/valid-QRDA-III-group.xml | /Integration/QPPCT/2017/valid-QRDA-III-latest.json | /Integration/QPPCT/2017/valid-QRDA-III-latest (1).json |