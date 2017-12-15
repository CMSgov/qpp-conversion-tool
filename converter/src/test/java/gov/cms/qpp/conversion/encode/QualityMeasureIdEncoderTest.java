package gov.cms.qpp.conversion.encode;

import static com.google.common.truth.Truth.assertWithMessage;

import java.util.LinkedHashMap;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.validation.SubPopulations;

class QualityMeasureIdEncoderTest {

	private Node qualityMeasureId;
	private Node populationNode;
	private Node denomExclusionNode;
	private Node numeratorNode;
	private Node denominatorNode;
	private Node aggregateCountNode;
	private JsonWrapper wrapper;
	private QualityMeasureIdEncoder encoder;
	private String type = "type";
	private static final String ELIGIBLE_POPULATION = "eligiblePopulation";

	@BeforeEach
	void setUp() {
		qualityMeasureId = new Node(TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2);
		qualityMeasureId.putValue("measureId", "40280381-51f0-825b-0152-22b98cff181a");

		aggregateCountNode = new Node(TemplateId.ACI_AGGREGATE_COUNT);
		aggregateCountNode.putValue("aggregateCount", "600");

		populationNode = new Node(TemplateId.MEASURE_DATA_CMS_V2);
		populationNode.putValue(type, SubPopulations.IPOP);
		populationNode.addChildNode(aggregateCountNode);

		denomExclusionNode = new Node(TemplateId.MEASURE_DATA_CMS_V2);
		denomExclusionNode.putValue(type, SubPopulations.DENEX);
		denomExclusionNode.addChildNode(aggregateCountNode);

		numeratorNode = new Node(TemplateId.MEASURE_DATA_CMS_V2);
		numeratorNode.putValue(type, SubPopulations.NUMER);
		numeratorNode.addChildNode(aggregateCountNode);

		denominatorNode = new Node(TemplateId.MEASURE_DATA_CMS_V2);
		denominatorNode.putValue(type, SubPopulations.DENOM);
		denominatorNode.addChildNode(aggregateCountNode);

		encoder = new QualityMeasureIdEncoder(new Context());
		wrapper = new JsonWrapper();
	}

	@Test
	void testMeasureIdIsEncoded() {
		executeInternalEncode();

		assertWithMessage("expected encoder to return a single value")
				.that(wrapper.getString("measureId"))
				.isEqualTo("236");
	}

	@Test
	void testEndToEndReportedIsEncoded() {
		executeInternalEncode();
		LinkedHashMap<String, Object> childValues = getChildValues();

		assertWithMessage("expected encoder to return a single value")
				.that((Boolean)childValues.get("isEndToEndReported"))
				.isTrue();
	}

	@Test
	void testPopulationTotalIsEncoded() {
		executeInternalEncode();
		LinkedHashMap<String, Object> childValues = getChildValues();


		assertWithMessage("expected encoder to return a single value")
				.that(childValues.get(ELIGIBLE_POPULATION))
				.isEqualTo(600);
	}

	@Test
	void testPopulationAltTotalIsEncoded() {
		populationNode = new Node(TemplateId.MEASURE_DATA_CMS_V2);
		populationNode.putValue(type, SubPopulations.IPP);
		populationNode.addChildNode(aggregateCountNode);
		executeInternalEncode();
		LinkedHashMap<String, Object> childValues = getChildValues();

		assertWithMessage("expected encoder to return a single value")
				.that(childValues.get(ELIGIBLE_POPULATION))
				.isEqualTo(600);
	}

	@Test
	void testPerformanceMetIsEncoded() {
		executeInternalEncode();
		LinkedHashMap<String, Object> childValues = getChildValues();
		assertWithMessage("expected encoder to return a single value")
				.that(childValues.get("performanceMet"))
				.isEqualTo(600);
	}

	@Test
	void testPerformanceExclusionIsEncoded() {
		executeInternalEncode();
		LinkedHashMap<String, Object> childValues = getChildValues();

		assertWithMessage("expected encoder to return a single value")
				.that(childValues.get("eligiblePopulationExclusion"))
				.isEqualTo(600);
	}

	@Test
	void testPerformanceNotMetIsEncoded() {
		executeInternalEncode();
		LinkedHashMap<String, Object> childValues = getChildValues();

		assertWithMessage("expected encoder to return a single value")
				.that(childValues.get("performanceNotMet"))
				.isEqualTo(-600);
	}

	private void executeInternalEncode() {
		qualityMeasureId.addChildNodes(populationNode, denomExclusionNode, numeratorNode, denominatorNode);
		try {
			encoder.internalEncode(wrapper, qualityMeasureId);
		} catch (EncodeException e) {
			Assertions.fail("Failure to encode: " + e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	private LinkedHashMap<String, Object> getChildValues() {
		return (LinkedHashMap<String, Object>)((LinkedHashMap<String, Object>) wrapper.getObject()).get("value");
	}
}