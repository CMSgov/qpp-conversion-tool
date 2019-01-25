  @tcID_261931
@thiel @testRail  @ConverterEndpoint @qppct_imp_api
Feature: C261931 This is a test that verifies QRDA-III MIPS and CPC+ files with negative Denex/Denom/Ipop/Numerator are not converted

  Scenario Outline: Negative
    Given User starts QPPCT API test
    When User makes a Multipart POST request with <file_path>
    Then User receives 422 response code
    And the JSON response at "errors/0/details/0/errorCode" should be 53
    And the JSON response at "errors/0/details/0/message" should be "CT - Measure data with population id '45A2163C-25C2-4245-A3C6-0BEE64E18B64' must be a whole number greater than or equal to 0"

    Examples:
      | file_path                                                                             |
      | 2017/Negative Denex Denom Ipop Numer Values/CPCPlus_Success_Prod_NegativeDenex.xml         |

  Scenario Outline: Negative
    Given User starts QPPCT API test
    When User makes a Multipart POST request with <file_path>
    Then User receives 422 response code
    And the JSON response at "errors/0/details/0/errorCode" should be 53
    And the JSON response at "errors/0/details/0/message" should be "CT - Measure data with population id 'D346DA74-F16E-4159-BEDF-331BA28837FB' must be a whole number greater than or equal to 0"

    Examples:
      | file_path                                                                             |
      | 2017/Negative Denex Denom Ipop Numer Values/CPCPlus_Success_Prod_NegativeDenom.xml         |

  Scenario Outline: Negative
    Given User starts QPPCT API test
    When User makes a Multipart POST request with <file_path>
    Then User receives 422 response code
    And the JSON response at "errors/0/details/0/errorCode" should be 53
    And the JSON response at "errors/0/details/0/message" should be "CT - Measure data with population id '0739FE2E-B8DE-4A56-B064-877CC8E0977D' must be a whole number greater than or equal to 0"

    Examples:
      | file_path                                                                             |
      | 2017/Negative Denex Denom Ipop Numer Values/CPCPlus_Success_Prod_NegativeIpop.xml          |

  Scenario Outline: Negative
    Given User starts QPPCT API test
    When User makes a Multipart POST request with <file_path>
    Then User receives 422 response code
    And the JSON response at "errors/0/details/0/errorCode" should be 53
    And the JSON response at "errors/0/details/0/message" should be "CT - Measure data with population id '9824B759-A263-44DE-9F5E-93DA4E8F4627' must be a whole number greater than or equal to 0"

    Examples:
      | file_path                                                                             |
      | 2017/Negative Denex Denom Ipop Numer Values/CPCPlus_Success_Prod_NegativeNumerator.xml     |

  Scenario Outline: Negative
    Given User starts QPPCT API test
    When User makes a Multipart POST request with <file_path>
    Then User receives 422 response code
    And the JSON response at "errors/0/details/0/errorCode" should be 53
    And the JSON response at "errors/0/details/0/message" should be "CT - Measure data with population id '3C100EC4-2990-4D79-AE14-E816F5E78AC8' must be a whole number greater than or equal to 0"

    Examples:
      | file_path                                                                             |
      | 2017/Negative Denex Denom Ipop Numer Values/invalid-QRDA-III-latest-denex-negative.xml     |

  Scenario Outline: Negative
    Given User starts QPPCT API test
    When User makes a Multipart POST request with <file_path>
    Then User receives 422 response code
    And the JSON response at "errors/0/details/0/errorCode" should be 53
    And the JSON response at "errors/0/details/0/message" should be "CT - Measure data with population id 'E62FEBA3-0F98-460D-93CD-44314D7203A8' must be a whole number greater than or equal to 0"

    Examples:
      | file_path                                                                             |
      | 2017/Negative Denex Denom Ipop Numer Values/invalid-QRDA-III-latest-denom-negative.xml     |

  Scenario Outline: Negative
    Given User starts QPPCT API test
    When User makes a Multipart POST request with <file_path>
    Then User receives 422 response code
    And the JSON response at "errors/0/details/0/errorCode" should be 53
    And the JSON response at "errors/0/details/0/message" should be "CT - Measure data with population id '3C100EC4-2990-4D79-AE14-E816F5E78AC8' must be a whole number greater than or equal to 0"

    Examples:
      | file_path                                                                             |
      | 2017/Negative Denex Denom Ipop Numer Values/invalid-QRDA-III-latest-ipop-negative.xml      |

  Scenario Outline: Negative
    Given User starts QPPCT API test
    When User makes a Multipart POST request with <file_path>
    Then User receives 422 response code
    And the JSON response at "errors/0/details/0/errorCode" should be 53
    And the JSON response at "errors/0/details/0/message" should be "CT - Measure data with population id 'F9FEBF42-4B21-47A9-B03E-D2DA5CF8492B' must be a whole number greater than or equal to 0"

    Examples:
      | file_path                                                                             |
      | 2017/Negative Denex Denom Ipop Numer Values/invalid-QRDA-III-latest-numerator-negative.xml |
