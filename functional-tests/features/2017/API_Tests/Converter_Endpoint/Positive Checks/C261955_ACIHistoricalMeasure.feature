  @tcID_261955
@testRail  @ConverterEndpoint @qppct_imp_api @O'Neill
Feature: C261955 This is a test to verify that a QRDA III file with a Historical ACI Measure passes validation. (Converter does not check for ACI Measures)

  Scenario Outline: Negative
    Given User starts QPPCT API test
    When  User makes a Multipart POST request with <file_path>
    Then  User receives 201 response code

    Examples:
      | file_path                                                    |
      | 2017/CPCPlus_Success_Prod_ValidAPMID_ACISection_ACI_EP_1_2014.xml |
