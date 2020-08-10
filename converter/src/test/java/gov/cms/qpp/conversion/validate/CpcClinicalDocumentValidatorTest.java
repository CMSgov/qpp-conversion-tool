package gov.cms.qpp.conversion.validate;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ProblemCode;
import gov.cms.qpp.conversion.model.error.correspondence.DetailsErrorEquals;
import gov.cms.qpp.conversion.model.validation.ApmEntityIds;

import java.time.ZonedDateTime;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

class CpcClinicalDocumentValidatorTest {

	private CpcClinicalDocumentValidator cpcValidator;

	@BeforeAll
	static void initApmIds() {
		ApmEntityIds.setApmDataFile("test_apm_entity_ids.json");
	}

	@AfterAll
	static void defaultApmIds() {
		ApmEntityIds.setApmDataFile(ApmEntityIds.DEFAULT_APM_ENTITY_FILE_NAME);
	}

	@BeforeEach
	void createNewValidator() {
		cpcValidator = new CpcClinicalDocumentValidator(new Context());
	}

	@AfterEach
	void cleanUp() {
		System.clearProperty(CpcClinicalDocumentValidator.END_DATE_VARIABLE);
		System.clearProperty(CpcClinicalDocumentValidator.CPC_PLUS_CONTACT_EMAIL);
	}

	@Test
	void validPracticeSiteAddress() {
		Node clinicalDocumentNode = createValidCpcPlusClinicalDocument();
		List<Detail> errors = cpcValidator.validateSingleNode(clinicalDocumentNode).getErrors();

		assertWithMessage("Must have no errors")
				.that(errors).isEmpty();
	}

