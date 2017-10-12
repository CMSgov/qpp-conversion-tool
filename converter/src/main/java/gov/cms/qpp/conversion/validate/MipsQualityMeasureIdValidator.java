package gov.cms.qpp.conversion.validate;

import com.google.common.collect.Sets;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.validation.MeasureConfig;
import gov.cms.qpp.conversion.model.validation.MeasureConfigs;
import gov.cms.qpp.conversion.model.validation.SubPopulation;
import gov.cms.qpp.conversion.model.validation.SubPopulations;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static gov.cms.qpp.conversion.decode.PerformanceRateProportionMeasureDecoder.PERFORMANCE_RATE_ID;

/**
 * Validates a Measure Reference Results node.
 */
@Validator(value = TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2, program = Program.MIPS)
public class MipsQualityMeasureIdValidator extends QualityMeasureIdValidator {

	MipsQualityMeasureIdValidator() {
		subPopulationExclusions = Sets.newHashSet("IPOP", "IPP");
	}


	@Override
	List<Consumer<Node>> prepValidations(SubPopulation subPopulation) {
		return Arrays.asList(makeValidator(subPopulation, subPopulation::getDenominatorExceptionsUuid, SubPopulations.DENEXCEP),
				makeValidator(subPopulation, subPopulation::getDenominatorExclusionsUuid, SubPopulations.DENEX),
				makeValidator(subPopulation, subPopulation::getNumeratorUuid, SubPopulations.NUMER),
				makeValidator(subPopulation, subPopulation::getDenominatorUuid, SubPopulations.DENOM));
	}

	void validateAllSubPopulations(final Node node, final MeasureConfig measureConfig) {
		List<SubPopulation> subPopulations = measureConfig.getSubPopulation();

		if (subPopulations.isEmpty()) {
			return;
		}

		SubPopulations.getExclusiveKeys(subPopulationExclusions)
				.forEach(key -> validateChildTypeCount(subPopulations, key, node));

		for (SubPopulation subPopulation : subPopulations) {
			validateSubPopulation(node, subPopulation);
		}
		validateExistingPerformanceRates(node, subPopulations);
	}

	private void validateExistingPerformanceRates(Node node, List<SubPopulation> subPopulations) {
		List<Node> performanceRateList = node
				.getChildNodes(TemplateId.PERFORMANCE_RATE_PROPORTION_MEASURE)
				.collect(Collectors.toList());

		for (Node performanceRateNode: performanceRateList) {
			thoroughlyCheck(performanceRateNode)
					.incompleteValidation()
					.singleValue(SINGLE_PERFORMANCE_RATE, PERFORMANCE_RATE_ID);
			String performanceUuid = performanceRateNode.getValue(PERFORMANCE_RATE_ID);

			if (performanceUuid != null) {
				SubPopulation subPopulation = subPopulations.stream()
						.filter(makePerformanceRateUuidFinder(performanceUuid))
						.findFirst()
						.orElse(null);

				if (subPopulation == null) {
					MeasureConfig config =
							MeasureConfigs.getConfigurationMap().get(node.getValue(MEASURE_ID));
					String message = String.format(INCORRECT_PERFORMANCE_UUID, config.getElectronicMeasureId(),
							PERFORMANCE_RATE_ID, performanceUuid);
					this.getDetails().add(new Detail(message, node.getPath()));
				}
			}

		}
	}

	private Predicate<SubPopulation> makePerformanceRateUuidFinder(String uuid) {
		return subPopulation -> uuid.equals(subPopulation.getNumeratorUuid());
	}
}
