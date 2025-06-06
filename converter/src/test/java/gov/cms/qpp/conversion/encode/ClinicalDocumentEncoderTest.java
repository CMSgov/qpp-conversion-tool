package gov.cms.qpp.conversion.encode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;
import static gov.cms.qpp.conversion.model.Constants.*;

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

		numeratorValueNode = new Node(TemplateId.PI_AGGREGATE_COUNT);

		numeratorValueNode.putValue(AGGREGATE_COUNT, "400");

		numeratorValueNode2 = new Node(TemplateId.PI_AGGREGATE_COUNT);
		numeratorValueNode2.putValue(AGGREGATE_COUNT, "500");

		numeratorValueNode3 = new Node(TemplateId.PI_AGGREGATE_COUNT);
		numeratorValueNode3.putValue(AGGREGATE_COUNT, "400");

		denominatorValueNode = new Node(TemplateId.PI_AGGREGATE_COUNT);
		denominatorValueNode.putValue(AGGREGATE_COUNT, "600");

		denominatorValueNode2 = new Node(TemplateId.PI_AGGREGATE_COUNT);
		denominatorValueNode2.putValue(AGGREGATE_COUNT, "700");

		denominatorValueNode3 = new Node(TemplateId.PI_AGGREGATE_COUNT);
		denominatorValueNode3.putValue(AGGREGATE_COUNT, "600");

		aciProportionDenominatorNode = new Node(TemplateId.PI_DENOMINATOR);
		aciProportionDenominatorNode.addChildNode(denominatorValueNode);

		aciProportionDenominatorNode2 = new Node(TemplateId.PI_DENOMINATOR);
		aciProportionDenominatorNode2.addChildNode(denominatorValueNode2);

		aciProportionDenominatorNode3 = new Node(TemplateId.PI_DENOMINATOR);
		aciProportionDenominatorNode3.addChildNode(denominatorValueNode3);

		aciProportionNumeratorNode = new Node(TemplateId.PI_NUMERATOR);
		aciProportionNumeratorNode.addChildNode(numeratorValueNode);

		aciProportionNumeratorNode2 = new Node(TemplateId.PI_NUMERATOR);
		aciProportionNumeratorNode2.addChildNode(numeratorValueNode2);

		aciProportionNumeratorNode3 = new Node(TemplateId.PI_NUMERATOR);
		aciProportionNumeratorNode3.addChildNode(numeratorValueNode3);

		aciProportionMeasureNode = new Node(TemplateId.PI_NUMERATOR_DENOMINATOR);
		aciProportionMeasureNode.addChildNode(aciProportionNumeratorNode);
		aciProportionMeasureNode.addChildNode(aciProportionDenominatorNode);
		aciProportionMeasureNode.putValue(MEASURE_ID, "ACI-PEA-1");

		aciProportionMeasureNode2 = new Node(TemplateId.PI_NUMERATOR_DENOMINATOR);
		aciProportionMeasureNode2.addChildNode(aciProportionNumeratorNode2);
		aciProportionMeasureNode2.addChildNode(aciProportionDenominatorNode2);
		aciProportionMeasureNode2.putValue(MEASURE_ID, "ACI_EP_1");

		aciProportionMeasureNode3 = new Node(TemplateId.PI_NUMERATOR_DENOMINATOR);
		aciProportionMeasureNode3.addChildNode(aciProportionNumeratorNode3);
		aciProportionMeasureNode3.addChildNode(aciProportionDenominatorNode3);
		aciProportionMeasureNode3.putValue(MEASURE_ID, "ACI_CCTPE_3");

		aciSectionNode = new Node(TemplateId.PI_SECTION_V3);
		aciSectionNode.putValue(CATEGORY, "aci");
		aciSectionNode.addChildNode(aciProportionMeasureNode);
		aciSectionNode.addChildNode(aciProportionMeasureNode2);
		aciSectionNode.addChildNode(aciProportionMeasureNode3);

		aciReportingPerformanceNode = new Node(TemplateId.REPORTING_PARAMETERS_ACT);
		aciReportingPerformanceNode.putValue(PERFORMANCE_YEAR, "2017");
		aciReportingPerformanceNode.putValue(PERFORMANCE_START, "20170101");
		aciReportingPerformanceNode.putValue(PERFORMANCE_END, "20171231");
		aciSectionNode.addChildNode(aciReportingPerformanceNode);

		clinicalDocumentNode = new Node(TemplateId.CLINICAL_DOCUMENT);
		clinicalDocumentNode.putValue(PROGRAM_NAME, "mips");
		clinicalDocumentNode.putValue(ENTITY_TYPE, "individual");
		clinicalDocumentNode.putValue(TAX_PAYER_IDENTIFICATION_NUMBER, "123456789");
		clinicalDocumentNode.putValue(NATIONAL_PROVIDER_IDENTIFIER, "2567891421");
		clinicalDocumentNode.putValue(PRACTICE_ID,  "AR000000" );
		clinicalDocumentNode.putValue(ENTITY_ID,  "x12345" );
		clinicalDocumentNode.putValue(VG_ID,  "x12345" );
		clinicalDocumentNode.putValue(CEHRT, "xxxxxxxxxx12345");
		clinicalDocumentNode.addChildNode(aciSectionNode);

		aciSectionNode.setParent(clinicalDocumentNode);

		nodes = new ArrayList<>();
		nodes.add(clinicalDocumentNode);
	}

	@Test
	void testPerformanceYear() {
		JsonWrapper testJsonWrapper = new JsonWrapper();
		ClinicalDocumentEncoder clinicalDocumentEncoder = new ClinicalDocumentEncoder(new Context());
		clinicalDocumentEncoder.internalEncode(testJsonWrapper, clinicalDocumentNode);
		Object performanceYear = testJsonWrapper.getInteger(PERFORMANCE_YEAR);

		assertThat(performanceYear).isEqualTo(2017);
	}

	@Test
	void testInternalEncode() throws EncodeException {
		JsonWrapper testJsonWrapper = new JsonWrapper();

		ClinicalDocumentEncoder clinicalDocumentEncoder = new ClinicalDocumentEncoder(new Context());
		clinicalDocumentEncoder.internalEncode(testJsonWrapper, clinicalDocumentNode);

		assertThat(testJsonWrapper.getString(ENTITY_TYPE))
				.isEqualTo("individual");
		assertThat(testJsonWrapper.getString(TAX_PAYER_IDENTIFICATION_NUMBER))
				.isEqualTo("123456789");
		assertThat(testJsonWrapper.getString(NATIONAL_PROVIDER_IDENTIFIER))
				.isEqualTo("2567891421");
	}

	@Test
	void testInternalEncodeWithoutMeasures() throws EncodeException {
		clinicalDocumentNode.getChildNodes().remove(aciSectionNode);
		JsonWrapper testJsonWrapper = new JsonWrapper();

		ClinicalDocumentEncoder clinicalDocumentEncoder = new ClinicalDocumentEncoder(new Context());
		clinicalDocumentEncoder.internalEncode(testJsonWrapper, clinicalDocumentNode);

		Map<?, ?> clinicalDocMap = ((Map<?, ?>) testJsonWrapper.toObject());

		assertThat(clinicalDocMap.get(MEASUREMENT_SETS))
				.isNull();
	}

	@Test
	void testInternalEncodeEmptyEntityId() throws EncodeException {
		clinicalDocumentNode.getChildNodes().remove(aciSectionNode);
		clinicalDocumentNode.putValue(PRACTICE_ID,"");
		JsonWrapper testJsonWrapper = new JsonWrapper();

		ClinicalDocumentEncoder clinicalDocumentEncoder = new ClinicalDocumentEncoder(new Context());
		clinicalDocumentEncoder.internalEncode(testJsonWrapper, clinicalDocumentNode);

		Map<?, ?> clinicalDocMap = ((Map<?, ?>) testJsonWrapper.toObject());

		assertThat(clinicalDocMap.get(PRACTICE_ID))
				.isNull();
	}

	@Test
	void testInternalEncodeNullEntityId() throws EncodeException {
		clinicalDocumentNode.getChildNodes().remove(aciSectionNode);
		clinicalDocumentNode.putValue(PRACTICE_ID,null);
		JsonWrapper testJsonWrapper = new JsonWrapper();

		ClinicalDocumentEncoder clinicalDocumentEncoder = new ClinicalDocumentEncoder(new Context());
		clinicalDocumentEncoder.internalEncode(testJsonWrapper, clinicalDocumentNode);

		Map<?, ?> clinicalDocMap = ((Map<?, ?>) testJsonWrapper.toObject());

		assertThat(clinicalDocMap.get(PRACTICE_ID))
				.isNull();
	}

	@Test
	void testClinicalDocumentEncoderIgnoresInvalidMeasurementSection() {
		Node reportingParamNode = new Node(TemplateId.REPORTING_PARAMETERS_ACT, clinicalDocumentNode);
		reportingParamNode.putValue(PERFORMANCE_START,"20170101");
		reportingParamNode.putValue(PERFORMANCE_END,"20171231");
		JsonWrapper testJsonWrapper = new JsonWrapper();
		String expectedSection = "aci";

		ClinicalDocumentEncoder clinicalDocumentEncoder = new ClinicalDocumentEncoder(new Context());
		clinicalDocumentEncoder.internalEncode(testJsonWrapper, clinicalDocumentNode);

		JsonWrapper measurementSets = getMeasurementSets(testJsonWrapper);
		String value = measurementSets.get(0).getString("category");

		assertThat(measurementSets.size()).isEqualTo(1);
		assertThat(value).isEqualTo(expectedSection);
	}

	@Test
	void testVirtualGroupIdEncode() {
		clinicalDocumentNode.putValue(ENTITY_TYPE, ENTITY_VIRTUAL_GROUP);

		JsonWrapper testJsonWrapper = new JsonWrapper();

		ClinicalDocumentEncoder clinicalDocumentEncoder = new ClinicalDocumentEncoder(new Context());
		clinicalDocumentEncoder.internalEncode(testJsonWrapper, clinicalDocumentNode);

		assertThat(testJsonWrapper.getString(ENTITY_ID))
			.isEqualTo("x12345"); // TODO asdf refactor equals to toObject maybe if not comparing to JsW
	}

	@Test
	void testApmExcludeNpiEncoding() throws EncodeException {
		JsonWrapper testJsonWrapper = new JsonWrapper();
		clinicalDocumentNode.putValue(ENTITY_TYPE, "apm");

		ClinicalDocumentEncoder clinicalDocumentEncoder = new ClinicalDocumentEncoder(new Context());
		clinicalDocumentEncoder.internalEncode(testJsonWrapper, clinicalDocumentNode);

		Map<?, ?> clinicalDocMap = ((Map<?, ?>) testJsonWrapper.toObject());

		assertThat(clinicalDocMap.get(NATIONAL_PROVIDER_IDENTIFIER))
			.isNull();
	}

	@Test
	void testApmIncludesEntityID() throws EncodeException {
		JsonWrapper testJsonWrapper = new JsonWrapper();
		clinicalDocumentNode.putValue(APM_ENTITY_ID, "apm");
		clinicalDocumentNode.putValue(ENTITY_TYPE, "apm");
		clinicalDocumentNode.putValue(RAW_PROGRAM_NAME, "MIPS_APP1_APMENTITY");
		clinicalDocumentNode.putValue(PROGRAM_NAME, "app1");

		ClinicalDocumentEncoder clinicalDocumentEncoder = new ClinicalDocumentEncoder(new Context());
		clinicalDocumentEncoder.internalEncode(testJsonWrapper, clinicalDocumentNode);

		Map<?, ?> clinicalDocMap = ((Map<?, ?>) testJsonWrapper.toObject());

		assertThat(clinicalDocMap.get(NATIONAL_PROVIDER_IDENTIFIER))
			.isNull();

		assertThat(clinicalDocMap.get(ENTITY_ID))
			.isNotNull();
	}

	@Test
	void testMeasurementSetIncludesSource() throws EncodeException {
		JsonWrapper testJsonWrapper = new JsonWrapper();

		ClinicalDocumentEncoder clinicalDocumentEncoder = new ClinicalDocumentEncoder(new Context());
		clinicalDocumentEncoder.internalEncode(testJsonWrapper, clinicalDocumentNode);

		@SuppressWarnings("unchecked")
		List<JsonWrapper> measurementSets = (List<JsonWrapper>) testJsonWrapper.get("measurementSets").toObject();
		assertThat(measurementSets).isNotEmpty();
		for (JsonWrapper measurement : measurementSets) {
			assertThat(measurement.get("source")).isNotNull();
		}
	}

	@Test
	void testAppApmIncludesEntityID() throws EncodeException {
		JsonWrapper testJsonWrapper = new JsonWrapper();
		clinicalDocumentNode.putValue(APM_ENTITY_ID, "apm");
		clinicalDocumentNode.putValue(ENTITY_TYPE, "apm");
		clinicalDocumentNode.putValue(RAW_PROGRAM_NAME, "MIPS_APP1_APMENTITY");
		clinicalDocumentNode.putValue(PROGRAM_NAME, "app1");

		ClinicalDocumentEncoder clinicalDocumentEncoder = new ClinicalDocumentEncoder(new Context());
		clinicalDocumentEncoder.internalEncode(testJsonWrapper, clinicalDocumentNode);

		Map<?, ?> clinicalDocMap = ((Map<?, ?>) testJsonWrapper.toObject());

		assertThat(clinicalDocMap.get(NATIONAL_PROVIDER_IDENTIFIER))
				.isNull();

		assertThat(clinicalDocMap.get(ENTITY_ID))
				.isNotNull();
	}

	@Test
	void testMvpIdReplacesProgramName() throws EncodeException {
		JsonWrapper testJsonWrapper = new JsonWrapper();
		clinicalDocumentNode.putValue(MVP_ID, "G0053");

		ClinicalDocumentEncoder clinicalDocumentEncoder = new ClinicalDocumentEncoder(new Context());
		clinicalDocumentEncoder.internalEncode(testJsonWrapper, clinicalDocumentNode);

		JsonWrapper measurementSets = getMeasurementSets(testJsonWrapper);
		String programName = measurementSets.get(0).getString("programName");

		assertThat(programName).isEqualTo("G0053");
	}

	@Test
	void testSubgroupIncludesSubgroupId() throws EncodeException {
		JsonWrapper testJsonWrapper = new JsonWrapper();
		clinicalDocumentNode.putValue(MVP_ID, "G0053");
		clinicalDocumentNode.putValue(ENTITY_TYPE, "subgroup");
		clinicalDocumentNode.putValue(SUBGROUP_ID, "SG-00000001");

		ClinicalDocumentEncoder clinicalDocumentEncoder = new ClinicalDocumentEncoder(new Context());
		clinicalDocumentEncoder.internalEncode(testJsonWrapper, clinicalDocumentNode);


		JsonWrapper measurementSets = getMeasurementSets(testJsonWrapper);
		String programName = measurementSets.get(0).getString("programName");

		assertThat(programName).isEqualTo("G0053");
		assertThat(testJsonWrapper.getString(ENTITY_TYPE)).isEqualTo("subgroup");
		assertThat(testJsonWrapper.getString(ENTITY_ID)).isEqualTo("SG-00000001");
	}


	private JsonWrapper getMeasurementSets(JsonWrapper clinicalDocument) {
		return clinicalDocument.get("measurementSets");
	}
}
