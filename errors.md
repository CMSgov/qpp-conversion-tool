# Error messages
Current list of all error messages being output by the converter.
Any text in the following are considered variables to be filled in:`(Example)`

##List of errors that output to users upon decoding and encoding failures:
* Unexpected exception occurred during conversion
* The file is not a valid XML document
* The file is not a QRDA-III XML document
* Failure to encode
* Failed to find an encoder
* Failed to find an encoder for child node `(encoder type)`
* No encoder for decoder : `(encoder type)`
* Error parsing reporting parameter `(performance start/end)`

##List of errors that output to users upon validation failure:
* This Denominator Node does not have an Aggregate Count Node
* This Denominator Node Aggregate Value is not an integer
* This Denominator Node Aggregate Value has an invalid value
* This Denominator Node does not have any child Nodes
* This Denominator Node has too many child Nodes
* An ACI Measure Performed RnR's requires a single Measure ID
* The ACI Measure Performed RnR's Measure Performed is required
* The ACI Measure Performed RnR's Measure Performed can only be present once
* This ACI Numerator Denominator Node should have an ACI Section Node as a parent
* This ACI Numerator Denominator Node does not contain a measure name ID
* This ACI Numerator Denominator Node does not contain a Numerator Node child
* This ACI Numerator Denominator Node contains too many Numerator Node children
* This ACI Numerator Denominator Node does not contain a Denominator Node child
* This ACI Numerator Denominator Node contains too many Denominator Node children
* This ACI Numerator Denominator Node does not have any child Nodes
* This Numerator Node does not have an Aggregate Count Node
* This Numerator Node Aggregate Value is not an integer
* This Numerator Node Aggregate Value has an invalid value
* This Numerator Node does not have any child Nodes
* This Numerator Node has too many child Nodes
* The ACI Section must have one Reporting Parameter ACT
* A single aggregate count value is required.
* Aggregate count value must be an integer.
* Clinical Document Node must have at least one Aci or IA or eCQM Section Node as a child
* Clinical Document must have one and only one program name
* Clinical Document program name is not recognized
* Clinical Document contains duplicate ACI sections
* Clinical Document contains duplicate IA sections
* Clinical Document contains duplicate eCQM sections
* Must contain a practice site address for CPC+ conversions
* One and only one Alternative Payment Model (APM) Entity Identifier should be specified
* Must contain one Measure (eCQM) section
* Must have at least one NPI/TIN combination
* Must be 01/01/2017 _Performance Start Period_
* Must be 12/31/2017 _Performance End Period_
* Must contain correct number of performance rate(s). Correct Number is `(Number of performance rates required)`
* Missing strata `(Reporting Stratum ID)` for `(Current subpopulation type)` measure `(Current subpopulation UUID)`
* Amount of stratifications `(Current number of Reporting Stratifiers)` does not meet expectations `(Number of stratifiers required)`
for `(Current subpopulation type)` measure `(Current eCQM Measure ID)`. Expected strata: `(Expected strata list)`
* A single measure performed value is required and must be either a Y or an N.
* Measure performed must have exactly one child.
* The IA Section must have at least one IA Measure
* The IA Section must have one Reporting Parameter ACT
* The IA Section must contain only measures and reporting parameter
* Measure performed must have exactly one Aggregate Count.
* Measure data must be a positive integer value
* Clinical Document Node is required
* Only one Clinical Document Node is allowed
* Must enter a valid Performance Rate value
* The measure reference results must have a measure GUID
* The measure reference results must have a single measure population
* The measure reference results must have a single measure type
* The measure reference results must have at least one measure
* The Denominator count must be less than or equal to Initial Population count 
for an eCQM that is proportion measure
* The eCQM (electronic measure id: `(Current eMeasure ID)`) requires `(Number of Subpopulations required)` `(Type of Subpopulation required)`(s) but there are `(Number of Subpopulations existing)`
* The eCQM (electronic measure id: `(Current eMeasure ID)`) requires a `(Subpopulation type)` with the correct UUID of `(Correct uuid required)`
* The eCQM (electronic measure id: `(Current eMeasure ID)`) has a performanceRateId with an incorrect UUID of `(Incorrect UUID)`
* A Performance Rate must contain a single Performance Rate UUID
* The Quality Measure Section must have only one Reporting Parameter ACT
* Must have a performance year
* Must have one and only one performance start
* Must have one and only one performance end
