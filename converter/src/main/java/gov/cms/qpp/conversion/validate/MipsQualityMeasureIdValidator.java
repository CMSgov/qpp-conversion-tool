package gov.cms.qpp.conversion.validate;

import com.google.common.collect.Sets;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
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
				makeValidator(subPopulation, subPopulation::getDenominatorUuid, SubPopulations.DENOM),
				makePerformanceRateUuidValidator(subPopulation::getNumeratorUuid, PERFORMANCE_RATE_ID));
	}

	/**
	 * Method for Performance Rate Uuid validations
	 *
	 * @param check a property existence check
	 * @param keys that identify measures
	 * @return a callback / consumer that will perform a measure specific validation against a given
	 * node.
	 */
	Consumer<Node> makePerformanceRateUuidValidator(Supplier<Object> check, String... keys) {
		return node -> {
			if (check.get() != null) {
				Predicate<Node> childUuidFinder =
						makeUuidChildFinder(check, SINGLE_PERFORMANCE_RATE, PERFORMANCE_RATE_ID);

				List<Node> performanceRateList = node
						.getChildNodes(TemplateId.PERFORMANCE_RATE_PROPORTION_MEASURE)
						.collect(Collectors.toList());

				if (!performanceRateList.isEmpty()) {
					Node existingUuidChild = performanceRateList.stream()
							.filter(childUuidFinder)
							.findFirst()
							.orElse(null);

					if (existingUuidChild == null) {
						addMeasureConfigurationValidationMessage(check, keys, node);
					}
				}
			}
		};
	}
}
