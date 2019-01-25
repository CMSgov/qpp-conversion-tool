  @tcID_261930
@thiel @testRail  @ConverterEndpoint @qppct_imp_api
Feature: C261930 This is a test that verifies error handing for a MIPS/CPC+ file with no Denex/Denom/Numerator

  Scenario Outline: Negative
    Given User starts QPPCT API test
    When User makes a Multipart POST request with <file_path>
    Then User receives 422 response code
    And the JSON response at "errors/0/details/0/errorCode" should be 9
    And the JSON response at "errors/0/details/0/message" should be "CT - Aggregate count value must be an integer"

    Examples:
      | file_path                                                 |
      | 2017/No Denex Value/invalid-QRDA-III-latest-no-denex.xml       |
      | 2017/No Denex Value/CPCPlus_Success_Prod_NoDenex.xml           |
      | 2017/No Denom Value/CPCPlus_Success_Prod_NoDenom.xml           |
      | 2017/No Denom Value/CPCPlus_Success_Prod_NoNumerator.xml       |
      | 2017/No Denom Value/invalid-QRDA-III-latest-no-denom.xml       |
      | 2017/No Denom Value/invalid-QRDA-III-latest-numerator-none.xml |
