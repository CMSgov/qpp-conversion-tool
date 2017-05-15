package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;

import gov.cms.qpp.conversion.model.error.ValidationError;
import gov.cms.qpp.conversion.model.validation.MeasureConfig;
import gov.cms.qpp.conversion.model.validation.MeasureConfigs;
import gov.cms.qpp.conversion.model.validation.SubPopulation;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Validates a Measure Reference Results node.
 */
@Validator(templateId = TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2)
public class QualityMeasureIdValidator extends NodeValidator {
	protected static final String MEASURE_GUID_MISSING = "The measure reference results must have a measure GUID";
	protected static final String NO_CHILD_MEASURE = "The measure reference results must have at least one measure";

	/**
	 * Validates that the Measure Reference Results node contains...
	 * <ul>
	 *     <li>A measure GUID.</li>
	 *     <li>At least one quality measure.</li>
	 * </ul>
	 *
	 * @param node The node to validate.
	 */
	@Override
	protected void internalValidateSingleNode(final Node node) {
		thoroughlyCheck(node)
			.value(MEASURE_GUID_MISSING, "measureId")
			.childMinimum(NO_CHILD_MEASURE, 1, TemplateId.MEASURE_DATA_CMS_V2);
		validateMeasureConfigs(node);
	}

	private void validateMeasureConfigs(Node node) {
		Map<String, MeasureConfig> configurationMap = MeasureConfigs.getConfigurationMap();

		MeasureConfig measureConfig = configurationMap.get(node.getValue("measureId"));

		if (measureConfig == null) {
			return;
		}

		List<SubPopulation> subPopulations = measureConfig.getSubPopulation();
		for (SubPopulation subPopulation: subPopulations) {
			validateDenominatorExclusion(node, subPopulation);
		}
	}

	private void validateDenominatorExclusion(Node node, SubPopulation subPopulation) {
		String denominatorExclusion = subPopulation.getDenominatorExclusionsUuid();
		if (denominatorExclusion != null) {
			List<Node> denominatorExclusionNode = node.getChildNodes(
					thisNode -> "DENEX".equals(thisNode.getValue("type"))).collect(Collectors.toList());
			if (denominatorExclusionNode.isEmpty()) {
				this.getValidationErrors().add(
						new ValidationError("The eCQM measure requires a denominator exclusion", node.getPath()));
			}
		}
	}


	/**
	 * Does nothing.
	 *
	 * @param nodes The list of nodes to validate.
	 */
	@Override
	protected void internalValidateSameTemplateIdNodes(final List<Node> nodes) {
		//no cross-node validations required
	}
}
