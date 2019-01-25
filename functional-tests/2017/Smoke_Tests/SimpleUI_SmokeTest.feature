
Feature: This is a test to check whether the Simple UI page can be accessed

Scenario Outline: Upload file to the Simple UI and Download Conversion
	Given User opens a browser with directory "<directory>"
	And User navigates to the Conversion Tool Simple UI page
	And User uploads a <file> file to the Simple UI
	Then  User receives Successful Upload message
    And   The system deletes the downloaded file at <download_path>

	Examples:
	| directory                                                        | file                                        	   | download_path                                 |
	| /features/step_definitions/API/Data Files/Integration/QPPCT/2017 | /Integration/QPPCT/2017/valid-QRDA-III-latest.xml | /Integration/QPPCT/2017/valid-QRDA-III-latest.json |