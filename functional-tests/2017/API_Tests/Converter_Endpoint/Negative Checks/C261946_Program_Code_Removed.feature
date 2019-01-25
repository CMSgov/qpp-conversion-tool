  @tcID_261946
  @testRail  @ConverterEndpoint @qppct_imp_api @taylor
  Feature: C261946 This is a test that verifies QRDA-III MIPS file with Program tag removed to converter/endpoint returns the correct error code and message.

  Scenario Outline: Negative
    Given User starts QPPCT API test
    When  User makes a Multipart POST request with <file_path>
    Then  User receives 422 response code
    And   the JSON response at "errors/0/details/0/errorCode" should be 24
    And   the JSON response at "errors/0/details/0/message" should be "CT - Clinical Document must have one and only one program name. Valid program names are MIPS_GROUP, CPCPLUS, or MIPS_INDIV"

    Examples:
      | file_path                                                              |
      | 2017/Program Code Removed/CPCPlus_Success_Prod_ProgramCodeRemoved.xml       |
      | 2017/Program Code Removed/valid-QRDA-III-latest_MIPS_ProgramNameRemoved.xml |
