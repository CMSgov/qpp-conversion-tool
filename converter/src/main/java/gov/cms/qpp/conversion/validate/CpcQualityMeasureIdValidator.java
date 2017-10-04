package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.decode.MeasureDataDecoder;
import gov.cms.qpp.conversion.decode.StratifierDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.validation.MeasureConfig;
import gov.cms.qpp.conversion.model.validation.MeasureConfigs;
import gov.cms.qpp.conversion.model.validation.SubPopulation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static gov.cms.qpp.conversion.decode.MeasureDataDecoder.MEASURE_POPULATION;
import static gov.cms.qpp.conversion.decode.MeasureDataDecoder.MEASURE_TYPE;
import static gov.cms.qpp.conversion.validate.QualityMeasureIdValidator.SINGLE_MEASURE_POPULATION;
import static gov.cms.qpp.conversion.validate.QualityMeasureIdValidator.SINGLE_MEASURE_TYPE;

/**
 * Validates a Measure Reference Results for CPC Plus requirements
 */
@Validator(value = TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2, program = Program.CPC)
public class CpcQualityMeasureIdValidator extends NodeValidator {

	static final String INVALID_PERFORMANCE_RATE_COUNT =
			"Must contain correct number of performance rate(s). Correct Number is %s";
	static final String MISSING_STRATA = "Missing strata %s for %s measure (%s)";
	static final String STRATA_MISMATCH = "Amount of stratifications %d does not meet expectations %d "
			+ "for %s measure (%s). Expected strata: %s";

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

		check(node)
				.childMinimum(String.format(INVALID_PERFORMANCE_RATE_COUNT, requiredPerformanceRateCount),
						requiredPerformanceRateCount, TemplateId.PERFORMANCE_RATE_PROPORTION_MEASURE)
				.childMaximum(String.format(INVALID_PERFORMANCE_RATE_COUNT, requiredPerformanceRateCount),
						requiredPerformanceRateCount, TemplateId.PERFORMANCE_RATE_PROPORTION_MEASURE);

		Set<Map<String, Object>> subHashes = measureConfig.getSubPopulation().stream()
				.map(this::getSuppliers).collect(Collectors.toSet());
		node.getChildNodes(TemplateId.MEASURE_DATA_CMS_V2)
				.forEach(child -> verifyMeasureData(child, subHashes));
	}

	/**
	 * Low order access to sub population properties.
	 *
	 * @param sub SubPopulation to hash
	 * @return hash of {@link SubPopulation}
	 */
	private Map<String, Object> getSuppliers(SubPopulation sub) {
		Map<String, Object> suppliers = new HashMap<>();
		suppliers.put("DENEXCEP", sub.getDenominatorExceptionsUuid());
		suppliers.put("DENEX", sub.getDenominatorExclusionsUuid());
		suppliers.put("NUMER", sub.getNumeratorUuid());
		suppliers.put("DENOM", sub.getDenominatorUuid());
		suppliers.put("IPP", sub.getInitialPopulationUuid());
		suppliers.put("IPOP", sub.getInitialPopulationUuid());
		suppliers.put("strata", sub.getStrata());
		return suppliers;
	}

	/**
	 * Match measure data to appropriate sub population id and search measure data node for required strata.
	 *
	 * @param child measure data node
	 * @param subHashes hash of sub population properties
	 */
	@SuppressWarnings("unchecked")
	private void verifyMeasureData(Node child, Set<Map<String, Object>> subHashes) {
		String measureDataType = child.getValue(MeasureDataDecoder.MEASURE_TYPE);
		String measureDataUuid = child.getValue(MeasureDataDecoder.MEASURE_POPULATION);

		thoroughlyCheck(child)
				.incompleteValidation()
				.singleValue(SINGLE_MEASURE_TYPE, MEASURE_TYPE)
				.singleValue(SINGLE_MEASURE_POPULATION, MEASURE_POPULATION);

		subHashes.stream()
				.filter(hash -> {

						return measureDataUuid.equals(hash.get(measureDataType));
				})
				.map(hash -> (List<String>) hash.get("strata"))
				.forEach(strata -> strataCheck(child, strata));
	}

	/**
	 * Validate measure strata
	 *
	 * @param node measure node
	 * @param strata sub population constituent ids
	 */
	private void strataCheck(Node node, List<String> strata) {
		List<Node> strataNodes = node.getChildNodes(TemplateId.REPORTING_STRATUM_CMS)
				.collect(Collectors.toList());

		if (strataNodes.size() != strata.size()) {
			String message = String.format(STRATA_MISMATCH, strataNodes.size(), strata.size(),
					node.getValue(MeasureDataDecoder.MEASURE_TYPE),
					node.getValue(MEASURE_POPULATION),
					strata);
			this.getDetails().add(new Detail(message, node.getPath()));
		}

		strata.forEach(stratum -> {
			Predicate<Node> seek = child ->
					child.getValue(StratifierDecoder.STRATIFIER_ID).equals(stratum);

			if (strataNodes.stream().noneMatch(seek)) {
				String message = String.format(MISSING_STRATA, stratum,
						node.getValue(MeasureDataDecoder.MEASURE_TYPE),
						node.getValue(MEASURE_POPULATION));
				this.getDetails().add(new Detail(message, node.getPath()));
			}
		});
	}

}
