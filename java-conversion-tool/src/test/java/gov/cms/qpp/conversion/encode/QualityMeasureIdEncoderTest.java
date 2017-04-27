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

	@Before
	public void setUp() {
		String type = "type";

		qualityMeasureId = new Node(TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2.getTemplateId());
		qualityMeasureId.putValue("measureId", "Measure Id Value");

		aggregateCountNode = new Node();
		aggregateCountNode.setId(TemplateId.ACI_AGGREGATE_COUNT.getTemplateId());
		aggregateCountNode.putValue("aggregateCount", "600");

		populationNode = new Node(TemplateId.MEASURE_DATA_CMS_V2.getTemplateId());
		populationNode.putValue(type, "IPOP");
		populationNode.addChildNode(aggregateCountNode);

		denomExclusionNode = new Node(TemplateId.MEASURE_DATA_CMS_V2.getTemplateId());
		denomExclusionNode.putValue(type, "DENEX");
		denomExclusionNode.addChildNode(aggregateCountNode);

		numeratorNode = new Node(TemplateId.MEASURE_DATA_CMS_V2.getTemplateId());
		numeratorNode.putValue(type, "NUMER");
		numeratorNode.addChildNode(aggregateCountNode);

		denominatorNode = new Node(TemplateId.MEASURE_DATA_CMS_V2.getTemplateId());
		denominatorNode.putValue(type, "DENOM");
		denominatorNode.addChildNode(aggregateCountNode);

		qualityMeasureId.addChildNodes(populationNode, denomExclusionNode, numeratorNode, denominatorNode);

		encoder = new QualityMeasureIdEncoder();
		wrapper = new JsonWrapper();
	}

	@Test
	public void testMeasureIdIsEncoded() {
		executeInternalEncode();

		assertThat("expected encoder to return a single value",
				wrapper.getString("measureId"), is("Measure Id Value"));
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
				childValues.get("populationTotal"), is(600));
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
				childValues.get("performanceExclusion"), is(600));
	}

	@Test
	public void testPerformanceNotMetIsEncoded() {
		executeInternalEncode();
		LinkedHashMap<String, Object> childValues = getChildValues();

		assertThat("expected encoder to return a single value",
				childValues.get("performanceNotMet"), is(0));
	}

	private void executeInternalEncode() {
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