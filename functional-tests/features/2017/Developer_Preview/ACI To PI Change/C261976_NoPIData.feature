@tcID_261976 @qppct_devprev
@testRail  @ConverterEndpoint @benton
Feature: C261976 This is a test that verifies QRDA-III CPC+ and MIPS files without any PI data receive the proper error code and message.

  Scenario Outline: Negative
    Given User starts QPPCT API test
    When User makes a Multipart POST request with <file_path>
    Then User receives 422 response code
    And the JSON response at "<json_path>/errorCode" should be 22
    And the JSON response at "<json_path>/message" should be "CT - The PI Section must have one Reporting Parameter Act. Please ensure the Reporting Parameters Act complies with the Implementation Guide (IG). Here is a link to the IG Reporting Parameter Act section: https://ecqi.healthit.gov/system/files/2018_CMS_QRDA_III_Eligible_Clinicians_and_EP_IG_v2_508.pdf#page=82"

    Examples:
      | json_path          | file_path                                                                     |
      | errors/0/details/0 | 2017/No ACI Data/invalid-QRDA-III-no-aci-measure.xml                               |
      | errors/0/details/1 | 2017/CPCPlus_Success_Prod_ValidAPMID_ACISection_RepParamActRemoved.xml |
