# Error messages
Current list of all error messages being output by the converter.
Any text in the following format `(Example)` are considered variables to be filled in.

### Format - Error Code : Error Message
* 1 : CT - The system could not complete the request, please try again.
* 2 : CT - Contact you Health IT vendor to review your file and confirm it's properly formatted as an XML document.
* 3 : CT - There was an unexpected system error during the file conversion. Contact the customer service center for assistance by email at QPP@cms.hhs.gov or by phone at 288-8292 (TTY: 1-877-715-6222)
* 4 : CT - There was an unexpected error during the file encoding.  Contact the customer service center for assistance by email at QPP@cms.hhs.gov or by phone at 288-8292 (TTY: 1-877-715-6222)
* 5 : CT - Verify that your file is a QRDA III XML document and that it complies with the `(Submission year's)` implementation guide. `(Implementation guide link)`
* 6 : CT - Verify the measure GUID for `(Provided measure id)` against table 15 of the `(Submission year's)` Implementation Guide for valid measure GUIDs: https://ecqi.healthit.gov/sites/default/files/2024-CMS-QRDA-III-EC-IG-v1.1-508.pdf#page=43
* 7 : CT - Review the measure section of your file to confirm it contains at least 1 measure. 
* 8 : CT - Review `(Parent element)`. It shows `(number of aggregate counts)` but it can only have 1.
* 9 : CT - The aggregate count must be a whole number without decimals.
* 11 : CT - Review this Promoting Interoperability reference for a missing required measure.
* 12 : CT - Review this Promoting Interoperability measure for multiple measure IDs. There can only  be 1 measure ID.
* 13 : CT - The denominator count must be less than or equal to the initial population count for the measure population `(measure population id)`. You can check Table 15 of the `(Submission Year)` Implementation guide for valid measure GUIDs: https://ecqi.healthit.gov/sites/default/files/2024-CMS-QRDA-III-EC-IG-v1.1-508.pdf#page=43
* 14 : CT - The electronic measure id: `(Current eMeasure ID)` requires `(Number of Subpopulations required)` `(Type of Subpopulation required)`(s) but there are `(Number of Subpopulations existing)`
* 15 : CT - Review the Promoting Interoperability Numerator Denominator element. It must have a parent Promoting Interoperability Section.
* 16 : CT - Review the Promoting Interoperability Numerator Denominator element. It must have a measure name ID
* 17 : CT - Review the Pomoting Interoperability Numerator Denominator element. it must have a child element.
* 18 : CT - This Promoting Interoperability Numerator Denominator element requires exactly one `(Denominator|Numerator)` element child
* 22 : CT - Review the Reporting Parameter Act in the Promoting Interoperability section. It must comply with the Implementation Guide: https://ecqi.healthit.gov/sites/default/files/2024-CMS-QRDA-III-EC-IG-v1.1-508.pdf#page=17
* 23 : CT - Review the element "Clinical Document." It must have at least one measure section or a child element of type Promoting Interoperability or Improvement Activities.
* 24 : CT - Review the QRDA III file. It must only have one program name from this list: `(list of valid program names)`
* 25 : CT - Review the Clinical Document for a valid program name from this list: `(list of valid program names)`.  `(program name)` is not valid.
* 26 : CT - Review the QRDA III file for duplicate Promoting Interoperability sections.
* 27 : CT - Review the QRDA III file for duplicate Improvement Activity sections.
* 28 : CT - Review the QRDA III file for duplicate measure sections.
* 29 : CT - The file must only have one performance period start. You can find more information on performance periods in the Implementation Guide: https://ecqi.healthit.gov/sites/default/files/2024-CMS-QRDA-III-EC-IG-v1.1-508.pdf#page=17
* 30 : CT - The file must only have one performance period end. You can find more information on performance periods in the Implementation Guide: https://ecqi.healthit.gov/sites/default/files/2024-CMS-QRDA-III-EC-IG-v1.1-508.pdf#page=17
* 31 : CT - The file must have a performance year. You can find more information on performance periods in the Implementation Guide: https://ecqi.healthit.gov/sites/default/files/2024-CMS-QRDA-III-EC-IG-v1.1-508.pdf#page=17
* 32 : CT - The Quality Measure section must only have one Reporting Parameter Act. You can find more information on performance periods in the Implementation Guide: https://ecqi.healthit.gov/sites/default/files/2024-CMS-QRDA-III-EC-IG-v1.1-508.pdf#page=17
* 33 : CT - The Performance Rate `(supplied value)` must be a decimal between 0 and 1.
* 34 : CT - PCF submissions must have a practice site address. You canfind more information on the `(Submission year's)` Implementation Guide: https://ecqi.healthit.gov/sites/default/files/2024-CMS-QRDA-III-EC-IG-v1.1-508.pdf#page=22
* 35 : CT - Review the file. It must only have one Alternative Payment Model (APM) Entity Identifier. You can find more information in the Implementation Guide: https://ecqi.healthit.gov/sites/default/files/2024-CMS-QRDA-III-EC-IG-v1.1-508.pdf#page=15
* 36 : CT - Review the file. It must have at least one measure section.
* 37 : CT - Review the performance rate(s) in the file. The number for measure `(Given measure id)` is `(Expected value)`
* 39 : CT - Review the aggregate count children for the Promoting Interoperability `(Numerator or Denominator)` element. It must have exactly one aggregate count element
* 41 : CT - Review the Promoting Interoperability `(Numerator or Denominator)` element's aggregate value. '`(value)`' must be a whole number.
* 42 : CT - Review the Promoting Interoperability `(Numerator or Denominator)` element's aggregate value. '`(value)`' is not valid.
* 43 : CT - The Improvement Activities section must have at least one Improvement Activity
* 44 : CT - The Improvement Activities section must have one Reporting Parameter Act. You can find more information on the Implementation Guide: https://ecqi.healthit.gov/sites/default/files/2024-CMS-QRDA-III-EC-IG-v1.1-508.pdf#page=17
* 45 : CT - The Improvement Activities section must only contain Improvement Activities and a Reporting Parameter Act
* 48 : CT - There's missing strata `(Reporting Stratum UUID)` for `(Current subpopulation type)` measure `(Current subpopulation UUID)`. You can find more information on the Implementation Guide: https://ecqi.healthit.gov/sites/default/files/2024-CMS-QRDA-III-EC-IG-v1.1-508.pdf#page=43
* 49 : CT - The amount of stratifications `(Current number of Reporting Stratifiers)` does not meet the `(Number of stratifiers required)` required for `(Current subpopulation type)` measure `(Current Subpopulation UUID)`. The strata required is: `(Expected strata uuid list)`. You can find more information on the Implementation Guide: https://ecqi.healthit.gov/sites/default/files/2024-CMS-QRDA-III-EC-IG-v1.1-508.pdf#page=43
* 50 : CT - Review your data. An Improvement Activities performed measure reference and results must have exactly one measure performed child.
* 51 : CT - Review your data. The data for a performed measure is required and must be either a Y or an N.
* 52 : CT - The measure data with population id `(population id)` must have exactly one Aggregate Count. You can find more information on GUIDs on the Implementation Guide: https://ecqi.healthit.gov/sites/default/files/2024-CMS-QRDA-III-EC-IG-v1.1-508.pdf#page=43
* 53 : CT - The measure data with population id '`(population id)`' must be a whole number greater than or equal to 0. You can find more information on GUIDs on the Implementation Guide: https://ecqi.healthit.gov/sites/default/files/2024-CMS-QRDA-III-EC-IG-v1.1-508.pdf#page=43
* 55 : CT - A `(Program name)` performance period must start on 01/01/2024. You can find additional information on the Implementation Guide: https://ecqi.healthit.gov/sites/default/files/2024-CMS-QRDA-III-EC-IG-v1.1-508.pdf#page=12
* 56 : CT - A `(Program name)` performance period must end on 12/31/2024. You can find additional information on the Implementation Guide: https://ecqi.healthit.gov/sites/default/files/2024-CMS-QRDA-III-EC-IG-v1.1-508.pdf#page=12
* 57 : CT - The reference results for the measure must have a single measure population
* 58 : CT - The reference results for the measure must have a single measure type
* 59 : CT - The electronic measure id: `(Current eMeasure ID)` requires a `(Subpopulation type)` with the correct UUID of `(Correct uuid required)`. You can find additional information on the implementation guide: https://ecqi.healthit.gov/sites/default/files/2024-CMS-QRDA-III-EC-IG-v1.1-508.pdf#page=43
* 61 : CT - Review your data. A performance rate must contain a single numerator UUID reference.
* 62 : CT - Review your data. The Alternative Payment Model (APM) Entity Identifier must not be empty. You can find additional information on the implementation guide: https://ecqi.healthit.gov/sites/default/files/2024-CMS-QRDA-III-EC-IG-v1.1-508.pdf#page=15
* 63 : CT - Review the Alternative Payment Model (APM) Entity Identifier.  You can find additional information on the implementation guide: https://ecqi.healthit.gov/sites/default/files/2024-CMS-QRDA-III-EC-IG-v1.1-508.pdf#page=15
* 66 : CT - There's missing data. Enter the `(Supplemental Type)` - `(Type Qualification)` supplemental data for code `(Supplemental Data Code)` for the Sub-population `(Sub Population)` of measure id `(Measure Id)`.
* 67 : CT - Review measure id `(Measure Id)`. It must have one count for Supplemental Data `(Supplemental Data Code)` on Sub-population `(Sub Population)`.
* 68 : CT - Your submission for `(Program name)` was made after the submission deadline of `(Submission end date)` for `(Program name)` measure section. Your `(Program name)` QRDA III file has not been processed. Please contact `(Program name)` Support at `(PCF+ contact email)` for assistance.
* 69 : CT - Review the `(Performance period start or end date)` format. Valid date formats are 2024-02-26, 2024/02/26T01:45:23, or 2024-02-26T01:45:23.123. You cn find more information on the implementation guide: https://ecqi.healthit.gov/sites/default/files/2024-CMS-QRDA-III-EC-IG-v1.1-508.pdf#page=17
* 70 : CT - Review the measure GUID for measure section, measure reference, and results. There must only be one GUID per measure. Refer to page 36 of the implementation guide: https://ecqi.healthit.gov/sites/default/files/2024-CMS-QRDA-III-EC-IG-v1.1-508.pdf#page=36
* 71 : CT - Review the file for duplicate GUIDs. Each measure section, measure reference, and results must have its own GUID.
* 72 : CT - Contact your Health IT vendor. The QRDA III file is missing a performance rate. Performance rate is required for PCF reporting. You can find more information on page 17 of the implementation guide: https://ecqi.healthit.gov/sites/default/files/2024-CMS-QRDA-III-EC-IG-v1.1-508.pdf#page=17
* 78 : CT - Enter an entity ID for the program 'MIPS Virtual Group'.
* 79 : CT - Enter a TIN number to verify the NPI/Alternative Payment Model (APM) combinations.
* 80 : CT - Review NPI `(npi)` and TIN `(tin)`. This NPI/TIN combination is missing from the QRDA III file or is not in the `(program)` practitioner roster for `(apm)`. Ensure your submission contains all required NPI/TIN combinations and your `(program)` practitioner roster is up-to-date.
* 81 : CT - At least one measure is required in a measure section
* 82 : CT - This QRDA III file shows 100 out of `(Error amount)` errors. Correct and re-submit the file. 
* 84 : CT - `(Program name)` QRDA-III Submissions require at least one TIN number.
* 85 : CT - `(Program name)` QRDA-III Submission TINs must be 9 numbers long.
* 86 : CT - The QRDA-III submission for `(Program name)` is missing a TIN. Ensure there is a TIN associated with every NPI submitted.
* 87 : CT - The QRDA-III submission for `(Program name)` must have at least one NPI number.
* 88 : CT - The NPIs for `(Program name)`'s QRDA-III submission must be 10 numbers long.
* 89 : CT - The QRDA-III submission for `(Program name)` is missing an NPI. Ensure there is an NPI associated with every TIN submitted.
* 90 : CT - The QRDA-III submission for `(Program name)` should not contain an Improvement Activities section. The Improvement Activities data will be ignored.
* 91 : CT - Review the performance rate `(performanceRateUuid)` for measure `(measure id)`. A performance rate cannot be null unless the performance denominator is 0
* 92 : CT - Review the performance denominator for measure `(measureId)`. A performance rate cannot be null unless the performance denominator is 0.
* 93 : CT - Review numerator ID `(numeratorUuid)` for measure `(measure id)`. It has a count value that is greater than the denominator and/or the performance denominator (Denominator count - Denominator exclusion count - Denominator Exception count)
* 94 : CT - Review the denominator exclusion id `(denexUuid)` for measure `(measure id)`. It cannot have a greater value than the denominator.
* 95 : CT - The QRDA III file must contain one Category Section v5 with the extension 2020-12-01
* 96 : CT - The APM to TIN/NPI Combination file is missing.
* 97 : CT - The QRDA-III submissions for `(Program name)` must have a valid CMS EHR Certification ID (Valid Formats: XX15CXXXXXXXXXX)
* 98 : CT - Review the performance rate. It must be of value Null Attribute (NA), not 0.
* 100 : CT - Found more than one CMS EHR Certification ID in your file. The submission must have only one CMS EHR Certification ID.
* 101 : CT - The measure population `(measure population id)` for `(Program name)` needs the Denominator count to be equal to Initial population count. You can find additional information on table 15 of the implementation guide:https://ecqi.healthit.gov/sites/default/files/2024-CMS-QRDA-III-EC-IG-v1.1-508.pdf#page=43
* 102 : CT - The Promoting Interoperability section cannot contain PI_HIE_5 with PI_HIE_1, PI_LVOTC_1, PI_HIE_4, or PI_LVITC_2
* 103 : CT - The PCF submissions must have the `(PCF Measure minimum)` following measures: `(Listing of valid measure ids)`
* 105 : CT - If multiple TINs/NPIs are submitted, each must be reported within a separate performer.
* 106 : CT - Promoting Interoperability data should not be reported in a PCF QRDA III file.
* 107 : CT - There's missing NPI/TIN combination. The NPI/TIN `(npi)`-`(tin)` was active on the PCF practitioner roster for `(apm)` during the performance year but was not found in the file. Ensure your submission contains all NPI/TIN combinations that were active on your roster at any point during the performance year. Your QRDA III file and/or roster may require updates. The QPP website doesn't have access to roster updates made after December 1, 2024. It's critical to ensure your roster is up to date and your QRDA III file contains all NPI/TIN values that were active on your roster during the performance year. Contact your health IT vendor if your QRDA III file requires updates. You can find instructions on updating rosters in the PCF Practice Management Guide: (https://cmmi.my.salesforce.com/sfc/p/#i0000000iryR/a/t00000028RsP/dMF_romOmf5VLe7p5lUj8vch11mPmELP6ZuyI16vS.Y).
* 108 : CT - Found an unexpected NPI/TIN combination. The NPI/TIN `(npi)`-`(tin)` was reported in the file but does not exist at the practice or was not active on the PCF practitioner roster for `(apm)` during the performance year. Ensure your submission only contains NPI/TIN combinations that were active on your roster at any point during the performance year. Your QRDA III file and/or roster may require updates. Note: The QPP website does not have access to roster updates made after December 1, 2024. It's critical that you ensure your roster is up to date and your QRDA III file contains all NPI/TIN values that were active on your roster during the performance year. Please contact your health IT vendor if your QRDA III file requires updates. You can find instructions on how updating rosters in the PCF Practice Management Guide (https://cmmi.my.salesforce.com/sfc/p/#i0000000iryR/a/t00000028RsP/dMF_romOmf5VLe7p5lUj8vch11mPmELP6ZuyI16vS.Y).
