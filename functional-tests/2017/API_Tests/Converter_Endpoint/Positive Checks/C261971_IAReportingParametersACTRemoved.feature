  @tcID_261971
@testRail  @ConverterEndpoint @qppct_imp_api @benton
Feature: C261971 This is a test that verifies QRDA-III CPC+ and MIPS files without an IA Reporting Parameters ACT receive the proper error code and message.

  Scenario Outline: Negative
    Given User starts QPPCT API test
    When User makes a Multipart POST request with <file_path>
    Then User receives 422 response code
    And the JSON response at "errors/0/details/0/errorCode" should be 44
    And the JSON response at "errors/0/details/0/message" should be "CT - The IA Section must have one Reporting Parameter Act. Please ensure the Reporting Parameters Act complies with the Implementation Guide (IG). Here is a link to the IG Reporting Parameter Act section: https://ecqi.healthit.gov/system/files/eCQM_QRDA_EC-508_0.pdf#page=80"

    Examples:
      | file_path                                                        |
      | 2017/valid-QRDA-III-latest_MIPS_IAReportingParametersRemoved.xml      |
      | 2017/CPCPlus_Success_Prod_ValidAPMID_IASection_RepParamActRemoved.xml |
