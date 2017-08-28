package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static gov.cms.qpp.conversion.model.error.ValidationErrorMatcher.hasValidationErrorsIgnoringPath;

public class CpcClinicalDocumentValidatorTest {

	private CpcClinicalDocumentValidator cpcValidator;

	@Before
	public void setup() {
		cpcValidator = new CpcClinicalDocumentValidator();
	}

	@Test
	public void validPracticeSiteAddress() {
		Node clinicalDocumentNode = createValidCpcPlusClinicalDocument();

		cpcValidator.internalValidateSingleNode(clinicalDocumentNode);

		Set<Detail> errors = cpcValidator.getDetails();
		assertThat("Must have no errors", errors, hasSize(0));
	}

	@Test
	public void missingPracticeSiteAddress() {
		Node clinicalDocumentNode = createValidCpcPlusClinicalDocument();
		clinicalDocumentNode.removeValue(ClinicalDocumentDecoder.PRACTICE_SITE_ADDR);

		cpcValidator.internalValidateSingleNode(clinicalDocumentNode);

		Set<Detail> errors = cpcValidator.getDetails();
		assertThat("Must contain error", errors,
				hasValidationErrorsIgnoringPath(CpcClinicalDocumentValidator.MISSING_PRACTICE_SITE_ADDRESS));
	}

	@Test
	public void emptyPracticeSiteAddress() {
		Node clinicalDocumentNode = createValidCpcPlusClinicalDocument();
		clinicalDocumentNode.removeValue(ClinicalDocumentDecoder.PRACTICE_SITE_ADDR);
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.PRACTICE_SITE_ADDR, "");

		cpcValidator.internalValidateSingleNode(clinicalDocumentNode);

		Set<Detail> errors = cpcValidator.getDetails();
		assertThat("Must contain error", errors,
				hasValidationErrorsIgnoringPath(CpcClinicalDocumentValidator.MISSING_PRACTICE_SITE_ADDRESS));
	}

	@Test
	public void testCpcPlusMultipleApm() {
		Node clinicalDocumentNode = createValidCpcPlusClinicalDocument();

		// extra APM
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.ENTITY_ID, "1234567", false);

		cpcValidator.internalValidateSingleNode(clinicalDocumentNode);

		assertThat("Must validate with the correct error",
				cpcValidator.getDetails(),
				hasValidationErrorsIgnoringPath(CpcClinicalDocumentValidator.ONLY_ONE_APM_ALLOWED));
	}

	@Test
	public void testCpcPlusNoApm() {
		Node clinicalDocumentNode = createValidCpcPlusClinicalDocument();
		clinicalDocumentNode.removeValue(ClinicalDocumentDecoder.ENTITY_ID);

		cpcValidator.internalValidateSingleNode(clinicalDocumentNode);

		assertThat("Must validate with the correct error",
				cpcValidator.getDetails(),
				hasValidationErrorsIgnoringPath(CpcClinicalDocumentValidator.ONLY_ONE_APM_ALLOWED));
	}

	@Test
	public void testCpcPlusMissingMeasureSection() {
		Node clinicalDocumentNode = createMissingMeasureSectionCpcplusClinicalDocument();

		cpcValidator.internalValidateSingleNode(clinicalDocumentNode);

		assertThat("Must validate with the correct error",
				cpcValidator.getDetails(),
				hasValidationErrorsIgnoringPath(CpcClinicalDocumentValidator.ONE_MEASURE_SECTION_REQUIRED));
	}

	private Node createValidCpcPcllusClinicalDocument() {
		Node clinicalDocumentNode = new Node(TemplateId.CLINICAL_DOCUMENT);
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.PROGRAM_NAME, ClinicalDocumentDecoder.CPCPLUS_PROGRAM_NAME);
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.ENTITY_TYPE, "");
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.PRACTICE_SITE_ADDR, "test");
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.ENTITY_ID, "AR00000");

		Node measureSection = new Node(TemplateId.MEASURE_SECTION_V2);

		clinicalDocumentNode.addChildNode(measureSection);

		return clinicalDocumentNode;
	}

	private Node createMissingMeasureSectionCpcplusClinicalDocument() {
		Node clinicalDocumentNode = new Node(TemplateId.CLINICAL_DOCUMENT);
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.PROGRAM_NAME, ClinicalDocumentDecoder.CPCPLUS_PROGRAM_NAME);
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.ENTITY_TYPE, "");
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.PRACTICE_SITE_ADDR, "test");
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.ENTITY_ID, "AR00000");

		return clinicalDocumentNode;
	}

}
