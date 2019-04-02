@rubin @qppct_dev_api

Feature: C261993 - Validate that the online sample files successfully convert in DEVPREV

  Scenario Outline: QPPCT-976 test
    Given User starts QPPCT API test
    When User makes a Multipart POST request with <file_path>
    Then User receives 201 response code
    And the JSON response at "measurementSets" should not be null

    Examples:
      | file_path                                                                               |
      | 2018/Sample_Files_Eligible_Clinicians/ComprehensivePrimaryCareSampleQRDA-IIIRev0918.xml |
      | 2018/Sample_Files_Eligible_Clinicians/MIPS_GROUP_Sample_QRDA_III_Informative.xml        |
#      | 2018/Sample_Files_Eligible_Clinicians/ComprehensivePrimaryCarePlusSampleQRDA-III-2019.xml |
#      | 2018/Sample_Files_Eligible_Clinicians/MIPS_GROUP_Sample_QRDA_III_Informative-2019.xml     |
