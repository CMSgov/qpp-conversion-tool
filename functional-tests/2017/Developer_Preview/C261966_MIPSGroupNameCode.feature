 @qppct_devprev @tcID_261966
@smoke @testRail  @O'Neill @qppct_imp_api
Feature: C261966 This is a test to verify that a QRDA III file is correctly validated when having a MIPS Group Name Code

  Scenario Outline: Positive
    Given User starts QPPCT API test
    When  User makes a Multipart POST request with <file_path>
    Then  User receives 201 response code
    And   the JSON response at "measurementSets" should not be null

    Examples:
      | file_path                           |
      | 2017/valid-QRDA-III-latest_mipsgroup.xml |
