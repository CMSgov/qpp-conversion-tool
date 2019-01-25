  @tcID_261928
@thiel @testRail  @ConverterEndpoint @qppct_imp_api
Feature: C261928 This is a test to verify that MIPS and CPC+ QRDA III files with multiple denex, denominator or numerators in quality measures will not be converted

Scenario Outline: Negative
	Given User starts QPPCT API test
	When User makes a Multipart POST request with <file_path>
	Then User receives 422 response code
	And the JSON response at "errors/0/details/0/errorCode" should be 14
	And the JSON response at "errors/0/details/0/message" should include "CT - The electronic measure id:"
	And the JSON response at "errors/0/details/0/message" should include "requires"
	And the JSON response at "errors/0/details/0/message" should include "but there are "

	Examples:
      | file_path                                                                            |
      | 2017/Quality Multiple Denex/CPCPlus_Success_Prod_MultipleDenexException.xml               |
      | 2017/Quality Multiple Denex/valid-QRDA-III-latest_MIPS_QualityMultipleDENEXExceptions.xml |
      | 2017/Quality Multiple Denom/CPCPlus_Success_Prod_MultipleDenomEx.xml                      |
      | 2017/Quality Multiple Denom/valid-QRDA-III-latest_MIPS_QualityMultipleDenoms.xml          |
      | 2017/Quality Multiple Numerators/CPCPlus_Success_Prod_MultipleNumeratorEx.xml             |
      | 2017/Quality Multiple Numerators/valid-QRDA-III-latest_MIPS_QualityMultipleNumerators.xml |
