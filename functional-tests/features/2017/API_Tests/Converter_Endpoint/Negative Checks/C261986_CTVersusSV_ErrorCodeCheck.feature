  @tcID_261986
@benton @testRail  @ConverterEndpoint @qppct_imp_api
Feature: C261986 This is a test to verify that the Converter sends error codes that differentiate between the Converter and Submissions

Scenario Outline: CT Error
	Given User starts QPPCT API test
	When User makes a Multipart POST request with <file_path>
	Then User receives 422 response code
	And the JSON response at "errors/0/details/0/errorCode" should be 63
	And the JSON response at "errors/0/details/0/message" should be "CT - The Alternative Payment Model (APM) Entity Identifier is not valid.  Here is a link to the IG section on identifiers: https://ecqi.healthit.gov/system/files/eCQM_QRDA_EC-508_0.pdf#page=15"

	Examples:
	  | file_path                             |
	  | 2017/CPCPlus_Success_Prod_InvalidAPMID.xml |

Scenario Outline: SV Error
	Given User starts QPPCT API test
	When User makes a Multipart POST request with <file_path>
	Then User receives 422 response code
	And the JSON response at "errors/0/details/0/errorCode" should be null
	And the JSON response at "errors/0/details/0/message" should be "SV - field 'measureId' in Submission.measurementSets[1].measurements[0] is missing"

	Examples:
      | file_path                                                         |
      | 2017/CPCPlus_Success_Prod_ValidAPMID_IASection_IAIdentifierRemoved.xml |
