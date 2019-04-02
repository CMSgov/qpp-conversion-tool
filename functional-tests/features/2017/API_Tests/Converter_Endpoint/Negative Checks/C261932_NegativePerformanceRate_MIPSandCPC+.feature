  @tcID_261932
@testRail  @ConverterEndpoint @qppct_imp_api
Feature: C261932 This is a test that verifies QRDA-III MIPS and CPC+ files with a negative performance rate receive the proper error code and message.

  Scenario Outline: Negative
    Given User starts QPPCT API test
    When  User makes a Multipart POST request with <file_path>
    Then  User receives 422 response code
    And   the JSON response at "errors/0/details/0/errorCode" should be 33
    And   the JSON response at "errors/0/details/0/message" should be "CT - The Performance Rate -.8 is invalid. It must be a decimal between 0 and 1."

    Examples:
      | file_path                                                                       |
      | 2017/Negative PerformanceRate/CPCPlus_Success_Prod_NegativePerformanceRate.xml       |

  Scenario Outline: Negative
    Given User starts QPPCT API test
    When  User makes a Multipart POST request with <file_path>
    Then  User receives 422 response code
    And   the JSON response at "errors/0/details/0/errorCode" should be 33
    And   the JSON response at "errors/0/details/0/message" should be "CT - The Performance Rate -0.842105 is invalid. It must be a decimal between 0 and 1."

    Examples:
      | file_path                                                                       |
      | 2017/Negative PerformanceRate/valid-QRDA-III-latest_MIPS_NegativePerformanceRate.xml |
