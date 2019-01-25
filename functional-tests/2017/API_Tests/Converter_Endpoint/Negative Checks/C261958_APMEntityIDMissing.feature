  @tcID_261958
@smoke @testRail  @O'Neill @qppct_imp_api
Feature: C261958 This is a test to verify that a QRDA III file with an APM Entity ID missing receives proper error code and message

  Scenario Outline: Negative
    Given User starts QPPCT API test
    When  User makes a Multipart POST request with <file_path>
    Then  User receives 422 response code
    And   the JSON response at "errors/0/details/0/errorCode" should be 62
    And   the JSON response at "errors/0/details/0/message" should be "CT - The Alternative Payment Model (APM) Entity Identifier must not be empty. Here is a link to the IG section on identifiers: https://ecqi.healthit.gov/system/files/eCQM_QRDA_EC-508_0.pdf#page=15"

    Examples:
      | file_path                           |
      | 2017/CPCPlus_Success_Prod_EmptyAPMID.xml |
