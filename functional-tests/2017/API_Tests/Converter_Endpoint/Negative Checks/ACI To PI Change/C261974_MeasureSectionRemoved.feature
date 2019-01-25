@tcID_261974 @ct_dev_regression
@testRail  @benton @ConverterEndpoint
Feature: C261974 This is a test to verify that a QRDA III file with Measure Section removed receives proper error code and message

  Scenario Outline: Negative
    Given User starts QPPCT API test
    When  User makes a Multipart POST request with <file_path>
    Then  User receives 422 response code
    And   the JSON response at "errors/0/details/0/errorCode" should be 23
    And   the JSON response at "errors/0/details/0/message" should be "CT - Clinical Document element must have at least one child element of type PI, IA, or Measure section"

    Examples:
      | file_path                                                 |
      | 2017/valid-QRDA-III-latest_MIPS_MeasureSectionRemoved.xml      |
      | 2017/CPCPlus_Success_Prod_ValidAPMID_MeasureSectionRemoved.xml |
