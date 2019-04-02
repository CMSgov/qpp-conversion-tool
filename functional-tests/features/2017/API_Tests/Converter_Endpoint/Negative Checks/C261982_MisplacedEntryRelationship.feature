  @tcID_261982
@testRail  @ConverterEndpoint @qppct_imp_api @benton
Feature: C261982 This is a test that verifies QRDA-III files with entry relationships that are out of order pass validation.

  Scenario Outline: Positive
    Given User starts QPPCT API test
    When User makes a Multipart POST request with <file_path>
    Then User receives 201 response code

    Examples:
      | file_path                                                      |
      | 2017/CPCPlus_Success_Prod_ValidAPMID_MisplacedentryRelationship.xml |
