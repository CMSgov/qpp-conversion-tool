package gov.cms.qpp.conversion.validate;

import static com.google.common.truth.Truth.assertWithMessage;
import static com.google.common.truth.Truth.assertThat;

import java.time.LocalDate;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ErrorCode;
import gov.cms.qpp.conversion.model.error.correspondence.DetailsErrorEquals;
import gov.cms.qpp.conversion.model.validation.ApmEntityIds;

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
				.containsExactly(ErrorCode.CPC_CLINICAL_DOCUMENT_MISSING_PRACTICE_SITE_ADDRESS);
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
				.containsExactly(ErrorCode.CPC_CLINICAL_DOCUMENT_MISSING_PRACTICE_SITE_ADDRESS);
	}

	@Test
	void testCpcPlusMultipleApm() {
		Node clinicalDocumentNode = createValidCpcPlusClinicalDocument();

		// extra APM
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.ENTITY_ID, "1234567", false);
		cpcValidator.internalValidateSingleNode(clinicalDocumentNode);

		assertWithMessage("Must validate with the correct error")
				.that(cpcValidator.getDetails()).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.CPC_CLINICAL_DOCUMENT_ONLY_ONE_APM_ALLOWED);
	}

	@Test
	void testCpcPlusNoApm() {
		Node clinicalDocumentNode = createValidCpcPlusClinicalDocument();
		clinicalDocumentNode.removeValue(ClinicalDocumentDecoder.ENTITY_ID);
		cpcValidator.internalValidateSingleNode(clinicalDocumentNode);

		assertWithMessage("Must validate with the correct error")
				.that(cpcValidator.getDetails()).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.CPC_CLINICAL_DOCUMENT_ONLY_ONE_APM_ALLOWED);
	}

	@Test
	void testCpcPlusEmptyApm() {
		Node clinicalDocumentNode = createValidCpcPlusClinicalDocument();
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.ENTITY_ID, "");
		cpcValidator.internalValidateSingleNode(clinicalDocumentNode);

		assertWithMessage("Must validate with the correct error")
			.that(cpcValidator.getDetails()).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
			.containsExactly(ErrorCode.CPC_CLINICAL_DOCUMENT_EMPTY_APM);
	}

	@Test
	void testCpcPlusInvalidApm() {
		Node clinicalDocumentNode = createValidCpcPlusClinicalDocument();
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.ENTITY_ID, "PropertyTaxes");
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
		System.clearProperty(CpcClinicalDocumentValidator.END_DATE_VARIABLE);
	}

	@Test
	void testCpcPlusSubmissionAfterEndDate() {
		LocalDate endDate = LocalDate.now().minusYears(3);
		System.setProperty(CpcClinicalDocumentValidator.END_DATE_VARIABLE, endDate.toString());
		Node clinicalDocument = createValidCpcPlusClinicalDocument();
		cpcValidator.internalValidateSingleNode(clinicalDocument);

		assertThat(cpcValidator.getDetails())
			.comparingElementsUsing(DetailsErrorEquals.INSTANCE)
			.containsExactly(ErrorCode.CPC_PLUS_SUBMISSION_ENDED.format(endDate));
		System.clearProperty(CpcClinicalDocumentValidator.END_DATE_VARIABLE);
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
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.ENTITY_ID, "DogCow");

		return clinicalDocumentNode;
	}

	private void addMeasureSectionNode(Node clinicalDocumentNode) {
		Node measureSection = new Node(TemplateId.MEASURE_SECTION_V2);
		clinicalDocumentNode.addChildNode(measureSection);
	}

}
