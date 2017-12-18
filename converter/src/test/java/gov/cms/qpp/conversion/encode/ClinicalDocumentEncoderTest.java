package gov.cms.qpp.conversion.encode;

import static com.google.common.truth.Truth.assertWithMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder;
import gov.cms.qpp.conversion.decode.ReportingParametersActDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

class ClinicalDocumentEncoderTest {

	private Node aciSectionNode;
	private Node aciReportingPerformanceNode;
	private Node aciProportionMeasureNode;
	private Node aciProportionNumeratorNode;
	private Node aciProportionDenominatorNode;
	private Node numeratorValueNode;
	private Node denominatorValueNode;
	private Node aciProportionMeasureNode2;
	private Node aciProportionNumeratorNode2;
	private Node aciProportionDenominatorNode2;
	private Node numeratorValueNode2;
	private Node denominatorValueNode2;
	private Node aciProportionMeasureNode3;
	private Node aciProportionNumeratorNode3;
	private Node aciProportionDenominatorNode3;
	private Node numeratorValueNode3;
	private Node denominatorValueNode3;
	private Node clinicalDocumentNode;
	private List<Node> nodes;
	private final String AGGREGATE_COUNT = "aggregateCount";
	private final String MEASURE_ID = "measureId";
	private final String CATEGORY = "category";
	private final String MEASUREMENT_SETS = "measurementSets";

