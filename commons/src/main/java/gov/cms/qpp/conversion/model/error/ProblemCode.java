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

	ENCODER_MISSING(1, "The system could not complete the request, please try again."),
	NOT_VALID_XML_DOCUMENT(2, "Contact you Health IT vendor to review your file and confirm "
		+ "it's properly formatted as an XML document."),
	UNEXPECTED_ERROR(3, "There was an unexpected system error during the file conversion. " + ServiceCenter.MESSAGE),
	UNEXPECTED_ENCODE_ERROR(4, "There was an unexpected error during the file encoding.  " + ServiceCenter.MESSAGE),
	NOT_VALID_QRDA_DOCUMENT(5, "Verify that your file is a QRDA III XML document and that it "
		+ "complies with the `(Submission year's)` implementation guide. `(Implementation guide link)`", true),
	MEASURE_GUID_MISSING(6, "Verify the measure GUID for `(Provided measure id)` against table 15 of the "
		+ "`(Submission year's)` Implementation Guide for valid measure GUIDs: " + DocumentationReference.MEASURE_IDS, true),
	CHILD_MEASURE_MISSING(7, "Review the measure section of your file to confirm it contains at least 1 measure. "),
	AGGREGATE_COUNT_VALUE_NOT_SINGULAR(8, "Review `(Parent element)`. It shows "
		+ "`(number of aggregate counts)` but it can only have 1.", true),
	AGGREGATE_COUNT_VALUE_NOT_INTEGER(9, "The aggregate count must be a whole number without decimals."),
	PI_MEASURE_PERFORMED_RNR_MEASURE_PERFORMED_EXACT(11, "Review this Promoting Interoperability reference "
		+ "for a missing required measure."),
	PI_MEASURE_PERFORMED_RNR_MEASURE_ID_NOT_SINGULAR(12, "Review this Promoting Interoperability measure for "
		+ "multiple measure IDs. There can only  be 1 measure ID."),
	DENOMINATOR_COUNT_INVALID(13, "The denominator count must be less than or equal to the initial population "
		+ "count for the measure population `(measure population id)`. You can check Table 15 of the "
		+ "`(Submission Year)` Implementation guide for valid measure GUIDs: " + DocumentationReference.MEASURE_IDS, true),
	POPULATION_CRITERIA_COUNT_INCORRECT(14, "The electronic measure id: `(Current eMeasure ID)` "
		+ "requires `(Number of Subpopulations required)` `(Type of Subpopulation required)`(s) but "
		+ "there are `(Number of Subpopulations existing)`", true),
	PI_NUMERATOR_DENOMINATOR_PARENT_NOT_PI_SECTION(15, "Review the Promoting Interoperability "
		+ "Numerator Denominator element. It must have a parent Promoting Interoperability Section."),
	PI_NUMERATOR_DENOMINATOR_MISSING_MEASURE_ID(16, "Review the Promoting Interoperability "
		+ "Numerator Denominator element. It must have a measure name ID"),
	PI_NUMERATOR_DENOMINATOR_MISSING_CHILDREN(17, "Review the Pomoting Interoperability "
		+ "Numerator Denominator element. it must have a child element."),
	PI_NUMERATOR_DENOMINATOR_VALIDATOR_EXACTLY_ONE_NUMERATOR_OR_DENOMINATOR_CHILD_NODE(18, "This Promoting "
		+ "Interoperability Numerator Denominator element requires exactly one `(Denominator|Numerator)` element child", true),
	PI_SECTION_MISSING_REPORTING_PARAMETER_ACT(22, "Review the Reporting Parameter Act in the "
		+ "Promoting Interoperability section. It must comply with the Implementation Guide: " + DocumentationReference.REPORTING_PARAMETERS_ACT),
	CLINICAL_DOCUMENT_MISSING_PI_OR_IA_OR_ECQM_CHILD(23, "Review the element \"Clinical Document.\" It must have "
		+ "at least one measure section or a child element of type Promoting Interoperability or Improvement Activities."),
	CLINICAL_DOCUMENT_MISSING_PROGRAM_NAME(24, "Review the QRDA III file. It must only have "
		+ "one program name from this list: `(list of valid program names)`", true),
	CLINICAL_DOCUMENT_INCORRECT_PROGRAM_NAME(25, "Review the Clinical Document for a valid program name "
		+ "from this list: `(list of valid program names)`.  `(program name)` is not valid.", true),
	CLINICAL_DOCUMENT_CONTAINS_DUPLICATE_PI_SECTIONS(26, "Review the QRDA III file "
		+ "for duplicate Promoting Interoperability sections."),
	CLINICAL_DOCUMENT_CONTAINS_DUPLICATE_IA_SECTIONS(27, "Review the QRDA III file "
		+ "for duplicate Improvement Activity sections."),
	CLINICAL_DOCUMENT_CONTAINS_DUPLICATE_ECQM_SECTIONS(28, "Review the QRDA III file "
		+ "for duplicate measure sections."),
	REPORTING_PARAMETERS_MUST_CONTAIN_SINGLE_PERFORMANCE_START(29, "The file must only have one performance period start. "
		+ "You can find more information on performance periods in the Implementation Guide: " + DocumentationReference.PERFORMANCE_PERIOD), //NOSONAR
	REPORTING_PARAMETERS_MUST_CONTAIN_SINGLE_PERFORMANCE_END(30, "The file must only have one performance period end. "
		+ "You can find more information on performance periods in the Implementation Guide: " + DocumentationReference.PERFORMANCE_PERIOD), //NOSONAR
	REPORTING_PARAMETERS_MISSING_PERFORMANCE_YEAR(31, "The file must have a performance year. "
		+ "You can find more information on performance periods in the Implementation Guide: " + DocumentationReference.PERFORMANCE_PERIOD), //NOSONAR
	QUALITY_MEASURE_SECTION_REQUIRED_REPORTING_PARAM_REQUIREMENT(32, "The Quality Measure section must only have one Reporting Parameter Act. "
		+ "You can find more information on performance periods in the Implementation Guide: " + DocumentationReference.PERFORMANCE_PERIOD), //NOSONAR
	PERFORMANCE_RATE_INVALID_VALUE(33, "The Performance Rate `(supplied value)` must be a decimal between 0 and 1.", true),
	PCF_CLINICAL_DOCUMENT_MISSING_PRACTICE_SITE_ADDRESS(34, "PCF submissions must have a practice site address. You can"
		+ "find more information on the `(Submission year's)` Implementation Guide: " + DocumentationReference.PRACTICE_SITE_ADDRESS, true),
	PCF_CLINICAL_DOCUMENT_ONLY_ONE_APM_ALLOWED(35, "Review the file. It must only have one Alternative Payment Model (APM) "
		+ "Entity Identifier. You can find more information in the Implementation Guide: " + DocumentationReference.IDENTIFIERS),
	PCF_CLINICAL_DOCUMENT_ONE_MEASURE_SECTION_REQUIRED(36, "Review the file. It must have at least one measure section."),
	PCF_QUALITY_MEASURE_ID_INVALID_PERFORMANCE_RATE_COUNT(37, "Review the performance rate(s) in the file. "
		+ "The number for measure `(Given measure id)` is `(Expected value)`", true),
	NUMERATOR_DENOMINATOR_CHILD_EXACT(39, "Review the aggregate count children for the Promoting Interoperability "
		+ "`(Numerator or Denominator)` element. It must have exactly one aggregate count element", true),
	NUMERATOR_DENOMINATOR_MUST_BE_INTEGER(41, "Review the Promoting Interoperability `(Numerator or Denominator)` "
		+ "element's aggregate value. '`(value)`' must be a whole number.", true),
	NUMERATOR_DENOMINATOR_INVALID_VALUE(42, "Review the Promoting Interoperability `(Numerator or Denominator)` "
		+ "element's aggregate value. '`(value)`' is not valid.", true),
	IA_SECTION_MISSING_IA_MEASURE(43, "The Improvement Activities section must have at least one Improvement Activity"),
	IA_SECTION_MISSING_REPORTING_PARAM(44, "The Improvement Activities section must have one Reporting Parameter Act. "
		+ "You can find more information on the Implementation Guide: " + DocumentationReference.REPORTING_PARAMETERS_ACT),
	IA_SECTION_WRONG_CHILD(45, "The Improvement Activities section must only contain "
		+ "Improvement Activities and a Reporting Parameter Act"),
	PCF_QUALITY_MEASURE_ID_MISSING_STRATA(48, "There's missing strata `(Reporting Stratum UUID)` for "
		+ "`(Current subpopulation type)` measure `(Current subpopulation UUID)`. You can find more information "
		+ "on the Implementation Guide: " + DocumentationReference.MEASURE_IDS, true),
	PCF_QUALITY_MEASURE_ID_STRATA_MISMATCH(49, "The amount of stratifications "
		+ "`(Current number of Reporting Stratifiers)` does not meet the `(Number of stratifiers required)` "
		+ "required for `(Current subpopulation type)` measure `(Current Subpopulation UUID)`. "
		+ "The strata required is: `(Expected strata uuid list)`. You can find more information "
		+ "on the Implementation Guide: " + DocumentationReference.MEASURE_IDS, true),
	IA_MEASURE_INCORRECT_CHILDREN_COUNT(50, "Review your data. An Improvement Activities performed "
		+ "measure reference and results must have exactly one measure performed child."),
	IA_MEASURE_INVALID_TYPE(51, "Review your data. The data for a performed measure is required and must be either a Y or an N."),
	MEASURE_PERFORMED_MISSING_AGGREGATE_COUNT(52, "The measure data with population id `(population id)` must have exactly one "
		+ "Aggregate Count. You can find more information on GUIDs on the Implementation Guide: " + DocumentationReference.MEASURE_IDS, true),
	MEASURE_DATA_VALUE_NOT_INTEGER(53, "The measure data with population id '`(population id)`' must be a whole number greater than "
		+ "or equal to 0. You can find more information on GUIDs on the Implementation Guide: " + DocumentationReference.MEASURE_IDS, true),
	PCF_PERFORMANCE_PERIOD_START(55, "A `(Program name)` performance period must start on " + DocumentationReference.PERFORMANCE_START_DATE
		+ ". You can find additional information on the Implementation Guide: " + DocumentationReference.PCF_SUBMISSIONS, true),
	PCF_PERFORMANCE_PERIOD_END(56, "A `(Program name)` performance period must end on " + DocumentationReference.PERFORMANCE_END_DATE
		+ ". You can find additional information on the Implementation Guide: " + DocumentationReference.PCF_SUBMISSIONS, true),
	QUALITY_MEASURE_ID_MISSING_SINGLE_MEASURE_POPULATION(57, "The reference results for the measure must have a "
		+ "single measure population"),
	QUALITY_MEASURE_ID_MISSING_SINGLE_MEASURE_TYPE(58, "The reference results for the measure must have "
		+ "a single measure type"),
	QUALITY_MEASURE_ID_INCORRECT_UUID(59, "The electronic measure id: `(Current eMeasure ID)` requires "
		+ "a `(Subpopulation type)` with the correct UUID of `(Correct uuid required)`. You can find additional "
		+ "information on the implementation guide: " + DocumentationReference.MEASURE_IDS, true),
	QUALITY_MEASURE_ID_MISSING_SINGLE_PERFORMANCE_RATE(61, "Review your data. A performance rate must "
		+ "contain a single numerator UUID reference."),
	PCF_CLINICAL_DOCUMENT_EMPTY_APM(62, "Review your data. The Alternative Payment Model (APM) Entity Identifier must "
		+ "not be empty. You can find additional information on the implementation guide: " + DocumentationReference.IDENTIFIERS),
	PCF_CLINICAL_DOCUMENT_INVALID_APM(63, "Review the Alternative Payment Model (APM) Entity Identifier.  You "
		+ "can find additional information on the implementation guide: " + DocumentationReference.IDENTIFIERS),
	PCF_MISSING_SUPPLEMENTAL_CODE(66, "There's missing data. Enter the `(Supplemental Type)` - "
		+ "`(Type Qualification)` supplemental data for code `(Supplemental Data Code)` for the "
		+ "Sub-population `(Sub Population)` of measure id `(Measure Id)`.", true),
	PCF_SUPPLEMENTAL_DATA_MISSING_COUNT(67, "Review measure id `(Measure Id)`. It must have one count for "
		+ "Supplemental Data `(Supplemental Data Code)` on Sub-population `(Sub Population)`.", true),
	PCF_SUBMISSION_ENDED(68, "Your submission for `(Program name)` was made after the submission "
		+ "deadline of `(Submission end date)` for `(Program name)` measure section. Your `(Program name)` QRDA III file "
		+ "has not been processed. Please contact `(Program name)` Support at `(PCF+ contact email)` for assistance.", true),
	INVALID_PERFORMANCE_PERIOD_FORMAT(69, "Review the `(Performance period start or end date)` format. "
		+ "Valid date formats are 2024-02-26, 2024/02/26T01:45:23, or 2024-02-26T01:45:23.123. You cn find more "
		+ "information on the implementation guide: " + DocumentationReference.PERFORMANCE_PERIOD, true),
	MISSING_OR_DUPLICATED_MEASURE_GUID(70, "Review the measure GUID for measure section, "
		+ "measure reference, and results. There must only be one GUID per measure. Refer to page 36 "
		+ "of the implementation guide: " + DocumentationReference.MEASURE_REFERENCE),
	MEASURES_RNR_WITH_DUPLICATED_MEASURE_GUID(71, "Review the file for duplicate GUIDs. "
		+ "Each measure section, measure reference, and results must have its own GUID."),
	PERFORMANCE_RATE_MISSING(72, "Contact your Health IT vendor. The QRDA III file is missing "
		+ "a performance rate. Performance rate is required for PCF reporting. You can find more information "
		+ "on page 17 of the implementation guide: " + DocumentationReference.REPORTING_PARAMETERS_ACT),
	VIRTUAL_GROUP_ID_REQUIRED(78, "Enter an entity ID for the program 'MIPS Virtual Group'."),
	MISSING_PII_VALIDATOR(79, "Enter a TIN number to verify the NPI/Alternative Payment Model (APM) combinations."),
	INCORRECT_API_NPI_COMBINATION(80, "Review NPI `(npi)` and TIN `(tin)`. This NPI/TIN combination is missing "
		+ "from the QRDA III file or is not in the `(program)` practitioner roster for `(apm)`. Ensure your submission "
		+ "contains all required NPI/TIN combinations and your `(program)` practitioner roster is up-to-date.", true),
	MEASURE_SECTION_MISSING_MEASURE(81, "At least one measure is required in a measure section"),
	TOO_MANY_ERRORS(82, "This QRDA III file shows 100 out of `(Error amount)` errors. Correct and re-submit the file. ", true),
	PCF_TIN_REQUIRED(84, "`(Program name)` QRDA-III Submissions require at least one TIN number.", true),
	PCF_INVALID_TIN(85, "`(Program name)` QRDA-III Submission TINs must be 9 numbers long.", true),
	PCF_MISSING_TIN(86, "The QRDA-III submission for `(Program name)` is missing a TIN. "
		+ "Ensure there is a TIN associated with every NPI submitted.", true),
	PCF_NPI_REQUIRED(87, "The QRDA-III submission for `(Program name)` must have at least one NPI number.", true),
	PCF_INVALID_NPI(88, "The NPIs for `(Program name)`'s QRDA-III submission must be 10 numbers long.",true ),
	PCF_MISSING_NPI(89, "The QRDA-III submission for `(Program name)` is missing an NPI. "
		+ "Ensure there is an NPI associated with every TIN submitted.", true),
	PCF_NO_IA_OR_PI(90, "The QRDA-III submission for `(Program name)` should not contain an "
		+ "Improvement Activities section. The Improvement Activities data will be ignored.", true),
	PCF_INVALID_NULL_PERFORMANCE_RATE(91, "Review the performance rate `(performanceRateUuid)` for measure "
		+ "`(measure id)`. A performance rate cannot be null unless the performance denominator is 0", true),
	PCF_PERFORMANCE_DENOM_LESS_THAN_ZERO(92, "Review the performance denominator for measure `(measureId)`. "
		+ "A performance rate cannot be null unless the performance denominator is 0.", true),
	PCF_NUMERATOR_GREATER_THAN_EITHER_DENOMINATORS(93, "Review numerator ID `(numeratorUuid)` for "
		+ "measure `(measure id)`. It has a count value that is greater than the denominator and/or the performance "
		+ "denominator (Denominator count - Denominator exclusion count - Denominator Exception count)", true),
	PCF_DENEX_GREATER_THAN_DENOMINATOR(94, "Review the denominator exclusion id `(denexUuid)` for "
		+ "measure `(measure id)`. It cannot have a greater value than the denominator.", true),
	MEASURE_SECTION_V5_REQUIRES_CATEGORY_SECTION(95, "The QRDA III file must contain one Category Section v5 with the extension 2020-12-01"),
	MISSING_API_TIN_NPI_FILE(96, "The APM to TIN/NPI Combination file is missing."),
	PCF_MISSING_CEHRT_ID(97, "The QRDA-III submissions for `(Program name)` must have a valid "
		+ "CMS EHR Certification ID (Valid Formats: XX15CXXXXXXXXXX)", true),
	PCF_ZERO_PERFORMANCE_RATE(98, "Review the performance rate. It must be of value Null Attribute (NA), not 0."),
	PCF_DUPLICATE_CEHRT(100, "Found more than one CMS EHR Certification ID in your file. The submission "
		+ "must have only one CMS EHR Certification ID."),
	PCF_DENOMINATOR_COUNT_INVALID(101, "The measure population `(measure population id)` for "
		+ "`(Program name)` needs the Denominator count to be equal to Initial population count. You can find "
		+ "additional information on table 15 of the implementation guide:" + DocumentationReference.MEASURE_IDS, true),
	PI_RESTRICTED_MEASURES(102, "The Promoting Interoperability section cannot contain "
		+ "PI_HIE_5 with PI_HIE_1, PI_LVOTC_1, PI_HIE_4, or PI_LVITC_2", false),
	PCF_TOO_FEW_QUALITY_MEASURE_CATEGORY(103, "The PCF submissions must have the `(PCF Measure minimum)` "
		+ "following measures: `(Listing of valid measure ids)`", true),
	PCF_MULTI_TIN_NPI_SINGLE_PERFORMER(105, "If multiple TINs/NPIs are submitted, each must be reported within a separate performer."),
	PCF_NO_PI(106, "Promoting Interoperability data should not be reported in a PCF QRDA III file."),
	PCF_MISSING_COMBINATION(107, "There's missing NPI/TIN combination. The NPI/TIN `(npi)`-`(tin)` was "
		+ "active on the PCF practitioner roster for `(apm)` during the performance year but was not found in the file. "
		+ "Ensure your submission contains all NPI/TIN combinations that were active on your roster at any point "
		+ "during the performance year. Your QRDA III file and/or roster may require updates. "
		+ "The QPP website doesn't have access to roster updates made after " + DocumentationReference.ROSTER_UPDATE_DATE + ". "
		+ "It's critical to ensure your roster is up to date and your QRDA III file contains all NPI/TIN values that were active "
		+ "on your roster during the performance year. Contact your health IT vendor if your QRDA III file requires updates. "
		+ "You can find instructions on updating rosters in the PCF Practice Management Guide: "
		+ "(https://cmmi.my.salesforce.com/sfc/p/#i0000000iryR/a/t00000028RsP/dMF_romOmf5VLe7p5lUj8vch11mPmELP6ZuyI16vS.Y).", true),
	PCF_INVALID_COMBINATION(108, "Found an unexpected NPI/TIN combination. The NPI/TIN "
		+ "`(npi)`-`(tin)` was reported in the file but does not exist at the practice or was not "
		+ "active on the PCF practitioner roster for `(apm)` during the performance year. "
		+ "Ensure your submission only contains NPI/TIN combinations that were active on your roster at "
		+ "any point during the performance year. Your QRDA III file and/or roster may require updates. "
		+ "Note: The QPP website does not have access to roster updates made after " + DocumentationReference.ROSTER_UPDATE_DATE + ". "
		+ "It's critical that you ensure your roster is up to date and your QRDA III file contains "
		+ "all NPI/TIN values that were active on your roster during the performance year. "
		+ "Please contact your health IT vendor if your QRDA III file requires updates. "
		+ "You can find instructions on how updating rosters in the PCF Practice Management Guide "
		+ "(https://cmmi.my.salesforce.com/sfc/p/#i0000000iryR/a/t00000028RsP/dMF_romOmf5VLe7p5lUj8vch11mPmELP6ZuyI16vS.Y).", true);

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
		static final String MESSAGE = "Contact the customer service center for assistance by email at QPP@cms.hhs.gov "
			+ "or by phone at 1-866-288-8292 (TRS: 711)";
	}

}
