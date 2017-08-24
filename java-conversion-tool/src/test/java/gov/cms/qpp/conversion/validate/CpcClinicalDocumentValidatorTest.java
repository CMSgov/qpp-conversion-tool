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
	public void setUp () {
		cpcValidator = new CpcClinicalDocumentValidator();
	}

	@Test
	public void validPracticeSiteAddress() {
		Node clinicalDocumentNode = createsValidCpcplusClinicalDocument();

		cpcValidator.internalValidateSingleNode(clinicalDocumentNode);

		Set<Detail> errors = cpcValidator.getDetails();
		assertThat("Must have no errors", errors, hasSize(0));
	}

	@Test
	public void missingPracticeSiteAddress() {
		Node clinicalDocumentNode = createsValidCpcplusClinicalDocument();
		clinicalDocumentNode.removeValue(ClinicalDocumentDecoder.PRACTICE_SITE_ADDR);

		cpcValidator.internalValidateSingleNode(clinicalDocumentNode);

		Set<Detail> errors = cpcValidator.getDetails();
		assertThat("Must contain error", errors,
				hasValidationErrorsIgnoringPath(CpcClinicalDocumentValidator.MISSING_PRACTICE_SITE_ADDRESS));
	}

	@Test
	public void emptyPracticeSiteAddress() {
		Node clinicalDocumentNode = createsValidCpcplusClinicalDocument();
		clinicalDocumentNode.removeValue(ClinicalDocumentDecoder.PRACTICE_SITE_ADDR);
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.PRACTICE_SITE_ADDR, "");

		cpcValidator.internalValidateSingleNode(clinicalDocumentNode);

		Set<Detail> errors = cpcValidator.getDetails();
		assertThat("Must contain error", errors,
				hasValidationErrorsIgnoringPath(CpcClinicalDocumentValidator.MISSING_PRACTICE_SITE_ADDRESS));
	}

	@Test
	public void testCpcPlusMultipleApm() {
		Node clinicalDocumentNode = createsValidCpcplusClinicalDocument();

		// extra APM
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.ENTITY_ID, "1234567", false);

		cpcValidator.internalValidateSingleNode(clinicalDocumentNode);

		assertThat("Must validate with the correct error",
				cpcValidator.getDetails(),
				hasValidationErrorsIgnoringPath(CpcClinicalDocumentValidator.ONLY_ONE_APM_ALLOWED));
	}

	@Test
	public void testCpcPlusNoApm() {
		Node clinicalDocumentNode = createsValidCpcplusClinicalDocument();
		clinicalDocumentNode.removeValue(ClinicalDocumentDecoder.ENTITY_ID);

		cpcValidator.internalValidateSingleNode(clinicalDocumentNode);

		assertThat("Must validate with the correct error",
				cpcValidator.getDetails(),
				hasValidationErrorsIgnoringPath(CpcClinicalDocumentValidator.ONLY_ONE_APM_ALLOWED));
	}

	private Node createsValidCpcplusClinicalDocument() {
		Node clinicalDocumentNode = new Node(TemplateId.CLINICAL_DOCUMENT);
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.PROGRAM_NAME, ClinicalDocumentDecoder.CPCPLUS_PROGRAM_NAME);
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.ENTITY_TYPE, "");
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.PRACTICE_SITE_ADDR, "test");
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.ENTITY_ID, "AR00000");

		return clinicalDocumentNode;
	}

}
