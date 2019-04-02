  @tcID_261961
@smoke @testRail  @qppct_imp_api @O'Neill
Feature: C261961 This is a test to verify that a QRDA III file with CPC+ Outcome Measures missing receives proper error code and message

  Scenario Outline: Negative
    Given User starts QPPCT API test
    When  User makes a Multipart POST request with <file_path>
    Then  User receives 422 response code
    And   the JSON response at "errors/0/details/0/errorCode" should be 64
    And   the JSON response at "errors/0/details/0/message" should be "CT - CPC+ Submissions must have at least 2 of the following outcome measures: 40280381-51f0-825b-0152-22b98cff181a,40280381-51f0-825b-0152-229afff616ee,40280381-5118-2f4e-0151-3a9382cd09ba"

    Examples:
      | file_path                                                  |
      | 2017/CPCPlus_Success_Prod_ValidAPMID_OutcomeMeasuresRemoved.xml |
