package gov.cms.qpp.conversion.model.error;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Error codes that may be returned by the converter
 */
public enum ErrorCode implements LocalizedError {

	ENCODER_MISSING(1, "Failed to find an encoder"),
	NOT_VALID_XML_DOCUMENT(2, "The file is not a valid XML document"),
	UNEXPECTED_ERROR(3, "Unexpected exception occurred during conversion"),
	UNEXPECTED_ENCODE_ERROR(4, "Unexpected exception occured during encoding"),
	NOT_VALID_QRDA_DOCUMENT(5, "The file is not a QRDA-III XML document"),
	MEASURE_GUID_MISSING(6, "The measure reference results must have a measure GUID"),
	CHILD_MEASURE_MISSING(7, "The measure reference results must have at least one measure"),
	AGGREGATE_COUNT_VALUE_NOT_SINGULAR(8, "A single aggregate count value is required"),
	AGGREGATE_COUNT_VALUE_NOT_INTEGER(9, "Aggregate count value must be an integer"),
	ACI_MEASURE_PERFORMED_RNR_MEASURE_PERFORMED_MISSING(10, "ACI Measure Performed RnR's Measure Performed is "
			+ "required"),
	ACI_MEASURE_PERFORMED_RNR_MEASURE_PERFORMED_REPEATED(11, "ACI Measure Performed RnR's Measure Performed "
			+ "can only be present once"),
	ACI_MEASURE_PERFORMED_RNR_MEASURE_ID_NOT_SINGULAR(12, "ACI Measure Performed RnR's requires a single "
			+ "Measure ID"),
	DENOMINATOR_COUNT_INVALID(13, "Denominator count must be less than or equal to Initial Population count "
			+ "for an eCQM that is proportion measure"),
	POPULATION_CRITERIA_COUNT_INCORRECT(14, "The eCQM (electronic measure id: %s) requires %d %s(s) but there "
			+ "are %d", true),
	ACI_NUMERATOR_DENOMINATOR_PARENT_NOT_ACI_SECTION(15, "ACI Numerator Denominator Node should have an ACI "
			+ "Section Node as a parent"),
	ACI_NUMERATOR_DENOMINATOR_MISSING_MEASURE_ID(16, "ACI Numerator Denominator Node does not contain a "
			+ "measure name ID"),
	ACI_NUMERATOR_DENOMINATOR_MISSING_CHILDREN(17, "ACI Numerator Denominator Node does not have any child "
			+ "Nodes"),
	ACI_NUMERATOR_DENOMINATOR_VALIDATOR_MISSING_DENOMINATOR_CHILD_NODE(18, "This ACI Numerator Denominator "
			+ "Node does not contain a Denominator Node child"),
	ACI_NUMERATOR_DENOMINATOR_VALIDATOR_MISSING_NUMERATOR_CHILD_NODE(19, "This ACI Numerator Denominator "
			+ "Node does not contain a Numerator Node child"),
	ACI_NUMERATOR_DENOMINATOR_VALIDATOR_TOO_MANY_DENOMINATORS(20, "This ACI Numerator Denominator Node "
			+ "contains too many Denominator Node children"),
	ACI_NUMERATOR_DENOMINATOR_VALIDATOR_TOO_MANY_NUMERATORS(21, "This ACI Numerator Denominator Node "
			+ "contains too many Numerator Node children"),
	ACI_SECTION_MISSING_REPORTING_PARAMETER_ACT(22, "The ACI Section must have one Reporting Parameter ACT"),
	CLINICAL_DOCUMENT_MISSING_ACI_OR_IA_OR_ECQM_CHILD(23, "Clinical Document Node must have at least one "
			+ "Aci or IA or eCQM Section Node as a child"),
	CLINICAL_DOCUMENT_MISSING_PROGRAM_NAME(24, "Clinical Document must have one and only one program name"),
	CLINICAL_DOCUMENT_INCORRECT_PROGRAM_NAME(25, "Clinical Document program name is not recognized"),
	CLINICAL_DOCUMENT_CONTAINS_DUPLICATE_ACI_SECTIONS(26, "Clinical Document contains duplicate ACI sections"),
	CLINICAL_DOCUMENT_CONTAINS_DUPLICATE_IA_SECTIONS(27, "Clinical Document contains duplicate IA sections"),
	CLINICAL_DOCUMENT_CONTAINS_DUPLICATE_ECQM_SECTIONS(28, "Clinical Document contains duplicate eCQN "
			+ "sections"),
	REPORTING_PARAMETERS_MUST_CONTAIN_SINGLE_PERFORMANCE_START(29, "Must have one and only one performance "
			+ "start"),
	REPORTING_PARAMETERS_MUST_CONTAIN_SINGLE_PERFORMANCE_END(30, "Must have one and only one performance end"),
	REPORTING_PARAMETERS_MISSING_PERFORMANCE_YEAR(31, "Must have a performance year"),
	QUALITY_MEASURE_SECTION_REQUIRED_REPORTING_PARAM_REQUIREMENT(32, "The Quality Measure Section must have "
			+ "only one Reporting Parameter ACT"),
	PERFORMANCE_RATE_INVALID_VALUE(33, "Must enter a valid Performance Rate value"),
	CPC_CLINICAL_DOCUMENT_MISSING_PRACTICE_SITE_ADDRESS(34, "Must contain a practice site address for CPC+ "
			+ "conversions"),
	CPC_CLINICAL_DOCUMENT_ONLY_ONE_APM_ALLOWED(35, "One and only one Alternative Payment Model (APM) Entity "
			+ "Identifier should be specified"),
	CPC_CLINICAL_DOCUMENT_ONE_MEASURE_SECTION_REQUIRED(36, "Must contain one Measure (eCQM) section"),
	CPC_QUALITY_MEASURE_ID_INVALID_PERFORMANCE_RATE_COUNT(37, "Must contain correct number of performance "
			+ "rate(s). Correct Number is %s", true),
	NUMERATOR_DENOMINATOR_MISSING_CHILDREN(38, "This %s Node does not have any child Nodes", true),
	NUMERATOR_DENOMINATOR_INCORRECT_CHILD(39, "This %s Node does not have an Aggregate Count Node", true),
	NUMERATOR_DENOMINATOR_TOO_MANY_CHILDREN(40, "This %s Node has too many child Nodes", true),
	NUMERATOR_DENOMINATOR_MUST_BE_INTEGER(41, "This %s Node Aggregate Value is not an integer", true),
	NUMERATOR_DENOMINATOR_INVALID_VALUE(42, "This %s Node Aggregate Value has an invalid value", true),
	IA_SECTION_MISSING_IA_MEASURE(43, "The IA Section must have at least one IA Measure"),
	IA_SECTION_MISSING_REPORTING_PARAM(44, "The IA Section must have one Reporting Parameter ACT"),
	IA_SECTION_WRONG_CHILD(45, "The IA Section must contain only measures and reporting parameter"),
	NPI_TIN_COMBINATION_MISSING_CLINICAL_DOCUMENT(46, "Clinical Document Node is required"),
	NPI_TIN_COMBINATION_EXACTLY_ONE_DOCUMENT_ALLOWED(47, "Only one Clinical Document Node is allowed"),
	CPC_QUALITY_MEASURE_ID_MISSING_STRATA(48, "Missing strata %s for %s measure (%s)", true),
	CPC_QUALITY_MEASURE_ID_STRATA_MISMATCH(49, "Amount of stratifications %d does not meet expectations %d for "
			+ "%s measure (%s). Expected strata: %s", true),
	IA_MEASURE_INCORRECT_CHILDREN_COUNT(50, "Measure performed must have exactly one child."),
	IA_MEASURE_INVALID_TYPE(51, "A single measure performed value is required and must be either a Y or an N."),
	MEASURE_PERFORMED_MISSING_AGGREGATE_COUNT(52, "Measure performed must have exactly one Aggregate Count."),
	MEASURE_DATA_VALUE_NOT_INTEGER(53, "Measure data must be a positive integer value"),
	CPC_NPI_TIN_COMBINATION_MISSING_NPI_TIN_COMBINATION(54, "Must have at least one NPI/TIN combination"),
	CPC_PERFORMANCE_PERIOD_START_JAN12017(55, "Must be 01/01/2017"),
	CPC_PERFORMANCE_PERIOD_END_DEC312017(56, "Must be 12/31/2017"),
	QUALITY_MEASURE_ID_MISSING_SINGLE_MEASURE_POPULATION(57, "The measure reference results must have a single "
			+ "measure population"),
	QUALITY_MEASURE_ID_MISSING_SINGLE_MEASURE_TYPE(58, "The measure reference results must have a single "
			+ "measure type"),
	QUALITY_MEASURE_ID_INCORRECT_UUID(59, "The eCQM (electronic measure id: %s) requires a %s with the correct "
			+ "UUID of %s", true),
	QUALITY_MEASURE_ID_INCORRECT_PERFORMANCE_UUID(60, "The eCQM (electronic measure id: %s) has a %s with an "
			+ "incorrect UUID of %s", true),
	QUALITY_MEASURE_ID_MISSING_SINGLE_PERFORMANCE_RATE(61, "A Performance Rate must contain a single "
			+ "Performance Rate UUID"),
	CPC_PLUS_MISSING_SUPPLEMENTAL_CODE(62,
			"Missing the Supplemental Code %s for eCQM measure %s's Sub-population %s", true);

