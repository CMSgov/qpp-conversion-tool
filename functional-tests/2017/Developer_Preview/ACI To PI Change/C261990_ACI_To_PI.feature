@tcID_261990 @qppct_devprev
@testRail  @ConverterEndpoint @rubin
Feature: C261990 - Verify changes to error messages involving 2017/ACI To PI change.

  Scenario Outline:
    Given User starts QPPCT API test
    When  User makes a Multipart POST request with <file_path>
    Then  User receives 422 response code
    And   the JSON response at "errors/0/details/0/errorCode" should be <error_code>
    And   the JSON response at "errors/0/details/0/message" should be "<error_message>"

    Examples:
      | file_path                                                       | error_code | error_message                                                                                                                                        |
      | 2017/ACI To PI/QRDA-III_PI_Missing_Measure_Performed_Child.xml       | 11         | CT - This PI Reference and Results is missing a required Measure Performed child                                                                     |
      | 2017/ACI To PI/QRDA-III_No_PI_Measure_Performed_ID.xml               | 12         | CT - This PI Measure Performed Reference and Results requires a single Measure ID                                                                    |
      | 2017/ACI To PI/QRDA-III_No_PI_Parent_Section.xml                     | 15         | CT - PI Numerator Denominator element should have a PI Section element as a parent                                                                   |
      | 2017/ACI To PI/QRDA-III_No_PI_NumDen_Measure_Name_ID.xml             | 16         | CT - PI Numerator Denominator element does not contain a measure name ID                                                                             |
      | 2017/ACI To PI/QRDA-III_No_PI_NumDen_Children.xml                    | 17         | CT - PI Numerator Denominator element does not have any child elements                                                                               |
      | 2017/ACI To PI/QRDA-III_PI_Duplicate_Section.xml                     | 26         | CT - Clinical Document contains duplicate PI sections                                                                                                |
      | 2017/ACI To PI/QRDA-III_PI_Incorrect_Aggregate_Count_Numerator.xml   | 39         | CT - This PI Numerator element has an incorrect number of Aggregate Count children. A PI Numerator must have exactly one Aggregate Count element     |
      | 2017/ACI To PI/QRDA-III_PI_Incorrect_Aggregate_Count_Denominator.xml | 39         | CT - This PI Denominator element has an incorrect number of Aggregate Count children. A PI Denominator must have exactly one Aggregate Count element |
      | 2017/ACI To PI/QRDA-III_PI_Aggregate_Val_Not_Integer_Numerator.xml   | 41         | CT - This PI Numerator element Aggregate Value '400.45' is not an integer                                                                            |
      | 2017/ACI To PI/QRDA-III_PI_Aggregate_Val_Not_Integer_Denominator.xml | 41         | CT - This PI Denominator element Aggregate Value '600.67' is not an integer                                                                          |
      | 2017/ACI To PI/QRDA-III_PI_Aggregate_Bad_Val_Numerator.xml           | 42         | CT - This PI Numerator element Aggregate Value has an invalid value of '-5'                                                                          |
      | 2017/ACI To PI/QRDA-III_PI_Aggregate_Bad_Val_Denominator.xml         | 42         | CT - This PI Denominator element Aggregate Value has an invalid value of '-6'                                                                        |
