package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.decode.MeasureDataDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.LocalizedProblem;
import gov.cms.qpp.conversion.model.error.ProblemCode;
import gov.cms.qpp.conversion.model.validation.MeasureConfig;
import gov.cms.qpp.conversion.model.validation.SupplementalData;
import gov.cms.qpp.conversion.model.validation.SupplementalData.SupplementalType;
import gov.cms.qpp.conversion.util.MeasureConfigHelper;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static gov.cms.qpp.conversion.decode.SkeletalSupplementalDataDecoder.SUPPLEMENTAL_DATA_KEY;

/**
 * Validates a Sub Population's Measure Data for the CPC Plus program entity
 */
@Validator(value = TemplateId.MEASURE_DATA_CMS_V4, program = Program.CPC)
public class CpcMeasureDataValidator extends NodeValidator {

	/**
	 * Validates a single measure data sub population
	 *
	 * @param node The node to validate.
	 */
	@Override
	protected void performValidation(Node node) {
		validateSupplementalDataByType(node);
	}

	/**
	 * Categorizes each validation by specified Supplemental Types
	 *
	 * @param node current measure data node
	 */
	private void validateSupplementalDataByType(Node node) {
		Map<SupplementalType, TemplateId> nodeTypeToTemplateIdMap =
				SupplementalData.getSupplementalTypeMapToTemplateId();
		for (Map.Entry<SupplementalType, TemplateId> entry: nodeTypeToTemplateIdMap.entrySet()) {
			validateAllSupplementalNodesOfSpecifiedType(
					node, entry.getValue(), entry.getKey());
		}
	}

	/**
	 * Validates all of the nodes under one specified Supplemental Type
	 *
	 * @param node Parent node of the nodes to be validated
	 * @param currSupplementalDataTemplateId Template Id of the nodes to be validated
	 * @param supplementalDataType current data type to be validated
	 */
	private void validateAllSupplementalNodesOfSpecifiedType(
			Node node, TemplateId currSupplementalDataTemplateId, SupplementalType supplementalDataType) {
		Set<Node> supplementalDataNodes =
				node.getChildNodes(currSupplementalDataTemplateId).collect(Collectors.toSet());
		EnumSet<SupplementalData> codes = EnumSet.copyOf(
				 SupplementalData.getSupplementalDataSetByType(supplementalDataType));
		MeasureConfig measureConfig = MeasureConfigHelper.getMeasureConfig(node.getParent());
		if (measureConfig != null) {
			String electronicMeasureId = measureConfig.getElectronicMeasureId();
			for (SupplementalData supplementalData : codes) {
				Node validatedSupplementalNode = filterCorrectNode(supplementalDataNodes, supplementalData);

				if (validatedSupplementalNode == null) {
					addSupplementalValidationError(node, supplementalData, electronicMeasureId);
				} else {
					LocalizedProblem error = makeIncorrectCountSizeLocalizedError(node, supplementalData.getCode(),
						electronicMeasureId);
					checkErrors(validatedSupplementalNode)
						.childExact(error, 1, TemplateId.PI_AGGREGATE_COUNT);
				}
			}
		}
	}

	/**
	 * Filters out the specified Supplemental Data Node to validate
	 *
	 * @param supplementalDataNodes List of nodes to filter
	 * @param supplementalData Current Supplemental Data to validate against
	 * @return first matching node
	 */
	private Node filterCorrectNode(Set<Node> supplementalDataNodes, SupplementalData supplementalData) {
		return supplementalDataNodes.stream()
				.filter(filterDataBySupplementalCode(supplementalData.getCode()))
				.findFirst()
				.orElse(null);
	}

	/**
	 * Predicate for filtering the correct Supplemental Data node.
	 *
	 * @param code Required code to be validate against
	 * @return filtering predicate
	 */
	private Predicate<Node> filterDataBySupplementalCode(String code) {
		return thisNode -> code.equalsIgnoreCase(thisNode.getValue(SUPPLEMENTAL_DATA_KEY));
	}

	/**
	 * Adds a Supplemental Validation Error upon failure
	 *
	 * @param node Object being validated
	 * @param supplementalData Object holding the current code that was validated
	 * @param measureId current electronic measure identification
	 */
	private void addSupplementalValidationError(Node node, SupplementalData supplementalData, String measureId) {
		LocalizedProblem error =
				ProblemCode.CPC_PLUS_MISSING_SUPPLEMENTAL_CODE.format(
					supplementalData.getType(), supplementalData, supplementalData.getCode(),
						measureId, node.getValue(MeasureDataDecoder.MEASURE_TYPE));
		addError(Detail.forProblemAndNode(error, node));
	}

	/**
	 * Creates a localized error for an invalid number of aggregate counts
	 *
	 * @param node holder of the measure type
	 * @param supplementalCode data code that is missing
	 * @param measureId electronic measure id
	 * @return initialized {@link LocalizedProblem}
	 */
	private LocalizedProblem makeIncorrectCountSizeLocalizedError(Node node, String supplementalCode, String measureId) {
		return ProblemCode.CPC_PLUS_SUPPLEMENTAL_DATA_MISSING_COUNT.format(
			supplementalCode, node.getValue(MeasureDataDecoder.MEASURE_TYPE),
				measureId);
	}
}
