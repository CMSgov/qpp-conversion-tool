package gov.cms.qpp.conversion.validate;

import com.google.common.collect.Sets;
import gov.cms.qpp.conversion.correlation.model.Template;
import gov.cms.qpp.conversion.decode.MeasureDataDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.validation.MeasureConfig;
import gov.cms.qpp.conversion.model.validation.MeasureConfigs;
import gov.cms.qpp.conversion.model.validation.SubPopulation;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Validates a Measure Reference Results for CPC Plus requirements
 */
@Validator(value = TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2, program = Program.CPC)
public class CpcQualityMeasureIdValidator extends NodeValidator {

	protected static final String INVALID_PERFORMANCE_RATE_COUNT = "Must contain correct number of performance rate(s)";

	/**
	 * Validates node of all criteria specified for CPC Plus
	 * <ul>
	 *     <li>checks that the node contains the correct number of performance rates</li>
	 * </ul>
	 * @param node The node to validate.
	 */
	@Override
	protected void internalValidateSingleNode(Node node) {
		Map<String, MeasureConfig> configurationMap = MeasureConfigs.getConfigurationMap();
		String value = node.getValue(QualityMeasureIdValidator.MEASURE_ID);
		MeasureConfig measureConfig = configurationMap.get(value);
		int requiredPerformanceRateCount = measureConfig.getStrata().size();
		List<SubPopulation> subPopulations = measureConfig.getSubPopulation();

		check(node)
				.childMinimum(INVALID_PERFORMANCE_RATE_COUNT,
						requiredPerformanceRateCount, TemplateId.PERFORMANCE_RATE_PROPORTION_MEASURE)
				.childMaximum(INVALID_PERFORMANCE_RATE_COUNT,
						requiredPerformanceRateCount, TemplateId.PERFORMANCE_RATE_PROPORTION_MEASURE);

		if (subPopulations == null) {
			return;
		}

		for (SubPopulation subPopulation : subPopulations) {
			validateSubPopulation(node, subPopulation);
		}
	}

	private void validateSubPopulation(Node node, SubPopulation subPopulation) {
		String denomId = subPopulation.getDenominatorUuid();
		String ipopId = subPopulation.getInitialPopulationUuid();
		Stream<Node> nodeStream = node.getChildNodes(TemplateId.MEASURE_DATA_CMS_V2);

		Node denomNode = nodeStream
				.filter(childNodes -> denomId.equals(childNodes.getValue(MeasureDataDecoder.MEASURE_POPULATION)))
				.findFirst().orElse(null);

		Node ipopNode = nodeStream
				.filter(childNodes -> ipopId.equals(childNodes.getValue(MeasureDataDecoder.MEASURE_POPULATION)))
				.findFirst().orElse(null);

		System.out.println("Denom : " + denomNode);
		System.out.println("Ipop : " + ipopNode);
	}

}
