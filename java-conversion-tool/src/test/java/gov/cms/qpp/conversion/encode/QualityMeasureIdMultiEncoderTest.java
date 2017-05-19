package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import java.util.LinkedHashMap;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class QualityMeasureIdMultiEncoderTest {
	private final String REQUIRE_POPULATION_TOTAL = "Must have a required populationTotal";
	private final String REQUIRE_PERFORMANCE_MET = "Must have a required performanceMet";
	private final String REQUIRE_DENOM_EXCEP = "Must have a required denominatorExceptions";
	private final String REQUIRE_DENOM = "Must have a required denominator";
	private final String REQUIRE_NUMER = "Must have a required numerator";
	private final String POPULATION_TOTAL = "populationTotal";
	private final String PERFORMANCE_MET = "performanceMet";
	private final String DENOMINATOR_EXCEPTIONS = "denominatorExceptions";
	private final String NUMERATOR = "numerator";
	private final String DENOMINATOR = "denominator";
	private final String TYPE = "type";
	private final String POPULATION_ID = "populationId";
	private Node qualityMeasureId;
	private Node populationNode;
	private Node populationNodeTwo;
	private Node populationNodeThree;
	private Node denomExceptionNode;
	private Node denomExceptionNodeTwo;
	private Node numeratorNode;
	private Node numeratorNodeTwo;
	private Node numeratorNodeThree;
	private Node denominatorNode;
	private Node denominatorNodeTwo;
	private Node denominatorNodeThree;
	private Node aggregateCountNode;
	private JsonWrapper wrapper;
	private QualityMeasureIdEncoder encoder;

	@Before
	public void setUp() {
		String ipop = "IPOP";
		String denexcep = "DENEXCEP";
		String numer = "NUMER";
		String denom = "DENOM";
		qualityMeasureId = new Node(TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2.getTemplateId());
		qualityMeasureId.putValue("measureId", "40280381-51f0-825b-0152-2273af5a150b");

		aggregateCountNode = new Node();
		aggregateCountNode.setId(TemplateId.ACI_AGGREGATE_COUNT.getTemplateId());
		aggregateCountNode.putValue("aggregateCount", "600");

		populationNode = new Node(TemplateId.MEASURE_DATA_CMS_V2.getTemplateId());
		populationNode.putValue(TYPE, ipop);
		populationNode.putValue(POPULATION_ID, "E681DBF8-F827-4586-B3E0-178FF19EC3A2");
		populationNode.addChildNode(aggregateCountNode);

		populationNodeTwo = new Node(TemplateId.MEASURE_DATA_CMS_V2.getTemplateId());
		populationNodeTwo.putValue(TYPE, ipop);
		populationNodeTwo.putValue(POPULATION_ID, "AAC578DB-1900-43BD-BBBF-50014A5457E5");
		populationNodeTwo.addChildNode(aggregateCountNode);

		populationNodeThree = new Node(TemplateId.MEASURE_DATA_CMS_V2.getTemplateId());
		populationNodeThree.putValue(TYPE, ipop);
		populationNodeThree.putValue(POPULATION_ID, "AF36C4A9-8BD9-4E21-838D-A47A1845EB90");
		populationNodeThree.addChildNode(aggregateCountNode);

		denomExceptionNode = new Node(TemplateId.MEASURE_DATA_CMS_V2.getTemplateId());
		denomExceptionNode.putValue(TYPE, denexcep);
		denomExceptionNode.putValue(POPULATION_ID, "58347456-D1F3-4BBB-9B35-5D42825A0AB3");
		denomExceptionNode.addChildNode(aggregateCountNode);

		denomExceptionNodeTwo = new Node(TemplateId.MEASURE_DATA_CMS_V2.getTemplateId());
		denomExceptionNodeTwo.putValue(TYPE, denexcep);
		denomExceptionNodeTwo.putValue(POPULATION_ID, "B7CCA1A6-F352-4A23-BC89-6FE9B60DC0C6");
		denomExceptionNodeTwo.addChildNode(aggregateCountNode);

		numeratorNode = new Node(TemplateId.MEASURE_DATA_CMS_V2.getTemplateId());

		numeratorNode.putValue(TYPE, numer);
		numeratorNode.putValue(POPULATION_ID, "631C0B49-83F4-4A54-96C4-7E0766B2407C");
		numeratorNode.addChildNode(aggregateCountNode);

		numeratorNodeTwo = new Node(TemplateId.MEASURE_DATA_CMS_V2.getTemplateId());
		numeratorNodeTwo.putValue(TYPE, numer);
		numeratorNodeTwo.putValue(POPULATION_ID, "5B7AC4EC-547A-47E5-AC5E-618401175511");
		numeratorNodeTwo.addChildNode(aggregateCountNode);

		numeratorNodeThree = new Node(TemplateId.MEASURE_DATA_CMS_V2.getTemplateId());
		numeratorNodeThree.putValue(TYPE, numer);
		numeratorNodeThree.putValue(POPULATION_ID, "86F74F07-D593-44F6-AA12-405966400963");
		numeratorNodeThree.addChildNode(aggregateCountNode);

		denominatorNode = new Node(TemplateId.MEASURE_DATA_CMS_V2.getTemplateId());
		denominatorNode.putValue(TYPE, denom);
		denominatorNode.putValue(POPULATION_ID, "04BF53CE-6993-4EA2-BFE5-66E36172B388");
		denominatorNode.addChildNode(aggregateCountNode);

		denominatorNodeTwo = new Node(TemplateId.MEASURE_DATA_CMS_V2.getTemplateId());
		denominatorNodeTwo.putValue(TYPE, denom);
		denominatorNodeTwo.putValue(POPULATION_ID, "1574973E-EB52-40C7-9709-25ABEDBA99A3");
		denominatorNodeTwo.addChildNode(aggregateCountNode);

		denominatorNodeThree = new Node(TemplateId.MEASURE_DATA_CMS_V2.getTemplateId());
		denominatorNodeThree.putValue(TYPE, denom);
		denominatorNodeThree.putValue(POPULATION_ID, "B95BC0D3-572E-462B-BAA2-46CD33A865CD");
		denominatorNodeThree.addChildNode(aggregateCountNode);

		encoder = new QualityMeasureIdEncoder();
		wrapper = new JsonWrapper();
	}

	@Test
	public void testInternalEncode() {
		qualityMeasureId.addChildNodes(
				populationNode, denomExceptionNode, numeratorNode, denominatorNode,
				populationNodeTwo, denomExceptionNodeTwo, numeratorNodeTwo, denominatorNodeTwo,
				populationNodeThree, numeratorNodeThree, denominatorNodeThree);

		encoder.internalEncode(wrapper, qualityMeasureId);

		LinkedHashMap<String, Object> childValues = getChildValues();
		List<LinkedHashMap<String, Integer>> subPopulations =
				(List<LinkedHashMap<String, Integer>>)childValues.get("strata");

		assertFirstSubPopulation(subPopulations);
		assertSecondSubPopulation(subPopulations);
		assertThirdSubPopulation(subPopulations);
	}

	@Test
	public void testInternalEncodeWithIgnoredMeasureData() {
		Node populationCriteriaNode = new Node(TemplateId.PERFORMANCE_RATE_PROPORTION_MEASURE_CMS_V2.getTemplateId());
		qualityMeasureId.addChildNodes(
				populationNode, denomExceptionNode, numeratorNode, denominatorNode,
				populationNodeTwo, denomExceptionNodeTwo, numeratorNodeTwo, denominatorNodeTwo,
				populationNodeThree, numeratorNodeThree, denominatorNodeThree,
				populationCriteriaNode);

		encoder.internalEncode(wrapper, qualityMeasureId);

		LinkedHashMap<String, Object> childValues = getChildValues();
		List<LinkedHashMap<String, Integer>> subPopulations =
				(List<LinkedHashMap<String, Integer>>)childValues.get("strata");

		assertFirstSubPopulation(subPopulations);
		assertSecondSubPopulation(subPopulations);
		assertThirdSubPopulation(subPopulations);
	}

	private LinkedHashMap<String, Object> getChildValues() {
		return (LinkedHashMap<String, Object>)((LinkedHashMap<String, Object>) wrapper.getObject()).get("value");
	}

	private void assertFirstSubPopulation(List<LinkedHashMap<String, Integer>> strata) {
		LinkedHashMap<String, Integer> firstSubPopulation = strata.get(0);

		assertThat(REQUIRE_POPULATION_TOTAL, firstSubPopulation.get(POPULATION_TOTAL), is(600));
		assertThat(REQUIRE_PERFORMANCE_MET, firstSubPopulation.get(PERFORMANCE_MET), is(600));
		assertThat(REQUIRE_DENOM_EXCEP, firstSubPopulation.get(DENOMINATOR_EXCEPTIONS), is(600));
		assertThat(REQUIRE_NUMER, firstSubPopulation.get(NUMERATOR), is(600));
		assertThat(REQUIRE_DENOM, firstSubPopulation.get(DENOMINATOR), is(600));
	}

	private void assertSecondSubPopulation(List<LinkedHashMap<String, Integer>> strata) {
		LinkedHashMap<String, Integer> secondSubPopulation = strata.get(1);

		assertThat(REQUIRE_POPULATION_TOTAL, secondSubPopulation	.get(POPULATION_TOTAL), is(600));
		assertThat(REQUIRE_PERFORMANCE_MET, secondSubPopulation.get(PERFORMANCE_MET), is(600));
		assertThat(REQUIRE_DENOM_EXCEP, secondSubPopulation.get(DENOMINATOR_EXCEPTIONS), is(600));
		assertThat(REQUIRE_NUMER, secondSubPopulation.get(NUMERATOR), is(600));
		assertThat(REQUIRE_DENOM, secondSubPopulation.get(DENOMINATOR), is(600));
	}

	private void assertThirdSubPopulation(List<LinkedHashMap<String, Integer>> strata) {
		LinkedHashMap<String, Integer> thirdSubPopulation = strata.get(2);

		assertThat(REQUIRE_POPULATION_TOTAL, thirdSubPopulation.get(POPULATION_TOTAL), is(600));
		assertThat(REQUIRE_PERFORMANCE_MET, thirdSubPopulation.get(PERFORMANCE_MET), is(600));
		assertThat(REQUIRE_NUMER, thirdSubPopulation.get(NUMERATOR), is(600));
		assertThat(REQUIRE_DENOM, thirdSubPopulation.get(DENOMINATOR), is(600));
	}
}
