  @tcID_261959
@smoke @testRail  @qppct_imp_api @O'Neill
Feature: C261959 This is a test to verify that a QRDA III file with CPC+ Complex Measures missing receives proper error code and message

  Scenario Outline: Negative
    Given User starts QPPCT API test
    When  User makes a Multipart POST request with <file_path>
    Then  User receives 422 response code
    And   the JSON response at "errors/0/details/0/errorCode" should be 64
    And   the JSON response at "errors/0/details/0/message" should be "CT - CPC+ Submissions must have at least 2 of the following complex process measures: 40280381-51f0-825b-0152-22aae8a21778,40280381-52fc-3a32-0153-1a401cc10b57,40280381-528a-60ff-0152-8e089ed20376,40280381-52fc-3a32-0153-56d2b4f01ae5"

    Examples:
      | file_path                                                  |
      | 2017/CPCPlus_Success_Prod_ValidAPMID_ComplexMeasuresRemoved.xml |
