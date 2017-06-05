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

		clinicalDocumentNode = new Node(TemplateId.CLINICAL_DOCUMENT);
		clinicalDocumentNode.putValue("programName", "mips");
		clinicalDocumentNode.putValue("entityType", "individual");
		clinicalDocumentNode.addChildNode(reportingParametersSectionNode);
		clinicalDocumentNode.addChildNode(aciSectionNode);

		multipleTinsNode = new Node(TemplateId.QRDA_CATEGORY_III_REPORT_V3);
		multipleTinsNode.addChildNode(clinicalDocumentNode);
		multipleTinsNode.addChildNode(npiTinNodeOne);
		multipleTinsNode.addChildNode(npiTinNodeTwo);

		testWrapper = new JsonWrapper();
		JsonOutputEncoder multipleTinsEncoder = new MultipleTinsEncoder();
		multipleTinsEncoder.internalEncode(testWrapper, multipleTinsNode);
	}

	@Test
	public void testFirstTinNpiCombinationConversion() {
		LinkedHashMap<String, Object> firstMeasurementMap = getIndexedClinicalDocumentFromWrapper(0);

		assertThat("Must contain the correct NPI",
			firstMeasurementMap.get(MultipleTinsDecoder.NATIONAL_PROVIDER_IDENTIFIER), is(NPI1));
		assertThat("Must contain the correct TIN",
			firstMeasurementMap.get(MultipleTinsDecoder.TAX_PAYER_IDENTIFICATION_NUMBER), is(TIN1));
	}

	@Test
	public void testSecondTinNpiCombinationConversion() {
		LinkedHashMap<String, Object> secondMeasurementMap = getIndexedClinicalDocumentFromWrapper(1);

		assertThat("Must contain the correct NPI",
				secondMeasurementMap.get(MultipleTinsDecoder.NATIONAL_PROVIDER_IDENTIFIER), is(NPI2));
		assertThat("Must contain the correct TIN",
			secondMeasurementMap.get(MultipleTinsDecoder.TAX_PAYER_IDENTIFICATION_NUMBER), is(TIN2));
	}

	@SuppressWarnings("unchecked")
	private LinkedHashMap<String, Object> getIndexedClinicalDocumentFromWrapper(Integer index) {
		return ((LinkedList<LinkedHashMap<String, Object>>)
				testWrapper.getObject()).get(index);
	}
}
