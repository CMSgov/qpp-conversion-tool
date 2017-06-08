package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder;
import gov.cms.qpp.conversion.decode.MultipleTinsDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

public class NpiTinCombinationValidationTest {
	private static final String CONTAINS_CORRECT_ERROR = "Must validate with the correct error";

	private Node firstValidNpiTinNode;
	private Node secondValidNpiTinNode;
	private Node validMipsGroupNpiTinNode;
	private Node missingTinNode;
	private Node clinicalDocumentNode;
	private Node npiTinCombinationNode;
	private NpiTinCombinationValidation validator;

	final static String NPI1 = "187654321";
	final static String TIN1 = "123456781";
	final static String NPI2 = "233222112";
	final static String TIN2 = "211222332";

	@Before
	public void setUpNpiTinCombinations() {
		validator = new NpiTinCombinationValidation();
		firstValidNpiTinNode = createNpiTinNode(NPI1, TIN1);
		secondValidNpiTinNode = createNpiTinNode(NPI2, TIN2);
		validMipsGroupNpiTinNode = createNpiTinNode(null , TIN1);
		missingTinNode = createNpiTinNode(NPI1, null);
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

		assertThat("Must validate with no errors", validator.getValidationErrors() , empty());
	}

	@Test
	public void testInvalidMipsIndividualNpiTinCombination() {
		createClinicalDocumentWithProgramType(ClinicalDocumentDecoder.MIPS_PROGRAM_NAME,
				ClinicalDocumentDecoder.ENTITY_INDIVIDUAL);

		npiTinCombinationNode = new Node(TemplateId.QRDA_CATEGORY_III_REPORT_V3);
		npiTinCombinationNode.addChildNode(clinicalDocumentNode);
		npiTinCombinationNode.addChildNode(firstValidNpiTinNode);
		npiTinCombinationNode.addChildNode(secondValidNpiTinNode);

		validator.internalValidateSingleNode(npiTinCombinationNode);

		assertThat(CONTAINS_CORRECT_ERROR, validator.getValidationErrors().get(0).getErrorText(),
				is(NpiTinCombinationValidation.ONLY_ONE_NPI_TIN_COMBINATION_ALLOWED));
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

		assertThat("Must validate with no errors", validator.getValidationErrors() , hasSize(0));
	}

	@Test
	public void testValidMipsGroupNpiTinCombination() {
		createClinicalDocumentWithProgramType(ClinicalDocumentDecoder.MIPS_PROGRAM_NAME,
				ClinicalDocumentDecoder.ENTITY_GROUP);

		npiTinCombinationNode = new Node(TemplateId.QRDA_CATEGORY_III_REPORT_V3);
		npiTinCombinationNode.addChildNode(clinicalDocumentNode);
		npiTinCombinationNode.addChildNode(validMipsGroupNpiTinNode);

		validator.internalValidateSingleNode(npiTinCombinationNode);

		assertThat("Must validate with no errors", validator.getValidationErrors() , empty());
	}

	@Test
	public void testMipsGroupMultipleNpiTinCombinationFails() {
		createClinicalDocumentWithProgramType(ClinicalDocumentDecoder.MIPS_PROGRAM_NAME,
				ClinicalDocumentDecoder.ENTITY_GROUP);

		npiTinCombinationNode = new Node(TemplateId.QRDA_CATEGORY_III_REPORT_V3);
		npiTinCombinationNode.addChildNode(clinicalDocumentNode);
		npiTinCombinationNode.addChildNode(firstValidNpiTinNode);
		npiTinCombinationNode.addChildNode(validMipsGroupNpiTinNode);

		validator.internalValidateSingleNode(npiTinCombinationNode);

		assertThat(CONTAINS_CORRECT_ERROR, validator.getValidationErrors().get(0).getErrorText(),
				is(NpiTinCombinationValidation.ONLY_ONE_NPI_TIN_COMBINATION_ALLOWED));
	}

	@Test
	public void testMipsGroupContainsNpiFails() {
		createClinicalDocumentWithProgramType(ClinicalDocumentDecoder.MIPS_PROGRAM_NAME,
				ClinicalDocumentDecoder.ENTITY_GROUP);

		npiTinCombinationNode = new Node(TemplateId.QRDA_CATEGORY_III_REPORT_V3);
		npiTinCombinationNode.addChildNode(clinicalDocumentNode);
		npiTinCombinationNode.addChildNode(firstValidNpiTinNode);

		validator.internalValidateSingleNode(npiTinCombinationNode);

		assertThat(CONTAINS_CORRECT_ERROR, validator.getValidationErrors().get(0).getErrorText(),
				is(NpiTinCombinationValidation.NO_NPI_ALLOWED));
	}

	@Test
	public void testMipsGroupMissingTinFails() {
		createClinicalDocumentWithProgramType(ClinicalDocumentDecoder.MIPS_PROGRAM_NAME,
				ClinicalDocumentDecoder.ENTITY_GROUP);

		npiTinCombinationNode = new Node(TemplateId.QRDA_CATEGORY_III_REPORT_V3);
		npiTinCombinationNode.addChildNode(clinicalDocumentNode);
		npiTinCombinationNode.addChildNode(missingTinNode);

		validator.internalValidateSingleNode(npiTinCombinationNode);

		assertThat(CONTAINS_CORRECT_ERROR, validator.getValidationErrors().get(0).getErrorText(),
				is(NpiTinCombinationValidation.CONTAINS_TAXPAYER_IDENTIFICATION_NUMBER));
	}

	private void createClinicalDocumentWithProgramType(final String programName, final String entityType) {
		clinicalDocumentNode = new Node(TemplateId.CLINICAL_DOCUMENT);
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.PROGRAM_NAME, programName);
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.ENTITY_TYPE, entityType);
	}
}
