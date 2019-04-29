@tcID_261994
@testRail  @rubin @qppct_dev_api @qppct_imp_api @qppct_smoke @qppct_devprev_api
Feature: Validate that the ct repository's sample files successfully convert

  Scenario Outline: Positive - QPPCT-975 and QPPCT-976 test
    Given User starts QPPCT API test
    When User makes a Multipart POST request with <file_path>
    Then User receives 201 response code

    Examples:
      | file_path                                                                    |
      | ./sample-files/2018/CPCPlus_No_strat.xml                                     |
      | ./sample-files/2018/CPCPlus_StructurallyCorrectAddressEx1_SampleQRDA-III.xml |
      | ./sample-files/2018/CPCPlus_With_Reporting_Stratum.xml                       |
      | ./sample-files/2018/MIPS_GROUP_QRDA_III_PI_IA_Sample1.xml                    |
      | ./sample-files/2018/MIPS_Sample.xml                                          |
      | ./sample-files/2018/quality-mips-1.xml                                       |

  Scenario Outline: Negative - QPPCT-975 and QPPCT-976 test
    Given User starts QPPCT API test
    When User makes a Multipart POST request with <file_path>
    Then User receives 422 response code

    Examples:
      | file_path                                                                   |
      | ./sample-files/2018/CPCPlus_Error_IncNumofGroup1Measures_SampleQRDA-III.xml |
      | ./sample-files/2018/CPCPlus_Error_Performance_Rate_Number.xml               |
      | ./sample-files/2018/CPCPlus_Missing_Perf_Rate.xml                           |
      | ./sample-files/2018/CPCPlus_Missing_Practice-Site-Addr.xml                  |
      | ./sample-files/2018/CPCPlus_Missing_TINs.xml                                |
      | ./sample-files/2018/CPCPlus_No_APM_ID.xml                                   |
      | ./sample-files/2018/CPCPlus_No_Quality_Section.xml                          |
      | ./sample-files/2018/CPCPlus_Wrong_End-Date.xml                              |
      | ./sample-files/2018/CPCPlus_Wrong_Start-Date.xml                            |
      | ./sample-files/2018/error-NaN-numerator.xml                                 |
      | ./sample-files/2018/error_missing_subpopulations.xml                        |

  Scenario: Positive - QPPCT-871 CPC+ Measure Group Validation
    Given User starts QPPCT API test
    When User makes a Multipart POST request with ./sample-files/2018/ComprehensivePrimaryCareSampleQRDA-III_SDE.xml
    Then User receives 201 response code
