  @tcID_261956
@testRail  @ConverterEndpoint @qppct_imp_api @O'Neill
Feature: C261956 This is a test to verify that a QRDA III file with an incorrect Aggregate Count Code receives proper error code and message

  Scenario Outline: Negative
    Given User starts QPPCT API test
    When  User makes a Multipart POST request with <file_path>
    Then  User receives 422 response code
    And   the JSON response at "errors/0/details/0/errorCode" should be 14
    And   the JSON response at "errors/0/details/0/message" should be "CT - The electronic measure id: CMS122v5 requires 1 IPP or IPOP(s) but there are 0"

    Examples:
      | file_path                                                 |
      | 2017/CPCPlus_Success_Prod_ValidAPMID_AggCountCodeIncorrect.xml |
