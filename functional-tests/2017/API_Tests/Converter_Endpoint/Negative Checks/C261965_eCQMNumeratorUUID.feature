  @tcID_261965
  @smoke @testRail  @O'Neill @qppct_imp_api
  Feature: C261965 This is a test to verify that a QRDA III file with an eCQM Numerator UUID receives proper error code and message

  Scenario Outline: Negative
    Given User starts QPPCT API test
    When  User makes a Multipart POST request with <file_path>
    Then  User receives 422 response code
    And   the JSON response at "errors/0/details/0/errorCode" should be 61
    And   the JSON response at "errors/0/details/0/message" should be "CT - A Performance Rate must contain a single Numerator UUID reference."

    Examples:
      | file_path                                               |
      | 2017/CPCPlus_Success_Prod_ValidAPMID_149NUMERUUIDRemoved.xml |
