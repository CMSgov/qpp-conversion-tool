package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedHashMap;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class QualityMeasureIdEncoderTest {
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

	@Before
	public void setUp() {
		qualityMeasureId = new Node(TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2);
		qualityMeasureId.putValue("measureId", "40280381-51f0-825b-0152-22b98cff181a");

		aggregateCountNode = new Node(TemplateId.ACI_AGGREGATE_COUNT);
		aggregateCountNode.putValue("aggregateCount", "600");

		populationNode = new Node(TemplateId.MEASURE_DATA_CMS_V2);
		populationNode.putValue(type, "IPOP");
		populationNode.addChildNode(aggregateCountNode);

		denomExclusionNode = new Node(TemplateId.MEASURE_DATA_CMS_V2);
		denomExclusionNode.putValue(type, "DENEX");
		denomExclusionNode.addChildNode(aggregateCountNode);

		numeratorNode = new Node(TemplateId.MEASURE_DATA_CMS_V2);
		numeratorNode.putValue(type, "NUMER");
		numeratorNode.addChildNode(aggregateCountNode);

		denominatorNode = new Node(TemplateId.MEASURE_DATA_CMS_V2);
		denominatorNode.putValue(type, "DENOM");
		denominatorNode.addChildNode(aggregateCountNode);

		encoder = new QualityMeasureIdEncoder();
		wrapper = new JsonWrapper();
	}

	@Test
	public void testMeasureIdIsEncoded() {
		executeInternalEncode();

		assertThat("expected encoder to return a single value",
				wrapper.getString("measureId"), is("236"));
	}

	@Test
	public void testEndToEndReportedIsEncoded() {
		executeInternalEncode();
		LinkedHashMap<String, Object> childValues = getChildValues();

		assertTrue("expected encoder to return a single value",
				(Boolean) childValues.get("isEndToEndReported"));
	}

	@Test
	public void testPopulationTotalIsEncoded() {
		executeInternalEncode();
		LinkedHashMap<String, Object> childValues = getChildValues();


		assertThat("expected encoder to return a single value",
				childValues.get(ELIGIBLE_POPULATION), is(600));
	}

	@Test
	public void testPopulationAltTotalIsEncoded() {
		populationNode = new Node(TemplateId.MEASURE_DATA_CMS_V2);
		populationNode.putValue(type, "IPP");
		populationNode.addChildNode(aggregateCountNode);
		executeInternalEncode();
		LinkedHashMap<String, Object> childValues = getChildValues();

		assertThat("expected encoder to return a single value",
				childValues.get(ELIGIBLE_POPULATION), is(600));
	}

	@Test
	public void testPerformanceMetIsEncoded() {
		executeInternalEncode();
		LinkedHashMap<String, Object> childValues = getChildValues();
		assertThat("expected encoder to return a single value",
				childValues.get("performanceMet"), is(600));
	}

	@Test
	public void testPerformanceExclusionIsEncoded() {
		executeInternalEncode();
		LinkedHashMap<String, Object> childValues = getChildValues();

		assertThat("expected encoder to return a single value",
				childValues.get("eligiblePopulationExclusion"), is(600));
	}

	@Test
	public void testPerformanceNotMetIsEncoded() {
		executeInternalEncode();
		LinkedHashMap<String, Object> childValues = getChildValues();

		assertThat("expected encoder to return a single value",
				childValues.get("performanceNotMet"), is(-600));
	}

	private void executeInternalEncode() {
		qualityMeasureId.addChildNodes(populationNode, denomExclusionNode, numeratorNode, denominatorNode);
		try {
			encoder.internalEncode(wrapper, qualityMeasureId);
		} catch (EncodeException e) {
			fail("Failure to encode: " + e.getMessage());
		}
	}

	private LinkedHashMap<String, Object> getChildValues() {
		return (LinkedHashMap<String, Object>)((LinkedHashMap<String, Object>) wrapper.getObject()).get("value");
	}
}