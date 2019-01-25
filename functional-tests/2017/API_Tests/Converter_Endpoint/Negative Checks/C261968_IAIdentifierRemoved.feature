  @tcID_261968
@smoke @testRail  @O'Neill @qppct_imp_api
Feature: C261968 This is a test to verify that a QRDA III file with an IA Identifier Removed receives proper error message

  Scenario Outline: Negative
    Given User starts QPPCT API test
    When  User makes a Multipart POST request with <file_path>
    Then  User receives 422 response code
    And   the JSON response at "errors/0/details/0/message" should be "SV - field 'measureId' in Submission.measurementSets[1].measurements[0] is missing"

    Examples:
      | file_path                                                         |
      | 2017/CPCPlus_Success_Prod_ValidAPMID_IASection_IAIdentifierRemoved.xml |
