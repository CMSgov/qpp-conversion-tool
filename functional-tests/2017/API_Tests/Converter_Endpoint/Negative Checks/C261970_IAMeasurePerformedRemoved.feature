  @tcID_261970
@smoke @testRail  @O'Neill @qppct_imp_api
Feature: C261970 This is a test to verify that a QRDA III file with an IA Measure Performed section received the proper error code and message

  Scenario Outline: Negative
    Given User starts QPPCT API test
    When  User makes a Multipart POST request with <file_path>
    Then  User receives 422 response code
    And   the JSON response at "errors/0/details/0/errorCode" should be 50
    And   the JSON response at "errors/0/details/0/message" should be "CT - An IA performed measure reference and results must have exactly one measure performed child"

    Examples:
      | file_path                                                          |
      | 2017/CPCPlus_Success_Prod_ValidAPMID_IASection_IAMeasurePerfRemoved.xml |
