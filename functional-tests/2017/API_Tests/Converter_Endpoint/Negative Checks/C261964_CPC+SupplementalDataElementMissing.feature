  @tcID_261964
@smoke @testRail  @O'Neill @qppct_imp_api
Feature: C261964 This is a test to verify that a QRDA III file with CPC+ Supplemental Data Element Missing receives proper error code and message

  Scenario Outline: Negative
    Given User starts QPPCT API test
    When  User makes a Multipart POST request with <file_path>
    Then  User receives 422 response code
    And   the JSON response at "errors/0/details/0/errorCode" should be 66
    And   the JSON response at "errors/0/details/0/message" should be "CT - Missing the SEX - MALE supplemental data for code M for the measure id CMS165v5's Sub-population IPOP"

    Examples:
      | file_path                                                          |
      | 2017/CPCPlus_Success_Prod_ValidAPMID_SupplementalDataElementRemoved.xml |
