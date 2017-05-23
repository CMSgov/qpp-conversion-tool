package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import org.junit.Before;
import org.junit.Test;

import static gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder.TAX_PAYER_IDENTIFICATION_NUMBER;
import static gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder.NATIONAL_PROVIDER_IDENTIFIER;
import static gov.cms.qpp.conversion.decode.MultipleTinsDecoder.NPI_TIN_ID;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MultipleTinsEncoderTest {
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
	private Node multipleTinsNode;
	private JsonWrapper testWrapper;

	@Before
	public void createMultipleTinNode() {
		npiTinNodeOne = new Node(NPI_TIN_ID);
		npiTinNodeOne.putValue(TAX_PAYER_IDENTIFICATION_NUMBER, "123456789");
		npiTinNodeOne.putValue(NATIONAL_PROVIDER_IDENTIFIER, "987654321");

		npiTinNodeTwo = new Node(NPI_TIN_ID);
		npiTinNodeTwo.putValue(TAX_PAYER_IDENTIFICATION_NUMBER, "111222333");
		npiTinNodeTwo.putValue(NATIONAL_PROVIDER_IDENTIFIER, "333222111");

		reportingParametersActNode = new Node(TemplateId.REPORTING_PARAMETERS_ACT.getTemplateId());
		reportingParametersActNode.putValue("performanceStart", "20170101");
		reportingParametersActNode.putValue("performanceEnd", "20171231");

		reportingParametersSectionNode = new Node(TemplateId.REPORTING_PARAMETERS_SECTION.getTemplateId());
		reportingParametersSectionNode.addChildNode(reportingParametersActNode);

		numeratorValueNode = new Node();
		numeratorValueNode.setId(TemplateId.ACI_AGGREGATE_COUNT.getTemplateId());
		numeratorValueNode.putValue("aggregateCount", "400");

		aciProportionNumeratorNode = new Node();
		aciProportionNumeratorNode.setId(TemplateId.ACI_NUMERATOR.getTemplateId());
		aciProportionNumeratorNode.addChildNode(numeratorValueNode);

		denominatorValueNode = new Node();
		denominatorValueNode.setId(TemplateId.ACI_AGGREGATE_COUNT.getTemplateId());
		denominatorValueNode.putValue("aggregateCount", "600");

		aciProportionDenominatorNode = new Node();
		aciProportionDenominatorNode.setId(TemplateId.ACI_DENOMINATOR.getTemplateId());
		aciProportionDenominatorNode.addChildNode(denominatorValueNode);

		aciProportionMeasureNode = new Node();
		aciProportionMeasureNode.setId(TemplateId.ACI_NUMERATOR_DENOMINATOR.getTemplateId());
		aciProportionMeasureNode.addChildNode(aciProportionNumeratorNode);
		aciProportionMeasureNode.addChildNode(aciProportionDenominatorNode);
		aciProportionMeasureNode.putValue("measureId", "ACI-PEA-1");

		aciSectionNode = new Node();
		aciSectionNode.setId(TemplateId.ACI_SECTION.getTemplateId());
		aciSectionNode.putValue("category", "aci");
		aciSectionNode.addChildNode(aciProportionMeasureNode);

		clinicalDocumentNode = new Node(TemplateId.CLINICAL_DOCUMENT.getTemplateId());
		clinicalDocumentNode.putValue("programName", "mips");
		clinicalDocumentNode.putValue("entityType", "individual");
		clinicalDocumentNode.addChildNode(reportingParametersSectionNode);
		clinicalDocumentNode.addChildNode(aciSectionNode);

		multipleTinsNode = new Node(TemplateId.MULTIPLE_TINS.getTemplateId());
		multipleTinsNode.addChildNode(clinicalDocumentNode);
		multipleTinsNode.addChildNode(npiTinNodeOne);
		multipleTinsNode.addChildNode(npiTinNodeTwo);

		testWrapper = new JsonWrapper();
		JsonOutputEncoder multipleTinsEncoder = new MultipleTinsEncoder();
		multipleTinsEncoder.internalEncode(testWrapper, multipleTinsNode);
	}

	@Test
	public void testFirstTinNpiCombinationConversion() {
		LinkedHashMap<String, Object> firstMeasurementMap = getClinicalDocumentMeasurementFromIndex(0);

		assertThat("Must contain the correct TIN",
				firstMeasurementMap.get(TAX_PAYER_IDENTIFICATION_NUMBER), is("123456789"));
		assertThat("Must contain the correct NPI",
				firstMeasurementMap.get(NATIONAL_PROVIDER_IDENTIFIER), is("987654321"));
	}

	@Test
	public void testSecondTinNpiCombinationConversion() {
		LinkedHashMap<String, Object> secondMeasurementMap = getClinicalDocumentMeasurementFromIndex(1);

		assertThat("Must contain the correct TIN",
				secondMeasurementMap.get(TAX_PAYER_IDENTIFICATION_NUMBER), is("111222333"));
		assertThat("Must contain the correct NPI",
				secondMeasurementMap.get(NATIONAL_PROVIDER_IDENTIFIER), is("333222111"));
	}

	private LinkedHashMap<String, Object> getClinicalDocumentMeasurementFromIndex(Integer index) {
		return ((LinkedList<LinkedHashMap<String, LinkedList<LinkedHashMap<String, Object>>>>)
						testWrapper.getObject()).get(index).get("measurementSets").get(0);
	}
}
