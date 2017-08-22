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

	private Node clinicalDocumentNode;

	@Before
	public void setUp () {
		clinicalDocumentNode = new Node(TemplateId.CLINICAL_DOCUMENT);
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.PROGRAM_NAME, "CPCPLUS");
	}

	@Test
	public void validPracticeSiteAddress() {
		clinicalDocumentNode.putValue("practiceSiteAddr", "test");

		CpcClinicalDocumentValidator validator = new CpcClinicalDocumentValidator();
		validator.internalValidateSingleNode(clinicalDocumentNode);

		Set<Detail> errors = validator.getDetails();
		assertThat("Must have no errors", errors, hasSize(0));
	}

	@Test
	public void invalidPracticeSiteAddress() {
		CpcClinicalDocumentValidator validator = new CpcClinicalDocumentValidator();
		validator.internalValidateSingleNode(clinicalDocumentNode);

		Set<Detail> errors = validator.getDetails();
		assertThat("Must contain error", errors,
				hasValidationErrorsIgnoringPath(CpcClinicalDocumentValidator.MISSING_PRACTICE_SITE_ADDRESS));
	}

}
