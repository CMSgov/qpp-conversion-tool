@tcID_261994
@testRail  @rubin @qppct_dev_api @qppct_imp_api @qppct_smoke @qppct_devprev_api
Feature: Validate that the ct repository's sample files successfully convert

  Scenario Outline: Positive - QPPCT-975 and QPPCT-976 test
    Given User starts QPPCT API test
    When User makes a Multipart POST request with <file_path>
    Then User receives 201 response code
    And the JSON response at "qpp/measurementSets" should not be null

    Examples:
      | file_path                                                                |
      | 2018/Repo_Files/CPCPlus_No_strat.xml                                     |
      | 2018/Repo_Files/CPCPlus_StructurallyCorrectAddressEx1_SampleQRDA-III.xml |
      | 2018/Repo_Files/CPCPlus_With_Reporting_Stratum.xml                       |
      | 2018/Repo_Files/MIPS_GROUP_QRDA_III_PI_IA_Sample1.xml                    |
      | 2018/Repo_Files/MIPS_Sample.xml                                          |
      | 2018/Repo_Files/quality-mips-1.xml                                       |

  Scenario Outline: Negative - QPPCT-975 and QPPCT-976 test
    Given User starts QPPCT API test
    When User makes a Multipart POST request with <file_path>
    Then User receives 422 response code

    Examples:
      | file_path                                                               |
      | 2018/Repo_Files/CPCPlus_Error_IncNumofGroup1Measures_SampleQRDA-III.xml |
      | 2018/Repo_Files/CPCPlus_Error_Performance_Rate_Number.xml               |
      | 2018/Repo_Files/CPCPlus_Missing_Perf_Rate.xml                           |
      | 2018/Repo_Files/CPCPlus_Missing_Practice-Site-Addr.xml                  |
      | 2018/Repo_Files/CPCPlus_Missing_TINs.xml                                |
      | 2018/Repo_Files/CPCPlus_No_APM_ID.xml                                   |
      | 2018/Repo_Files/CPCPlus_No_Quality_Section.xml                          |
      | 2018/Repo_Files/CPCPlus_Wrong_End-Date.xml                              |
      | 2018/Repo_Files/CPCPlus_Wrong_Start-Date.xml                            |
      | 2018/Repo_Files/error-NaN-numerator.xml                                 |
      | 2018/Repo_Files/error_missing_subpopulations.xml                        |

  Scenario: Positive - QPPCT-871 CPC+ Measure Group Validation
    Given User starts QPPCT API test
    When User makes a Multipart POST request with 2018/Repo_Files/ComprehensivePrimaryCareSampleQRDA-III_SDE.xml
    Then User receives 201 response code
    And the JSON response at "qpp/measurementSets" should not be null