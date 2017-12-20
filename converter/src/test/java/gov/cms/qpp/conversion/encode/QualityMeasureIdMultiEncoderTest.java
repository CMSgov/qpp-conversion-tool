package gov.cms.qpp.conversion.encode;

import static com.google.common.truth.Truth.assertWithMessage;

import java.util.LinkedHashMap;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.validation.MeasureConfigs;
import gov.cms.qpp.conversion.model.validation.SubPopulations;

import java.util.LinkedHashMap;
import java.util.List;

import static com.google.common.truth.Truth.assertWithMessage;
import static com.google.common.truth.Truth.assertThat;

class QualityMeasureIdMultiEncoderTest {

	private static final String REQUIRE_POPULATION_TOTAL = "Must have a required eligiblePopulation";
	private static final String REQUIRE_PERFORMANCE_MET = "Must have a required performanceMet";
	private static final String REQUIRE_ELIGIBLE_POPULATION_EXCEP = "Must have a required eligiblePopulationException";
	private static final String REQUIRE_ELIGIBLE_POPULATION_EXCLUS = "Must have a required eligiblePopulationExclusion";
	private static final String REQUIRE_STRATUM = "The stratum is incorrect.";
	private static final String ELIGIBLE_POPULATION = "eligiblePopulation";
	private static final String PERFORMANCE_MET = "performanceMet";
	private static final String ELIGIBLE_POPULATION_EXCEPTION = "eligiblePopulationException";
	private static final String ELIGIBLE_POPULATION_EXCLUSION = "eligiblePopulationExclusion";
	private static final String TYPE = "type";
	private static final String POPULATION_ID = "populationId";
	private static final String MEASURE_ID = "measureId";
	private static final String STRATUM = "stratum";

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

	@BeforeAll
	static  void setUpCustomMeasureData() {
		MeasureConfigs.setMeasureDataFile("test-multi-prop-measure-data.json");
	}

	@AfterAll
	static void resetMeasuresData() {
		MeasureConfigs.setMeasureDataFile(MeasureConfigs.DEFAULT_MEASURE_DATA_FILE_NAME);
	}

	@BeforeEach
	void setUp() {
		qualityMeasureId = new Node(TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2);
		qualityMeasureId.putValue(MEASURE_ID, "test1");

		aggregateCountNode = new Node(TemplateId.ACI_AGGREGATE_COUNT);
		aggregateCountNode.putValue("aggregateCount", "600");

		eligiblePopulationNode = new Node(TemplateId.MEASURE_DATA_CMS_V2);
		eligiblePopulationNode.putValue(TYPE, SubPopulations.IPOP);
		eligiblePopulationNode.putValue(POPULATION_ID, "ipop1");
		eligiblePopulationNode.addChildNode(aggregateCountNode);

		eligiblePopulationNodeTwo = new Node(TemplateId.MEASURE_DATA_CMS_V2);
		eligiblePopulationNodeTwo.putValue(TYPE, SubPopulations.IPOP);
		eligiblePopulationNodeTwo.putValue(POPULATION_ID, "ipop2");
		eligiblePopulationNodeTwo.addChildNode(aggregateCountNode);

		eligiblePopulationExclusionNode = new Node(TemplateId.MEASURE_DATA_CMS_V2);
		eligiblePopulationExclusionNode.putValue(TYPE, SubPopulations.DENEX);
		eligiblePopulationExclusionNode.putValue(POPULATION_ID, "denex1");
		eligiblePopulationExclusionNode.addChildNode(aggregateCountNode);

		eligiblePopulationExclusionNodeTwo = new Node(TemplateId.MEASURE_DATA_CMS_V2);
		eligiblePopulationExclusionNodeTwo.putValue(TYPE, SubPopulations.DENEX);
		eligiblePopulationExclusionNodeTwo.putValue(POPULATION_ID, "denex2");
		eligiblePopulationExclusionNodeTwo.addChildNode(aggregateCountNode);

		eligiblePopulationExceptionNode = new Node(TemplateId.MEASURE_DATA_CMS_V2);
		eligiblePopulationExceptionNode.putValue(TYPE, SubPopulations.DENEXCEP);
		eligiblePopulationExceptionNode.putValue(POPULATION_ID, "denexcep1");
		eligiblePopulationExceptionNode.addChildNode(aggregateCountNode);

		eligiblePopulationExceptionNodeTwo = new Node(TemplateId.MEASURE_DATA_CMS_V2);
		eligiblePopulationExceptionNodeTwo.putValue(TYPE, SubPopulations.DENEXCEP);
		eligiblePopulationExceptionNodeTwo.putValue(POPULATION_ID, "denexcep2");
		eligiblePopulationExceptionNodeTwo.addChildNode(aggregateCountNode);

		numeratorNode = new Node(TemplateId.MEASURE_DATA_CMS_V2);

		numeratorNode.putValue(TYPE, SubPopulations.NUMER);
		numeratorNode.putValue(POPULATION_ID, "numer1");
		numeratorNode.addChildNode(aggregateCountNode);

		numeratorNodeTwo = new Node(TemplateId.MEASURE_DATA_CMS_V2);
		numeratorNodeTwo.putValue(TYPE, SubPopulations.NUMER);
		numeratorNodeTwo.putValue(POPULATION_ID, "numer2");
		numeratorNodeTwo.addChildNode(aggregateCountNode);

		denominatorNode = new Node(TemplateId.MEASURE_DATA_CMS_V2);
		denominatorNode.putValue(TYPE, SubPopulations.DENOM);
		denominatorNode.putValue(POPULATION_ID, "denom1");
		denominatorNode.addChildNode(aggregateCountNode);

		denominatorNodeTwo = new Node(TemplateId.MEASURE_DATA_CMS_V2);
		denominatorNodeTwo.putValue(TYPE, SubPopulations.DENOM);
		denominatorNodeTwo.putValue(POPULATION_ID, "denom2");
		denominatorNodeTwo.addChildNode(aggregateCountNode);

		encoder = new QualityMeasureIdEncoder(new Context());
		wrapper = new JsonWrapper();
	}

