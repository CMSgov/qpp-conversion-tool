# Error messages
Current list of all error messages being output by the converter.
Any text in the following format `(Example)` are considered variables to be filled in.

### Format - Error Code : Error Message
* 1 : CT - Failed to find an encoder
* 2 : CT - The file is not a valid XML document
* 3 : CT - Unexpected exception occurred during conversion
* 4 : CT - Unexpected exception occured during encoding
* 5 : CT - The file is not a QRDA-III XML document. Please ensure that the submission complies with the `(Submission year's)` implementation guide. `(Implementation guide link)`
* 6 : CT - The measure reference results must have a single occurrence of the recognized measure GUID `(Provided measure id)` is invalid. Did you intend to send one of these `(Valid measure id suggestions)`?
* 7 : CT - The measure reference results must have at least one measure
* 8 : CT - A single aggregate count value is required
* 9 : CT - Aggregate count value must be an integer
* 10 : CT - ACI Measure Performed RnR's Measure Performed is required
* 11 : CT - ACI Measure Performed RnR's Measure Performed can only be present once
* 12 : CT - ACI Measure Performed RnR's requires a single Measure ID
* 13 : CT - Denominator count must be less than or equal to Initial Population count for an eCQM that is proportion measure
* 14 : CT - The eCQM (electronic measure id: `(Current eMeasure ID)`) requires `(Number of Subpopulations required)` `(Type of Subpopulation required)`(s) but there are `(Number of Subpopulations existing)`
* 15 : CT - ACI Numerator Denominator Node should have an ACI Section Node as a parent
* 16 : CT - ACI Numerator Denominator Node does not contain a measure name ID
* 17 : CT - ACI Numerator Denominator Node does not have any child Nodes
* 18 : CT - This ACI Numerator Denominator Node does not contain a Denominator Node child
* 19 : CT - This ACI Numerator Denominator Node does not contain a Numerator Node child
* 20 : CT - This ACI Numerator Denominator Node contains too many Denominator Node children
* 21 : CT - This ACI Numerator Denominator Node contains too many Numerator Node children
* 22 : CT - The ACI Section must have one Reporting Parameter ACT
* 23 : CT - Clinical Document Node must have at least one Aci or IA or eCQM Section Node as a child
* 24 : CT - Clinical Document must have one and only one program name
* 25 : CT - The Clinical Document program name `(program name)` is not recognized. Valid program names are `(list of valid program names)`.
* 26 : CT - Clinical Document contains duplicate ACI sections
* 27 : CT - Clinical Document contains duplicate IA sections
* 28 : CT - Clinical Document contains duplicate eCQN sections
* 29 : CT - Must have one and only one performance start
* 30 : CT - Must have one and only one performance end
* 31 : CT - Must have a performance year
* 32 : CT - The Quality Measure Section must have only one Reporting Parameter ACT
* 33 : CT - Must enter a valid Performance Rate value
* 34 : CT - Must contain a practice site address for CPC+ conversions
* 35 : CT - One and only one Alternative Payment Model (APM) Entity Identifier should be specified
* 62 : CT - The Alternative Payment Model (APM) Entity Identifier must not be empty
* 63 : CT - The Alternative Payment Model (APM) Entity Identifier is not valid
* 36 : CT - Must contain one Measure (eCQM) section
* 37 : CT - Must contain correct number of performance rate(s). Correct Number is `(Expected value)`
* 38 : CT - This `(Numerator or Denominator)` Node does not have any child Nodes
* 39 : CT - This `(Numerator or Denominator)` Node does not have an Aggregate Count Node
* 40 : CT - This `(Numerator or Denominator)` Node has too many child Nodes
* 41 : CT - This `(Numerator or Denominator)` Node Aggregate Value is not an integer
* 42 : CT - This `(Numerator or Denominator)` Node Aggregate Value has an invalid value
* 43 : CT - The IA Section must have at least one IA Measure
* 44 : CT - The IA Section must have one Reporting Parameter ACT
* 45 : CT - The IA Section must contain only measures and reporting parameter
* 46 : CT - Clinical Document Node is required
* 47 : CT - Only one Clinical Document Node is allowed
* 48 : CT - Missing strata `(Reporting Stratum UUID)` for `(Current subpopulation type)` measure `(Current subpopulation UUID)`
* 49 : CT - Amount of stratifications `(Current number of Reporting Stratifiers)` does not meet expectations `(Number of stratifiers required)` for `(Current subpopulation type)` measure `(Current Subpopulation UUID)`. Expected strata: `(Expected strata uuid list)`
* 50 : CT - Measure performed must have exactly one child.
* 51 : CT - A single measure performed value is required and must be either a Y or an N.
* 52 : CT - Measure performed must have exactly one Aggregate Count.
* 53 : CT - Measure data must be a positive integer value
* 54 : CT - Must have at least one NPI/TIN combination
* 55 : CT - Must be 01/01/2017
* 56 : CT - Must be 12/31/2017
* 57 : CT - The measure reference results must have a single measure population
* 58 : CT - The measure reference results must have a single measure type
* 59 : CT - The eCQM (electronic measure id: `(Current eMeasure ID)`) requires a `(Subpopulation type)` with the correct UUID of `(Correct uuid required)`
* 60 : CT - The eCQM (electronic measure id: `(Current eMeasure ID)`) has a performanceRateId with an incorrect UUID of `(Incorrect UUID)`
* 61 : CT - A Performance Rate must contain a single Performance Rate UUID
* 64 : CT - CPC+ Submissions must have at least `(CPC+ measure group minimum)` of the following `(CPC+ measure group label)` measures: `(Listing of valid measure ids)`
* 65 : CT - CPC+ Submissions must have at least `(Overall CPC+ measure minimum)` of the following measures: `(Listing of all CPC+ measure ids)`.
* 66 : CT - Missing the Supplemental Code `(Supplemental Data Code)` for eCQM measure `(Measure Id)`'s Sub-population `(Sub Population)`
* 67 : CT - Must have one count for Supplemental Data `(Supplemental Data Code)` on Sub-population `(Sub Population)` for eCQM measure `(Measure Id)`
* 68 : CT - CPC+ Submission is after the end date `(Submission end date)`
* 69 : CT - `(Performance period start or end date)` is an invalid date format. Please use a standard ISO date format. Example valid values are 2017-02-26, 2017/02/26T01:45:23, or 2017-02-26T01:45:23.123
