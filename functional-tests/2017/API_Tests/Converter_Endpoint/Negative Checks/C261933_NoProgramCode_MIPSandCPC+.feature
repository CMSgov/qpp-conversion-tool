  @tcID_261933
@testRail  @ConverterEndpoint @qppct_imp_api
Feature: C261933 This is a test that verifies QRDA-III MIPS and CPC+ files with no program code receive the proper error code and message.

  Scenario Outline: Negative
    Given User starts QPPCT API test
    When  User makes a Multipart POST request with <file_path>
    Then  User receives 422 response code
    And   the JSON response at "errors/0/details/0/errorCode" should be 25
    And   the JSON response at "errors/0/details/0/message" should be "CT - The Clinical Document program name  is not recognized. Valid program names are MIPS_GROUP, CPCPLUS, or MIPS_INDIV."

    Examples:
      | file_path                                              |
      | 2017/No Program Code/CPCPlus_Success_Prod_NoProgramCode.xml |
      | 2017/No Program Code/QRDA-III-no-program.xml                |
