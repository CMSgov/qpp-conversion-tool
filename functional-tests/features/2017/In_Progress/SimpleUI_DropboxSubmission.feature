Feature: This is a test to check whether the Simple UI page can be accessed

Scenario Outline: Upload file to the Simple UI and Download Conversion
  Given User opens a browser with directory "<directory>"
	And User navigates to the Conversion Tool Simple UI page
	And User Drops a file into the Drag and Drop box at <file>
    And The system deletes the downloaded file at <download_path>

	Examples:
	| directory                                                        | file                                         | download_path                                 |
	| /features/step_definitions/API/Data Files/Integration/QPPCT/2017 | /Integration/QPPCT/2017/valid-QRDA-III-latest.xml | /Integration/QPPCT/valid-QRDA-III-latest.json |