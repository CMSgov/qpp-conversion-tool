  @tcID_261963
@smoke @testRail  @O'Neill @qppct_imp_api
Feature: C261963 This is a test to verify that a QRDA III file with CPC+ Practice Site Address removed receives proper error code and message

  Scenario Outline: Negative
    Given User starts QPPCT API test
    When  User makes a Multipart POST request with <file_path>
    Then  User receives 422 response code
    And   the JSON response at "errors/0/details/0/errorCode" should be 34
    And   the JSON response at "errors/0/details/0/message" should be "CT - CPC+ submissions must contain a practice site address. Please refer to the 2017 IG for more details https://ecqi.healthit.gov/system/files/eCQM_QRDA_EC-508_0.pdf#page=25 regarding practice site addresses."

    Examples:
      | file_path                                              |
      | 2017/CPCPlus_Success_Prod_ValidAPMID_PracSiteAddRemoved.xml |
