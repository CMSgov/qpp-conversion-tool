@tcID_261952 @qppct_devprev
@testRail  @ConverterEndpoint @O'Neill
Feature: C261952 This is a test to verify that a QRDA III file with Multiple Numerators in the PI section returns the proper error code and message

  Scenario Outline: Negative
    Given User starts QPPCT API test
    When  User makes a Multipart POST request with <file_path>
    Then  User receives 422 response code
    And   the JSON response at "errors/0/details/0/errorCode" should be 18
    And   the JSON response at "errors/0/details/0/message" should be "CT - This PI Numerator Denominator element requires exactly one Numerator element child"

    Examples:
      | file_path                                           |
      | 2017/valid-QRDA-III-latest_MIPS_ACIMultipleNumerator.xml |
