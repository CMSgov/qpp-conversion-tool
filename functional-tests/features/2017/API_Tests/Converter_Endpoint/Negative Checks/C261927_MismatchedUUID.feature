@tcID_261927
@thiel  @testRail @ConverterEndpoint @qppct_imp_api
Feature: C261927 This is a test that verifies QRDA-III MIPS and CPC+ files with mismatched UUIDs are not converted.

  Scenario Outline: Negative
    Given User starts QPPCT API test
    When  User makes a Multipart POST request with <file_path>
    Then  User receives 422 response code
    And   the JSON response at "errors/0/details/0/errorCode" should be 59
    And   the JSON response at "errors/0/details/0/message" should not be null

    Examples:
      | file_path                                                         |
      | 2017/MismatchedUUID/CPCPlus_Success_Prod_ValidAPMID_MismatchedUUID.xml |
      | 2017/MismatchedUUID/valid-QRDA-III-latest_MIPS_MisMatchedUUID.xml      |
