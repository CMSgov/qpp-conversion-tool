 @qppct_devprev @tcID_261948
@testRail  @ConverterEndpoint @qppct_imp_api @O'Neill
Feature: C261948 This is a test to verify that a valid QRDA III file with Lower/Mixed Case UUID Receives the proper response code and error number.

  Scenario Outline: Negative
    Given User starts QPPCT API test
    When  User makes a Multipart POST request with <file_path>
    Then  User receives 201 response code
    And the JSON response at "measurementSets" should not be null

    Examples:
      | file_path                                    |
      | 2017/valid-QRDA-III-latest_MIPS_MixedCaseUUID.xml |
      | 2017/valid-QRDA-III-latest_MIPS_LowerCaseUUID.xml |