	@BeforeEach
	void createNode() {

		numeratorValueNode = new Node(TemplateId.ACI_AGGREGATE_COUNT);

		numeratorValueNode.putValue(AGGREGATE_COUNT, "400");

		numeratorValueNode2 = new Node(TemplateId.ACI_AGGREGATE_COUNT);
		numeratorValueNode2.putValue(AGGREGATE_COUNT, "500");

		numeratorValueNode3 = new Node(TemplateId.ACI_AGGREGATE_COUNT);
		numeratorValueNode3.putValue(AGGREGATE_COUNT, "400");

		denominatorValueNode = new Node(TemplateId.ACI_AGGREGATE_COUNT);
		denominatorValueNode.putValue(AGGREGATE_COUNT, "600");

		denominatorValueNode2 = new Node(TemplateId.ACI_AGGREGATE_COUNT);
		denominatorValueNode2.putValue(AGGREGATE_COUNT, "700");

		denominatorValueNode3 = new Node(TemplateId.ACI_AGGREGATE_COUNT);
		denominatorValueNode3.putValue(AGGREGATE_COUNT, "600");

		aciProportionDenominatorNode = new Node(TemplateId.ACI_DENOMINATOR);
		aciProportionDenominatorNode.addChildNode(denominatorValueNode);

		aciProportionDenominatorNode2 = new Node(TemplateId.ACI_DENOMINATOR);
		aciProportionDenominatorNode2.addChildNode(denominatorValueNode2);

		aciProportionDenominatorNode3 = new Node(TemplateId.ACI_DENOMINATOR);
		aciProportionDenominatorNode3.addChildNode(denominatorValueNode3);

		aciProportionNumeratorNode = new Node(TemplateId.ACI_NUMERATOR);
		aciProportionNumeratorNode.addChildNode(numeratorValueNode);

		aciProportionNumeratorNode2 = new Node(TemplateId.ACI_NUMERATOR);
		aciProportionNumeratorNode2.addChildNode(numeratorValueNode2);

		aciProportionNumeratorNode3 = new Node(TemplateId.ACI_NUMERATOR);
		aciProportionNumeratorNode3.addChildNode(numeratorValueNode3);

		aciProportionMeasureNode = new Node(TemplateId.ACI_NUMERATOR_DENOMINATOR);
		aciProportionMeasureNode.addChildNode(aciProportionNumeratorNode);
		aciProportionMeasureNode.addChildNode(aciProportionDenominatorNode);
		aciProportionMeasureNode.putValue(MEASURE_ID, "ACI-PEA-1");

		aciProportionMeasureNode2 = new Node(TemplateId.ACI_NUMERATOR_DENOMINATOR);
		aciProportionMeasureNode2.addChildNode(aciProportionNumeratorNode2);
		aciProportionMeasureNode2.addChildNode(aciProportionDenominatorNode2);
		aciProportionMeasureNode2.putValue(MEASURE_ID, "ACI_EP_1");

		aciProportionMeasureNode3 = new Node(TemplateId.ACI_NUMERATOR_DENOMINATOR);
		aciProportionMeasureNode3.addChildNode(aciProportionNumeratorNode3);
		aciProportionMeasureNode3.addChildNode(aciProportionDenominatorNode3);
		aciProportionMeasureNode3.putValue(MEASURE_ID, "ACI_CCTPE_3");

		aciSectionNode = new Node(TemplateId.ACI_SECTION);
		aciSectionNode.putValue(CATEGORY, "aci");
		aciSectionNode.addChildNode(aciProportionMeasureNode);
		aciSectionNode.addChildNode(aciProportionMeasureNode2);
		aciSectionNode.addChildNode(aciProportionMeasureNode3);

		aciReportingPerformanceNode = new Node(TemplateId.REPORTING_PARAMETERS_ACT);
		aciReportingPerformanceNode.putValue(ReportingParametersActDecoder.PERFORMANCE_YEAR, "2017");
		aciReportingPerformanceNode.putValue(ReportingParametersActDecoder.PERFORMANCE_START, "20170101");
		aciReportingPerformanceNode.putValue(ReportingParametersActDecoder.PERFORMANCE_END, "20171231");
		aciSectionNode.addChildNode(aciReportingPerformanceNode);

		clinicalDocumentNode = new Node(TemplateId.CLINICAL_DOCUMENT);
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.PROGRAM_NAME, "mips");
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.ENTITY_TYPE, "individual");
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.TAX_PAYER_IDENTIFICATION_NUMBER, "123456789");
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.NATIONAL_PROVIDER_IDENTIFIER, "2567891421");
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.ENTITY_ID,  "AR000000" );
		clinicalDocumentNode.addChildNode(aciSectionNode);

		nodes = new ArrayList<>();
		nodes.add(clinicalDocumentNode);
	}

	@Test
	void testPerformanceYear() {
		JsonWrapper testJsonWrapper = new JsonWrapper();
		ClinicalDocumentEncoder clinicalDocumentEncoder = new ClinicalDocumentEncoder(new Context());
		clinicalDocumentEncoder.internalEncode(testJsonWrapper, clinicalDocumentNode);
		Object performanceYear = testJsonWrapper.getValue(ReportingParametersActDecoder.PERFORMANCE_YEAR);

		assertWithMessage("performance year should be 2017")
				.that(performanceYear)
				.isEqualTo(2017);
	}

	@Test
	void testInternalEncode() throws EncodeException {
		JsonWrapper testJsonWrapper = new JsonWrapper();

		ClinicalDocumentEncoder clinicalDocumentEncoder = new ClinicalDocumentEncoder(new Context());
		clinicalDocumentEncoder.internalEncode(testJsonWrapper, clinicalDocumentNode);

		Map<?, ?> clinicalDocMap = ((Map<?, ?>) testJsonWrapper.getObject());

		assertWithMessage("Must have a correct entityType")
				.that(clinicalDocMap.get(ClinicalDocumentDecoder.ENTITY_TYPE))
				.isEqualTo("individual");
		assertWithMessage("Must have a correct taxpayerIdentificationNumber")
				.that(clinicalDocMap.get(ClinicalDocumentDecoder.TAX_PAYER_IDENTIFICATION_NUMBER))
				.isEqualTo("123456789");
		assertWithMessage("Must have a correct nationalProviderIdentifier")
				.that(clinicalDocMap.get(ClinicalDocumentDecoder.NATIONAL_PROVIDER_IDENTIFIER))
				.isEqualTo("2567891421");
	}

	@Test
	void testInternalEncodeNegative() throws EncodeException {
		Assertions.assertThrows(EncodeException.class, () -> {
			JsonWrapper testJsonWrapper = new JsonWrapper();

			ClinicalDocumentEncoder clinicalDocumentEncoder = new ClinicalDocumentEncoder(new Context());
			clinicalDocumentNode.addChildNode(new Node());
			clinicalDocumentEncoder.internalEncode(testJsonWrapper, clinicalDocumentNode);
		});
	}

	@Test
	void testInternalEncodeWithoutMeasures() throws EncodeException {
		clinicalDocumentNode.getChildNodes().remove(aciSectionNode);
		JsonWrapper testJsonWrapper = new JsonWrapper();

		ClinicalDocumentEncoder clinicalDocumentEncoder = new ClinicalDocumentEncoder(new Context());
		clinicalDocumentEncoder.internalEncode(testJsonWrapper, clinicalDocumentNode);

		Map<?, ?> clinicalDocMap = ((Map<?, ?>) testJsonWrapper.getObject());

		assertWithMessage("Must not contain a measure because the measurements are missing.")
				.that(clinicalDocMap.get(MEASUREMENT_SETS))
				.isNull();
	}

	@Test
	void testInternalEncodeEmptyEntityId() throws EncodeException {
		clinicalDocumentNode.getChildNodes().remove(aciSectionNode);
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.ENTITY_ID,"");
		JsonWrapper testJsonWrapper = new JsonWrapper();

		ClinicalDocumentEncoder clinicalDocumentEncoder = new ClinicalDocumentEncoder(new Context());
		clinicalDocumentEncoder.internalEncode(testJsonWrapper, clinicalDocumentNode);

		Map<?, ?> clinicalDocMap = ((Map<?, ?>) testJsonWrapper.getObject());

		assertWithMessage("Must not contain an Entity Id.")
				.that(clinicalDocMap.get(ClinicalDocumentDecoder.ENTITY_ID))
				.isNull();
	}
	@Test
	void testInternalEncodeNullEntityId() throws EncodeException {
		clinicalDocumentNode.getChildNodes().remove(aciSectionNode);
		clinicalDocumentNode.putValue(ClinicalDocumentDecoder.ENTITY_ID,null);
		JsonWrapper testJsonWrapper = new JsonWrapper();

		ClinicalDocumentEncoder clinicalDocumentEncoder = new ClinicalDocumentEncoder(new Context());
		clinicalDocumentEncoder.internalEncode(testJsonWrapper, clinicalDocumentNode);

		Map<?, ?> clinicalDocMap = ((Map<?, ?>) testJsonWrapper.getObject());

		assertWithMessage("Must not contain an Entity Id.")
				.that(clinicalDocMap.get(ClinicalDocumentDecoder.ENTITY_ID))
				.isNull();
	}
}
