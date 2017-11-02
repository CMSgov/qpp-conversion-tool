package gov.cms.qpp.conversion.validate;

import static com.google.common.truth.Truth.assertWithMessage;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ErrorCode;
import gov.cms.qpp.conversion.model.error.correspondence.DetailsErrorEquals;

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

		assertWithMessage("Must have no errors")
				.that(errors).isEmpty();
	}

	@Test
	public void missingPracticeSiteAddress() {
		Node clinicalDocumentNode = createValidCpcPlusClinicalDocument();
		clinicalDocumentNode.removeValue(ClinicalDocumentDecoder.PRACTICE_SITE_ADDR);
		cpcValidator.internalValidateSingleNode(clinicalDocumentNode);
		Set<Detail> errors = cpcValidator.getDetails();

		assertWithMessage("Must contain error")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.CPC_CLINICAL_DOCUMENT_MISSING_PRACTICE_SITE_ADDRESS);
	}

	@Test
	public void emptyPracticeSiteAddress() {
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
	public void testCpcPlusMultipleApm() {
		Node clinicalDocumentNode = createValidCpcPlusClinicalDocument();

		// extra APM
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.ENTITY_ID, "1234567", false);
		cpcValidator.internalValidateSingleNode(clinicalDocumentNode);

		assertWithMessage("Must validate with the correct error")
				.that(cpcValidator.getDetails()).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.CPC_CLINICAL_DOCUMENT_ONLY_ONE_APM_ALLOWED);
	}

	@Test
	public void testCpcPlusNoApm() {
		Node clinicalDocumentNode = createValidCpcPlusClinicalDocument();
		clinicalDocumentNode.removeValue(ClinicalDocumentDecoder.ENTITY_ID);
		cpcValidator.internalValidateSingleNode(clinicalDocumentNode);

		assertWithMessage("Must validate with the correct error")
				.that(cpcValidator.getDetails()).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.CPC_CLINICAL_DOCUMENT_ONLY_ONE_APM_ALLOWED);
	}

	@Test
	public void testCpcPlusMissingMeasureSection() {
		Node clinicalDocumentNode = createCpcPlusClinicalDocument();
		cpcValidator.internalValidateSingleNode(clinicalDocumentNode);

		assertWithMessage("Must validate with the correct error")
				.that(cpcValidator.getDetails()).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.CPC_CLINICAL_DOCUMENT_ONE_MEASURE_SECTION_REQUIRED);
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
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.ENTITY_ID, "AR00000");

		return clinicalDocumentNode;
	}

	private void addMeasureSectionNode(Node clinicalDocumentNode) {
		Node measureSection = new Node(TemplateId.MEASURE_SECTION_V2);
		clinicalDocumentNode.addChildNode(measureSection);
	}

}
