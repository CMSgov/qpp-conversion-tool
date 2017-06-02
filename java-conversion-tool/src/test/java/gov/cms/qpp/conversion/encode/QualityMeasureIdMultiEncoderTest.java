package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.validation.MeasureConfigs;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

public class QualityMeasureIdMultiEncoderTest {
	private final String REQUIRE_POPULATION_TOTAL = "Must have a required populationTotal";
	private final String REQUIRE_PERFORMANCE_MET = "Must have a required performanceMet";
	private final String REQUIRE_DENOM_EXCEP = "Must have a required denominatorExceptions";
	private final String REQUIRE_DENOM_EXCLUS = "Must have a required denominatorExclusion";
	private final String REQUIRE_DENOM = "Must have a required denominator";
	private final String REQUIRE_NUMER = "Must have a required numerator";
	private final String ELIGIBLE_POPULATION = "eligiblePopulation";
	private final String PERFORMANCE_MET = "performanceMet";
	private final String ELIGIBLE_POPULATION_EXCEPTION = "eligiblePopulationException";
	private final String ELIGIBLE_POPULATION_EXCLUSION = "eligiblePopulationExclusion";
	private final String NUMERATOR = "numerator";
	private final String DENOMINATOR = "denominator";
	private final String TYPE = "type";
	private final String POPULATION_ID = "populationId";
	private final String MEASURE_ID = "measureId";

	private Node qualityMeasureId;
	private Node eligiblePopulationNode;
	private Node eligiblePopulationNodeTwo;
	private Node eligiblePopulationExclusionNode;
	private Node eligiblePopulationExclusionNodeTwo;
	private Node eligiblePopulationExceptionNode;
	private Node eligiblePopulationExceptionNodeTwo;
	private Node numeratorNode;
	private Node numeratorNodeTwo;
	private Node denominatorNode;
	private Node denominatorNodeTwo;
	private Node aggregateCountNode;
	private JsonWrapper wrapper;
	private QualityMeasureIdEncoder encoder;

	@BeforeClass
	public static  void setUpCustomMeasureData() {
		MeasureConfigs.setMeasureDataFile("test-multi-prop-measure-data.json");
	}

	@AfterClass
	public static void resetMeasuresData() {
		MeasureConfigs.setMeasureDataFile("measures-data-short.json");
	}

	@Before
	public void setUp() {
		String ipop = "IPOP";
		String denexcep = "DENEXCEP";
		String numer = "NUMER";
		String denom = "DENOM";
		String denex = "DENEX";
		qualityMeasureId = new Node(TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2);
		qualityMeasureId.putValue(MEASURE_ID, "test1");

		aggregateCountNode = new Node(TemplateId.ACI_AGGREGATE_COUNT);
		aggregateCountNode.putValue("aggregateCount", "600");

		eligiblePopulationNode = new Node(TemplateId.MEASURE_DATA_CMS_V2);
		eligiblePopulationNode.putValue(TYPE, ipop);
		eligiblePopulationNode.putValue(POPULATION_ID, "ipop1");
		eligiblePopulationNode.addChildNode(aggregateCountNode);

		eligiblePopulationNodeTwo = new Node(TemplateId.MEASURE_DATA_CMS_V2);
		eligiblePopulationNodeTwo.putValue(TYPE, ipop);
		eligiblePopulationNodeTwo.putValue(POPULATION_ID, "ipop2");
		eligiblePopulationNodeTwo.addChildNode(aggregateCountNode);

		eligiblePopulationExclusionNode = new Node(TemplateId.MEASURE_DATA_CMS_V2);
		eligiblePopulationExclusionNode.putValue(TYPE, denex);
		eligiblePopulationExclusionNode.putValue(POPULATION_ID, "denex1");
		eligiblePopulationExclusionNode.addChildNode(aggregateCountNode);

		eligiblePopulationExclusionNodeTwo = new Node(TemplateId.MEASURE_DATA_CMS_V2);
		eligiblePopulationExclusionNodeTwo.putValue(TYPE, denex);
		eligiblePopulationExclusionNodeTwo.putValue(POPULATION_ID, "denex2");
		eligiblePopulationExclusionNodeTwo.addChildNode(aggregateCountNode);

		eligiblePopulationExceptionNode = new Node(TemplateId.MEASURE_DATA_CMS_V2);
		eligiblePopulationExceptionNode.putValue(TYPE, denexcep);
		eligiblePopulationExceptionNode.putValue(POPULATION_ID, "denexcep1");
		eligiblePopulationExceptionNode.addChildNode(aggregateCountNode);

		eligiblePopulationExceptionNodeTwo = new Node(TemplateId.MEASURE_DATA_CMS_V2);
		eligiblePopulationExceptionNodeTwo.putValue(TYPE, denexcep);
		eligiblePopulationExceptionNodeTwo.putValue(POPULATION_ID, "denexcep2");
		eligiblePopulationExceptionNodeTwo.addChildNode(aggregateCountNode);

		numeratorNode = new Node(TemplateId.MEASURE_DATA_CMS_V2);

		numeratorNode.putValue(TYPE, numer);
		numeratorNode.putValue(POPULATION_ID, "numer1");
		numeratorNode.addChildNode(aggregateCountNode);

		numeratorNodeTwo = new Node(TemplateId.MEASURE_DATA_CMS_V2);
		numeratorNodeTwo.putValue(TYPE, numer);
		numeratorNodeTwo.putValue(POPULATION_ID, "numer2");
		numeratorNodeTwo.addChildNode(aggregateCountNode);

		denominatorNode = new Node(TemplateId.MEASURE_DATA_CMS_V2);
		denominatorNode.putValue(TYPE, denom);
		denominatorNode.putValue(POPULATION_ID, "denom1");
		denominatorNode.addChildNode(aggregateCountNode);

		denominatorNodeTwo = new Node(TemplateId.MEASURE_DATA_CMS_V2);
		denominatorNodeTwo.putValue(TYPE, denom);
		denominatorNodeTwo.putValue(POPULATION_ID, "denom2");
		denominatorNodeTwo.addChildNode(aggregateCountNode);

		encoder = new QualityMeasureIdEncoder();
		wrapper = new JsonWrapper();
	}

