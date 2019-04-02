@tcID_261936
@thiel @testRail @qppct_smoke @CI @ConverterEndpoint
Feature: C261936 This is a test to verify that a valid QRDA III file can be succesfully converted to JSON when sent to the /api/submissions/public/converter endpoint using a Multipart POST

Scenario Outline: Positive
	Given User starts QPPCT API test
	When User makes a Multipart POST request with <file_path>
	Then User receives 201 response code
	And the JSON response at "measurementSets" should not be null

	Examples:
      | file_path                                            |
      | 2017/Valid Conversion/valid-QRDA-III-latest.xml           |
