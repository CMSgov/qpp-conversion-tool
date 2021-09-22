package gov.cms.qpp.conversion.validate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

public class PcfClinicalDocumentValidatorTest {
	private PcfClinicalDocumentValidator validator;
	private ApmEntityIds apmEntityIds;

	@BeforeEach
	void setUp() {
		apmEntityIds = new ApmEntityIds("test_apm_entity_ids.json");
		validator = new PcfClinicalDocumentValidator(new Context(apmEntityIds));
	}

	@AfterEach
	void cleanUp() {
		System.clearProperty(CpcClinicalDocumentValidator.END_DATE_VARIABLE);
		System.clearProperty(CpcClinicalDocumentValidator.CPC_PLUS_CONTACT_EMAIL);
	}

	@Test
	void testValidPcfPracticeSiteAddress() {
		Node clinicalDocumentNode = (createPcfClinicalDocumentNodeWithMeasureSection());
		List<Detail> errors = validator.validateSingleNode(clinicalDocumentNode).getErrors();

		assertThat(errors).isEmpty();
	}

	@Test
	void testMissingPcfPracticeSiteAddress() {
		Node clinicalDocumentNode = createPcfClinicalDocumentNodeWithMeasureSection();
		clinicalDocumentNode.removeValue(ClinicalDocumentDecoder.PRACTICE_SITE_ADDR);
		List<Detail> errors = validator.validateSingleNode(clinicalDocumentNode).getErrors();

		assertThat(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
			.containsExactly(ProblemCode.CPC_PCF_CLINICAL_DOCUMENT_MISSING_PRACTICE_SITE_ADDRESS
				.format(Context.REPORTING_YEAR));
	}

	@Test
	void testPcfMultipleApm() {
		Node clinicalDocumentNode = createPcfClinicalDocumentNodeWithMeasureSection();

		// extra APM
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.PCF_ENTITY_ID, "1234567", false);
		List<Detail> errors = validator.validateSingleNode(clinicalDocumentNode).getErrors();

		assertThat(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
			.containsExactly(ProblemCode.CPC_PCF_CLINICAL_DOCUMENT_ONLY_ONE_APM_ALLOWED);
	}

	@Test
	void testPcfNoApm() {
		Node clinicalDocumentNode = createPcfClinicalDocumentNodeWithMeasureSection();
		clinicalDocumentNode.removeValue(ClinicalDocumentDecoder.PCF_ENTITY_ID);
		List<Detail> errors = validator.validateSingleNode(clinicalDocumentNode).getErrors();

		assertThat(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
			.containsExactly(ProblemCode.CPC_PCF_CLINICAL_DOCUMENT_ONLY_ONE_APM_ALLOWED);
	}

	@Test
	void testPcfEmptyApm() {
		Node clinicalDocumentNode = createPcfClinicalDocumentNodeWithMeasureSection();
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.PCF_ENTITY_ID, "");
		List<Detail> errors = validator.validateSingleNode(clinicalDocumentNode).getErrors();
		assertThat(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
			.containsExactly(ProblemCode.CPC_PCF_CLINICAL_DOCUMENT_EMPTY_APM);
	}

	@Test
	void testPcfInvalidApm() {
		Node clinicalDocumentNode = createPcfClinicalDocumentNodeWithMeasureSection();
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.PCF_ENTITY_ID, "PropertyTaxes");
		List<Detail> errors = validator.validateSingleNode(clinicalDocumentNode).getErrors();
		assertThat(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
			.containsExactly(ProblemCode.CPC_PCF_CLINICAL_DOCUMENT_INVALID_APM);
	}

	@Test
	void testPcfNoMeasureSection() {
		Node clinicalDocumentNode = createPcfClinicalDocumentNodeOnly();
		List<Detail> errors = validator.validateSingleNode(clinicalDocumentNode).getErrors();

		assertThat(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
			.containsExactly(ProblemCode.CPC_PCF_CLINICAL_DOCUMENT_ONE_MEASURE_SECTION_REQUIRED);
	}

	@Test
	void testCpcPlusSubmissionBeforeEndDate() {
		System.setProperty(CpcClinicalDocumentValidator.END_DATE_VARIABLE,
			ZonedDateTime.now(CpcClinicalDocumentValidator.EASTERN_TIME_ZONE).plusYears(3)
				.format(CpcClinicalDocumentValidator.INPUT_END_DATE_FORMAT));
		Node clinicalDocument = createPcfClinicalDocumentNodeWithMeasureSection();
		List<Detail> errors = validator.validateSingleNode(clinicalDocument).getErrors();

		assertThat(errors)
			.isEmpty();
	}


	@Test
	void testNoPi() {
		Node clinicalDocumentNode = createPcfClinicalDocumentNodeWithMeasureSection();
		Node pi = new Node(TemplateId.PI_SECTION_V2);
		clinicalDocumentNode.addChildNode(pi);

		List<Detail> errors = validator.validateSingleNode(clinicalDocumentNode).getErrors();
		assertThat(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ProblemCode.PCF_NO_PI);
	}

	@Test
	void testNonNumericNpi() {
		Node clinicalDocumentNode = createPcfClinicalDocumentNodeWithMeasureSection();
		clinicalDocumentNode.removeValue(ClinicalDocumentDecoder.NATIONAL_PROVIDER_IDENTIFIER);
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.NATIONAL_PROVIDER_IDENTIFIER, "9900000.99");

		List<Detail> errors = validator.validateSingleNode(clinicalDocumentNode).getErrors();
		assertThat(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ProblemCode.CPC_PCF_PLUS_INVALID_NPI);
	}

	private Node createPcfClinicalDocumentNodeWithMeasureSection() {
		Node clinicalDocumentNode = createPcfClinicalDocumentNodeOnly();
		addMeasureSectionNode(clinicalDocumentNode);
		return clinicalDocumentNode;
	}

	private Node createPcfClinicalDocumentNodeOnly() {
		Node clinicalDocumentNode = new Node(TemplateId.CLINICAL_DOCUMENT);
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.PROGRAM_NAME, ClinicalDocumentDecoder.PCF);
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.ENTITY_TYPE, "");
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.PRACTICE_SITE_ADDR, "test");
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.PCF_ENTITY_ID, "DogCow");
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.TAX_PAYER_IDENTIFICATION_NUMBER, "123456789");
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.NATIONAL_PROVIDER_IDENTIFIER, "9900000099");
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.CEHRT, "XX15EXXXXXXXXXX");

		return clinicalDocumentNode;
	}

	private void addMeasureSectionNode(Node clinicalDocumentNode) {
		Node measureSection = new Node(TemplateId.MEASURE_SECTION_V4);
		clinicalDocumentNode.addChildNode(measureSection);
	}
}
