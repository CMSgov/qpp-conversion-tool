package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.decode.MultipleTinsDecoder;
import gov.cms.qpp.conversion.decode.ReportingParametersActDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import org.junit.Before;
import org.junit.Test;

import static com.google.common.truth.Truth.assertWithMessage;

public class MultipleTinsEncoderTest {
	private Node npiTinNodeOne;
	private Node npiTinNodeTwo;
	private Node numeratorValueNode;
	private Node aciProportionNumeratorNode;
	private Node denominatorValueNode;
	private Node aciProportionDenominatorNode;
	private Node aciProportionMeasureNode;
	private Node aciSectionNode;
	private Node reportingPerformanceNode;
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

		reportingPerformanceNode = new Node(TemplateId.REPORTING_PARAMETERS_ACT);
		reportingPerformanceNode.putValue(ReportingParametersActDecoder.PERFORMANCE_YEAR, "2017");
		reportingPerformanceNode.putValue(ReportingParametersActDecoder.PERFORMANCE_START, "20170101");
		reportingPerformanceNode.putValue(ReportingParametersActDecoder.PERFORMANCE_END, "20171231");
		aciSectionNode.addChildNode(reportingPerformanceNode);

		clinicalDocumentNode = new Node(TemplateId.CLINICAL_DOCUMENT);
		clinicalDocumentNode.putValue("programName", "mips");
		clinicalDocumentNode.putValue("entityType", "individual");
		clinicalDocumentNode.addChildNode(aciSectionNode);

		multipleTinsNode = new Node(TemplateId.QRDA_CATEGORY_III_REPORT_V3);
		multipleTinsNode.addChildNode(clinicalDocumentNode);
		multipleTinsNode.addChildNode(npiTinNodeOne);
		multipleTinsNode.addChildNode(npiTinNodeTwo);

		testWrapper = new JsonWrapper();
		JsonOutputEncoder multipleTinsEncoder = new MultipleTinsEncoder(new Context());
		multipleTinsEncoder.internalEncode(testWrapper, multipleTinsNode);
	}

	@Test
	public void testFirstTinNpiCombinationConversion() {
		LinkedHashMap<String, Object> firstMeasurementMap = getIndexedClinicalDocumentFromWrapper(0);

		assertWithMessage("Must contain the correct NPI")
				.that(firstMeasurementMap.get(MultipleTinsDecoder.NATIONAL_PROVIDER_IDENTIFIER))
				.isEqualTo(NPI1);
		assertWithMessage("Must contain the correct TIN")
				.that(firstMeasurementMap.get(MultipleTinsDecoder.TAX_PAYER_IDENTIFICATION_NUMBER))
				.isEqualTo(TIN1);
	}

	@Test
	public void testSecondTinNpiCombinationConversion() {
		LinkedHashMap<String, Object> secondMeasurementMap = getIndexedClinicalDocumentFromWrapper(1);

		assertWithMessage("Must contain the correct NPI")
				.that(secondMeasurementMap.get(MultipleTinsDecoder.NATIONAL_PROVIDER_IDENTIFIER))
				.isEqualTo(NPI2);
		assertWithMessage("Must contain the correct TIN")
				.that(secondMeasurementMap.get(MultipleTinsDecoder.TAX_PAYER_IDENTIFICATION_NUMBER))
				.isEqualTo(TIN2);
	}

	@SuppressWarnings("unchecked")
	private LinkedHashMap<String, Object> getIndexedClinicalDocumentFromWrapper(Integer index) {
		return ((LinkedList<LinkedHashMap<String, Object>>)
				testWrapper.getObject()).get(index);
	}
}
