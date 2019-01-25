  @tcID_261983
@testRail  @ConverterEndpoint @qppct_imp_api @benton
Feature: C261983 This is a test that verifies QRDA-III files with a properly formatted Timestamp in the Performance Period pass validation.

  Scenario Outline: Negative
    Given User starts QPPCT API test
    When User makes a Multipart POST request with <file_path>
    Then User receives 201 response code

    Examples:
      | file_path                                                     |
      | 2017/valid-QRDA-III-latest_MIPS_PerformancePeriodWithTimestamp.xml |
