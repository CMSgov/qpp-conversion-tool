# Error messages
Current list of all error messages being output by the converter.
Any text in the following format `(Example)` are considered variables to be filled in.

### Format - Error Code : Error Message
* 1 : CT - Failed to find an encoder
* 2 : CT - The file is not a valid XML document. The file you are submitting is not a properly formatted XML document. Please check your document to ensure proper formatting.
* 3 : CT - Unexpected exception occurred during conversion. Please contact the Service Center for assistance via phone at 1-866-288-8292 or TTY: 1-877-715-6222, or by emailing QPP@cms.hhs.gov
* 4 : CT - Unexpected exception occurred during encoding. Please contact the Service Center for assistance via phone at 1-866-288-8292 or TTY: 1-877-715-6222, or by emailing QPP@cms.hhs.gov
* 5 : CT - The file is not a QRDA-III XML document. Please ensure that the submission complies with the `(Submission year's)` implementation guide. `(Implementation guide link)`
* 6 : CT - The measure GUID supplied `(Provided measure id)` is invalid. Please see the `(Submission year's)` IG https://ecqi.healthit.gov/system/files/eCQM_QRDA_EC-508_0.pdf#page=88 for valid measure GUIDs.
* 7 : CT - The measure reference results must have at least one measure. Please review the measures section of your file as it cannot be empty.
* 8 : CT - A single aggregate count value is required
* 9 : CT - Aggregate count value must be an integer
* 11 : CT - This ACI Reference and Results is missing a required Measure Performed child
* 12 : CT - This ACI Measure Performed Reference and Results requires a single Measure ID
* 13 : CT - Denominator count must be less than or equal to Initial Population count for a measure that is a proportion measure
* 14 : CT - The electronic measure id: `(Current eMeasure ID)` requires `(Number of Subpopulations required)` `(Type of Subpopulation required)`(s) but there are `(Number of Subpopulations existing)`
* 15 : CT - ACI Numerator Denominator element should have an ACI Section element as a parent
* 16 : CT - ACI Numerator Denominator element does not contain a measure name ID
* 17 : CT - ACI Numerator Denominator element does not have any child elements
* 18 : CT - This ACI Numerator Denominator element requires exactly one `(Denominator|Numerator)` element child
* 22 : CT - The ACI Section must have one Reporting Parameter ACT
* 23 : CT - Clinical Document element must have at least one child element of type ACI, IA, or Measure section
* 24 : CT - Clinical Document must have one and only one program name. Valid program names are `(list of valid program names)`
* 25 : CT - The Clinical Document program name `(program name)` is not recognized. Valid program names are `(list of valid program names)`.
* 26 : CT - Clinical Document contains duplicate ACI sections
* 27 : CT - Clinical Document contains duplicate IA sections
* 28 : CT - Clinical Document contains duplicate Measure sections
* 29 : CT - Must have one and only one performance start
* 30 : CT - Must have one and only one performance end
* 31 : CT - Must have a performance year
* 32 : CT - The Quality Measure Section must have exactly one Reporting Parameter ACT
* 33 : CT - Must enter a valid Performance Rate value
* 34 : CT - Must contain a practice site address for CPC+ conversions
* 35 : CT - One and only one Alternative Payment Model (APM) Entity Identifier should be specified
* 62 : CT - The Alternative Payment Model (APM) Entity Identifier must not be empty
* 63 : CT - The Alternative Payment Model (APM) Entity Identifier is not valid
* 36 : CT - Must contain one Measure section
* 37 : CT - Must contain correct number of performance rate(s). Correct Number is `(Expected value)`
* 38 : CT - This `(Numerator or Denominator)` Node does not have any child Nodes
* 39 : CT - This `(Numerator or Denominator)` Node must have exactly one Aggregate Count node
* 41 : CT - This `(Numerator or Denominator)` Node Aggregate Value is not an integer
* 42 : CT - This `(Numerator or Denominator)` Node Aggregate Value has an invalid value
* 43 : CT - The IA Section must have at least one IA Measure
* 44 : CT - The IA Section must have one Reporting Parameter ACT
* 45 : CT - The IA Section must contain only Improvement Activity and a reporting parameter Act
* 46 : CT - Clinical Document Node is required
* 47 : CT - Only one Clinical Document Node is allowed
* 48 : CT - Missing strata `(Reporting Stratum UUID)` for `(Current subpopulation type)` measure `(Current subpopulation UUID)`
* 49 : CT - Amount of stratifications `(Current number of Reporting Stratifiers)` does not meet expectations `(Number of stratifiers required)` for `(Current subpopulation type)` measure `(Current Subpopulation UUID)`. Expected strata: `(Expected strata uuid list)`
* 50 : CT - Measure performed must have exactly one child.
* 51 : CT - A single measure performed value is required and must be either a Y or an N.
* 52 : CT - Measure data must have exactly one Aggregate Count.
* 53 : CT - Measure data must be a positive integer value
* 54 : CT - Must have at least one NPI/TIN combination
* 55 : CT - Must be 01/01/2017
* 56 : CT - Must be 12/31/2017
* 57 : CT - The measure reference results must have a single measure population
* 58 : CT - The measure reference results must have a single measure type
* 59 : CT - The electronic measure id: `(Current eMeasure ID)` requires a `(Subpopulation type)` with the correct UUID of `(Correct uuid required)`
* 60 : CT - The electronic measure id: `(Current eMeasure ID)` has a performanceRateId with an incorrect UUID of `(Incorrect UUID)`
* 61 : CT - A Performance Rate must contain a single Performance Rate UUID
* 64 : CT - CPC+ Submissions must have at least `(CPC+ measure group minimum)` of the following `(CPC+ measure group label)` measures: `(Listing of valid measure ids)`
* 65 : CT - CPC+ Submissions must have at least `(Overall CPC+ measure minimum)` of the following measures: `(Listing of all CPC+ measure ids)`.
* 66 : CT - Missing the `(Supplemental Type)` - `(Type Qualification)` supplemental data for code `(Supplemental Data Code)` for the measure id `(Measure Id)`'s Sub-population `(Sub Population)`
* 67 : CT - Must have one count for Supplemental Data `(Supplemental Data Code)` on Sub-population `(Sub Population)` for the measure id `(Measure Id)`
* 68 : CT - Your CPC+ submission was made after the CPC+ Measure section submission deadline of `(Submission end date)`. Your CPC+ QRDA III file has not been processed. Please contact CPC+ Support at `(CPC+ contact email)` for assistance.
* 69 : CT - `(Performance period start or end date)` is an invalid date format. Please use a standard ISO date format. Example valid values are 2017-02-26, 2017/02/26T01:45:23, or 2017-02-26T01:45:23.123