	@Test
	public void testInternalEncode() {
		qualityMeasureId.addChildNodes(
				eligiblePopulationNode, eligiblePopulationExceptionNode,
				eligiblePopulationExclusionNode, numeratorNode, denominatorNode,
				eligiblePopulationNodeTwo, eligiblePopulationExceptionNodeTwo,
				eligiblePopulationExclusionNodeTwo, numeratorNodeTwo, denominatorNodeTwo);

		encoder.internalEncode(wrapper, qualityMeasureId);

		LinkedHashMap<String, Object> childValues = getChildValues();
		List<LinkedHashMap<String, Integer>> subPopulations =
				(List<LinkedHashMap<String, Integer>>)childValues.get("strata");
		assertFirstSubPopulation(subPopulations);
		assertSecondSubPopulation(subPopulations);
	}

	@Test
	public void testNullSubPopulations() {
		qualityMeasureId.putValue(MEASURE_ID, "test2");
		qualityMeasureId.addChildNodes(eligiblePopulationNode, eligiblePopulationExceptionNode,
				numeratorNode, denominatorNode, eligiblePopulationNodeTwo,
				eligiblePopulationExceptionNodeTwo, numeratorNodeTwo, denominatorNodeTwo);


		encoder.internalEncode(wrapper, qualityMeasureId);

		LinkedHashMap<String, Object> childValues = getChildValues();
		List<LinkedHashMap<String, Integer>> subPopulations =
				(List<LinkedHashMap<String, Integer>>)childValues.get("strata");

		assertThat("Must have no sub populations encoded", subPopulations, hasSize(0));
	}

	private LinkedHashMap<String, Object> getChildValues() {
		return (LinkedHashMap<String, Object>)((LinkedHashMap<String, Object>) wrapper.getObject()).get("value");
	}

	private void assertFirstSubPopulation(List<LinkedHashMap<String, Integer>> strata) {
		LinkedHashMap<String, Integer> firstSubPopulation = strata.get(0);

		assertThat(REQUIRE_POPULATION_TOTAL, firstSubPopulation.get(ELIGIBLE_POPULATION), is(600));
		assertThat(REQUIRE_PERFORMANCE_MET, firstSubPopulation.get(PERFORMANCE_MET), is(600));
		assertThat(REQUIRE_DENOM_EXCEP, firstSubPopulation.get(ELIGIBLE_POPULATION_EXCEPTION), is(600));
		assertThat(REQUIRE_DENOM_EXCLUS, firstSubPopulation.get("eligiblePopulationExclusion"), is(600));
		assertThat(REQUIRE_NUMER, firstSubPopulation.get(NUMERATOR), is(600));
		assertThat(REQUIRE_DENOM, firstSubPopulation.get(DENOMINATOR), is(600));
	}

	private void assertSecondSubPopulation(List<LinkedHashMap<String, Integer>> strata) {
		LinkedHashMap<String, Integer> secondSubPopulation = strata.get(1);

		assertThat(REQUIRE_POPULATION_TOTAL, secondSubPopulation.get(ELIGIBLE_POPULATION), is(600));
		assertThat(REQUIRE_PERFORMANCE_MET, secondSubPopulation.get(PERFORMANCE_MET), is(600));
		assertThat(REQUIRE_DENOM_EXCEP, secondSubPopulation.get(ELIGIBLE_POPULATION_EXCEPTION), is(600));
		assertThat(REQUIRE_DENOM_EXCLUS, secondSubPopulation.get(ELIGIBLE_POPULATION_EXCLUSION), is(600));
		assertThat(REQUIRE_NUMER, secondSubPopulation.get(NUMERATOR), is(600));
		assertThat(REQUIRE_DENOM, secondSubPopulation.get(DENOMINATOR), is(600));
	}
}
