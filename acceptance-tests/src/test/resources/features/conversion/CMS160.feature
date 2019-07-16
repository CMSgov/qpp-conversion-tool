@tcID_
@testRail  @rubin @qppct_dev_api @qppct_imp_api @qppct_smoke
Feature: Validate that the new CMS160 GUID validates in the converter

  Scenario: Positive - CMS160 new GUID 201
    Given User starts QPPCT API test
    When User makes a Multipart POST request with ./sample-files/2018/CMS160_New_GUID.xml
    Then User receives 201 response code

  Scenario: Negative - CMS160 old GUID 422
    Given User starts QPPCT API test
    When User makes a Multipart POST request with ./sample-files/2018/CMS160_Old_GUID.xml
    Then User receives 422 response code
    And  the JSON response at $.errors[*].details[*].message should contain CT - The measure GUID supplied 40280381-503f-a1fc-0150-afe320c01761 is invalid. Please see the 2018 IG https://ecqi.healthit.gov/system/files/2018_CMS_QRDA_III_Eligible_Clinicians_and_EP_IG_v2_508.pdf#page=94 for valid measure GUIDs.
    