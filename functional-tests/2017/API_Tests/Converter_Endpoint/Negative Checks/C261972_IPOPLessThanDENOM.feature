  @tcID_261972
@testRail  @benton @ConverterEndpoint @qppct_imp_api
Feature: C261972 This is a test to verify that a QRDA III file with its IPOP value less than its DENOM value receives proper error code and message

  Scenario Outline: Negative
    Given User starts QPPCT API test
    When  User makes a Multipart POST request with <file_path>
    Then  User receives 422 response code
    And   the JSON response at "errors/0/details/0/errorCode" should be 13
    And   the JSON response at "errors/0/details/0/message" should be "CT - Denominator count must be less than or equal to Initial Population count for a measure that is a proportion measure"

    Examples:
      | file_path                                             |
      | 2017/valid-QRDA-III-latest_MIPS_IPOPLessThanDENOM.xml      |
      | 2017/CPCPlus_Success_Prod_ValidAPMID_IPOPLessThanDENOM.xml |
