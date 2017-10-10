package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder;
import gov.cms.qpp.conversion.decode.MultipleTinsDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.correspondence.DetailsMessageEquals;
import org.junit.Before;
import org.junit.Test;

import static com.google.common.truth.Truth.assertWithMessage;

public class NpiTinCombinationValidationTest {
	private Node firstValidNpiTinNode;
	private Node secondValidNpiTinNode;
	private Node validMipsGroupNpiTinNode;
	private Node clinicalDocumentNode;
	private Node npiTinCombinationNode;
	private NpiTinCombinationValidation validator;

	private final static String NPI1 = "187654321";
	private final static String TIN1 = "123456781";
	private final static String NPI2 = "233222112";
	private final static String TIN2 = "211222332";

	@Before
	public void setUpNpiTinCombinations() {
		validator = new NpiTinCombinationValidation();
		firstValidNpiTinNode = createNpiTinNode(NPI1, TIN1);
		secondValidNpiTinNode = createNpiTinNode(NPI2, TIN2);
		validMipsGroupNpiTinNode = createNpiTinNode(null , TIN1);
	}

	private Node createNpiTinNode(String npi, String tin) {
		Node node = new Node(TemplateId.NPI_TIN_ID);
		node.putValue(MultipleTinsDecoder.NATIONAL_PROVIDER_IDENTIFIER, npi);
		node.putValue(MultipleTinsDecoder.TAX_PAYER_IDENTIFICATION_NUMBER, tin);

		return node;
	}

	@Test
	public void testValidMipsIndividualNpiTinCombination() {
		createClinicalDocumentWithProgramType(ClinicalDocumentDecoder.MIPS_PROGRAM_NAME,
				ClinicalDocumentDecoder.ENTITY_INDIVIDUAL);

		npiTinCombinationNode = new Node(TemplateId.QRDA_CATEGORY_III_REPORT_V3);
		npiTinCombinationNode.addChildNode(clinicalDocumentNode);
		npiTinCombinationNode.addChildNode(firstValidNpiTinNode);

		validator.internalValidateSingleNode(npiTinCombinationNode);

		assertWithMessage("Must validate with no errors")
				.that(validator.getDetails()).isEmpty();
	}

	@Test
	public void testValidCpcPlusMultipleNpiTinCombination() {
		createClinicalDocumentWithProgramType(
				ClinicalDocumentDecoder.CPCPLUS_PROGRAM_NAME, "", "AR00000");

		npiTinCombinationNode = new Node(TemplateId.QRDA_CATEGORY_III_REPORT_V3);
		npiTinCombinationNode.addChildNode(clinicalDocumentNode);
		npiTinCombinationNode.addChildNode(firstValidNpiTinNode);
		npiTinCombinationNode.addChildNode(secondValidNpiTinNode);

		validator.internalValidateSingleNode(npiTinCombinationNode);

		assertWithMessage("Must validate with no errors")
				.that(validator.getDetails()).isEmpty();
	}

	@Test
	public void testValidCpcPlusIndividualNpiTinCombination() {
		createClinicalDocumentWithProgramType(
				ClinicalDocumentDecoder.CPCPLUS_PROGRAM_NAME, "", "AR00000");

		npiTinCombinationNode = new Node(TemplateId.QRDA_CATEGORY_III_REPORT_V3);
		npiTinCombinationNode.addChildNode(clinicalDocumentNode);
		npiTinCombinationNode.addChildNode(firstValidNpiTinNode);

		validator.internalValidateSingleNode(npiTinCombinationNode);

		assertWithMessage("Must validate with no errors")
				.that(validator.getDetails()).isEmpty();
	}

	@Test
	public void testInvalidEntityType() {
		createClinicalDocumentWithProgramType(ClinicalDocumentDecoder.MIPS_PROGRAM_NAME,
				"Invalid");

		npiTinCombinationNode = new Node(TemplateId.QRDA_CATEGORY_III_REPORT_V3);
		npiTinCombinationNode.addChildNode(clinicalDocumentNode);
		npiTinCombinationNode.addChildNode(firstValidNpiTinNode);
		npiTinCombinationNode.addChildNode(secondValidNpiTinNode);

		validator.internalValidateSingleNode(npiTinCombinationNode);

		assertWithMessage("Must validate with no errors")
				.that(validator.getDetails()).isEmpty();
	}

	@Test
	public void testValidMipsGroupNpiTinCombination() {
		createClinicalDocumentWithProgramType(ClinicalDocumentDecoder.MIPS_PROGRAM_NAME,
				ClinicalDocumentDecoder.ENTITY_GROUP);

		npiTinCombinationNode = new Node(TemplateId.QRDA_CATEGORY_III_REPORT_V3);
		npiTinCombinationNode.addChildNode(clinicalDocumentNode);
		npiTinCombinationNode.addChildNode(validMipsGroupNpiTinNode);

		validator.internalValidateSingleNode(npiTinCombinationNode);

		assertWithMessage("Must validate with no errors")
				.that(validator.getDetails()).isEmpty();
	}

	@Test
	public void testClinicalDocumentNotPresent() {
		Node rootWithoutClinicalDocument = new Node(TemplateId.QRDA_CATEGORY_III_REPORT_V3);
		validator.internalValidateSingleNode(rootWithoutClinicalDocument);

		assertWithMessage("error should be about missing Clinical Document node")
				.that(validator.getDetails()).comparingElementsUsing(DetailsMessageEquals.INSTANCE)
				.containsExactly(NpiTinCombinationValidation.CLINICAL_DOCUMENT_REQUIRED);
	}

	@Test
	public void testTooManyClinicalDocumentNodes() {
		Node rootWithTwoClinicalDocument = new Node(TemplateId.QRDA_CATEGORY_III_REPORT_V3);
		Node clinicalDocumentNode = new Node(TemplateId.CLINICAL_DOCUMENT);
		Node clinicalDocumentNode2 = new Node(TemplateId.CLINICAL_DOCUMENT);
		rootWithTwoClinicalDocument.addChildNodes(clinicalDocumentNode, clinicalDocumentNode2);

		validator.internalValidateSingleNode(rootWithTwoClinicalDocument);

		assertWithMessage("error should be about too many Clinical Document nodes")
				.that(validator.getDetails()).comparingElementsUsing(DetailsMessageEquals.INSTANCE)
				.containsExactly(NpiTinCombinationValidation.EXACTLY_ONE_DOCUMENT_ALLOWED);
	}

	private void createClinicalDocumentWithProgramType(final String programName, final String entityType,
													   final String entityId) {
		createClinicalDocumentWithProgramType(programName, entityType);
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.ENTITY_ID, entityId);
	}

	private void createClinicalDocumentWithProgramType(final String programName, final String entityType) {
		clinicalDocumentNode = new Node(TemplateId.CLINICAL_DOCUMENT);
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.PROGRAM_NAME, programName);
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.ENTITY_TYPE, entityType);
	}
}
