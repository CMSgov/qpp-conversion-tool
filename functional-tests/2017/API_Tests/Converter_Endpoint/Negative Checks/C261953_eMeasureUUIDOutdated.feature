  @tcID_261953
@testRail  @ConverterEndpoint @qppct_imp_api @benton
Feature: C261953 This is a test that verifies QRDA-III MIPS and CPC+ files with an Outdated eMeasure UUID receive the proper error code and message.

  Scenario Outline: Negative
    Given User starts QPPCT API test
    When  User makes a Multipart POST request with <file_path>
    Then  User receives 422 response code
    And   the JSON response at "errors/0/details/0/errorCode" should be 6
    And   the JSON response at "errors/0/details/0/message" should be "CT - The measure GUID supplied 40280381-4b9a-3825-014b-c11ae59d069b is invalid. Please see the 2017 IG https://ecqi.healthit.gov/system/files/eCQM_QRDA_EC-508_0.pdf#page=88 for valid measure GUIDs."

    Examples:
      | file_path                                                |
      | 2017/valid-QRDA-III-latest_MIPS_eMeasureUUIDOutdated.xml      |
      | 2017/CPCPlus_Success_Prod_ValidAPMID_OutdatedeMeasureUUID.xml |
