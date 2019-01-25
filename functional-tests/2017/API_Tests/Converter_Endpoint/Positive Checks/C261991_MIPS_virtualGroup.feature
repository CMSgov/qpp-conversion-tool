@tcID_261991
@rubin @testRail @ConverterEndpoint @qppct_dev_regression
Feature:

  Scenario: Positive Submit MIPS virtualGroup
    Given User starts QPPCT API test
    When  User makes a Multipart POST request with MIPS_VirtualGroup/valid-QRDA-III-latest-MIPS_virtualGroup.xml
    Then  User receives 201 response code
    And   the JSON response at "measurementSets" should not be null

  Scenario Outline: Negative 422 MIPS VirtualGroup Submissions
    Given User starts QPPCT API test
    When  User makes a Multipart POST request with <file_path>
    Then  User receives 422 response code
    And   the JSON response at "errors/0/details/0/message" should be "<message>"

    Examples:
      | file_path                                                     | message                                                                                                                        |
      | 2017/MIPS_VirtualGroup/invalid-QRDA-III-2017-MIPS_virtualGroup.xml | SV - field 'measureId' in Measurement is invalid: PI_PEA_1 does not exist, see qpp-measures-data for list of valid measureIds  |
      | 2017/MIPS_VirtualGroup/invalid-QRDA-III-ACI-MIPS_virtualGroup.xml  | SV - field 'measureId' in Measurement is invalid: ACI_PEA_1 does not exist, see qpp-measures-data for list of valid measureIds |
