  @tcID_261949
  @testRail  @ConverterEndpoint @qppct_imp_api @benton
  Feature: C261949 This is a test that verifies a CPC+ QRDA-III file with Multiple APM Entity IDs returns the correct error code and message.

  Scenario Outline: Negative
    Given User starts QPPCT API test
    When  User makes a Multipart POST request with <file_path>
    Then  User receives 422 response code
    And   the JSON response at "errors/0/details/0/errorCode" should be 35
    And   the JSON response at "errors/0/details/0/message" should be "CT - One and only one Alternative Payment Model (APM) Entity Identifier should be specified. Here is a link to the IG section on identifiers: https://ecqi.healthit.gov/system/files/eCQM_QRDA_EC-508_0.pdf#page=15"

    Examples:
      | file_path                               |
      | 2017/CPCPlus_Success_Prod_MultipleAPMIDs.xml |
