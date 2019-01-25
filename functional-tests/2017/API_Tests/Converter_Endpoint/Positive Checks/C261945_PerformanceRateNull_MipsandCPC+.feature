  @tcID_261945
@testRail  @ConverterEndpoint @qppct_imp_api @O'Neill
Feature: C261945 This is a test to verify that a valid QRDA III file with Performance Rate of Null can be succesfully posted to the converter endpoint

  Scenario Outline: Positive
    Given User starts QPPCT API test
    When  User makes a Multipart POST request with <file_path>
    Then  User receives 201 response code
    And   the JSON response at "measurementSets" should not be null

    Examples:
      | file_path                                                                |
      | 2017/Performance Rate Null/CPCPlus_Success_Prod_PerformanceRateNull.xml       |
      | 2017/Performance Rate Null/valid-QRDA-III-latest_MIPS_PerformanceRateNull.xml |
