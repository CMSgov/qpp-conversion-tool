package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import org.junit.Before;
import org.junit.Test;

import static gov.cms.qpp.conversion.model.error.ValidationErrorMatcher.hasValidationErrorsIgnoringPath;
import static org.hamcrest.MatcherAssert.assertThat;

public class CpcClinicalDocumentValidatorTest {

	private CpcClinicalDocumentValidator cpcValidator;

	@Before
	public void beforeEachTest() {
		cpcValidator = new CpcClinicalDocumentValidator();
	}

	@Test
	public void testCpcPlusMultipleApm() {
		Node clinicalDocumentNode = createClinicalDocumentWithProgramType(
			ClinicalDocumentDecoder.CPCPLUS_PROGRAM_NAME, "", "AR00000");
		// extra APM
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.ENTITY_ID, "1234567", false);

		cpcValidator.internalValidateSingleNode(clinicalDocumentNode);

		assertThat("Must validate with the correct error",
			cpcValidator.getDetails(),
			hasValidationErrorsIgnoringPath(CpcClinicalDocumentValidator.ONLY_ONE_APM_ALLOWED));
	}

	@Test
	public void testCpcPlusNoApm() {
		Node clinicalDocumentNode = createClinicalDocumentWithProgramType(
			ClinicalDocumentDecoder.CPCPLUS_PROGRAM_NAME, "");

		cpcValidator.internalValidateSingleNode(clinicalDocumentNode);

		assertThat("Must validate with the correct error",
			cpcValidator.getDetails(),
			hasValidationErrorsIgnoringPath(CpcClinicalDocumentValidator.ONLY_ONE_APM_ALLOWED));
	}

	private Node createClinicalDocumentWithProgramType(final String programName, final String entityType,
		final String entityId) {
		Node clinicalDocumentNode = createClinicalDocumentWithProgramType(programName, entityType);
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.ENTITY_ID, entityId);

		return clinicalDocumentNode;
	}

	private Node createClinicalDocumentWithProgramType(final String programName, final String entityType) {
		Node clinicalDocumentNode = new Node(TemplateId.CLINICAL_DOCUMENT);
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.PROGRAM_NAME, programName);
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.ENTITY_TYPE, entityType);

		return clinicalDocumentNode;
	}
}