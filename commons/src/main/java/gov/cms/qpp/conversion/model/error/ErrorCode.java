package gov.cms.qpp.conversion.model.error;


import org.apache.commons.text.StrSubstitutor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Error codes that may be returned by the converter
 */
public enum ErrorCode implements LocalizedError {

	ENCODER_MISSING(1, "Failed to find an encoder"),
	NOT_VALID_XML_DOCUMENT(2, "The file is not a valid XML document"),
	UNEXPECTED_ERROR(3, "Unexpected exception occurred during conversion"),
	UNEXPECTED_ENCODE_ERROR(4, "Unexpected exception occured during encoding"),
	NOT_VALID_QRDA_DOCUMENT(5, "The file is not a QRDA-III XML document"),
	MEASURE_GUID_MISSING(6, "The measure reference results must have a single occurrence of the recognized measure GUID "
	+ "`(Provided measure id)` is invalid. Did you intend to send one of these `(Valid measure id suggestions)`?"),
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
	POPULATION_CRITERIA_COUNT_INCORRECT(14,
			"The eCQM (electronic measure id: `(Current eMeasure ID)`) requires `(Number of Subpopulations required)` " +
			"`(Type of Subpopulation required)`(s) but there are `(Number of Subpopulations existing)`", true),
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
	CPC_CLINICAL_DOCUMENT_EMPTY_APM(62, "The Alternative Payment Model (APM) Entity Identifier must not be empty"),
	CPC_CLINICAL_DOCUMENT_INVALID_APM(63, "The Alternative Payment Model (APM) Entity Identifier is not valid"),
	CPC_CLINICAL_DOCUMENT_ONE_MEASURE_SECTION_REQUIRED(36, "Must contain one Measure (eCQM) section"),
	CPC_QUALITY_MEASURE_ID_INVALID_PERFORMANCE_RATE_COUNT(37, "Must contain correct number of performance rate(s). " +
			"Correct Number is `(Expected value)`", true),
	NUMERATOR_DENOMINATOR_MISSING_CHILDREN(38,
			"This `(Numerator or Denominator)` Node does not have any child Nodes", true),
	NUMERATOR_DENOMINATOR_INCORRECT_CHILD(39,
			"This `(Numerator or Denominator)` Node does not have an Aggregate Count Node", true),
	NUMERATOR_DENOMINATOR_TOO_MANY_CHILDREN(40,
			"This `(Numerator or Denominator)` Node has too many child Nodes", true),
	NUMERATOR_DENOMINATOR_MUST_BE_INTEGER(41,
			"This `(Numerator or Denominator)` Node Aggregate Value is not an integer", true),
	NUMERATOR_DENOMINATOR_INVALID_VALUE(42,
			"This `(Numerator or Denominator)` Node Aggregate Value has an invalid value", true),
	IA_SECTION_MISSING_IA_MEASURE(43, "The IA Section must have at least one IA Measure"),
	IA_SECTION_MISSING_REPORTING_PARAM(44, "The IA Section must have one Reporting Parameter ACT"),
	IA_SECTION_WRONG_CHILD(45, "The IA Section must contain only measures and reporting parameter"),
	NPI_TIN_COMBINATION_MISSING_CLINICAL_DOCUMENT(46, "Clinical Document Node is required"),
	NPI_TIN_COMBINATION_EXACTLY_ONE_DOCUMENT_ALLOWED(47, "Only one Clinical Document Node is allowed"),
	CPC_QUALITY_MEASURE_ID_MISSING_STRATA(48, "Missing strata `(Reporting Stratum UUID)` for " +
			"`(Current subpopulation type)` measure `(Current subpopulation UUID)`", true),
	CPC_QUALITY_MEASURE_ID_STRATA_MISMATCH(49,
			"Amount of stratifications `(Current number of Reporting Stratifiers)` does not meet expectations " +
			"`(Number of stratifiers required)` for `(Current subpopulation type)` measure " +
			"`(Current Subpopulation UUID)`. Expected strata: `(Expected strata uuid list)`"
			, true),
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
	QUALITY_MEASURE_ID_INCORRECT_UUID(59, "The eCQM (electronic measure id: `(Current eMeasure ID)`) requires a " +
			"`(Subpopulation type)` with the correct UUID of `(Correct uuid required)`", true),
	QUALITY_MEASURE_ID_INCORRECT_PERFORMANCE_UUID(60, "The eCQM (electronic measure id: `(Current eMeasure ID)`) has " +
			"a performanceRateId with an incorrect UUID of `(Incorrect UUID)`", true),
	QUALITY_MEASURE_ID_MISSING_SINGLE_PERFORMANCE_RATE(61, "A Performance Rate must contain a single "
			+ "Performance Rate UUID"),
	CPC_PLUS_TOO_FEW_QUALITY_MEASURE_CATEGORY(64, "CPC+ Submissions must have at least `(CPC+ measure group minimum)` " +
			"of the following `(CPC+ measure group label)` measures: `(Listing of valid measure ids)`", true),
	CPC_PLUS_TOO_FEW_QUALITY_MEASURES(65, "CPC+ Submissions must have at least `(Overall CPC+ measure minimum)` of " +
			"the following measures: `(Listing of all CPC+ measure ids)`.", true),
	CPC_PLUS_MISSING_SUPPLEMENTAL_CODE(66, "Missing the Supplemental Code `(Supplemental Data Code)` for eCQM measure " +
			"`(Measure Id)`'s Sub-population `(Sub Population)`", true),
	CPC_PLUS_SUPPLEMENTAL_DATA_MISSING_COUNT(67, "Must have one count for Supplemental Data `(Supplemental Data Code)` " +
			"on Sub-population `(Sub Population)` for eCQM measure `(Measure Id)`", true),
	CPC_PLUS_SUBMISSION_ENDED(68, "CPC+ Submission is after the end date `(Submission end date)`", true);


	private static final Map<Integer, ErrorCode> CODE_TO_VALUE = Arrays.stream(values())
			.collect(Collectors.toMap(ErrorCode::getCode, Function.identity()));
	private static final String VARIABLE_MARKER = "`\\(([^()]*)\\)`";
	private static Pattern replacePattern;

	private final int code;
	private final String message;
	private final boolean hasFormat;
	private final List<String> messageVariables;

	ErrorCode(int code, String message) {
		this(code, message, false);
	}

	ErrorCode(int code, String message, boolean hasFormat) {
		this.code = code;
		this.message = message;
		this.hasFormat = hasFormat;
		this.messageVariables = new ArrayList<>();
		initMessageMarkers(message);
	}

	private void initMessageMarkers(String message) {
		Matcher matcher = getPattern().matcher(message);
		while(matcher.find()) {
			messageVariables.add(matcher.group(1));
		}
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
			String formatted = subValues(arguments);
			return new FormattedErrorCode(this, formatted);
		}

		throw new IllegalStateException(this + " does not support formatting");
	}

	private String subValues(Object... arguments) {
		Map<String, String> valueSub = new HashMap<>();
		IntStream.range(0, arguments.length)
				.forEach(index -> valueSub.put(messageVariables.get(index), arguments[index].toString()));
		return new StrSubstitutor(valueSub, "`(", ")`").replace(message);
	}

	private static Pattern getPattern() {
		if (replacePattern == null) {
			replacePattern = Pattern.compile(VARIABLE_MARKER);
		}
		return replacePattern;
	}

	public static ErrorCode getByCode(int code) {
		return CODE_TO_VALUE.get(code);
	}
}