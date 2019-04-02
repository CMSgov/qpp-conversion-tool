@tcID_261944 @ct_dev_regression
@testRail  @ConverterEndpoint
Feature: C261944 This is a test that verifies QRDA-III MIPS and CPC+ files with an PI measure that is posted without Reporting Parameter ACT returns the correct error code and message.

  Scenario Outline: Negative
    Given User starts QPPCT API test
    When  User makes a Multipart POST request with <file_path>
    Then  User receives 422 response code
    And   the JSON response at "<json_path>/errorCode" should be 22
    And   the JSON response at "<json_path>/message" should be "CT - The PI Section must have one Reporting Parameter Act. Please ensure the Reporting Parameters Act complies with the Implementation Guide (IG). Here is a link to the IG Reporting Parameter Act section: https://ecqi.healthit.gov/system/files/eCQM_QRDA_EC-508_0.pdf#page=80"

    Examples:
      | json_path          | file_path                                                                     |
      | errors/0/details/3 | 2017/No ACI Data/CPCPlus_Success_Prod_ValidAPMID_ACISection_RepParamActRemoved.xml |
      | errors/0/details/0 | 2017/No ACI Data/invalid-QRDA-III-no-aci-measure.xml                               |