	private static final Map<Integer, ErrorCode> CODE_TO_VALUE = Arrays.stream(values())
			.collect(Collectors.toMap(ErrorCode::getCode, Function.identity()));

	private final int code;
	private final String message;
	private final boolean hasFormat;

	ErrorCode(int code,String message) {
		this(code, message, false);
	}

	ErrorCode(int code, String message, boolean hasFormat) {
		this.code = code;
		this.message = message;
		this.hasFormat = hasFormat;
	}

	/**
	 * Gets the message associated with this error code
	 */
	public final String getMessage() {
		return message;
	}

	/**
	 * Self returning
	 */
	@Override
	public final ErrorCode getErrorCode() {
		return this;
	}

	public final int getCode() {
		return code;
	}

	/**
	 * Creates a formatted version of this error code, or throws an exception
	 *
	 * @param arguments arguments to format with
	 * @return the formatted version of this error code, or throws an exception if formatting is 
	 * not supported.
	 */
	public final LocalizedError format(Object... arguments) {
		if (hasFormat) {
			String formatted = String.format(message, arguments);
			return new FormattedErrorCode(this, formatted);
		}

		throw new IllegalStateException(this + " does not support formatting");
	}

	public static ErrorCode getByCode(int code) {
		return CODE_TO_VALUE.get(code);
	}

}
