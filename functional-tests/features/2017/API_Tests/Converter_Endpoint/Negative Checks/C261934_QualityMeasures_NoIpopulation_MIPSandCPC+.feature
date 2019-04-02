  @tcID_261934
@testRail  @ConverterEndpoint @qppct_imp_api
Feature: C261934 This is a test that verifies QRDA-III MIPS and CPC+ files with no Ipopulation receive the proper error code and message.

  Scenario Outline: Negative
    Given User starts QPPCT API test
    When  User makes a Multipart POST request with <file_path>
    Then  User receives 422 response code
    And   the JSON response at "errors/0/details/0/errorCode" should be 3
    And   the JSON response at "errors/0/details/0/message" should be "CT - Unexpected exception occurred during conversion. Please contact the Service Center for assistance via phone at 1-866-288-8292 or TTY: 1-877-715-6222, or by emailing QPP@cms.hhs.gov"

    Examples:
      | file_path                                         |
      | 2017/No Ipop Value/CPCPlus_Success_Prod_NoIpop.xml     |
      | 2017/No Ipop Value/invalid-QRDA-III-latest-no-ipop.xml |
