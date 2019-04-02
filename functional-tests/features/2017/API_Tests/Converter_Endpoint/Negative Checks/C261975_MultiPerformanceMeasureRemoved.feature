  @tcID_261975
@testRail  @benton @ConverterEndpoint @qppct_imp_api
Feature: C261975 This is a test to verify that a QRDA III file with a Multi Performance Measure removed receives proper error code and message

  Scenario Outline: Negative
    Given User starts QPPCT API test
    When  User makes a Multipart POST request with <file_path>
    Then  User receives 422 response code
    And   the JSON response at "errors/0/details/0/errorCode" should be 57
    And   the JSON response at "errors/0/details/0/message" should be "CT - The measure reference results must have a single measure population"

    Examples:
      | file_path                                                         |
      | 2017/CPCPlus_Success_Prod_ValidAPMID_MultiPerfMeas305_DENEXRemoved.xml |
      | 2017/CPCPlus_Success_Prod_ValidAPMID_MultiPerfMeas305_IPOPRemoved.xml  |
