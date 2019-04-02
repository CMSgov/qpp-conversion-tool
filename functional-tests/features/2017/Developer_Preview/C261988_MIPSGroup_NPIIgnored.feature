 @qppct_devprev @tcID_261988
@testRail  @Benton @qppct_imp_api
Feature: C261988 This is a test to verify that a QRDA III file is correctly validated when having a MIPS Group Name Code

  Scenario Outline: Positive
    Given User starts QPPCT API test
    When  User makes a Multipart POST request with <file_path>
    Then  User receives 201 response code
    And   the JSON response at "measurementSets" should not be null

    Examples:
      | file_path                                      |
      | 2017/valid-QRDA-III-latest_mipsgroup.xml            |
      | 2017/valid-QRDA-III-latest_mipsgroup_npiEmpty.xml   |
      | 2017/valid-QRDA-III-latest_mipsgroup_npiInvalid.xml |
      | 2017/valid-QRDA-III-latest_mipsgroup_npiremoved.xml |
