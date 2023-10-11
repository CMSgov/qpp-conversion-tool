# Error messages
Current list of all error messages being output by the converter.
Any text in the following format `(Example)` are considered variables to be filled in.

### Format - Error Code : Error Message
* 1 : CT - Failed to find an encoder
* 2 : CT - The file is not a valid XML document. The file you are submitting is not a properly formatted XML document. Please check your document to ensure proper formatting.
* 3 : CT - Unexpected exception occurred during conversion. Please contact the Service Center for assistance via phone at 1-866-288-8292 or TTY: 1-877-715-6222, or by emailing QPP@cms.hhs.gov
* 4 : CT - Unexpected exception occurred during encoding. Please contact the Service Center for assistance via phone at 1-866-288-8292 or TTY: 1-877-715-6222, or by emailing QPP@cms.hhs.gov
* 5 : CT - The file is not a QRDA-III XML document. Please ensure that the submission complies with the `(Submission year's)` implementation guide. `(Implementation guide link)`
* 6 : CT - The measure GUID `(Provided measure id)` is invalid. Please see the Table 14 of the `(Submission year's)` Implementation Guide for valid measure GUIDs: https://ecqi.healthit.gov/sites/default/files/2023-CMS-QRDA-III-Eligible-Clinicians-IG-v1.1-508.pdf#page=43
* 7 : CT - The measure reference results must have at least one measure. Please review the measures section of your file as it cannot be empty.
* 8 : CT - The `(Parent element)` has `(number of aggregate counts)` aggregate count values. A single aggregate count value is required.
* 9 : CT - Aggregate count value must be an integer
* 11 : CT - This PI Reference and Results is missing a required Measure Performed child
* 12 : CT - This PI Measure Performed Reference and Results requires a single Measure ID
* 13 : CT - Denominator count must be less than or equal to Initial Population count for the measure population `(measure population id)`. Please see the Table 14 of the `(Submission Year)` Implementation guide for valid measure GUIDs: https://ecqi.healthit.gov/sites/default/files/2023-CMS-QRDA-III-Eligible-Clinicians-IG-v1.1-508.pdf#page=43
* 14 : CT - The electronic measure id: `(Current eMeasure ID)` requires `(Number of Subpopulations required)` `(Type of Subpopulation required)`(s) but there are `(Number of Subpopulations existing)`
* 15 : CT - PI Numerator Denominator element should have a PI Section element as a parent
* 16 : CT - PI Numerator Denominator element does not contain a measure name ID
* 17 : CT - PI Numerator Denominator element does not have any child elements
* 18 : CT - This PI Numerator Denominator element requires exactly one `(Denominator|Numerator)` element child
* 22 : CT - The PI Section must have one Reporting Parameter Act. Please ensure the Reporting Parameters Act complies with the Implementation Guide (IG). Here is a link to the IG Reporting Parameter Act section: https://ecqi.healthit.gov/sites/default/files/2023-CMS-QRDA-III-Eligible-Clinicians-IG-v1.1-508.pdf#page=17
* 23 : CT - Clinical Document element must have at least one child element of type PI, IA, or Measure section
* 24 : CT - Clinical Document must have one and only one program name. Valid program names are `(list of valid program names)`
* 25 : CT - The Clinical Document program name `(program name)` is not recognized. Valid program names are `(list of valid program names)`.
* 26 : CT - Clinical Document contains duplicate PI sections
* 27 : CT - Clinical Document contains duplicate IA sections
* 28 : CT - Clinical Document contains duplicate Measure sections
* 29 : CT - Must have one and only one performance period start. Please see the Implementation Guide for information on the performance period here: https://ecqi.healthit.gov/sites/default/files/2023-CMS-QRDA-III-Eligible-Clinicians-IG-v1.1-508.pdf#page=17
* 30 : CT - Must have one and only one performance period end. Please see the Implementation Guide for information on the performance period here: https://ecqi.healthit.gov/sites/default/files/2023-CMS-QRDA-III-Eligible-Clinicians-IG-v1.1-508.pdf#page=17
* 31 : CT - Must have a performance year. Please see the Implementation Guide for information on the performance period here: https://ecqi.healthit.gov/sites/default/files/2023-CMS-QRDA-III-Eligible-Clinicians-IG-v1.1-508.pdf#page=17
* 32 : CT - The Quality Measure Section must have exactly one Reporting Parameter Act. Please ensure the Reporting Parameters Act complies with the Implementation Guide (IG). Here is a link to the IG Reporting Parameter Act section: https://ecqi.healthit.gov/sites/default/files/2023-CMS-QRDA-III-Eligible-Clinicians-IG-v1.1-508.pdf#page=17
* 33 : CT - The Performance Rate `(supplied value)` is invalid. It must be a decimal between 0 and 1.
* 34 : CT - PCF submissions must contain a practice site address. Please refer to the `(Submission year's)` IG for more details https://ecqi.healthit.gov/sites/default/files/2023-CMS-QRDA-III-Eligible-Clinicians-IG-v1.1-508.pdf#page=25 regarding practice site addresses.
* 35 : CT - One and only one Alternative Payment Model (APM) Entity Identifier should be specified. Here is a link to the IG section on identifiers: https://ecqi.healthit.gov/sites/default/files/2023-CMS-QRDA-III-Eligible-Clinicians-IG-v1.1-508.pdf#page=15
* 36 : CT - PCF submissions must contain one Measure section
* 37 : CT - PCF submissions must contain correct number of performance rate(s). Correct Number is `(Expected value)` for measure `(Given measure id)`
* 39 : CT - This PI `(Numerator or Denominator)` element has an incorrect number of Aggregate Count children. A PI `(Numerator or Denominator)` must have exactly one Aggregate Count element
* 41 : CT - This PI `(Numerator or Denominator)` element Aggregate Value '`(value)`' is not an integer
* 42 : CT - This PI `(Numerator or Denominator)` element Aggregate Value has an invalid value of '`(value)`'
* 43 : CT - The IA Section must have at least one Improvement Activity
* 44 : CT - The IA Section must have one Reporting Parameter Act. Please ensure the Reporting Parameters Act complies with the Implementation Guide (IG). Here is a link to the IG Reporting Parameter Act section: https://ecqi.healthit.gov/sites/default/files/2023-CMS-QRDA-III-Eligible-Clinicians-IG-v1.1-508.pdf#page=17
* 45 : CT - The IA Section must contain only Improvement Activities and a Reporting Parameter Act
* 48 : CT - Missing strata `(Reporting Stratum UUID)` for `(Current subpopulation type)` measure `(Current subpopulation UUID)`. Here is a link to the IG valid Measure Ids section: https://ecqi.healthit.gov/sites/default/files/2023-CMS-QRDA-III-Eligible-Clinicians-IG-v1.1-508.pdf#page=43
* 49 : CT - Amount of stratifications `(Current number of Reporting Stratifiers)` does not meet expectations `(Number of stratifiers required)` for `(Current subpopulation type)` measure `(Current Subpopulation UUID)`. Expected strata: `(Expected strata uuid list)`. Please refer to the Implementation Guide for correct stratification UUID's here: https://ecqi.healthit.gov/sites/default/files/2023-CMS-QRDA-III-Eligible-Clinicians-IG-v1.1-508.pdf#page=43
* 50 : CT - An IA performed measure reference and results must have exactly one measure performed child
* 51 : CT - A single measure performed value is required and must be either a Y or an N.
* 52 : CT -  The measure data with population id '`(population id)`' must have exactly one Aggregate Count. Please see the Table 14 of `(Submission year's)` Implementation Guide for valid measure GUIDs: https://ecqi.healthit.gov/sites/default/files/2023-CMS-QRDA-III-Eligible-Clinicians-IG-v1.1-508.pdf#page=43
* 53 : CT - Measure data with population id '`(population id)`' must be a whole number greater than or equal to 0. Please see the Table 14 of `(Submission year's)` Implementation Guide for valid measure GUIDs: https://ecqi.healthit.gov/sites/default/files/2023-CMS-QRDA-III-Eligible-Clinicians-IG-v1.1-508.pdf#page=43
* 55 : CT - A `(Program name)` Performance period start must be 01/01/2023. Please refer to the IG for more information here: https://ecqi.healthit.gov/sites/default/files/2023-CMS-QRDA-III-Eligible-Clinicians-IG-v1.1-508.pdf#page=14
* 56 : CT - A `(Program name)` Performance period end must be 12/31/2023. Please refer to the IG for more information here: https://ecqi.healthit.gov/sites/default/files/2023-CMS-QRDA-III-Eligible-Clinicians-IG-v1.1-508.pdf#page=14
* 57 : CT - The measure reference results must have a single measure population
* 58 : CT - The measure reference results must have a single measure type
* 59 : CT - The electronic measure id: `(Current eMeasure ID)` requires a `(Subpopulation type)` with the correct UUID of `(Correct uuid required)`. Here is a link to the IG containing all the valid measure ids: https://ecqi.healthit.gov/sites/default/files/2023-CMS-QRDA-III-Eligible-Clinicians-IG-v1.1-508.pdf#page=43
* 61 : CT - A Performance Rate must contain a single Numerator UUID reference.
* 62 : CT - The Alternative Payment Model (APM) Entity Identifier must not be empty. Here is a link to the IG section on identifiers: https://ecqi.healthit.gov/sites/default/files/2023-CMS-QRDA-III-Eligible-Clinicians-IG-v1.1-508.pdf#page=15
* 63 : CT - The Alternative Payment Model (APM) Entity Identifier is not valid.  Here is a link to the IG section on identifiers: https://ecqi.healthit.gov/sites/default/files/2023-CMS-QRDA-III-Eligible-Clinicians-IG-v1.1-508.pdf#page=15
* 66 : CT - Missing the `(Supplemental Type)` - `(Type Qualification)` supplemental data for code `(Supplemental Data Code)` for the measure id `(Measure Id)`'s Sub-population `(Sub Population)`
* 67 : CT - Must have one count for Supplemental Data `(Supplemental Data Code)` on Sub-population `(Sub Population)` for the measure id `(Measure Id)`
* 68 : CT - Your `(Program name)` submission was made after the `(Program name)` Measure section submission deadline of `(Submission end date)`. Your `(Program name)` QRDA III file has not been processed. Please contact `(Program name)` Support at `(PCF+ contact email)` for assistance.
* 69 : CT - `(Performance period start or end date)` is an invalid date format. Please use a standard ISO date format. Example valid values are 2019-02-26, 2019/02/26T01:45:23, or 2019-02-26T01:45:23.123. Please see the Implementation Guide for information on the performance period here: https://ecqi.healthit.gov/sites/default/files/2023-CMS-QRDA-III-Eligible-Clinicians-IG-v1.1-508.pdf#page=17
* 70 : CT - The measure section measure reference and results has an incorrect number of measure GUID supplied. Please ensure that only one measure GUID is provided per measure.
* 71 : CT - Two or more different measure section measure reference and results have the same measure GUID. Please ensure that each measure section measure reference and results do not have the same measure GUID.
* 72 : CT - The Performance Rate is missing
* 78 : CT - The Program 'Mips Virtual Group' was found. The required entity id for this program name was missing. Please provide a virtual group identifier with the 'Mips Virtual Group' program name.
* 79 : CT - There is no TIN validator present, so NPI/Alternative Payment Model (APM) combinations cannot be verified
* 80 : CT - NPI `(npi)` and TIN `(tin)` are not reported as expected. This NPI/TIN combination is missing from the QRDA III file or is not in the `(program)` Practitioner Roster for `(apm)`. Please ensure your submission contains all required NPI/TIN combinations and your `(program)` Practitioner Roster is up-to-date.
* 81 : CT - At least one measure is required in a measure section
* 82 : CT - There are too many errors associated with this QRDA-III file. Showing 100 out of `(Error amount)` errors. Please fix the given errors and re-submit
* 84 : CT - `(Program name)` QRDA-III Submissions require at least one TIN to be present.
* 85 : CT - `(Program name)` QRDA-III Submission TINs require a 9 digit numerical value
* 86 : CT - This `(Program name)` QRDA-III submission is missing a TIN. Please ensure there is a TIN associated with every NPI submitted
* 87 : CT - `(Program name)` QRDA-III Submissions require at least one NPI to be present
* 88 : CT - `(Program name)` QRDA-III Submission NPIs require a 10 digit numerical value
* 89 : CT - This `(Program name)` QRDA-III submission is missing a NPI. Please ensure there is an NPI associated with every TIN submitted
* 90 : CT - `(Program name)` QRDA-III submissions should not contain an IA section. IA data will be ignored.
* 91 : CT - The performance rate `(performanceRateUuid)` for measure `(measure id)` has an invalid null value. A performance rate cannot be null unless the performance denominator is 0
* 92 : CT - The performance denominator for measure `(measureId)` was less than 0. A performance rate cannot be null unless the performance denominator is 0
* 93 : CT - The numerator id `(numeratorUuid)` for measure `(measure id)` has a count value that is greater than the denominator and/or the performance denominator (Denominator count - Denominator exclusion count - Denominator Exception count)
* 94 : CT - The denominator exclusion id `(denexUuid)` for measure `(measure id)` has a count value that is greater than the denominator. The Denominator exclusion cannot be a greater value than the denominator.
* 95 : CT - The Clinical Document must contain one Category Section v5 with the extension 2020-12-01
* 96 : CT - The APM to TIN/NPI Combination file is missing.
* 97 : CT - `(Program name)` QRDA-III Submissions require a valid CMS EHR Certification ID (Valid Formats: XX15EXXXXXXXXXX, XX15CXXXXXXXXXX)
* 98 : CT - The performance rate cannot have a value of 0 and must be of value Null Attribute (NA).
* 100 : CT - More than one CMS EHR Certification ID was found. Please submit with only one CMS EHR Certification id.
* 101 : CT - Denominator count must be equal to Initial Population count for `(Program name)` measure population `(measure population id)`.Please see the Table 14 of `(Submission year's)` Implementation Guide for valid measure GUIDs: https://ecqi.healthit.gov/sites/default/files/2023-CMS-QRDA-III-Eligible-Clinicians-IG-v1.1-508.pdf#page=43
* 102 : CT - A PI section cannot contain PI_HIE_5 with PI_HIE_1, PI_LVOTC_1, PI_HIE_4, or PI_LVITC_2
* 103 : CT - PCF Submissions must have the `(PCF Measure minimum)` following measures: `(Listing of valid measure ids)`
* 105 : CT - If multiple TINs/NPIs are submitted, each must be reported within a separate performer
* 106 : CT - PI submissions are not allowed within PCF
* 107 : CT - NPI/TIN Warning: NPI/TIN `(npi)`-`(tin)` was active on the PCF practitioner roster for `(apm)` during the performance year but was not found in the file. Please ensure your submission contains all NPI/TIN combinations that were active on your roster at any point during the performance year. Your QRDA III file and/or roster may require updates. Note: The QPP website does not have access to roster updates made after December 2, 2022. It is therefore critical that you ensure your roster is up to date and your QRDA III file contains all NPI/TIN values that were active on your roster during the performance year. Instructions on how to update your roster are available in the PCF Practice Management Guide, available on PCF Connect (https://app.innovation.cms.gov/CMMIConnect/IDMLogin).
* 108 : CT - NPI/TIN Warning: NPI/TIN `(npi)`-`(tin)` was reported in the file but does not exist at the practice or was not active on the PCF practitioner roster for `(apm)` during the performance year. Please ensure your submission only contains NPI/TIN combinations that were active on your roster at any point during the performance year. Your QRDA III file and/or roster may require updates. Note: The QPP website does not have access to roster updates made after December 2, 2022. It is therefore critical that you ensure your roster is up to date and your QRDA III file contains all NPI/TIN values that were active on your roster during the performance year. Instructions on how to update your roster are available in the PCF Practice Management Guide, available on PCF Connect (https://app.innovation.cms.gov/CMMIConnect/IDMLogin).
