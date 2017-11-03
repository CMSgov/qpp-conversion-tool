# Error messages
Current list of all error messages being output by the converter.
Any text in the following format `(Example)` are considered variables to be filled in.

### Format - Error Code : Error Message
* 0 : Failed to find an encoder
* 1 : The file is not a valid XML document
* 2 : Unexpected exception occurred during conversion
* 3 : Unexpected exception occured during encoding
* 4 : The file is not a QRDA-III XML document
* 5 : The measure reference results must have a measure GUID
* 6 : The measure reference results must have at least one measure
* 7 : A single aggregate count value is required
* 8 : Aggregate count value must be an integer
* 9 : ACI Measure Performed RnR's Measure Performed is required
* 10 : ACI Measure Performed RnR's Measure Performed can only be present once
* 11 : ACI Measure Performed RnR's requires a single Measure ID
* 12 : Denominator count must be less than or equal to Initial Population count for an eCQM that is proportion measure
* 13 : The eCQM (electronic measure id: `(Current eMeasure ID)`) requires `(Number of Subpopulations required)` `(Type of Subpopulation required)`(s) but there are `(Number of Subpopulations existing)`
  * Ex. : The eCQM (electronic measure id: CMS165v5) requires 1 DENEX(s) but there are 0
* 14 : ACI Numerator Denominator Node should have an ACI Section Node as a parent
* 15 : ACI Numerator Denominator Node does not contain a measure name ID
* 16 : ACI Numerator Denominator Node does not have any child Nodes
* 17 : This ACI Numerator Denominator Node does not contain a Denominator Node child
* 18 : This ACI Numerator Denominator Node does not contain a Numerator Node child
* 19 : This ACI Numerator Denominator Node contains too many Denominator Node children
* 20 : This ACI Numerator Denominator Node contains too many Numerator Node children
* 21 : The ACI Section must have one Reporting Parameter ACT
* 22 : Clinical Document Node must have at least one Aci or IA or eCQM Section Node as a child
* 23 : Clinical Document must have one and only one program name
* 24 : Clinical Document program name is not recognized
* 25 : Clinical Document contains duplicate ACI sections
* 26 : Clinical Document contains duplicate IA sections
* 27 : Clinical Document contains duplicate eCQN sections
* 28 : Must have one and only one performance start
* 29 : Must have one and only one performance end
* 30 : Must have a performance year
* 31 : The Quality Measure Section must have only one Reporting Parameter ACT
* 32 : Must enter a valid Performance Rate value
* 33 : Must contain a practice site address for CPC+ conversions
* 34 : One and only one Alternative Payment Model (APM) Entity Identifier should be specified
* 35 : Must contain one Measure (eCQM) section
* 36 : Must contain correct number of performance rate(s). Correct Number is %s
  * Ex. : Must contain correct number of performance rate(s). Correct Number is 3
* 37 : This `(Numerate or Denominator)` Node does not have any child Nodes
* 38 : This `(Numerate or Denominator)` Node does not have an Aggregate Count Node
* 39 : This `(Numerate or Denominator)` Node has too many child Nodes
* 40 : This `(Numerate or Denominator)` Node Aggregate Value is not an integer
* 41 : This `(Numerate or Denominator)` Node Aggregate Value has an invalid value
* 42 : The IA Section must have at least one IA Measure
* 43 : The IA Section must have one Reporting Parameter ACT
* 44 : The IA Section must contain only measures and reporting parameter
* 45 : Clinical Document Node is required
* 46 : Only one Clinical Document Node is allowed
* 47 : Missing strata `(Reporting Stratum UUID)` for `(Current subpopulation type)` measure `(Current subpopulation UUID)`
  * Ex. : Missing strata EFB5B088-CE10-43DE-ACCD-9913B7AC12A2 for DENEX measure (56BC7FA2-C22A-4440-8652-2D3568852C60)
* 48 : Amount of stratifications `(Current number of Reporting Stratifiers)` does not meet expectations `(Number of stratifiers required)` for `(Current subpopulation type)` measure `(Current Subpopulation UUID)`. Expected strata: `(Expected strata uuid list)`
  * Ex. : Amount of stratifications 0 does not meet expectations 2 for DENEX measure (56BC7FA2-C22A-4440-8652-2D3568852C60).
* 49 : Measure performed must have exactly one child.
* 50 : A single measure performed value is required and must be either a Y or an N.
* 51 : Measure performed must have exactly one Aggregate Count.
* 52 : Measure data must be a positive integer value
* 53 : Must have at least one NPI/TIN combination
* 54 : Must be 01/01/2017
* 55 : Must be 12/31/2017
* 56 : The measure reference results must have a single measure population
* 57 : The measure reference results must have a single measure type
* 58 : The eCQM (electronic measure id: `(Current eMeasure ID)`) requires a `(Subpopulation type)` with the correct UUID of `(Correct uuid required)`
  * Ex. : The eCQM (electronic measure id: CMS165v5) requires a DENEX with the correct UUID of 55A6D5F3-2029-4896-B850-4C7894161D7D
* 59 : The eCQM (electronic measure id: `(Current eMeasure ID)`) has a performanceRateId with an incorrect UUID of `(Incorrect UUID)`
  * Ex. : The eCQM (electronic measure id: CMS68v6) has a performanceRateUuid with an incorrect UUID of 00000000-0000-0000-0000-1NV4L1D
* 60 : A Performance Rate must contain a single Performance Rate UUID