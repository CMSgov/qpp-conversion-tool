  @tcID_261941
@testRail  @ConverterEndpoint @qppct_imp_api
Feature: C261941 This is a test that verifies QRDA-III MIPS and CPC+ files with a performance rate greater than one receive the proper error code and message.

  Scenario Outline: Negative
    Given User starts QPPCT API test
    When  User makes a Multipart POST request with <file_path>
    Then  User receives 422 response code
    And   the JSON response at "errors/0/details/0/errorCode" should be 33
    And   the JSON response at "errors/0/details/0/message" should be "CT - The Performance Rate 1.8 is invalid. It must be a decimal between 0 and 1."

    Examples:
      | file_path                                                                               |
      | 2017/Perf Rate Greater Than One/CPCPlus_Success_Prod_PerfRateGreaterThanOne.xml              |

  Scenario Outline: Negative
    Given User starts QPPCT API test
    When  User makes a Multipart POST request with <file_path>
    Then  User receives 422 response code
    And   the JSON response at "errors/0/details/0/errorCode" should be 33
    And   the JSON response at "errors/0/details/0/message" should be "CT - The Performance Rate 2.842105 is invalid. It must be a decimal between 0 and 1."

    Examples:
      | file_path                                                                               |
      | 2017/Perf Rate Greater Than One/valid-QRDA-III-latest_MIPS_PerformanceRateGreaterThanOne.xml |
