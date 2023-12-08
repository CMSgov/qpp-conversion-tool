package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.correlation.PathCorrelator;
import gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.LocalizedProblem;
import gov.cms.qpp.conversion.model.error.ProblemCode;
import gov.cms.qpp.conversion.util.EnvironmentHelper;

import org.apache.commons.lang3.StringUtils;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.located.LocatedElement;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import java.time.Clock;
import java.time.Year;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.Collectors;


/**
 * Validates the Clinical Document level node for the given program: PCF
 * Using the same validation of CPC+
 */
@Validator(value = TemplateId.CLINICAL_DOCUMENT, program = Program.PCF)
public class PcfClinicalDocumentValidator extends NodeValidator {

	public PcfClinicalDocumentValidator(Context context) {
		super(context);
	}

	/**
	 * Constant end date name
	 */
	static final String END_DATE_VARIABLE = "CPC_END_DATE";
	/**
	 * Eastern time zone
	 */
	static final ZoneId EASTERN_TIME_ZONE = ZoneId.of("US/Eastern");
	/**
	 * Constant end date format
	 */
	static final DateTimeFormatter OUTPUT_END_DATE_FORMAT = DateTimeFormatter.ofPattern("MMMM dd, yyyy - HH:mm:ss")
		.withZone(EASTERN_TIME_ZONE);
	/**
	 * Constant cpc end date format
	 */
	static final DateTimeFormatter INPUT_END_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd - HH:mm:ss")
		.withZone(EASTERN_TIME_ZONE);

	/**
	 * Constant default email contact
	 */
	static final String DEFAULT_CPC_PLUS_CONTACT_EMAIL = "cpcplus@telligen.com";
	/**
	 * Constant contact email name
	 */
	static final String CPC_PLUS_CONTACT_EMAIL = "CPC_PLUS_CONTACT_EMAIL";

	//LocalDate.now() creates extra unneeded clock objects before Java 9.
	//It also uses the system clock, rather than Eastern Time.
	private static final Clock CLOCK = Clock.system(ZoneId.of("US/Eastern"));

	/**
	 * Validates a single clinical document node
	 *
	 * @param node The node to validate.
	 */
	@Override
	protected void performValidation(Node node) {
		validateSubmissionDate(node);
		String programName = node.getValue(ClinicalDocumentDecoder.PROGRAM_NAME).toUpperCase(Locale.ROOT);

		LocalizedProblem addressError = ProblemCode.PCF_CLINICAL_DOCUMENT_MISSING_PRACTICE_SITE_ADDRESS
			.format(Context.REPORTING_YEAR);

		checkErrors(node)
			.valueIsNotEmpty(ProblemCode.PCF_TIN_REQUIRED.format(programName),
				ClinicalDocumentDecoder.TAX_PAYER_IDENTIFICATION_NUMBER)
			.listValuesAreValid(
				ProblemCode.PCF_INVALID_TIN.format(programName),
				ClinicalDocumentDecoder.TAX_PAYER_IDENTIFICATION_NUMBER, 9)
			.valueIsNotEmpty(ProblemCode.PCF_NPI_REQUIRED.format(programName),
				ClinicalDocumentDecoder.NATIONAL_PROVIDER_IDENTIFIER)
			.listValuesAreValid(
				ProblemCode.PCF_INVALID_NPI.format(programName),
				ClinicalDocumentDecoder.NATIONAL_PROVIDER_IDENTIFIER, 10)
			.value(addressError, ClinicalDocumentDecoder.PRACTICE_SITE_ADDR)
			.childMinimum(ProblemCode.PCF_CLINICAL_DOCUMENT_ONE_MEASURE_SECTION_REQUIRED,
				1, TemplateId.MEASURE_SECTION_V5)
			.singleValue(ProblemCode.PCF_CLINICAL_DOCUMENT_ONLY_ONE_APM_ALLOWED, ClinicalDocumentDecoder.PCF_ENTITY_ID)
			.valueIsNotEmpty(ProblemCode.PCF_CLINICAL_DOCUMENT_EMPTY_APM, ClinicalDocumentDecoder.PCF_ENTITY_ID)
			.childExact(ProblemCode.PCF_NO_PI, 0, TemplateId.PI_SECTION_V3)
			.listValuesAreInts(ProblemCode.PCF_INVALID_NPI.format(node.getValue(ClinicalDocumentDecoder.PROGRAM_NAME)),
				ClinicalDocumentDecoder.NATIONAL_PROVIDER_IDENTIFIER);

		validateApmEntityId(node, ClinicalDocumentDecoder.PCF_ENTITY_ID, false);
		checkWarnings(node)
			.doesNotHaveChildren(ProblemCode.PCF_NO_IA_OR_PI.format(programName), TemplateId.IA_SECTION_V3, TemplateId.PI_SECTION_V3);

		if (hasTinAndNpi(node)) {
			validateNumberOfTinsAndNpis(node, programName);
			validateApmNpiCombination(node);
			validateSingleTinNpiPerPerformer(node);
		}
		validateCehrtId(node, programName);
	}

	/**
	 * Validates the APM Entity ID in the given node is valid.
	 *
	 * A validation error is created if the APM Entity ID is invalid.
	 *
	 * @param node The node to validate
	 * @param key  Identifier of the apm entity id value map
	 */
	protected void validateApmEntityId(Node node, String key, boolean cpcPlus) {
		String apmEntityId = node.getValue(key);

		if (StringUtils.isEmpty(apmEntityId)) {
			return;
		}

		if (cpcPlus) {
			if (!context.getApmEntityIds().cpcIdExists(apmEntityId)) {
				addError(Detail.forProblemAndNode(ProblemCode.PCF_CLINICAL_DOCUMENT_INVALID_APM, node));
			}
		} else {
			if (!context.getApmEntityIds().pcfIdExists(apmEntityId)) {
				addError(Detail.forProblemAndNode(ProblemCode.PCF_CLINICAL_DOCUMENT_INVALID_APM, node));
			}
		}
	}

