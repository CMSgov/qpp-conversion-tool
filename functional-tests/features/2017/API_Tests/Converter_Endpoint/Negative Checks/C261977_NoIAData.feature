  @tcID_261977
@testRail  @ConverterEndpoint @qppct_imp_api @benton
Feature: C261977 This is a test that verifies QRDA-III CPC+ and MIPS files without any IA data receive the proper error code and message.

  Scenario Outline: Negative
    Given User starts QPPCT API test
    When User makes a Multipart POST request with <file_path>
    Then User receives 422 response code
    And the JSON response at "errors/0/details/0/errorCode" should be 43
    And the JSON response at "errors/0/details/0/message" should be "CT - The IA Section must have at least one Improvement Activity"

    Examples:
      | file_path                                                       |
      | 2017/No IA Data/CPCPlus_Success_Prod_ValidAPMID_IASectionRemoved.xml |
      | 2017/No IA Data/valid-QRDA-III-latest-no-ia.xml                      |
