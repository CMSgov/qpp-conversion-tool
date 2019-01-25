  @tcID_261967
@smoke @testRail  @O'Neill @qppct_imp_api
Feature: C261967 This is a test to verify that a QRDA III file with a Historical IA Measure is correctly validated

  Scenario Outline: Positive
    Given User starts QPPCT API test
    When  User makes a Multipart POST request with <file_path>
    Then  User receives 201 response code
    And   the JSON response at "measurementSets" should not be null

    Examples:
      | file_path                                                   |
      | 2017/CPCPlus_Success_Prod_ValidAPMID_IASection_IA_CC_10_2014.xml |
