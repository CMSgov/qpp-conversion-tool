package gov.cms.qpp.conversion.encode;

import static com.google.common.truth.Truth.assertThat;

import com.jayway.jsonpath.TypeRef;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.decode.MeasureDataDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.validation.MeasureConfigs;
import gov.cms.qpp.conversion.model.validation.SubPopulationLabel;
import gov.cms.qpp.conversion.util.JsonHelper;

import java.util.List;

class QualityMeasureIdEncoderTest {

	private Node qualityMeasureId;
	private Node populationNode;
	private Node denomExclusionNode;
	private Node denominatorExceptionNode;
	private Node numeratorNode;
	private Node denominatorNode;
	private Node aggregateCountNode;
	private JsonWrapper wrapper;
	private QualityMeasureIdEncoder encoder;
	private String type = "type";
	private static final String ELIGIBLE_POPULATION = "eligiblePopulation";

	@BeforeEach
	void setUp() {
		qualityMeasureId = new Node(TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V4);
		qualityMeasureId.putValue("measureId", "40280382-6258-7581-0162-92d6e6db1680");

		aggregateCountNode = new Node(TemplateId.PI_AGGREGATE_COUNT);
		aggregateCountNode.putValue("aggregateCount", "600");

		Node paymentNode = new Node(TemplateId.PAYER_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2);
		paymentNode.putValue("place", "holder");

		populationNode = new Node(TemplateId.MEASURE_DATA_CMS_V4);
		populationNode.putValue(type, SubPopulationLabel.IPOP.name());
		populationNode.addChildNode(aggregateCountNode);

		denomExclusionNode = new Node(TemplateId.MEASURE_DATA_CMS_V4);
		denomExclusionNode.putValue(type, SubPopulationLabel.DENEX.name());
		denomExclusionNode.addChildNode(aggregateCountNode);

		denominatorExceptionNode = new Node(TemplateId.MEASURE_DATA_CMS_V4);
		denominatorExceptionNode.putValue(type, SubPopulationLabel.DENEXCEP.name());
		denominatorExceptionNode.addChildNode(aggregateCountNode);

		numeratorNode = new Node(TemplateId.MEASURE_DATA_CMS_V4);
		numeratorNode.putValue(type, SubPopulationLabel.NUMER.name());
		numeratorNode.addChildNode(aggregateCountNode);
		numeratorNode.addChildNode(paymentNode);

		denominatorNode = new Node(TemplateId.MEASURE_DATA_CMS_V4);
		denominatorNode.putValue(type, SubPopulationLabel.DENOM.name());
		denominatorNode.addChildNode(paymentNode);
		denominatorNode.addChildNode(aggregateCountNode);

		encoder = new QualityMeasureIdEncoder(new Context());
		wrapper = new JsonWrapper();

		MeasureConfigs.initMeasureConfigs(MeasureConfigs.TEST_MEASURE_DATA);
	}

	@Test
	void testMeasureIdIsEncoded() {
		executeInternalEncode();

		assertThat(wrapper.getString("measureId"))
				.isEqualTo("236");
	}

	@Test
	void testEndToEndReportedIsEncoded() {
		executeInternalEncode();
		JsonWrapper childValues = getChildValues();

		assertThat(childValues.getBoolean("isEndToEndReported"))
				.isTrue();
	}

	@Test
	void testPopulationTotalIsEncoded() {
		executeInternalEncode();
		JsonWrapper childValues = getChildValues();


		assertThat(childValues.getInteger(ELIGIBLE_POPULATION))
				.isEqualTo(600);
	}

	@Test
	void testPopulationAltTotalIsEncoded() {
		populationNode = new Node(TemplateId.MEASURE_DATA_CMS_V4);
		populationNode.putValue(type, "IPP");
		populationNode.addChildNode(aggregateCountNode);
		executeInternalEncode();
		JsonWrapper childValues = getChildValues();

		assertThat(childValues.getInteger(ELIGIBLE_POPULATION))
				.isEqualTo(600);
	}

	@Test
	void testPerformanceMetIsEncoded() {
		executeInternalEncode();
		JsonWrapper childValues = getChildValues();
		assertThat(childValues.getInteger("performanceMet"))
				.isEqualTo(600);
	}

