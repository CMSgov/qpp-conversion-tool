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
	private Node reportingParametersActNode;
	private Node reportingParametersSectionNode;
	private Node numeratorValueNode;
	private Node aciProportionNumeratorNode;
	private Node denominatorValueNode;
	private Node aciProportionDenominatorNode;
	private Node aciProportionMeasureNode;
	private Node aciSectionNode;
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

		reportingParametersActNode = new Node(TemplateId.REPORTING_PARAMETERS_ACT);
		reportingParametersActNode.putValue("performanceStart", "20170101");
		reportingParametersActNode.putValue("performanceEnd", "20171231");

		reportingParametersSectionNode = new Node(TemplateId.REPORTING_PARAMETERS_SECTION);
		reportingParametersSectionNode.addChildNode(reportingParametersActNode);

		numeratorValueNode = new Node();
		numeratorValueNode.setType(TemplateId.ACI_AGGREGATE_COUNT);
		numeratorValueNode.putValue("aggregateCount", "400");

		aciProportionNumeratorNode = new Node();
		aciProportionNumeratorNode.setType(TemplateId.ACI_NUMERATOR);
		aciProportionNumeratorNode.addChildNode(numeratorValueNode);

		denominatorValueNode = new Node();
		denominatorValueNode.setType(TemplateId.ACI_AGGREGATE_COUNT);
		denominatorValueNode.putValue("aggregateCount", "600");

		aciProportionDenominatorNode = new Node();
		aciProportionDenominatorNode.setType(TemplateId.ACI_DENOMINATOR);
		aciProportionDenominatorNode.addChildNode(denominatorValueNode);

		aciProportionMeasureNode = new Node();
		aciProportionMeasureNode.setType(TemplateId.ACI_NUMERATOR_DENOMINATOR);
		aciProportionMeasureNode.addChildNode(aciProportionNumeratorNode);
		aciProportionMeasureNode.addChildNode(aciProportionDenominatorNode);
		aciProportionMeasureNode.putValue("measureId", "ACI-PEA-1");

		aciSectionNode = new Node();
		aciSectionNode.setType(TemplateId.ACI_SECTION);
		aciSectionNode.putValue("category", "aci");
		aciSectionNode.addChildNode(aciProportionMeasureNode);

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

	private void createClinicalDocumentWithProgramType(final String programName, final String entityType) {
		clinicalDocumentNode = new Node(TemplateId.CLINICAL_DOCUMENT);
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.PROGRAM_NAME, programName);
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.ENTITY_TYPE, entityType);
		clinicalDocumentNode.addChildNode(reportingParametersSectionNode);
		clinicalDocumentNode.addChildNode(aciSectionNode);
	}
}
