  @tcID_261984
@testRail  @ConverterEndpoint @qppct_imp_api @benton
Feature: C261984 This is a test that verifies CPC+ QRDA-III files with mixed and lowercase Stratifier UUID's pass validation.

  Scenario Outline: Positive
    Given User starts QPPCT API test
    When User makes a Multipart POST request with <file_path>
    Then User receives 201 response code
    And the JSON response at "measurementSets" should not be null

    Examples:
      | file_path                                                   |
      | 2017/CPCPlus_Success_Prod_ValidAPMID_LowerCaseStratifierUUID.xml |
      | 2017/CPCPlus_Success_Prod_ValidAPMID_MixedCaseStratifierUUID.xml |