	@Test
	void testPerformanceExclusionIsEncoded() {
		executeInternalEncode();
		JsonWrapper childValues = getChildValues();

		assertThat(childValues.getInteger("eligiblePopulationExclusion"))
				.isEqualTo(600);
	}

	@Test
	void testPerformanceNotMetIsEncoded() {
		executeInternalEncode();
		JsonWrapper childValues = getChildValues();

		assertThat(childValues.getInteger("performanceNotMet"))
				.isEqualTo(-1200);
	}

	@Test
	void testMeasure438EncodingEndToEndEncoded() {
		qualityMeasureId.putValue("measureId", "40280382-6258-7581-0162-92d6e6db1680");
		executeInternalEncode();
		JsonWrapper childValues = getChildValues();

		assertThat(childValues.getBoolean("isEndToEndReported"))
			.isTrue();
	}

	@Test
	void testMeasureMultiToSingleEncodingEligiblePopulation() {
		qualityMeasureId.putValue("measureId", "40280382-610b-e7a4-0161-9a6155603811");
		executeInternalEncode();
		JsonWrapper childValues = getChildValues();

		assertThat(childValues.getInteger(ELIGIBLE_POPULATION))
			.isEqualTo(600);
	}

	@Test
	void testMultiToSingleEncodingPerformanceMet() {
		qualityMeasureId.putValue("measureId", "40280382-6258-7581-0162-92d6e6db1680");
		executeInternalEncode();
		JsonWrapper childValues = getChildValues();

		assertThat(childValues.getInteger("performanceMet"))
			.isEqualTo(600);
	}

	@Test
	void testMeasureMultiToSingleEncodingEligiblePopulationExclusion() {
		qualityMeasureId.putValue("measureId", "40280382-6258-7581-0162-92d6e6db1680");
		executeInternalEncode();
		JsonWrapper childValues = getChildValues();

		assertThat(childValues.getInteger("eligiblePopulationExclusion"))
			.isEqualTo(600);
	}

	@Test
	void testMeasureMultiToSingleEncodingEligiblePopulationException() {
		qualityMeasureId.putValue("measureId", "40280382-6258-7581-0162-92d6e6db1680");
		executeInternalEncode();
		JsonWrapper childValues = getChildValues();

		assertThat(childValues.getInteger("eligiblePopulationException"))
			.isEqualTo(600);
	}

	@Test
	void testMeasureMultiToSingleEncodingPerformanceNotMet() {
		qualityMeasureId.putValue("measureId", "40280382-6258-7581-0162-92d6e6db1680");
		executeInternalEncode();
		JsonWrapper childValues = getChildValues();

		assertThat(childValues.getInteger("performanceNotMet"))
			.isEqualTo(-1200);
	}

	@Test
	void testIgnoresNonMeasureDataNodes() {
		qualityMeasureId.addChildNode(aggregateCountNode);
		executeInternalEncode();
		JsonWrapper childValues = getChildValues();

		assertThat(childValues.getInteger("aggregateCount")).isNull();
	}

	@Test
	void testEncodeSingleToMultiDefault() {
		qualityMeasureId.putValue("measureId", "40280382-6258-7581-0162-626f31a0009e");
		numeratorNode.putValue(MeasureDataDecoder.MEASURE_POPULATION,"F4580E7F-EB6C-42AB-93A8-9AF1A4FD46EE");
		executeInternalEncode();
		JsonWrapper childValues = getChildValues();
		List<?> strata = JsonHelper.readJsonAtJsonPath(childValues.toString(), "$.strata", new TypeRef<List<?>>() {});
		assertThat(strata.size()).isEqualTo(2);
	}

	private void executeInternalEncode() {
		qualityMeasureId.addChildNodes(populationNode, denomExclusionNode, numeratorNode, denominatorNode,
			denominatorExceptionNode);
		try {
			encoder.internalEncode(wrapper, qualityMeasureId);
		} catch (EncodeException e) {
			Assertions.fail("Failure to encode: " + e.getMessage());
		}
	}

	private JsonWrapper getChildValues() {
		return wrapper.get("value");
	}
}