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
		qualityMeasureId.putValue("measureId", "2c928085-7198-38ee-0171-9da6456007ab");

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
		qualityMeasureId.putValue("measureId", "40280382-68d3-a5fe-0169-0c78bec911bb");
		executeInternalEncode();
		JsonWrapper childValues = getChildValues();

		assertThat(childValues.getBoolean("isEndToEndReported"))
			.isTrue();
	}

	@Test
	void testMeasureMultiToSingleEncodingEligiblePopulation() {
		qualityMeasureId.putValue("measureId", "40280382-68d3-a5fe-0169-0c78bec911bb");
		executeInternalEncode();
		JsonWrapper childValues = getChildValues();

		assertThat(childValues.getInteger(ELIGIBLE_POPULATION))
			.isEqualTo(600);
	}

	@Test
	void testMultiToSingleEncodingPerformanceMet() {
		qualityMeasureId.putValue("measureId", "40280382-68d3-a5fe-0169-0c78bec911bb");
		executeInternalEncode();
		JsonWrapper childValues = getChildValues();

		assertThat(childValues.getInteger("performanceMet"))
			.isEqualTo(600);
	}

	@Test
	void testMeasureMultiToSingleEncodingEligiblePopulationExclusion() {
		qualityMeasureId.putValue("measureId", "40280382-68d3-a5fe-0169-0c78bec911bb");
		executeInternalEncode();
		JsonWrapper childValues = getChildValues();

		assertThat(childValues.getInteger("eligiblePopulationExclusion"))
			.isEqualTo(600);
	}

	@Test
	void testMeasureMultiToSingleEncodingEligiblePopulationException() {
		qualityMeasureId.putValue("measureId", "40280382-68d3-a5fe-0169-0c78bec911bb");
		executeInternalEncode();
		JsonWrapper childValues = getChildValues();

		assertThat(childValues.getInteger("eligiblePopulationException"))
			.isEqualTo(600);
	}

	@Test
	void testMeasureMultiToSingleEncodingPerformanceNotMet() {
		qualityMeasureId.putValue("measureId", "40280382-68d3-a5fe-0169-0c78bec911bb");
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
		qualityMeasureId.putValue("measureId", "40280382-6963-bf5e-016a-03dca9e446e0");
		numeratorNode.putValue(MeasureDataDecoder.MEASURE_POPULATION,"A5976BE6-7F1C-419D-898D-7AFEB141A355");
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