@tcID_271882 @rubin @qppct_dev_api @qppct_imp_api @qppct_smoke @qppct_devprev_api

Feature: C271882 - Test the issue brought up when submitting cpc plus apm files to the converter

  Scenario Outline: CPC+ apm xml file should convert to json with submissionType "apm"
    Given User starts QPPCT API test
    When  User makes a Multipart POST request with 2018/CPC+_apm/<file>
    Then  User receives 201 response code
    And   the JSON response at "qpp/entityType" should be "apm"
    And   the JSON response at "qpp/entityId" should not be null

    Examples:
      | file                                  |
      | cpc_test.xml                          |
      | cpc_test_correct_Practice_Site_ID.xml |
#      When  I keep the JSON response as "Submission"
#      Given User starts QPPA API test
#      And   User authenticates the QPPA API with role=CpcPlus_APM_SO
#      And   User makes POST request to "/api/submissions/submissions" with:
#      """
#      ${Submission}
#      """
#      Then  User receives 201 response code
#      And   I keep the JSON response at "data/submission/id" as "Sub_ID"
#      When  User makes DELETE request to "/api/submissions/submissions/%{Sub_ID}"
#      Then  User receives 204 response code