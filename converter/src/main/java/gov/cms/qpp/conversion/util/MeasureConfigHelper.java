package gov.cms.qpp.conversion.util;

import gov.cms.qpp.conversion.decode.MeasureDataDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.validation.MeasureConfig;
import gov.cms.qpp.conversion.model.validation.MeasureConfigs;
import gov.cms.qpp.conversion.model.validation.SubPopulation;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static gov.cms.qpp.conversion.model.Constants.MEASURE_POPULATION;

public class MeasureConfigHelper {

	public static final String MEASURE_ID = "measureId";
	public static final String NO_MEASURE = "No given measure id";

	// Initialize via static block to avoid Java version issues with Set.of(...)
	private static Set<String> MULTI_TO_SINGLE_PERF_RATE_MEASURE_ID;
	static {
		Set<String> temp = new HashSet<>();
		temp.add("005");
		temp.add("007");
		temp.add("008");
		temp.add("143");
		temp.add("438");
		MULTI_TO_SINGLE_PERF_RATE_MEASURE_ID = Collections.unmodifiableSet(temp);
	}

	public static final String SINGLE_TO_MULTI_PERF_RATE_MEASURE_ID = "370";
	public static final Set<String> CPC_PLUS_MEASURES;
	static {
		Set<String> cpc = new HashSet<>();
		cpc.add("001");
		cpc.add("236");
		CPC_PLUS_MEASURES = Collections.unmodifiableSet(cpc);
	}

	private MeasureConfigHelper() {
		// private for this helper class
	}

	/**
	 * Convenience method to retrieve the measure configuration for validation from an eCQM node
	 *
	 * @param node Contains the id that associates with the measure config
	 * @return the MeasureConfig corresponding to that node, or null if not found
	 */
	public static MeasureConfig getMeasureConfig(Node node) {
		String measureId = node.getValue(MEASURE_ID);
		return findMeasureConfigByUuid(measureId);
	}

	/**
	 * Gets the electronic measure id by uuid or defaults to null if none exists
	 *
	 * @param uuid identifier used to find the electronic measure id
	 * @return electronic measure id, or null if none
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
	 * Creates a grouping of subpopulations extracted from the measure configurations
	 *
	 * @param node          object that holds the nodes to be grouped
	 * @param measureConfig object that holds the groupings
	 * @return List of decoded Nodes
	 */
	public static List<Node> createSubPopulationGrouping(Node node, MeasureConfig measureConfig) {
		List<SubPopulation> measureConfigSubPopulations = measureConfig.getSubPopulation();

		if (SINGLE_TO_MULTI_PERF_RATE_MEASURE_ID.equalsIgnoreCase(measureConfig.getMeasureId())) {
			measureConfigSubPopulations = setUpSingleToMultiSubPops(measureConfigSubPopulations);
		}

		int subPopCount = measureConfigSubPopulations.size();
		List<Node> subPopNodes = initializeMeasureDataList(subPopCount);
		Map<String, Integer> mapPopulationIdToSubPopIndex = createSubPopulationIndexMap(measureConfigSubPopulations);

		node.getChildNodes().stream()
				.filter(childNode -> TemplateId.MEASURE_DATA_CMS_V4 == childNode.getType())
				.forEach(childNode -> {
					String populationId = childNode.getValue(MEASURE_POPULATION);
					Integer subPopIndex = mapPopulationIdToSubPopIndex.get(populationId.toUpperCase(Locale.ENGLISH));
					if (subPopIndex != null) {
						Node newParentNode = subPopNodes.get(subPopIndex);
						newParentNode.addChildNode(childNode);
					}
				});

		return subPopNodes;
	}

	private static List<SubPopulation> setUpSingleToMultiSubPops(List<SubPopulation> measureConfigSubPopulations) {
		return measureConfigSubPopulations.stream()
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
	}

	/**
	 * Initializes a list of Measure Section nodes from how many subpopulations are being converted
	 *
	 * @param subPopulationCount number of subpopulations to convert
	 * @return List of decoded Nodes
	 */
	private static List<Node> initializeMeasureDataList(int subPopulationCount) {
		return IntStream.range(0, subPopulationCount)
				.mapToObj(ignore -> new Node(TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V5))
				.collect(Collectors.toList());
	}

	/**
	 * Creates a map of child UUIDs to indexes for subpopulation grouping
	 *
	 * @param subPopulations list of Subpopulations to index
	 * @return Map of Population UUID keys and index values
	 */
	private static Map<String, Integer> createSubPopulationIndexMap(List<SubPopulation> subPopulations) {
		Map<String, Integer> supPopMap = new HashMap<>();
		int index = 0;
		for (SubPopulation subPopulation : subPopulations) {
			if (subPopulation.getNumeratorUuid() != null) {
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
		return Stream.of(
						measureConfig.getElectronicMeasureId(),
						measureConfig.getElectronicMeasureVerUuid(),
						measureConfig.getMeasureId()
				)
				.filter(Objects::nonNull)
				.findFirst()
				.orElse(NO_MEASURE);
	}

	/**
	 * Checks the given measure id if it is a multi-to-single performance rate id.
	 *
	 * @param measureId a measure ID to check
	 * @return true if it is configured as a multi-to-single performance rate ID
	 */
	public static boolean checkMultiToSinglePerformanceRateId(String measureId) {
		return MULTI_TO_SINGLE_PERF_RATE_MEASURE_ID.contains(measureId);
	}

	/**
	 * Replaces the internal MULTI_TO_SINGLE_PERF_RATE_MEASURE_ID set with a defensive copy
	 * of the provided set, so the static state cannot be mutated externally.
	 *
	 * @param multiToSinglePerfRateMeasureIdSet a set of measure IDs
	 */
	public static void setMultiToSinglePerfRateMeasureId(Set<String> multiToSinglePerfRateMeasureIdSet) {
		if (multiToSinglePerfRateMeasureIdSet == null) {
			MULTI_TO_SINGLE_PERF_RATE_MEASURE_ID = Collections.emptySet();
		} else {
			Set<String> copy = new HashSet<>(multiToSinglePerfRateMeasureIdSet);
			MULTI_TO_SINGLE_PERF_RATE_MEASURE_ID = Collections.unmodifiableSet(copy);
		}
	}
}