	@Test
	void testInternalEncode() {
		qualityMeasureId.addChildNodes(
				eligiblePopulationNode, eligiblePopulationExceptionNode,
				eligiblePopulationExclusionNode, numeratorNode, denominatorNode,
				eligiblePopulationNodeTwo, eligiblePopulationExceptionNodeTwo,
				eligiblePopulationExclusionNodeTwo, numeratorNodeTwo, denominatorNodeTwo);

		encoder.internalEncode(wrapper, qualityMeasureId);

		LinkedHashMap<String, Object> childValues = getChildValues();
		@SuppressWarnings("unchecked")
		List<LinkedHashMap<String, ?>> subPopulations =
				(List<LinkedHashMap<String, ?>>)childValues.get("strata");
		assertFirstSubPopulation(subPopulations);
		assertSecondSubPopulation(subPopulations);
	}

	@Test
	void testNullSubPopulations() {
		qualityMeasureId.putValue(MEASURE_ID, "test2");
		qualityMeasureId.addChildNodes(eligiblePopulationNode, eligiblePopulationExceptionNode,
				numeratorNode, denominatorNode, eligiblePopulationNodeTwo,
				eligiblePopulationExceptionNodeTwo, numeratorNodeTwo, denominatorNodeTwo);

		encoder.internalEncode(wrapper, qualityMeasureId);

		LinkedHashMap<String, Object> childValues = getChildValues();
		@SuppressWarnings("unchecked")
		List<LinkedHashMap<String, ?>> subPopulations =
				(List<LinkedHashMap<String, ?>>)childValues.get("strata");

		assertThat(subPopulations).isEmpty();
	}

	@SuppressWarnings("unchecked")
	private LinkedHashMap<String, Object> getChildValues() {
		return (LinkedHashMap<String, Object>)((LinkedHashMap<String, Object>) wrapper.getObject()).get("value");
	}

	private void assertFirstSubPopulation(List<LinkedHashMap<String, ?>> strata) {
		LinkedHashMap<String, ?> firstSubPopulation = strata.get(0);

		assertWithMessage(REQUIRE_POPULATION_TOTAL).that(firstSubPopulation.get(ELIGIBLE_POPULATION)).isEqualTo(600);
		assertWithMessage(REQUIRE_PERFORMANCE_MET).that(firstSubPopulation.get(PERFORMANCE_MET)).isEqualTo(600);
		assertWithMessage(REQUIRE_ELIGIBLE_POPULATION_EXCEP).that(firstSubPopulation.get(ELIGIBLE_POPULATION_EXCEPTION)).isEqualTo(600);
		assertWithMessage(REQUIRE_ELIGIBLE_POPULATION_EXCLUS).that(firstSubPopulation.get(ELIGIBLE_POPULATION_EXCLUSION)).isEqualTo(600);
		assertWithMessage(REQUIRE_STRATUM).that(firstSubPopulation.get(STRATUM)).isEqualTo("test1strata1");
	}

	private void assertSecondSubPopulation(List<LinkedHashMap<String, ?>> strata) {
		LinkedHashMap<String, ?> secondSubPopulation = strata.get(1);

		assertWithMessage(REQUIRE_POPULATION_TOTAL).that(secondSubPopulation.get(ELIGIBLE_POPULATION)).isEqualTo(600);
		assertWithMessage(REQUIRE_PERFORMANCE_MET).that(secondSubPopulation.get(PERFORMANCE_MET)).isEqualTo(600);
		assertWithMessage(REQUIRE_ELIGIBLE_POPULATION_EXCEP).that(secondSubPopulation.get(ELIGIBLE_POPULATION_EXCEPTION)).isEqualTo(600);
		assertWithMessage(REQUIRE_ELIGIBLE_POPULATION_EXCLUS).that(secondSubPopulation.get(ELIGIBLE_POPULATION_EXCLUSION)).isEqualTo(600);
		assertWithMessage(REQUIRE_STRATUM).that(secondSubPopulation.get(STRATUM)).isEqualTo("test1strata2");
	}
}
