package gov.cms.qpp.conversion.model.error;

import org.apache.commons.text.StringSubstitutor;

import gov.cms.qpp.conversion.DocumentationReference;

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
	NOT_VALID_XML_DOCUMENT(2, "The file is not a valid XML document. The file you are submitting is not a "
			+ "properly formatted XML document. Please check your document to ensure proper formatting."),
	UNEXPECTED_ERROR(3, "Unexpected exception occurred during conversion. " + ServiceCenter.MESSAGE),
	UNEXPECTED_ENCODE_ERROR(4, "Unexpected exception occurred during encoding. " + ServiceCenter.MESSAGE),
	NOT_VALID_QRDA_DOCUMENT(5, "The file is not a QRDA-III XML document. "
		+ "Please ensure that the submission complies with the `(Submission year's)` implementation guide. "
		+ "`(Implementation guide link)`", true),
	MEASURE_GUID_MISSING(6, "The measure GUID supplied `(Provided measure id)` is invalid. Please see the `(Submission year's)` IG "
			+ DocumentationReference.MEASURE_IDS + " for valid measure GUIDs.", true),
	CHILD_MEASURE_MISSING(7, "The measure reference results must have at least one measure. "
			+ "Please review the measures section of your file as it cannot be empty."),
	AGGREGATE_COUNT_VALUE_NOT_SINGULAR(8, "The `(Parent element)` has `(number of aggregate counts)` aggregate count values."
		+ " A single aggregate count value is required. ", true),
	AGGREGATE_COUNT_VALUE_NOT_INTEGER(9, "Aggregate count value must be an integer"),
	ACI_MEASURE_PERFORMED_RNR_MEASURE_PERFORMED_EXACT(11, "This ACI Reference and Results is missing a required "
		+ "Measure Performed child"),
	ACI_MEASURE_PERFORMED_RNR_MEASURE_ID_NOT_SINGULAR(12, "This ACI Measure Performed Reference and Results requires "
		+ "a single Measure ID"),
	DENOMINATOR_COUNT_INVALID(13, "Denominator count must be less than or equal to Initial Population count "
			+ "for a measure that is a proportion measure"),
	POPULATION_CRITERIA_COUNT_INCORRECT(14,
			"The electronic measure id: `(Current eMeasure ID)` requires `(Number of Subpopulations required)` "
			+ "`(Type of Subpopulation required)`(s) but there are `(Number of Subpopulations existing)`", true),
	ACI_NUMERATOR_DENOMINATOR_PARENT_NOT_ACI_SECTION(15, "ACI Numerator Denominator element should have an ACI "
			+ "Section element as a parent"),
	ACI_NUMERATOR_DENOMINATOR_MISSING_MEASURE_ID(16, "ACI Numerator Denominator element does not contain a "
			+ "measure name ID"),
	ACI_NUMERATOR_DENOMINATOR_MISSING_CHILDREN(17, "ACI Numerator Denominator element does not have any child "
			+ "elements"),
	ACI_NUMERATOR_DENOMINATOR_VALIDATOR_EXACTLY_ONE_NUMERATOR_OR_DENOMINATOR_CHILD_NODE(18, "This ACI Numerator Denominator "
			+ "element requires exactly one `(Denominator|Numerator)` element child", true),
	ACI_SECTION_MISSING_REPORTING_PARAMETER_ACT(22, "The ACI Section must have one Reporting Parameter Act."
		+ " Please ensure the Reporting Parameters Act complies with the Implementation Guide (IG)."
		+ " Here is a link to the IG Reporting Parameter Act section: " + DocumentationReference.REPORTING_PARAMETERS_ACT),
	CLINICAL_DOCUMENT_MISSING_ACI_OR_IA_OR_ECQM_CHILD(23, "Clinical Document element must have at least one child "
			+ "element of type ACI, IA, or Measure section"),
	CLINICAL_DOCUMENT_MISSING_PROGRAM_NAME(24, "Clinical Document must have one and only one program name."
		+ " Valid program names are `(list of valid program names)`", true),
	CLINICAL_DOCUMENT_INCORRECT_PROGRAM_NAME(25, "The Clinical Document program name `(program name)` is not recognized. Valid "
		+ "program names are `(list of valid program names)`.", true),
	CLINICAL_DOCUMENT_CONTAINS_DUPLICATE_ACI_SECTIONS(26, "Clinical Document contains duplicate ACI sections"),
	CLINICAL_DOCUMENT_CONTAINS_DUPLICATE_IA_SECTIONS(27, "Clinical Document contains duplicate IA sections"),
	CLINICAL_DOCUMENT_CONTAINS_DUPLICATE_ECQM_SECTIONS(28, "Clinical Document contains duplicate Measure "
			+ "sections"),
	REPORTING_PARAMETERS_MUST_CONTAIN_SINGLE_PERFORMANCE_START(29, "Must have one and only one performance period "
			+ "start. Please see the Implementation Guide for information on the performance period here: "
			+ DocumentationReference.PERFORMANCE_PERIOD),
	REPORTING_PARAMETERS_MUST_CONTAIN_SINGLE_PERFORMANCE_END(30, "Must have one and only one performance period end. "
			+ "Please see the Implementation Guide for information on the performance period here: "
			+ DocumentationReference.PERFORMANCE_PERIOD),
	REPORTING_PARAMETERS_MISSING_PERFORMANCE_YEAR(31, "Must have a performance year. "
			+ "Please see the Implementation Guide for information on the performance period here: "
			+ DocumentationReference.PERFORMANCE_PERIOD),
	QUALITY_MEASURE_SECTION_REQUIRED_REPORTING_PARAM_REQUIREMENT(32, "The Quality Measure Section must have "
			+ "exactly one Reporting Parameter Act. "
			+ "Please ensure the Reporting Parameters Act complies with the Implementation Guide (IG). "
			+ "Here is a link to the IG Reporting Parameter Act section: " + DocumentationReference.REPORTING_PARAMETERS_ACT),
	PERFORMANCE_RATE_INVALID_VALUE(33, "Must enter a valid Performance Rate value"),
	CPC_CLINICAL_DOCUMENT_MISSING_PRACTICE_SITE_ADDRESS(34, "Must contain a practice site address for CPC+ "
			+ "conversions"),
	CPC_CLINICAL_DOCUMENT_ONLY_ONE_APM_ALLOWED(35, "One and only one Alternative Payment Model (APM) Entity "
			+ "Identifier should be specified"),
	CPC_CLINICAL_DOCUMENT_ONE_MEASURE_SECTION_REQUIRED(36, "CPC+ submissions must contain one Measure section"),
	CPC_QUALITY_MEASURE_ID_INVALID_PERFORMANCE_RATE_COUNT(37, "Must contain correct number of performance rate(s). "
			+ "Correct Number is `(Expected value)`", true),
	NUMERATOR_DENOMINATOR_MISSING_CHILDREN(38,
			"This `(Numerator or Denominator)` Node does not have any child Nodes", true),
	NUMERATOR_DENOMINATOR_CHILD_EXACT(39,
			"This `(Numerator or Denominator)` Node must have exactly one Aggregate Count node", true),
	NUMERATOR_DENOMINATOR_MUST_BE_INTEGER(41,
			"This `(Numerator or Denominator)` Node Aggregate Value is not an integer", true),
	NUMERATOR_DENOMINATOR_INVALID_VALUE(42,
			"This `(Numerator or Denominator)` Node Aggregate Value has an invalid value", true),
	IA_SECTION_MISSING_IA_MEASURE(43, "The IA Section must have at least one Improvement Activity"),
	IA_SECTION_MISSING_REPORTING_PARAM(44, "The IA Section must have one Reporting Parameter Act. "
			+ "Please ensure the Reporting Parameters Act complies with the Implementation Guide (IG). "
			+ "Here is a link to the IG Reporting Parameter Act section: " + DocumentationReference.REPORTING_PARAMETERS_ACT),
	IA_SECTION_WRONG_CHILD(45, "The IA Section must contain only Improvement Activity and a Reporting Parameter Act"),
	NPI_TIN_COMBINATION_MISSING_CLINICAL_DOCUMENT(46, "Clinical Document Node is required"),
	CPC_QUALITY_MEASURE_ID_MISSING_STRATA(48, "Missing strata `(Reporting Stratum UUID)` for "
			+ "`(Current subpopulation type)` measure `(Current subpopulation UUID)`", true),
	CPC_QUALITY_MEASURE_ID_STRATA_MISMATCH(49,"Amount of stratifications `(Current number of "
			+ "Reporting Stratifiers)` does not meet expectations "
			+ "`(Number of stratifiers required)` for `(Current subpopulation type)` measure "
			+ "`(Current Subpopulation UUID)`. Expected strata: `(Expected strata uuid list)`", true),
	IA_MEASURE_INCORRECT_CHILDREN_COUNT(50, "Measure performed must have exactly one child."),
	IA_MEASURE_INVALID_TYPE(51, "A single measure performed value is required and must be either a Y or an N."),
	MEASURE_PERFORMED_MISSING_AGGREGATE_COUNT(52, "Measure data must have exactly one Aggregate Count."),
	MEASURE_DATA_VALUE_NOT_INTEGER(53, "Measure data must be a positive integer value"),
	CPC_NPI_TIN_COMBINATION_MISSING_NPI_TIN_COMBINATION(54, "Must have at least one NPI/TIN combination"),
	CPC_PERFORMANCE_PERIOD_START_JAN12017(55, "Must be 01/01/2017"),
	CPC_PERFORMANCE_PERIOD_END_DEC312017(56, "Must be 12/31/2017"),
	QUALITY_MEASURE_ID_MISSING_SINGLE_MEASURE_POPULATION(57, "The measure reference results must have a single "
			+ "measure population"),
	QUALITY_MEASURE_ID_MISSING_SINGLE_MEASURE_TYPE(58, "The measure reference results must have a single "
			+ "measure type"),
	QUALITY_MEASURE_ID_INCORRECT_UUID(59, "The electronic measure id: `(Current eMeasure ID)` requires a "
			+ "`(Subpopulation type)` with the correct UUID of `(Correct uuid required)`", true),
	QUALITY_MEASURE_ID_INCORRECT_PERFORMANCE_UUID(60, "The electronic measure id: `(Current eMeasure ID)` has "
			+ "a performanceRateId with an incorrect UUID of `(Incorrect UUID)`", true),
	QUALITY_MEASURE_ID_MISSING_SINGLE_PERFORMANCE_RATE(61, "A Performance Rate must contain a single "
			+ "Performance Rate UUID"),
	CPC_CLINICAL_DOCUMENT_EMPTY_APM(62, "The Alternative Payment Model (APM) Entity Identifier must not be empty"),
	CPC_CLINICAL_DOCUMENT_INVALID_APM(63, "The Alternative Payment Model (APM) Entity Identifier is not valid"),
	CPC_PLUS_TOO_FEW_QUALITY_MEASURE_CATEGORY(64, "CPC+ Submissions must have at least `(CPC+ measure group minimum)` "
			+ "of the following `(CPC+ measure group label)` measures: `(Listing of valid measure ids)`", true),
	CPC_PLUS_TOO_FEW_QUALITY_MEASURES(65, "CPC+ Submissions must have at least `(Overall CPC+ measure minimum)` of "
		+ "the following measures: `(Listing of all CPC+ measure ids)`.", true),
	CPC_PLUS_MISSING_SUPPLEMENTAL_CODE(66, "Missing the `(Supplemental Type)` - `(Type Qualification)` supplemental data for code "
		+ "`(Supplemental Data Code)` for the measure id `(Measure Id)`'s Sub-population `(Sub Population)`", true),
	CPC_PLUS_SUPPLEMENTAL_DATA_MISSING_COUNT(67, "Must have one count for Supplemental Data `(Supplemental Data Code)` "
		+ "on Sub-population `(Sub Population)` for the measure id `(Measure Id)`", true),
	CPC_PLUS_SUBMISSION_ENDED(68, "Your CPC+ submission was made after the CPC+ Measure section submission deadline of "
		+ "`(Submission end date)`. Your CPC+ QRDA III file has not been processed. Please contact CPC+ Support at "
		+ "`(CPC+ contact email)` for assistance.", true),
	INVALID_PERFORMANCE_PERIOD_FORMAT(69, "`(Performance period start or end date)` is an invalid date format. "
		+ "Please use a standard ISO date format. "
		+ "Example valid values are 2017-02-26, 2017/02/26T01:45:23, or 2017-02-26T01:45:23.123", true);

	private static final Map<Integer, ErrorCode> CODE_TO_VALUE = Arrays.stream(values())
			.collect(Collectors.toMap(ErrorCode::getCode, Function.identity()));
	public static final String CT_LABEL = "CT - ";

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
		Matcher matcher = VariableMarker.REPLACE_PATTERN.matcher(message);
		while (matcher.find()) {
			messageVariables.add(matcher.group(1));
		}
	}

	/**
	 * Gets the message associated with this error code
	 */
	public final String getMessage() {
		return CT_LABEL + message;
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
		IntStream.range(0, arguments.length).forEach(index ->
			valueSub.put(messageVariables.get(index), arguments[index].toString()));
		return new StringSubstitutor(valueSub, "`(", ")`").replace(getMessage());
	}

	public static ErrorCode getByCode(int code) {
		return CODE_TO_VALUE.get(code);
	}

	private static final class VariableMarker {
		static final Pattern REPLACE_PATTERN = Pattern.compile("`\\(([^()]*)\\)`");
	}

	private static final class ServiceCenter {
		static final String MESSAGE = "Please contact the Service Center for assistance via phone at "
				+ "1-866-288-8292 or TTY: 1-877-715-6222, or by emailing QPP@cms.hhs.gov";
	}
}