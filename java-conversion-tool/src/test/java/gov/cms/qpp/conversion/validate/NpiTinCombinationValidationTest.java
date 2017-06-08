package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder;
import gov.cms.qpp.conversion.decode.MultipleTinsDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

public class NpiTinCombinationValidationTest {
	private Node npiTinNodeOne;
	private Node npiTinNodeTwo;
	private Node clinicalDocumentNode;
	private Node npiTinCombinationNode;
	private NpiTinCombinationValidation validator;

	final static String NPI1 = "987654321";
	final static String TIN1 = "123456789";
	final static String NPI2 = "333222111";
	final static String TIN2 = "111222333";

	@Before
	public void createMultipleTinNode() {
		npiTinNodeOne = new Node(TemplateId.NPI_TIN_ID);
		npiTinNodeOne.putValue(MultipleTinsDecoder.NATIONAL_PROVIDER_IDENTIFIER, NPI1);
		npiTinNodeOne.putValue(MultipleTinsDecoder.TAX_PAYER_IDENTIFICATION_NUMBER, TIN1);

		npiTinNodeTwo = new Node(TemplateId.NPI_TIN_ID);
		npiTinNodeTwo.putValue(MultipleTinsDecoder.NATIONAL_PROVIDER_IDENTIFIER, NPI2);
		npiTinNodeTwo.putValue(MultipleTinsDecoder.TAX_PAYER_IDENTIFICATION_NUMBER, TIN2);

		validator = new NpiTinCombinationValidation();
	}

	@Test
	public void testValidMipsIndividualNpiTinCombination() {
		createClinicalDocumentWithProgramType(ClinicalDocumentDecoder.MIPS_PROGRAM_NAME,
				ClinicalDocumentDecoder.ENTITY_INDIVIDUAL);

		npiTinCombinationNode = new Node(TemplateId.QRDA_CATEGORY_III_REPORT_V3);
		npiTinCombinationNode.addChildNode(clinicalDocumentNode);
		npiTinCombinationNode.addChildNode(npiTinNodeOne);

		validator.internalValidateSingleNode(npiTinCombinationNode);

		assertThat("Must validate with no errors", validator.getValidationErrors() , hasSize(0));
	}

	@Test
	public void testInvalidMipsIndividualNpiTinCombination() {
		createClinicalDocumentWithProgramType(ClinicalDocumentDecoder.MIPS_PROGRAM_NAME,
				ClinicalDocumentDecoder.ENTITY_INDIVIDUAL);

		npiTinCombinationNode = new Node(TemplateId.QRDA_CATEGORY_III_REPORT_V3);
		npiTinCombinationNode.addChildNode(clinicalDocumentNode);
		npiTinCombinationNode.addChildNode(npiTinNodeOne);
		npiTinCombinationNode.addChildNode(npiTinNodeTwo);

		validator.internalValidateSingleNode(npiTinCombinationNode);

		assertThat("Must validate with the correct error", validator.getValidationErrors().get(0).getErrorText() ,
				is(NpiTinCombinationValidation.ONLY_ONE_NPI_TIN_COMBINATION_ALLOWED));
	}

	@Test
	public void testInvalidEntityType() {
		createClinicalDocumentWithProgramType(ClinicalDocumentDecoder.MIPS_PROGRAM_NAME,
				"Invalid");

		npiTinCombinationNode = new Node(TemplateId.QRDA_CATEGORY_III_REPORT_V3);
		npiTinCombinationNode.addChildNode(clinicalDocumentNode);
		npiTinCombinationNode.addChildNode(npiTinNodeOne);
		npiTinCombinationNode.addChildNode(npiTinNodeTwo);

		validator.internalValidateSingleNode(npiTinCombinationNode);

		assertThat("Must validate with no errors", validator.getValidationErrors() , hasSize(0));
	}

	private void createClinicalDocumentWithProgramType(final String programName, final String entityType) {
		clinicalDocumentNode = new Node(TemplateId.CLINICAL_DOCUMENT);
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.PROGRAM_NAME, programName);
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.ENTITY_TYPE, entityType);gi
	}
}
