package gov.cms.qpp.conversion.util;

import gov.cms.qpp.conversion.decode.MeasureDataDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.validation.MeasureConfig;
import gov.cms.qpp.conversion.model.validation.MeasureConfigs;
import gov.cms.qpp.conversion.model.validation.SubPopulation;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MeasureConfigHelper {

	public static final String MEASURE_ID = "measureId";
	public static final String NO_MEASURE = "No given measure id";
	public static final String SINGLE_TO_MULTIPLE_SUP_POPULATION = "CMS159v7";

	private MeasureConfigHelper() {
		// private for this helper class
	}

	/**
	 * Convenience method to retrieve the measure configuration for validation from an ecqm node
	 *
	 * @param node Contains the id that associates with the measure config
	 * @return
	 */
	public static MeasureConfig getMeasureConfig(Node node) {
		String measureId =  node.getValue(MEASURE_ID);
		return findMeasureConfigByUuid(measureId);
	}

	/**
	 * Gets the electronic measure id by uuid or defaults to null if none exists
	 *
	 * @param uuid identifier used to fined the electronic measure id
	 * @return electronic measure id
	 */
	public static String getMeasureConfigIdByUuidOrDefault(String uuid) {
		MeasureConfig config = findMeasureConfigByUuid(uuid);
		if (config != null) {
			return config.getElectronicMeasureId();
		}
		return null;
	}

	private static MeasureConfig findMeasureConfigByUuid(String uuid) {
		if (uuid == null) {
			return null;
		}

		return MeasureConfigs.getConfigurationMap().get(uuid.toLowerCase(Locale.US));
	}

	/**
	 * Determine which measure id-ish value should be used for a given node.
	 *
	 * @param node a decoded node
	 * @return the best available measure id value
	 */
	public static String getPrioritizedId(Node node) {
		MeasureConfig measureConfig = getMeasureConfig(node);
		return getPrioritizedId(measureConfig);
	}

	/**
	 * Creates a grouping of sub populations extracted from the measure configurations
	 *
	 * @param node object that holds the nodes to be grouped
	 * @param measureConfig object that holds the groupings
	 * @return List of decoded Nodes
	 */
	public static List<Node> createSubPopulationGrouping(Node node, MeasureConfig measureConfig) {
		List<SubPopulation> measureConfigSubPopulations = measureConfig.getSubPopulation();
		if (SINGLE_TO_MULTIPLE_SUP_POPULATION.equalsIgnoreCase(measureConfig.getElectronicMeasureId())) {
			measureConfigSubPopulations = setUpSingleToMultiSubPops(measureConfigSubPopulations);
		}
		int subPopCount = measureConfigSubPopulations.size();
		List<Node> subPopNodes = initializeMeasureDataList(subPopCount);
		Map<String, Integer> mapPopulationIdToSubPopIndex = createSubPopulationIndexMap(measureConfigSubPopulations);
		node.getChildNodes().stream()
			.filter(childNode -> TemplateId.MEASURE_DATA_CMS_V2 == childNode.getType())
			.forEach(childNode -> {
				String populationId = childNode.getValue(MeasureDataDecoder.MEASURE_POPULATION);
				Integer subPopIndex = mapPopulationIdToSubPopIndex.get(populationId.toUpperCase(Locale.ENGLISH));
				if (subPopIndex != null) {
					Node newParentNode = subPopNodes.get(subPopIndex);
					newParentNode.addChildNode(childNode);
				}
			});
		return subPopNodes;
	}

	private static List<SubPopulation> setUpSingleToMultiSubPops(List<SubPopulation> measureConfigSubPopulations) {
		return measureConfigSubPopulations
			.stream()
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
	}

	/**
	 * Initializes a list of Measure Section nodes from how many sub populations are being converted
	 *
	 * @param subPopulationCount number of sub populations to convert
	 * @return List of decoded Nodes
	 */
	private static List<Node> initializeMeasureDataList(int subPopulationCount) {
		return IntStream.range(0, subPopulationCount)
			.mapToObj(ignore -> new Node(TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2))
			.collect(Collectors.toList());
	}


	/**
	 * Creates a map of child uuids to indexes for sub population grouping
	 *
	 * @param subPopulations list of Subpopulations to index
	 * @return Map of Population UUID keys and index values
	 */
	private static Map<String, Integer> createSubPopulationIndexMap(List<SubPopulation> subPopulations) {
		Map<String, Integer> supPopMap = new HashMap<>();
		int index = 0;
		for (SubPopulation subPopulation : subPopulations) {
			if (null !=subPopulation.getNumeratorUuid()) {
				supPopMap.put(subPopulation.getDenominatorUuid(), index);
				supPopMap.put(subPopulation.getDenominatorExceptionsUuid(), index);
				supPopMap.put(subPopulation.getDenominatorExclusionsUuid(), index);
				supPopMap.put(subPopulation.getNumeratorUuid(), index);
				supPopMap.put(subPopulation.getInitialPopulationUuid(), index);
				index++;
			}
		}
		return supPopMap;
	}

	/**
	 * Find the best available measure id value within the given {@link MeasureConfig}
	 *
	 * @param measureConfig a configuration that details a measure
	 * @return the best available measure id from the given config or a message stating that none were found.
	 */
	static String getPrioritizedId(MeasureConfig measureConfig) {
		return Stream.of(measureConfig.getElectronicMeasureId(),
			measureConfig.getElectronicMeasureVerUuid(), measureConfig.getMeasureId())
			.filter(Objects::nonNull)
			.findFirst()
			.orElse(NO_MEASURE);
	}
}
