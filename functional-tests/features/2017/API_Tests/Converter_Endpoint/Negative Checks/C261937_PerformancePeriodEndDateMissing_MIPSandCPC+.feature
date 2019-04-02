  @tcID_261937
@thiel @testRail  @ConverterEndpoint @qppct_imp_api
Feature: C261937 This is a test that verifies error handing for a MIPS/CPC+ file missing the Performance Period End Date

  Scenario Outline: Negative
    Given User starts QPPCT API test
    When User makes a Multipart POST request with <file_path>
    Then User receives 422 response code
    And the JSON response at "errors/0/details/0/message" should be "CT - Must have one and only one performance period end. Please see the Implementation Guide for information on the performance period here: https://ecqi.healthit.gov/system/files/eCQM_QRDA_EC-508_0.pdf#page=17"

    Examples:
      | file_path                                                                            |
      | 2017/Perf End Date Missing/CPCPlus_Success_Prod_PerfEndDateMissing.xml                    |
      | 2017/Perf End Date Missing/valid-QRDA-III-latest_MIPS_PerformancePeriodEndDateMissing.xml |
