  @tcID_261940
@testRail  @ConverterEndpoint @qppct_imp_api
Feature: C261940 This is a test that verifies QRDA-III MIPS and CPC+ files without an IA measure receive the proper error code and message.

  Scenario Outline: Negative
    Given User starts QPPCT API test
    When  User makes a Multipart POST request with <file_path>
    Then  User receives 422 response code
    And   the JSON response at "errors/0/details/0/errorCode" should be 43
    And   the JSON response at "errors/0/details/0/message" should be "CT - The IA Section must have at least one Improvement Activity"

    Examples:
      | file_path                                                       |
      | 2017/No IA Data/CPCPlus_Success_Prod_ValidAPMID_IASectionRemoved.xml |
      | 2017/No IA Data/valid-QRDA-III-latest-no-ia.xml                      |
