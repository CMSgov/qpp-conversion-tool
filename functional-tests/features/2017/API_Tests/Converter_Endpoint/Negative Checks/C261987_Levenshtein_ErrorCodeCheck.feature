  @tcID_261987
@benton @testRail  @ConverterEndpoint @qppct_imp_api
Feature: C261987 This is a test to verify that the Covnerter will provide suggestions for the closest matching Measure.

Scenario Outline: CT Error
	Given User starts QPPCT API test
	When User makes a Multipart POST request with <file_path>
	Then User receives 422 response code
	And the JSON response at "errors/0/details/0/errorCode" should be 6
	And the JSON response at "errors/0/details/0/message" should be "CT - The measure GUID supplied 40280381-51f0-825b-0152-22b98cff181b is invalid. Please see the 2017 IG https://ecqi.healthit.gov/system/files/eCQM_QRDA_EC-508_0.pdf#page=88 for valid measure GUIDs."

	Examples:
	  | file_path                                          |
	  | 2017/valid-QRDA-III-latest-EditedQualityMeasureUUID.xml |
