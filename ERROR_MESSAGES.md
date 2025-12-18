# Error messages
Current list of all error messages being output by the converter.
Any text in the following format `(Example)` are considered variables to be filled in.

### Format - Error Code : Error Message
* 1 : CT - The system could not complete the request, please try again.
* 2 : CT - Contact you Health IT vendor to review your file and confirm it's properly formatted as an XML document.
* 3 : CT - There was an unexpected system error during the file conversion. Contact the customer service center for assistance by email at QPP@cms.hhs.gov or by phone at 1-866-288-8292 (TRS: 711)
* 4 : CT - There was an unexpected error during the file encoding.  Contact the customer service center for assistance by email at QPP@cms.hhs.gov or by phone at 1-866-288-8292 (TRS: 711)
* 5 : CT - Verify that your file is a QRDA III XML document and that it complies with the `(Submission year's)` implementation guide. `(Implementation guide link)`
* 6 : CT - Verify the measure GUID for `(Provided measure id)` against table 15 of the `(Submission year's)` Implementation Guide for valid measure GUIDs: https://ecqi.healthit.gov/sites/default/files/2025-CMS-QRDA-III-EC-IG-v1.1.pdf#page=43
* 7 : CT - Review the measure section of your file to confirm it contains at least 1 measure. 
* 8 : CT - Review `(Parent element)`. It shows `(number of aggregate counts)` but it can only have 1.
* 9 : CT - The aggregate count must be a whole number without decimals.
* 10 : CT - Review this Promoting Interoperability reference for a missing required measure.
* 11 : CT - Review this Promoting Interoperability measure for multiple measure IDs. There can only  be 1 measure ID.
* 12 : CT - The denominator count must be less than or equal to the initial population count for the measure population `(measure population id)`. You can check Table 15 of the `(Submission Year)` Implementation guide for valid measure GUIDs: https://ecqi.healthit.gov/sites/default/files/2025-CMS-QRDA-III-EC-IG-v1.1.pdf#page=43
* 13 : CT - The electronic measure id: `(Current eMeasure ID)` requires `(Number of Subpopulations required)` `(Type of Subpopulation required)`(s) but there are `(Number of Subpopulations existing)`
* 14 : CT - Review the Promoting Interoperability Numerator Denominator element. It must have a parent Promoting Interoperability Section.
* 15 : CT - Review the Promoting Interoperability Numerator Denominator element. It must have a measure name ID
* 16 : CT - Review the Promoting Interoperability Numerator Denominator element. it must have a child element.
* 17 : CT - This Promoting Interoperability Numerator Denominator element requires exactly one `(Denominator|Numerator)` element child
* 18 : CT - Review the Reporting Parameter Act in the Promoting Interoperability section. It must comply with the Implementation Guide: https://ecqi.healthit.gov/sites/default/files/2025-CMS-QRDA-III-EC-IG-v1.1.pdf#page=17
* 19 : CT - Review the element 'Clinical Document'. It must have at least one measure section or a child element of type Promoting Interoperability or Improvement Activities.
* 20 : CT - Review the QRDA III file. It must only have one program name from this list: `(list of valid program names)`
* 21 : CT - Review the Clinical Document for a valid program name from this list: `(list of valid program names)`.  `(program name)` is not valid.
* 22 : CT - Review the QRDA III file for duplicate Promoting Interoperability sections.
* 23 : CT - Review the QRDA III file for duplicate Improvement Activity sections.
* 24 : CT - Review the QRDA III file for duplicate measure sections.
* 25 : CT - The file must only have one performance period start. You can find more information on performance periods in the Implementation Guide: https://ecqi.healthit.gov/sites/default/files/2025-CMS-QRDA-III-EC-IG-v1.1.pdf#page=17
* 26 : CT - The file must only have one performance period end. You can find more information on performance periods in the Implementation Guide: https://ecqi.healthit.gov/sites/default/files/2025-CMS-QRDA-III-EC-IG-v1.1.pdf#page=17
* 27 : CT - The file must have a performance year. You can find more information on performance periods in the Implementation Guide: https://ecqi.healthit.gov/sites/default/files/2025-CMS-QRDA-III-EC-IG-v1.1.pdf#page=17
* 28 : CT - The Quality Measure section must only have one Reporting Parameter Act. You can find more information on performance periods in the Implementation Guide: https://ecqi.healthit.gov/sites/default/files/2025-CMS-QRDA-III-EC-IG-v1.1.pdf#page=17
* 29 : CT - The Performance Rate `(supplied value)` must be a decimal between 0 and 1.
* 30 : CT - Review the aggregate count children for the Promoting Interoperability `(Numerator or Denominator)` element. It must have exactly one aggregate count element
* 31 : CT - Review the Promoting Interoperability `(Numerator or Denominator)` element's aggregate value. '`(value)`' must be a whole number.
* 32 : CT - Review the Promoting Interoperability `(Numerator or Denominator)` element's aggregate value. '`(value)`' is not valid.
* 33 : CT - The Improvement Activities section must have at least one Improvement Activity
* 34 : CT - The Improvement Activities section must have one Reporting Parameter Act. You can find more information on the Implementation Guide: https://ecqi.healthit.gov/sites/default/files/2025-CMS-QRDA-III-EC-IG-v1.1.pdf#page=17
* 35 : CT - The Improvement Activities section must only contain Improvement Activities and a Reporting Parameter Act
* 36 : CT - Review your data. An Improvement Activities performed measure reference and results must have exactly one measure performed child.
* 37 : CT - Review your data. The data for a performed measure is required and must be either a Y or an N.
* 38 : CT - The measure data with population id `(population id)` must have exactly one Aggregate Count. You can find more information on GUIDs on the Implementation Guide: https://ecqi.healthit.gov/sites/default/files/2025-CMS-QRDA-III-EC-IG-v1.1.pdf#page=43
* 39 : CT - The measure data with population id '`(population id)`' must be a whole number greater than or equal to 0. You can find more information on GUIDs on the Implementation Guide: https://ecqi.healthit.gov/sites/default/files/2025-CMS-QRDA-III-EC-IG-v1.1.pdf#page=43
* 40 : CT - The reference results for the measure must have a single measure population
* 41 : CT - The reference results for the measure must have a single measure type
* 42 : CT - The electronic measure id: `(Current eMeasure ID)` requires a `(Subpopulation type)` with the correct UUID of `(Correct uuid required)`. You can find additional information on the implementation guide: https://ecqi.healthit.gov/sites/default/files/2025-CMS-QRDA-III-EC-IG-v1.1.pdf#page=43
* 43 : CT - Review your data. A performance rate must contain a single numerator UUID reference.
* 44 : CT - Review the `(Performance period start or end date)` format. Valid date formats are 2024-02-26, 2024/02/26T01:45:23, or 2024-02-26T01:45:23.123. You cn find more information on the implementation guide: https://ecqi.healthit.gov/sites/default/files/2025-CMS-QRDA-III-EC-IG-v1.1.pdf#page=17
* 45 : CT - Review the measure GUID for measure section, measure reference, and results. There must only be one GUID per measure. Refer to page 36 of the implementation guide: https://ecqi.healthit.gov/sites/default/files/2025-CMS-QRDA-III-EC-IG-v1.1.pdf#page=36
* 46 : CT - Review the file for duplicate GUIDs. Each measure section, measure reference, and results must have its own GUID.
* 47 : CT - Contact your Health IT vendor. The QRDA III file is missing a performance rate. Performance rate is required for PCF reporting. You can find more information on page 17 of the implementation guide: https://ecqi.healthit.gov/sites/default/files/2025-CMS-QRDA-III-EC-IG-v1.1.pdf#page=17
* 47 : CT - Enter an entity ID for the program 'MIPS Virtual Group'.
* 49 : CT - Enter a TIN number to verify the NPI/Alternative Payment Model (APM) combinations.
* 50 : CT - Review count of TINs (`(tinCount)`) and NPIs (`(npiCount)`).  Ensure your TIN and NPI counts match.
* 51 : CT - At least one measure is required in a measure section
* 52 : CT - This QRDA III file shows 100 out of `(Error amount)` errors. Correct and re-submit the file. 
* 53 : CT - The QRDA III file must contain one Category Section v5 with the extension 2020-12-01
* 54 : CT - The APM to TIN/NPI Combination file is missing.
* 55 : CT - The Promoting Interoperability section cannot contain PI_HIE_5 with PI_HIE_1, PI_LVOTC_1, PI_HIE_4, or PI_LVITC_2
