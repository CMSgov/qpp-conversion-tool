  @tcID_261951
  @testRail  @ConverterEndpoint @qppct_imp_api @benton
  Feature: C261951 This is a test that verifies that QRDA-III files with Incorrect Start and End Dates return the correct error code and message.

  Scenario Outline: Negative
    Given User starts QPPCT API test
    When  User makes a Multipart POST request with <file_path>
    Then  User receives 422 response code
    And   the JSON response at "errors/0/details/0/errorCode" should be 55
    And   the JSON response at "errors/0/details/0/message" should be "CT - A CPC Plus Performance period start must be 01/01/2017. Please refer to the IG for more information here: https://ecqi.healthit.gov/system/files/eCQM_QRDA_EC-508_0.pdf#page=14"

    Examples:
      | file_path                                                                                            |
      | 2017/Performance Period Incorrect Dates/CPCPlus_Success_Prod_ValidAPMID_BeginPerformanceDateIncorrect.xml |

  Scenario Outline: Negative
    Given User starts QPPCT API test
    When  User makes a Multipart POST request with <file_path>
    Then  User receives 422 response code
    And   the JSON response at "errors/0/details/0/errorCode" should be 56
    And   the JSON response at "errors/0/details/0/message" should be "CT - A CPC Plus Performance period end must be 12/31/2017. Please refer to the IG for more information here: https://ecqi.healthit.gov/system/files/eCQM_QRDA_EC-508_0.pdf#page=14"

    Examples:
      | file_path                                                                                          |
      | 2017/Performance Period Incorrect Dates/CPCPlus_Success_Prod_ValidAPMID_EndPerformanceDateIncorrect.xml |
