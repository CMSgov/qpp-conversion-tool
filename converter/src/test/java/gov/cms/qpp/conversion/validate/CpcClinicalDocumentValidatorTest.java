package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ErrorCode;
import gov.cms.qpp.conversion.model.error.correspondence.DetailsErrorEquals;
import gov.cms.qpp.conversion.model.validation.ApmEntityIds;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.Set;

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
		cpcValidator = new CpcClinicalDocumentValidator();
	}

	@AfterEach
	void cleanUp() {
		System.clearProperty(CpcClinicalDocumentValidator.END_DATE_VARIABLE);
		System.clearProperty(CpcClinicalDocumentValidator.CPC_PLUS_CONTACT_EMAIL);
	}

	@Test
	void validPracticeSiteAddress() {
		Node clinicalDocumentNode = createValidCpcPlusClinicalDocument();
		cpcValidator.internalValidateSingleNode(clinicalDocumentNode);
		Set<Detail> errors = cpcValidator.getDetails();

		assertWithMessage("Must have no errors")
				.that(errors).isEmpty();
	}

	@Test
	void missingPracticeSiteAddress() {
		Node clinicalDocumentNode = createValidCpcPlusClinicalDocument();
		clinicalDocumentNode.removeValue(ClinicalDocumentDecoder.PRACTICE_SITE_ADDR);
		cpcValidator.internalValidateSingleNode(clinicalDocumentNode);
		Set<Detail> errors = cpcValidator.getDetails();

		assertWithMessage("Must contain error")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.CPC_CLINICAL_DOCUMENT_MISSING_PRACTICE_SITE_ADDRESS
					.format(Context.REPORTING_YEAR));
	}

	@Test
	void emptyPracticeSiteAddress() {
		Node clinicalDocumentNode = createValidCpcPlusClinicalDocument();
		clinicalDocumentNode.removeValue(ClinicalDocumentDecoder.PRACTICE_SITE_ADDR);
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.PRACTICE_SITE_ADDR, "");
		cpcValidator.internalValidateSingleNode(clinicalDocumentNode);
		Set<Detail> errors = cpcValidator.getDetails();

		assertWithMessage("Must contain error")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.CPC_CLINICAL_DOCUMENT_MISSING_PRACTICE_SITE_ADDRESS
					.format(Context.REPORTING_YEAR));
	}

	@Test
	void testCpcPlusMultipleApm() {
		Node clinicalDocumentNode = createValidCpcPlusClinicalDocument();

		// extra APM
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.PRACTICE_ID, "1234567", false);
		cpcValidator.internalValidateSingleNode(clinicalDocumentNode);

		assertWithMessage("Must validate with the correct error")
				.that(cpcValidator.getDetails()).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.CPC_CLINICAL_DOCUMENT_ONLY_ONE_APM_ALLOWED);
	}

	@Test
	void testCpcPlusNoApm() {
		Node clinicalDocumentNode = createValidCpcPlusClinicalDocument();
		clinicalDocumentNode.removeValue(ClinicalDocumentDecoder.PRACTICE_ID);
		cpcValidator.internalValidateSingleNode(clinicalDocumentNode);

		assertWithMessage("Must validate with the correct error")
				.that(cpcValidator.getDetails()).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.CPC_CLINICAL_DOCUMENT_ONLY_ONE_APM_ALLOWED);
	}

	@Test
	void testCpcPlusEmptyApm() {
		Node clinicalDocumentNode = createValidCpcPlusClinicalDocument();
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.PRACTICE_ID, "");
		cpcValidator.internalValidateSingleNode(clinicalDocumentNode);

		assertWithMessage("Must validate with the correct error")
			.that(cpcValidator.getDetails()).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
			.containsExactly(ErrorCode.CPC_CLINICAL_DOCUMENT_EMPTY_APM);
	}

	@Test
	void testCpcPlusInvalidApm() {
		Node clinicalDocumentNode = createValidCpcPlusClinicalDocument();
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.PRACTICE_ID, "PropertyTaxes");
		cpcValidator.internalValidateSingleNode(clinicalDocumentNode);

		assertWithMessage("Must validate with the correct error")
			.that(cpcValidator.getDetails()).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
			.containsExactly(ErrorCode.CPC_CLINICAL_DOCUMENT_INVALID_APM);
	}

	@Test
	void testCpcPlusMissingMeasureSection() {
		Node clinicalDocumentNode = createCpcPlusClinicalDocument();
		cpcValidator.internalValidateSingleNode(clinicalDocumentNode);

		assertWithMessage("Must validate with the correct error")
				.that(cpcValidator.getDetails()).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.CPC_CLINICAL_DOCUMENT_ONE_MEASURE_SECTION_REQUIRED);
	}

	@Test
	void testCpcPlusSubmissionBeforeEndDate() {
		System.setProperty(CpcClinicalDocumentValidator.END_DATE_VARIABLE, LocalDate.now().plusYears(3).toString());
		Node clinicalDocument = createValidCpcPlusClinicalDocument();
		cpcValidator.internalValidateSingleNode(clinicalDocument);

		assertThat(cpcValidator.getDetails())
			.isEmpty();
	}

	@ParameterizedTest
	@CsvSource({"meep@mawp.blah, meep@mawp.blah", ", cpcplus@telligen.com"})
	void testCpcPlusSubmissionAfterEndDate(String systemValue, String expected) {
		LocalDate endDate = LocalDate.now().minusYears(3);
		String formattedDate = endDate.format(CpcClinicalDocumentValidator.END_DATE_FORMAT);
		if (systemValue != null) {
			System.setProperty(CpcClinicalDocumentValidator.CPC_PLUS_CONTACT_EMAIL, systemValue);
		}
		System.setProperty(CpcClinicalDocumentValidator.END_DATE_VARIABLE, endDate.toString());
		Node clinicalDocument = createValidCpcPlusClinicalDocument();

		cpcValidator.internalValidateSingleNode(clinicalDocument);

		assertThat(cpcValidator.getDetails())
			.comparingElementsUsing(DetailsErrorEquals.INSTANCE)
			.containsExactly(ErrorCode.CPC_PLUS_SUBMISSION_ENDED.format(formattedDate, expected));
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

		return clinicalDocumentNode;
	}

	private void addMeasureSectionNode(Node clinicalDocumentNode) {
		Node measureSection = new Node(TemplateId.MEASURE_SECTION_V2);
		clinicalDocumentNode.addChildNode(measureSection);
	}

}
