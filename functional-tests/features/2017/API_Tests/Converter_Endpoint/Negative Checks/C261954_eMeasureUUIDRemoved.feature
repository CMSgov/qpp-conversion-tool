  @tcID_261954
@testRail  @ConverterEndpoint @qppct_imp_api @benton
Feature: C261954 This is a test that verifies QRDA-III MIPS and CPC+ files without an eMeasure UUID receive the proper error code and message.

  Scenario Outline: Negative
    Given User starts QPPCT API test
    When  User makes a Multipart POST request with <file_path>
    Then  User receives 422 response code
    And   the JSON response at "errors/0/details/0/errorCode" should be 70
    And   the JSON response at "errors/0/details/0/message" should be "CT - The measure section measure reference and results has an incorrect number of measure GUID supplied. Please ensure that only one measure GUID is provided per measure."

    Examples:
      | file_path                                               |
      | 2017/valid-QRDA-III-latest_MIPS_eMeasureUUIDRemoved.xml      |
      | 2017/CPCPlus_Success_Prod_ValidAPMID_RemovedeMeasureUUID.xml |
