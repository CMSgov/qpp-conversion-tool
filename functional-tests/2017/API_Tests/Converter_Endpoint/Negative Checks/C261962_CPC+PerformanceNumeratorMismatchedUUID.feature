  @tcID_261962
@smoke @testRail  @qppct_imp_api @O'Neill
Feature: C261962 This is a test to verify that a QRDA III file with CPC+ Performance Numerator Mismatched UUID receives proper error code and message

  Scenario Outline: Negative
    Given User starts QPPCT API test
    When  User makes a Multipart POST request with <file_path>
    Then  User receives 422 response code
    And   the JSON response at "errors/0/details/0/errorCode" should be 59
    And   the JSON response at "errors/0/details/0/message" should be "CT - The electronic measure id: CMS165v5 requires a performanceRateUuid with the correct UUID of F9FEBF42-4B21-47A9-B03E-D2DA5CF8492B. Here is a link to the IG containing all the valid measure ids: https://ecqi.healthit.gov/system/files/eCQM_QRDA_EC-508_0.pdf#page=88"

    Examples:
      | file_path                                                   |
      | 2017/CPCPlus_Success_Prod_ValidAPMID_PerfNumerMismatchedUUID.xml |
