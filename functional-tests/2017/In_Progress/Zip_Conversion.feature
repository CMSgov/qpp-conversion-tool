@tcID_261988
@testRail @Benton
Feature:

  Scenario Outline: Positive
    Given User starts QPPCT API test
    When  User makes a Multipart POST request with <file_path>

    Examples:
      | file_path                   |
      | 2017/Zip_Conversion/qrda-III.zip |
