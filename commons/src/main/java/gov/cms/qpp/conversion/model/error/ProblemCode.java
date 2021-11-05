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
public enum ProblemCode implements LocalizedProblem {

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
		+ " A single aggregate count value is required.", true),
	AGGREGATE_COUNT_VALUE_NOT_INTEGER(9, "Aggregate count value must be an integer"),
	PI_MEASURE_PERFORMED_RNR_MEASURE_PERFORMED_EXACT(11, "This PI Reference and Results is missing a required "
		+ "Measure Performed child"),
	PI_MEASURE_PERFORMED_RNR_MEASURE_ID_NOT_SINGULAR(12, "This PI Measure Performed Reference and Results requires "
		+ "a single Measure ID"),
	DENOMINATOR_COUNT_INVALID(13, "Denominator count must be less than or equal to Initial Population count "
			+ "for the measure population `(measure population id)`", true),
	POPULATION_CRITERIA_COUNT_INCORRECT(14,
			"The electronic measure id: `(Current eMeasure ID)` requires `(Number of Subpopulations required)` "
			+ "`(Type of Subpopulation required)`(s) but there are `(Number of Subpopulations existing)`", true),
	PI_NUMERATOR_DENOMINATOR_PARENT_NOT_PI_SECTION(15, "PI Numerator Denominator element should have a PI "
			+ "Section element as a parent"),
	PI_NUMERATOR_DENOMINATOR_MISSING_MEASURE_ID(16, "PI Numerator Denominator element does not contain a "
			+ "measure name ID"),
	PI_NUMERATOR_DENOMINATOR_MISSING_CHILDREN(17, "PI Numerator Denominator element does not have any child "
			+ "elements"),
	PI_NUMERATOR_DENOMINATOR_VALIDATOR_EXACTLY_ONE_NUMERATOR_OR_DENOMINATOR_CHILD_NODE(18, "This PI Numerator Denominator "
			+ "element requires exactly one `(Denominator|Numerator)` element child", true),
	PI_SECTION_MISSING_REPORTING_PARAMETER_ACT(22, "The PI Section must have one Reporting Parameter Act."
		+ " Please ensure the Reporting Parameters Act complies with the Implementation Guide (IG)."
		+ " Here is a link to the IG Reporting Parameter Act section: " + DocumentationReference.REPORTING_PARAMETERS_ACT),
	CLINICAL_DOCUMENT_MISSING_PI_OR_IA_OR_ECQM_CHILD(23, "Clinical Document element must have at least one child "
			+ "element of type PI, IA, or Measure section"),
	CLINICAL_DOCUMENT_MISSING_PROGRAM_NAME(24, "Clinical Document must have one and only one program name."
		+ " Valid program names are `(list of valid program names)`", true),
	CLINICAL_DOCUMENT_INCORRECT_PROGRAM_NAME(25, "The Clinical Document program name `(program name)` is not recognized. Valid "
		+ "program names are `(list of valid program names)`.", true),
	CLINICAL_DOCUMENT_CONTAINS_DUPLICATE_PI_SECTIONS(26, "Clinical Document contains duplicate PI sections"),
	CLINICAL_DOCUMENT_CONTAINS_DUPLICATE_IA_SECTIONS(27, "Clinical Document contains duplicate IA sections"),
	CLINICAL_DOCUMENT_CONTAINS_DUPLICATE_ECQM_SECTIONS(28, "Clinical Document contains duplicate Measure "
			+ "sections"),
	REPORTING_PARAMETERS_MUST_CONTAIN_SINGLE_PERFORMANCE_START(29, "Must have one and only one performance period "
			+ "start. Please see the Implementation Guide for information on the performance period here: " //NOSONAR
			+ DocumentationReference.PERFORMANCE_PERIOD),
	REPORTING_PARAMETERS_MUST_CONTAIN_SINGLE_PERFORMANCE_END(30, "Must have one and only one performance period end. "
			+ "Please see the Implementation Guide for information on the performance period here: " //NOSONAR
			+ DocumentationReference.PERFORMANCE_PERIOD),
	REPORTING_PARAMETERS_MISSING_PERFORMANCE_YEAR(31, "Must have a performance year. "
			+ "Please see the Implementation Guide for information on the performance period here: " //NOSONAR
			+ DocumentationReference.PERFORMANCE_PERIOD),
	QUALITY_MEASURE_SECTION_REQUIRED_REPORTING_PARAM_REQUIREMENT(32, "The Quality Measure Section must have "
			+ "exactly one Reporting Parameter Act. "
			+ "Please ensure the Reporting Parameters Act complies with the Implementation Guide (IG). "
			+ "Here is a link to the IG Reporting Parameter Act section: " + DocumentationReference.REPORTING_PARAMETERS_ACT),
	PERFORMANCE_RATE_INVALID_VALUE(33, "The Performance Rate `(supplied value)` is invalid. "
		+ "It must be a decimal between 0 and 1.", true),
	CPC_PCF_CLINICAL_DOCUMENT_MISSING_PRACTICE_SITE_ADDRESS(34, "CPC+/PCF submissions must contain a practice site address."
		+ " Please refer to the `(Submission year's)` IG for more details " + DocumentationReference.PRACTICE_SITE_ADDRESS
	    + " regarding practice site addresses.", true),
	CPC_PCF_CLINICAL_DOCUMENT_ONLY_ONE_APM_ALLOWED(35, "One and only one Alternative Payment Model (APM) Entity "
			+ "Identifier should be specified. Here is a link to the IG section on identifiers: " + DocumentationReference.IDENTIFIERS),
	CPC_PCF_CLINICAL_DOCUMENT_ONE_MEASURE_SECTION_REQUIRED(36, "CPC+/PCF submissions must contain one Measure section"),
	CPC_PCF_QUALITY_MEASURE_ID_INVALID_PERFORMANCE_RATE_COUNT(37, "CPC+/PCF submissions must contain correct number of performance rate(s). "
			+ "Correct Number is `(Expected value)` for measure `(Given measure id)`", true),
	NUMERATOR_DENOMINATOR_CHILD_EXACT(39,
			"This PI `(Numerator or Denominator)` element has an incorrect number of Aggregate Count children. A PI "
			+ "`(Numerator or Denominator)` must have exactly one Aggregate Count element", true),
	NUMERATOR_DENOMINATOR_MUST_BE_INTEGER(41,
			"This PI `(Numerator or Denominator)` element Aggregate Value '`(value)`' is not an integer", true),
	NUMERATOR_DENOMINATOR_INVALID_VALUE(42,
			"This PI `(Numerator or Denominator)` element Aggregate Value has an invalid value of '`(value)`'", true),
	IA_SECTION_MISSING_IA_MEASURE(43, "The IA Section must have at least one Improvement Activity"),
	IA_SECTION_MISSING_REPORTING_PARAM(44, "The IA Section must have one Reporting Parameter Act. "
			+ "Please ensure the Reporting Parameters Act complies with the Implementation Guide (IG). "
			+ "Here is a link to the IG Reporting Parameter Act section: " + DocumentationReference.REPORTING_PARAMETERS_ACT),
	IA_SECTION_WRONG_CHILD(45, "The IA Section must contain only Improvement Activities and a Reporting Parameter Act"),
	CPC_PCF_QUALITY_MEASURE_ID_MISSING_STRATA(48, "Missing strata `(Reporting Stratum UUID)` for "
			+ "`(Current subpopulation type)` measure `(Current subpopulation UUID)`. "
			+ "Here is a link to the IG valid Measure Ids section: " + DocumentationReference.MEASURE_IDS, true),
	CPC_PCF_QUALITY_MEASURE_ID_STRATA_MISMATCH(49,"Amount of stratifications `(Current number of "
			+ "Reporting Stratifiers)` does not meet expectations "
			+ "`(Number of stratifiers required)` for `(Current subpopulation type)` measure "
			+ "`(Current Subpopulation UUID)`. Expected strata: `(Expected strata uuid list)`. "
			+ "Please refer to the Implementation Guide for correct stratification UUID's here: "
			+ DocumentationReference.MEASURE_IDS, true),
	IA_MEASURE_INCORRECT_CHILDREN_COUNT(50, "An IA performed measure reference and results must "
			+ "have exactly one measure performed child"),
	IA_MEASURE_INVALID_TYPE(51, "A single measure performed value is required and must be either a Y or an N."),
	MEASURE_PERFORMED_MISSING_AGGREGATE_COUNT(52, "The measure data with population id '`(population id)`' must have exactly one "
			+ "Aggregate Count.", true),
	MEASURE_DATA_VALUE_NOT_INTEGER(53, "Measure data with population id '`(population id)`' "
			+ "must be a whole number greater than or equal to 0", true),
	CPC_PCF_PERFORMANCE_PERIOD_START(55, "A CPC Plus or PCF Performance period start must be 01/01/2021. "
			+ "Please refer to the IG for more information here: " + DocumentationReference.CPC_PLUS_SUBMISSIONS),
	CPC_PCF_PERFORMANCE_PERIOD_END(56, "A CPC Plus or PCF Performance period end must be 12/31/2021. "
			+ "Please refer to the IG for more information here: " + DocumentationReference.CPC_PLUS_SUBMISSIONS),
	QUALITY_MEASURE_ID_MISSING_SINGLE_MEASURE_POPULATION(57, "The measure reference results must have a single "
			+ "measure population"),
	QUALITY_MEASURE_ID_MISSING_SINGLE_MEASURE_TYPE(58, "The measure reference results must have a single "
			+ "measure type"),
	QUALITY_MEASURE_ID_INCORRECT_UUID(59, "The electronic measure id: `(Current eMeasure ID)` requires a "
			+ "`(Subpopulation type)` with the correct UUID of `(Correct uuid required)`. Here is a link to the IG "
			+ "containing all the valid measure ids: " + DocumentationReference.MEASURE_IDS, true),
	QUALITY_MEASURE_ID_MISSING_SINGLE_PERFORMANCE_RATE(61, "A Performance Rate must contain a single "
			+ "Numerator UUID reference."),
	CPC_PCF_CLINICAL_DOCUMENT_EMPTY_APM(62, "The Alternative Payment Model (APM) Entity Identifier must not be empty. "
			+ "Here is a link to the IG section on identifiers: " + DocumentationReference.IDENTIFIERS),
	CPC_PCF_CLINICAL_DOCUMENT_INVALID_APM(63, "The Alternative Payment Model (APM) Entity Identifier is not valid. "
			+ " Here is a link to the IG section on identifiers: " + DocumentationReference.IDENTIFIERS),
	CPC_PLUS_TOO_FEW_QUALITY_MEASURE_CATEGORY(64, "CPC+ or PCF Submissions must have at least `(CPC+ measure group minimum)` "
			+ "of the following `(CPC+ measure group label)` measures: `(Listing of valid measure ids)`", true),
	CPC_PCF_PLUS_MISSING_SUPPLEMENTAL_CODE(66, "Missing the `(Supplemental Type)` - `(Type Qualification)` supplemental data for code "
		+ "`(Supplemental Data Code)` for the measure id `(Measure Id)`'s Sub-population `(Sub Population)`", true),
	CPC_PCF_PLUS_SUPPLEMENTAL_DATA_MISSING_COUNT(67, "Must have one count for Supplemental Data `(Supplemental Data Code)` "
		+ "on Sub-population `(Sub Population)` for the measure id `(Measure Id)`", true),
	CPC_PCF_PLUS_SUBMISSION_ENDED(68, "Your CPC+ or PCF submission was made after the CPC+/PCF Measure section submission deadline of "
		+ "`(Submission end date)`. Your CPC+ QRDA III file has not been processed. Please contact CPC+ Support at "
		+ "`(CPC+ contact email)` for assistance.", true),
	INVALID_PERFORMANCE_PERIOD_FORMAT(69, "`(Performance period start or end date)` is an invalid date format. "
		+ "Please use a standard ISO date format. "
		+ "Example valid values are 2019-02-26, 2019/02/26T01:45:23, or 2019-02-26T01:45:23.123. "
		+ "Please see the Implementation Guide for information on the performance period here: "
		+ DocumentationReference.PERFORMANCE_PERIOD, true),
	MISSING_OR_DUPLICATED_MEASURE_GUID(70, "The measure section measure reference and results has an incorrect number of "
		+ "measure GUID supplied. Please ensure that only one measure GUID is provided per measure."),
	MEASURES_RNR_WITH_DUPLICATED_MEASURE_GUID(71, "Two or more different measure section measure reference and results have "
		+ "the same measure GUID. Please ensure that each measure section measure reference and results do not have "
		+ "the same measure GUID."),
	PERFORMANCE_RATE_MISSING(72, "The Performance Rate is missing"),
	VIRTUAL_GROUP_ID_REQUIRED(78, "The Program 'Mips Virtual Group' was found. The required entity id for this "
		+ "program name was missing. Please provide a virtual group identifier with the 'Mips Virtual Group' program name."),
	MISSING_PII_VALIDATOR(79, "There is no TIN validator present, so NPI/Alternative Payment Model (APM) "
		+ "combinations cannot be verified"),
	INCORRECT_API_NPI_COMBINATION(80, "NPI `(npi)` and TIN `(tin)` are not reported as expected. "
		+ "This NPI/TIN combination is missing from the QRDA III file or is not in the CPC+ Practitioner Roster for `(apm)`."
		+ " Please ensure your submission contains all required NPI/TIN combinations and your CPC+ Practitioner Roster is up-to-date.", true),
	MEASURE_SECTION_MISSING_MEASURE(81, "At least one measure is required in a measure section"),
	TOO_MANY_ERRORS(82, "There are too many errors associated with this QRDA-III file. Showing 100 out of `(Error amount)` errors."
		+ " Please fix the given errors and re-submit", true),
	CPC_PCF_PLUS_TIN_REQUIRED(84, "CPC+ or PCF QRDA-III Submissions require at least one TIN to be present."),
	CPC_PCF_PLUS_INVALID_TIN(85, "CPC+ or PCF QRDA-III Submission TINs require a 9 digit numerical value"),
	CPC_PCF_PLUS_MISSING_TIN(86, "This CPC+ or PCF QRDA-III submission is missing a TIN. Please ensure there is a TIN associated with every "
		+ "NPI submitted"),
	CPC_PCF_PLUS_NPI_REQUIRED(87, "CPC+ or PCF QRDA-III Submissions require at least one NPI to be present"),
	CPC_PCF_PLUS_INVALID_NPI(88, "CPC+ or PCF QRDA-III Submission NPIs require a 10 digit numerical value"),
	CPC_PCF_PLUS_MISSING_NPI(89, "This CPC+ or PCF QRDA-III submission is missing a NPI. Please ensure there is an NPI associated with "
		+ "every TIN submitted"),
	CPC_PCF_PLUS_NO_IA_OR_PI(90, "CPC+ or PCF QRDA-III submissions must not contain an IA or PI section"),
	CPC_PCF_PLUS_INVALID_NULL_PERFORMANCE_RATE(91, "The performance rate `(performanceRateUuid)` has an invalid null value. "
		+ "A performance rate cannot be null unless the performance denominator is 0", true),
	CPC_PCF_PLUS_PERFORMANCE_DENOM_LESS_THAN_ZERO(92, "The performance denominator for measure `(measureId)` was less than 0. "
		+ "A performance rate cannot be null unless the performance denominator is 0", true),
	CPC_PCF_PLUS_NUMERATOR_GREATER_THAN_EITHER_DENOMINATORS(93, "The numerator id `(numeratorUuid)` has a count value that is "
		+ "greater than the denominator and/or the performance denominator "
		+ "(Denominator count - Denominator exclusion count - Denominator Exception count)", true),
	CPC_PCF_PLUS_DENEX_GREATER_THAN_DENOMINATOR(94, "The denominator exclusion id `(denexUuid)` has a count value that is greater than the "
		+ "denominator. The Denominator exclusion cannot be a greater value than the denominator.", true),
	MEASURE_SECTION_V4_REQUIRED(95, "The Clinical Document must contain one Measure Section v4 with the extension 2017-06-01"),
	MISSING_API_TIN_NPI_FILE(96, "The APM to TIN/NPI Combination file is missing."),
	CPC_PCF_MISSING_CEHRT_ID(97, "CPC+ or PCF QRDA-III Submissions require a valid Cehrt ID (Valid Format: XX15EXXXXXXXXXX)"),
	CPC_PCF_PLUS_ZERO_PERFORMANCE_RATE(98, "The performance rate cannot have a value of 0 and must be of value Null Attribute (NA)."),
	CPC_PCF_PLUS_DUPLICATE_CEHRT(100, "More than one Cehrt ID was found. Please submit with only one Cehrt id."),
	CPC_PCF_PLUS_DENOMINATOR_COUNT_INVALID(101, "Denominator count must be equal to Initial Population count for CPC Plus measure population `(measure population id)`.", true),
	PI_RESTRICTED_MEASURES(102, "A PI section cannot contain PI_HIE_5 with PI_HIE_1, PI_LVOTC_1, PI_HIE_4, or PI_LVITC_2", false),
	PCF_TOO_FEW_QUALITY_MEASURE_CATEGORY(103, "PCF Submissions must have at least `(PCF Measure minimum)` "
		+ "of the following measures: `(Listing of valid measure ids)`", true),
	CPC_PLUS_NO_PI(104, "The file contains Promoting Interoperability (PI) data; PI data must not be reported for CPC+"),
	PCF_CPC_MULTI_TIN_NPI_SINGLE_PERFORMER(105, "If multiple TINs/NPIs are submitted, each must be reported within a separate performer"),
	PCF_NO_PI(106, "PI submissions are not allowed within PCF")
	;

	private static final Map<Integer, ProblemCode> CODE_TO_VALUE = Arrays.stream(values())
			.collect(Collectors.toMap(ProblemCode::getCode, Function.identity()));
	public static final String CT_LABEL = "CT - ";

	private final int code;
	private final String message;
	private final boolean hasFormat;
	private final List<String> messageVariables;

	ProblemCode(int code, String message) {
		this(code, message, false);
	}

	ProblemCode(int code, String message, boolean hasFormat) {
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
	public final ProblemCode getProblemCode() {
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
	public final LocalizedProblem format(Object... arguments) {
		if (hasFormat) {
			String formatted = subValues(arguments);
			return new FormattedProblemCode(this, formatted);
		}

		throw new IllegalStateException(this + " does not support formatting");
	}

	private String subValues(Object... arguments) {
		Map<String, String> valueSub = new HashMap<>();
		IntStream.range(0, arguments.length).forEach(index ->
			valueSub.put(messageVariables.get(index), arguments[index].toString()));
		return new StringSubstitutor(valueSub, "`(", ")`").replace(getMessage());
	}

	public static ProblemCode getByCode(int code) {
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
