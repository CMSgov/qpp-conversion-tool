package gov.cms.qpp.conversion.model.error;

public enum ErrorCode implements LocalizedError {

	ENCODER_MISSING("Failed to find an encoder"),
	NOT_VALID_XML_DOCUMENT("The file is not a valid XML document"),
	UNEXPECTED_ERROR("Unexpected exception occurred during conversion"),
	UNEXPECTED_ENCODE_ERROR("Unexpected exception occured during encoding"),
	NOT_VALID_QRDA_DOCUMENT("The file is not a QRDA-III XML document"),
	MEASURE_GUID_MISSING("The measure reference results must have a measure GUID"),
	CHILD_MEASURE_MISSING("The measure reference results must have at least one measure"),
	AGGREGATE_COUNT_VALUE_NOT_SINGULAR("A single aggregate count value is required"),
	AGGREGATE_COUNT_VALUE_NOT_INTEGER("Aggregate count value must be an integer"),
	ACI_MEASURE_PERFORMED_RNR_MEASURE_PERFORMED_MISSING("ACI Measure Performed RnR's Measure Performed is required"),
	ACI_MEASURE_PERFORMED_RNR_MEASURE_PERFORMED_REPEATED("ACI Measure Performed RnR's Measure Performed can only be present once"),
	ACI_MEASURE_PERFORMED_RNR_MEASURE_ID_NOT_SINGULAR("ACI Measure Performed RnR's requires a single Measure ID"),
	DENOMINATOR_COUNT_INVALID("Denominator count must be less than or equal to Initial Population count for an eCQM that is proportion measure"),
	POPULATION_CRITERIA_COUNT_INCORRECT("The eCQM (electronic measure id: %s) requires %d %s(s) but there are %d", true),
	ACI_NUMERATOR_DENOMINATOR_PARENT_NOT_ACI_SECTION("ACI Numerator Denominator Node should have an ACI Section Node as a parent"),
	ACI_NUMERATOR_DENOMINATOR_MISSING_MEASURE_ID("ACI Numerator Denominator Node does not contain a measure name ID"),
	ACI_NUMERATOR_DENOMINATOR_MISSING_CHILDREN("ACI Numerator Denominator Node does not have any child Nodes"),
	ACI_NUMERATOR_DENOMINATOR_VALIDATOR_MISSING_DENOMINATOR_CHILD_NODE("This ACI Numerator Denominator Node does not contain a Denominator Node child"),
	ACI_NUMERATOR_DENOMINATOR_VALIDATOR_MISSING_NUMERATOR_CHILD_NODE("This ACI Numerator Denominator Node does not contain a Numerator Node child"),
	ACI_NUMERATOR_DENOMINATOR_VALIDATOR_TOO_MANY_DENOMINATORS("This ACI Numerator Denominator Node contains too many Denominator Node children"),
	ACI_NUMERATOR_DENOMINATOR_VALIDATOR_TOO_MANY_NUMERATORS("This ACI Numerator Denominator Node contains too many Numerator Node children"),
	ACI_SECTION_MISSING_REPORTING_PARAMETER_ACT("The ACI Section must have one Reporting Parameter ACT"),
	CLINICAL_DOCUMENT_MISSING_ACI_OR_IA_OR_ECQM_CHILD("Clinical Document Node must have at least one Aci or IA or eCQM Section Node as a child"),
	CLINICAL_DOCUMENT_MISSING_PROGRAM_NAME("Clinical Document must have one and only one program name"),
	CLINICAL_DOCUMENT_INCORRECT_PROGRAM_NAME("Clinical Document program name is not recognized"),
	CLINICAL_DOCUMENT_CONTAINS_DUPLICATE_ACI_SECTIONS("Clinical Document contains duplicate ACI sections"),
	CLINICAL_DOCUMENT_CONTAINS_DUPLICATE_IA_SECTIONS("Clinical Document contains duplicate IA sections"),
	CLINICAL_DOCUMENT_CONTAINS_DUPLICATE_eCQM_SECTIONS("Clinical Document contains duplicate eCQN sections"),
	REPORTING_PARAMETERS_MUST_CONTAIN_SINGLE_PERFORMANCE_START("Must have one and only one performance start"),
	REPORTING_PARAMETERS_MUST_CONTAIN_SINGLE_PERFORMANCE_END("Must have one and only one performance end"),
	REPORTING_PARAMETERS_MISSING_PERFORMANCE_YEAR("Must have a performance year"),
	QUALITY_MEASURE_SECTION_REQUIRED_REPORTING_PARAM_REQUIREMENT_ERROR("The Quality Measure Section must have only one Reporting Parameter ACT"),
	PERFORMANCE_RATE_INVALID_VALUE("Must enter a valid Performance Rate value"),
	CPC_CLINICAL_DOCUMENT_MISSING_PRACTICE_SITE_ADDRESS("Must contain a practice site address for CPC+ conversions"),
	CPC_CLINICAL_DOCUMENT_ONLY_ONE_APM_ALLOWED("One and only one Alternative Payment Model (APM) Entity Identifier should be specified"),
	CPC_CLINICAL_DOCUMENT_ONE_MEASURE_SECTION_REQUIRED("Must contain one Measure (eCQM) section"),
	CPC_QUALITY_MEASURE_ID_INVALID_PERFORMANCE_RATE_COUNT("Must contain correct number of performance rate(s). Correct Number is %s", true),
	NUMERATOR_DENOMINATOR_MISSING_CHILDREN("This %s Node does not have any child Nodes", true),
	NUMERATOR_DENOMINATOR_INCORRECT_CHILD("This %s Node does not have an Aggregate Count Node", true),
	NUMERATOR_DENOMINATOR_TOO_MANY_CHILDREN("This %s Node has too many child Nodes", true),
	NUMERATOR_DENOMINATOR_MUST_BE_INTEGER("This %s Node Aggregate Value is not an integer", true),
	NUMERATOR_DENOMINATOR_INVALID_VALUE("This %s Node Aggregate Value has an invalid value", true),
	IA_SECTION_MISSING_IA_MEASURE("The IA Section must have at least one IA Measure"),
	IA_SECTION_MISSING_REPORTING_PARAM("The IA Section must have one Reporting Parameter ACT"),
	IA_SECTION_WRONG_CHILD("The IA Section must contain only measures and reporting parameter"),
	NPI_TIN_COMBINATION_MISSING_CLINICAL_DOCUMENT("Clinical Document Node is required"),
	NPI_TIN_COMBINATION_EXACTLY_ONE_DOCUMENT_ALLOWED("Only one Clinical Document Node is allowed"),
	CPC_QUALITY_MEASURE_ID_MISSING_STRATA("Missing strata %s for %s measure (%s)", true),
	CPC_QUALITY_MEASURE_ID_STRATA_MISMATCH("Amount of stratifications %d does not meet expectations %d for %s measure (%s). Expected strata: %s", true),
	IA_MEASURE_INCORRECT_CHILDREN_COUNT("Measure performed must have exactly one child."),
	IA_MEASURE_INVALID_TYPE("A single measure performed value is required and must be either a Y or an N."),
	MEASURE_PERFORMED_MISSING_AGGREGATE_COUNT("Measure performed must have exactly one Aggregate Count."),
	MEASURE_DATA_INVALID_VALUE("Measure data must be a positive integer value"),
	CPC_NPI_TIN_COMBINATION_MISSING_NPI_TIN_COMBINATION("Must have at least one NPI/TIN combination"),
	CPC_PERFORMANCE_PERIOD_START_JAN12017("Must be 01/01/2017"),
	CPC_PERFORMANCE_PERIOD_END_DEC312017("Must be 12/31/2017"),
	QUALITY_MEASURE_ID_MISSING_SINGLE_MEASURE_POPULATION("The measure reference results must have a single measure population"),
	QUALITY_MEASURE_ID_MISSING_SINGLE_MEASURE_TYPE("The measure reference results must have a single measure type"),
	QUALITY_MEASURE_ID_INCORRECT_UUID("The eCQM (electronic measure id: %s) requires a %s with the correct UUID of %s", true),
	QUALITY_MEASURE_ID_INCORRECT_PERFORMANCE_UUID("The eCQM (electronic measure id: %s) has a %s with an incorrect UUID of %s", true),
	QUALITY_MEASURE_ID_MISSING_SINGLE_PERFORMANCE_RATE("A Performance Rate must contain a single Performance Rate UUID");

	private final String message;
	private final boolean hasFormat;

	ErrorCode(String message) {
		this(message, false);
	}

	ErrorCode(String message, boolean hasFormat) {
		this.message = message;
		this.hasFormat = hasFormat;
	}

	public final String getMessage() {
		return message;
	}

	@Override
	@Deprecated // no use calling this
	public ErrorCode getErrorCode() {
		return this;
	}

	public FormattedErrorCode format(Object... arguments) {
		if (hasFormat) {
			String formatted = String.format(message, arguments);
			return new FormattedErrorCode(formatted);
		}

		throw new IllegalStateException(this + " does not support formatting");
	}

	public final class FormattedErrorCode implements LocalizedError {
		private final String message;

		FormattedErrorCode(String message) {
			this.message = message;
		}

		@Override
		public ErrorCode getErrorCode() {
			return ErrorCode.this;
		}

		@Override
		public String getMessage() {
			return message;
		}
	}

}
