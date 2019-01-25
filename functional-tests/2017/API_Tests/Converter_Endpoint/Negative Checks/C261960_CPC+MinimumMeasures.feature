 @tcID_261960
@testRail  @ConverterEndpoint @qppct_imp_api @benton
Feature: C261960 This is a test that verifies QRDA-III CPC+ files with incorrect Minimum Measures receive the proper error code and message.

  Scenario Outline: Negative
    Given User starts QPPCT API test
    When User makes a Multipart POST request with <file_path>
    Then User receives 422 response code
    And the JSON response at "errors/0/details/1/errorCode" should be 65
    And the JSON response at "errors/0/details/1/message" should be "CT - CPC+ Submissions must have at least 9 of the following measures: 40280381-51f0-825b-0152-22b98cff181a,40280381-51f0-825b-0152-229afff616ee,40280381-5118-2f4e-0151-3a9382cd09ba,40280381-51f0-825b-0152-22aae8a21778,40280381-52fc-3a32-0153-1a401cc10b57,40280381-528a-60ff-0152-8e089ed20376,40280381-52fc-3a32-0153-56d2b4f01ae5,40280381-51f0-825b-0152-22ba7621182e,40280381-5118-2f4e-0151-59fb81bf1055,40280381-51f0-825b-0152-22a1e7e81737,40280381-51f0-825b-0152-229c4ea3170c,40280381-51f0-825b-0152-22a24cdd1740,40280381-51f0-825b-0152-229bdcab1702,40280381-503f-a1fc-0150-d33f5b0a1b8c."

    Examples:
      | file_path                                                |
      | 2017/valid-QRDA-III-latest_CPCPLUS_MinimumMeasuresMissing.xml |