	/**
	 * Checks to see if the node has a tin and npi
	 *
	 * @param node
	 * @return
	 */
	private boolean hasTinAndNpi(final Node node) {
		return null != node.getValue(ClinicalDocumentDecoder.NATIONAL_PROVIDER_IDENTIFIER) &&
			null != node.getValue(ClinicalDocumentDecoder.TAX_PAYER_IDENTIFICATION_NUMBER);
	}

	/**
	 * Validates to ensure that for every TIN there is an NPI submitted to it.
	 *
	 * @param node
	 */
	private void validateNumberOfTinsAndNpis(final Node node, final String programName) {
		int numOfTins = Arrays.asList(
			node.getValue(ClinicalDocumentDecoder.TAX_PAYER_IDENTIFICATION_NUMBER).split(",")).size();
		int numOfNpis = Arrays.asList(
			node.getValue(ClinicalDocumentDecoder.NATIONAL_PROVIDER_IDENTIFIER).split(",")).size();
		if (numOfTins > numOfNpis) {
			addError(Detail.forProblemAndNode(ProblemCode.PCF_MISSING_NPI.format(programName), node));
		} else if (numOfNpis > numOfTins) {
			addError(Detail.forProblemAndNode(ProblemCode.PCF_MISSING_TIN
				.format(node.getValue(ClinicalDocumentDecoder.PROGRAM_NAME)), node));
		}
	}

	private void validateApmNpiCombination(Node node) {
		context.getPiiValidator().validateApmTinNpiCombination(node, this);
	}

	private void validateCehrtId(Node node, String programName) {
		String cehrtId = node.getValue(ClinicalDocumentDecoder.CEHRT);
		if(cehrtId == null || cehrtId.length() != 15 || !cehrtFormat(cehrtId.substring(2, 5))) {
			addError(Detail.forProblemAndNode(ProblemCode.PCF_MISSING_CEHRT_ID.format(programName), node));
		}
		List<String> duplicateCehrts = node.getDuplicateValues(ClinicalDocumentDecoder.CEHRT);
		if (duplicateCehrts != null && duplicateCehrts.size() > 0) {
			addError(Detail.forProblemAndNode(ProblemCode.PCF_DUPLICATE_CEHRT, node));
		}
	}

	private boolean cehrtFormat(String requiredSubstring) {
		return requiredSubstring.equalsIgnoreCase("15E") || requiredSubstring.equalsIgnoreCase("15C");
	}

	/**
	 * Validates the submission is not after the set end date
	 *
	 * @param node The node to give in the error if the submission is after the set end date
	 */
	private void validateSubmissionDate(Node node) {
		ZonedDateTime endDate = endDate();
		if (now().isAfter(endDate)) {
			String formatted = endDate.format(OUTPUT_END_DATE_FORMAT);
			String program = node.getValue(ClinicalDocumentDecoder.PROGRAM_NAME);
			addError(Detail.forProblemAndNode(
				ProblemCode.PCF_SUBMISSION_ENDED.format(program, program, formatted, program, program,
					EnvironmentHelper.getOrDefault(CPC_PLUS_CONTACT_EMAIL, DEFAULT_CPC_PLUS_CONTACT_EMAIL)),
				node));
		}
	}

	/**
	 * @return the current Zoned Date time for eastern time
	 */
	private ZonedDateTime now() {
		ZoneId zone = ZoneId.of("US/Eastern");
		return ZonedDateTime.now(zone);
	}

	/**
	 * @return the configured cpc+ end date, or {@link ZonedDateTime} will default to max year decemeber
	 */
	private ZonedDateTime endDate() {
		String endDate = EnvironmentHelper.get(END_DATE_VARIABLE);
		if (endDate == null) {
			return ZonedDateTime.of(Year.MAX_VALUE, 12, 31,
				0, 0, 0, 0, ZoneId.of("US/Eastern"));
		}
		return ZonedDateTime.parse(endDate, INPUT_END_DATE_FORMAT);
	}

	private void validateSingleTinNpiPerPerformer(Node node) {
		String performersXpath = PathCorrelator.getXpath(TemplateId.CLINICAL_DOCUMENT.toString(), "performer", Namespace.NO_NAMESPACE.getURI());
		XPathExpression<Element> expression = XPathFactory.instance().compile(performersXpath, Filters.element(), null,  Namespace.NO_NAMESPACE);
		List<Element> performers = expression.evaluate(node.getElementForLocation());
		// Should be maximum of one TIN and one NPI per performer.
		for (Element performer : performers) {
			List<Element> tins = performer.getChildren().stream().filter(filterTinNpi("representedOrganization")).collect(Collectors.toList());
			List<Element> npis = performer.getChildren().stream().filter(filterTinNpi("id")).collect(Collectors.toList());
			forceCheckErrors(node).predicate(ProblemCode.PCF_MULTI_TIN_NPI_SINGLE_PERFORMER, tins.size() <= 1);
			forceCheckErrors(node).predicate(ProblemCode.PCF_MULTI_TIN_NPI_SINGLE_PERFORMER, npis.size() <= 1);
		}
	}

	private Predicate<Element> filterTinNpi(String name) {
		return (Element child) -> (child.getContent().stream()
			.filter(c -> c.getClass().equals(LocatedElement.class) && ((LocatedElement) c).getName().equals(name))
			.toArray().length >= 1);
	}

}