	@Test
	void missingPracticeSiteAddress() {
		Node clinicalDocumentNode = createValidCpcPlusClinicalDocument();
		clinicalDocumentNode.removeValue(ClinicalDocumentDecoder.PRACTICE_SITE_ADDR);
		List<Detail> errors = cpcValidator.validateSingleNode(clinicalDocumentNode).getErrors();

		assertWithMessage("Must contain error")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ProblemCode.CPC_CLINICAL_DOCUMENT_MISSING_PRACTICE_SITE_ADDRESS
					.format(Context.REPORTING_YEAR));
	}

	@Test
	void emptyPracticeSiteAddress() {
		Node clinicalDocumentNode = createValidCpcPlusClinicalDocument();
		clinicalDocumentNode.removeValue(ClinicalDocumentDecoder.PRACTICE_SITE_ADDR);
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.PRACTICE_SITE_ADDR, "");
		List<Detail> errors = cpcValidator.validateSingleNode(clinicalDocumentNode).getErrors();

		assertWithMessage("Must contain error")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ProblemCode.CPC_CLINICAL_DOCUMENT_MISSING_PRACTICE_SITE_ADDRESS
					.format(Context.REPORTING_YEAR));
	}

	@Test
	void testCpcPlusMultipleApm() {
		Node clinicalDocumentNode = createValidCpcPlusClinicalDocument();

		// extra APM
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.PRACTICE_ID, "1234567", false);
		List<Detail> errors = cpcValidator.validateSingleNode(clinicalDocumentNode).getErrors();

		assertWithMessage("Must validate with the correct error")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ProblemCode.CPC_CLINICAL_DOCUMENT_ONLY_ONE_APM_ALLOWED);
	}

	@Test
	void testCpcPlusNoApm() {
		Node clinicalDocumentNode = createValidCpcPlusClinicalDocument();
		clinicalDocumentNode.removeValue(ClinicalDocumentDecoder.PRACTICE_ID);
		List<Detail> errors = cpcValidator.validateSingleNode(clinicalDocumentNode).getErrors();

		assertWithMessage("Must validate with the correct error")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ProblemCode.CPC_CLINICAL_DOCUMENT_ONLY_ONE_APM_ALLOWED);
	}

	@Test
	void testCpcPlusEmptyApm() {
		Node clinicalDocumentNode = createValidCpcPlusClinicalDocument();
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.PRACTICE_ID, "");
		List<Detail> errors = cpcValidator.validateSingleNode(clinicalDocumentNode).getErrors();
		assertWithMessage("Must validate with the correct error")
			.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
			.containsExactly(ProblemCode.CPC_CLINICAL_DOCUMENT_EMPTY_APM);
	}

	@Test
	void testCpcPlusInvalidApm() {
		Node clinicalDocumentNode = createValidCpcPlusClinicalDocument();
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.PRACTICE_ID, "PropertyTaxes");
		List<Detail> errors = cpcValidator.validateSingleNode(clinicalDocumentNode).getErrors();
		assertWithMessage("Must validate with the correct error")
			.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
			.containsExactly(ProblemCode.CPC_CLINICAL_DOCUMENT_INVALID_APM);
	}

	@Test
	void testCpcPlusMissingMeasureSection() {
		Node clinicalDocumentNode = createCpcPlusClinicalDocument();
		List<Detail> errors = cpcValidator.validateSingleNode(clinicalDocumentNode).getErrors();

		assertWithMessage("Must validate with the correct error")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ProblemCode.CPC_CLINICAL_DOCUMENT_ONE_MEASURE_SECTION_REQUIRED);
	}

	@Test
	void testCpcPlusSubmissionBeforeEndDate() {
		System.setProperty(CpcClinicalDocumentValidator.END_DATE_VARIABLE,
			ZonedDateTime.now(CpcClinicalDocumentValidator.EASTERN_TIME_ZONE).plusYears(3)
			.format(CpcClinicalDocumentValidator.INPUT_END_DATE_FORMAT));
		Node clinicalDocument = createValidCpcPlusClinicalDocument();
		List<Detail> errors = cpcValidator.validateSingleNode(clinicalDocument).getErrors();

		assertThat(errors)
			.isEmpty();
	}

	@ParameterizedTest
	@CsvSource({"meep@mawp.blah, meep@mawp.blah", ", cpcplus@telligen.com"})
	void testCpcPlusSubmissionAfterEndDate(String systemValue, String expected) {
		ZonedDateTime endDate = ZonedDateTime.now(CpcClinicalDocumentValidator.EASTERN_TIME_ZONE).minusYears(3);
		String formattedDate = endDate.format(CpcClinicalDocumentValidator.OUTPUT_END_DATE_FORMAT);
		if (systemValue != null) {
			System.setProperty(CpcClinicalDocumentValidator.CPC_PLUS_CONTACT_EMAIL, systemValue);
		}
		System.setProperty(CpcClinicalDocumentValidator.END_DATE_VARIABLE, endDate.format(CpcClinicalDocumentValidator.INPUT_END_DATE_FORMAT));
		Node clinicalDocument = createValidCpcPlusClinicalDocument();

		List<Detail> errors = cpcValidator.validateSingleNode(clinicalDocument).getErrors();

		assertThat(errors)
			.comparingElementsUsing(DetailsErrorEquals.INSTANCE)
			.containsExactly(ProblemCode.CPC_PLUS_SUBMISSION_ENDED.format(formattedDate, expected));
	}

	@Test
	void testCpcPlusMissingTin() {
		Node clinicalDocumentNode = createCpcPlusClinicalDocument();
		clinicalDocumentNode.removeValue(ClinicalDocumentDecoder.TAX_PAYER_IDENTIFICATION_NUMBER);
		List<Detail> errors = cpcValidator.validateSingleNode(clinicalDocumentNode).getErrors();

		assertWithMessage("Must validate with the correct error")
			.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
			.containsExactly(ProblemCode.CPC_PLUS_TIN_REQUIRED);
	}

	@Test
	void testCpcPlusMissingNpi() {
		Node clinicalDocumentNode = createCpcPlusClinicalDocument();
		clinicalDocumentNode.removeValue(ClinicalDocumentDecoder.NATIONAL_PROVIDER_IDENTIFIER);
		List<Detail> errors = cpcValidator.validateSingleNode(clinicalDocumentNode).getErrors();

		assertWithMessage("Must validate with the correct error")
			.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
			.containsExactly(ProblemCode.CPC_PLUS_NPI_REQUIRED);
	}

	@Test
	void testWarnWhenContainsIa() {
		Node clinicalDocumentNode = createCpcPlusClinicalDocument();
		Node iaSection = new Node(TemplateId.IA_SECTION);
		clinicalDocumentNode.addChildNode(iaSection);
		List<Detail> warnings = cpcValidator.validateSingleNode(clinicalDocumentNode).getWarnings();

		assertThat(warnings)
			.comparingElementsUsing(DetailsErrorEquals.INSTANCE)
			.contains(ProblemCode.CPC_PLUS_NO_IA_OR_PI);
	}

	@Test
	void testWarnWhenContainsPi() {
		Node clinicalDocumentNode = createCpcPlusClinicalDocument();
		Node piSection = new Node(TemplateId.PI_SECTION_V2);
		clinicalDocumentNode.addChildNode(piSection);
		List<Detail> warnings = cpcValidator.validateSingleNode(clinicalDocumentNode).getWarnings();

		assertThat(warnings)
			.comparingElementsUsing(DetailsErrorEquals.INSTANCE)
			.contains(ProblemCode.CPC_PLUS_NO_IA_OR_PI);
	}

	private Node createValidCpcPlusClinicalDocument() {
		Node clinicalDocumentNode = createCpcPlusClinicalDocument();
		addMeasureSectionNode(clinicalDocumentNode);
		return clinicalDocumentNode;
	}

	private Node createCpcPlusClinicalDocument() {
		Node clinicalDocumentNode = new Node(TemplateId.CLINICAL_DOCUMENT);
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.PROGRAM_NAME, ClinicalDocumentDecoder.CPCPLUS_PROGRAM_NAME);
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.ENTITY_TYPE, "");
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.PRACTICE_SITE_ADDR, "test");
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.PRACTICE_ID, "DogCow");
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.TAX_PAYER_IDENTIFICATION_NUMBER, "123456789");
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.NATIONAL_PROVIDER_IDENTIFIER, "9900000099");
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.CEHRT, "1234567890x");

		return clinicalDocumentNode;
	}

	private void addMeasureSectionNode(Node clinicalDocumentNode) {
		Node measureSection = new Node(TemplateId.MEASURE_SECTION_V3);
		clinicalDocumentNode.addChildNode(measureSection);
	}
}
