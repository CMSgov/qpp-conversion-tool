package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.decode.MultipleTinsDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.LinkedList;

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

	final static String NPI1 = "987654321";
	final static String TIN1 = "123456789";
	final static String NPI2 = "333222111";
	final static String TIN2 = "111222333";

	@Before
	public void createMultipleTinNode() {
		npiTinNodeOne = new Node(MultipleTinsDecoder.NPI_TIN_ID);
		npiTinNodeOne.putValue(MultipleTinsDecoder.NATIONAL_PROVIDER_IDENTIFIER, NPI1);
		npiTinNodeOne.putValue(MultipleTinsDecoder.TAX_PAYER_IDENTIFICATION_NUMBER, TIN1);

		npiTinNodeTwo = new Node(MultipleTinsDecoder.NPI_TIN_ID);
		npiTinNodeTwo.putValue(MultipleTinsDecoder.NATIONAL_PROVIDER_IDENTIFIER, NPI2);
		npiTinNodeTwo.putValue(MultipleTinsDecoder.TAX_PAYER_IDENTIFICATION_NUMBER, TIN2);

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

		assertThat("Must contain the correct NPI",
				firstMeasurementMap.get(MultipleTinsDecoder.NATIONAL_PROVIDER_IDENTIFIER), is(NPI1));
		assertThat("Must contain the correct TIN",
			firstMeasurementMap.get(MultipleTinsDecoder.TAX_PAYER_IDENTIFICATION_NUMBER), is(TIN1));
	}

	@Test
	public void testSecondTinNpiCombinationConversion() {
		LinkedHashMap<String, Object> secondMeasurementMap = getClinicalDocumentMeasurementFromIndex(1);

		assertThat("Must contain the correct NPI",
				secondMeasurementMap.get(MultipleTinsDecoder.NATIONAL_PROVIDER_IDENTIFIER), is(NPI2));
		assertThat("Must contain the correct TIN",
			secondMeasurementMap.get(MultipleTinsDecoder.TAX_PAYER_IDENTIFICATION_NUMBER), is(TIN2));
	}

	private LinkedHashMap<String, Object> getClinicalDocumentMeasurementFromIndex(Integer index) {
		return ((LinkedList<LinkedHashMap<String, LinkedList<LinkedHashMap<String, Object>>>>)
				testWrapper.getObject()).get(index).get("measurementSets").get(0);
	}
}
