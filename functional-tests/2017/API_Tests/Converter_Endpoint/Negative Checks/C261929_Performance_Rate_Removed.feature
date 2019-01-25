  @tcID_261929
@thiel @testRail  @ConverterEndpoint @qppct_imp_api
Feature: C261929 This is a test to verify that MIPS and CPC+ QRDA-III files with performance rate removed cannot be successfully converted when sent to the /converter endpoint

Scenario Outline: Negative
	Given User starts QPPCT API test
	When User makes a Multipart POST request with <file_path>
	Then User receives 422 response code
	And the JSON response at "errors/0/details/0/errorCode" should be 72
	And the JSON response at "errors/0/details/0/message" should be "CT - The Performance Rate is missing"

	Examples:
      | file_path                                                                              |
      | 2017/Quality Performance Rate Removed/CPCPlus_Success_Prod_PerformanceRateRemoved.xml       |
      | 2017/Quality Performance Rate Removed/valid-QRDA-III-latest_MIPS_PerformanceRateRemoved.xml |
