  @tcID_261939
@benton @testRail  @ConverterEndpoint @qppct_imp_api
Feature: C261939 This is a test to verify that a valid QRDA III file with ACI measures can be successfully converted to JSON when sent to the /api/submissions/public/converter endpoint using a Multipart POST

Scenario Outline: Positive
	Given User starts QPPCT API test
	When User makes a Multipart POST request with <file_path>
	Then User receives 201 response code
	And the JSON response at "measurementSets" should not be null

	Examples:
      | file_path                                      |
      | 2017/valid-QRDA-III-aci-700.xml                     |
      | 2017/CPCPlus_Success_Prod_ValidAPMID_ACISection.xml |
