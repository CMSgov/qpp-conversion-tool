  @tcID_261973
@testRail  @benton @ConverterEndpoint @qppct_imp_api
Feature: C261973 This is a test to verify that a QRDA III file with the Measure Performance Value removed receives proper error code and message

  Scenario Outline: Negative
    Given User starts QPPCT API test
    When  User makes a Multipart POST request with <file_path>
    Then  User receives 422 response code
    And   the JSON response at "errors/0/details/0/errorCode" should be 51
    And   the JSON response at "errors/0/details/0/message" should be "CT - A single measure performed value is required and must be either a Y or an N."

    Examples:
      | file_path                                                               |
      | 2017/valid-QRDA-III-latest_MIPS_MeasurePerformedStatusandValueRemoved.xml    |
      | 2017/CPCPlus_Success_Prod_ValidAPMID_IASection_StatusCodeandValueRemoved.xml |
