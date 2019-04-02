  @tcID_261947
@testRail  @ConverterEndpoint @qppct_imp_api
Feature: C261947 This is a test that verifies a QRDA-III files with a fake program name receive the proper error code and message.

  Scenario Outline: Negative
    Given User starts QPPCT API test
    When  User makes a Multipart POST request with <file_path>
    Then  User receives 422 response code
    And   the JSON response at "errors/0/details/0/errorCode" should be 25
    And   the JSON response at "errors/0/details/0/message" should be "CT - The Clinical Document program name pqrs_mips_indiv is not recognized. Valid program names are MIPS_GROUP, CPCPLUS, or MIPS_INDIV."

    Examples:
      | file_path                                      |
      | 2017/valid-QRDA-III-latest_MIPS_FakeProgramName.xml |
