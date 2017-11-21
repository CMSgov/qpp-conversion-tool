package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.decode.MeasureDataDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Program;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ErrorCode;
import gov.cms.qpp.conversion.model.error.LocalizedError;
import gov.cms.qpp.conversion.model.validation.MeasureConfig;
import gov.cms.qpp.conversion.model.validation.MeasureConfigs;
import gov.cms.qpp.conversion.model.validation.SupplementalData;
import gov.cms.qpp.conversion.model.validation.SupplementalData.SupplementalType;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Validates a Sub Population's Measure Data for the CPC Plus program entity
 */
@Validator(value = TemplateId.MEASURE_DATA_CMS_V2, program = Program.CPC)
public class CpcMeasureDataValidator extends NodeValidator {

	/**
	 * Validates a single measure data sub population
	 *
	 * @param node The node to validate.
	 */
	@Override
	protected void internalValidateSingleNode(Node node) {
		validateSupplementalDataByType(node);
	}

	/**
	 * Categorizes each validation by specified Supplemental Types
	 *
	 * @param node current measure data node
	 */
	private void validateSupplementalDataByType(Node node) {
		Map<SupplementalType, TemplateId> nodeTypeToTemplateIdMap = SupplementalData.getSupplementalTypeMapToTemplateId();
		for (Map.Entry<SupplementalType, TemplateId> entry: nodeTypeToTemplateIdMap.entrySet()) {
			validateAllSupplementalNodesOfSpecifiedType(
					node, entry.getValue(), entry.getKey().toString());
		}
	}

	/**
	 * Validates all of the nodes under one specified Supplemental Type
	 *
	 * @param node Parent node of the nodes to be validated
	 * @param currSupplementalDataTemplateId Template Id of the nodes to be validated
	 * @param supplementalDataType current data type to be validated
	 */
	private void validateAllSupplementalNodesOfSpecifiedType(Node node,
															 TemplateId currSupplementalDataTemplateId,
															 String supplementalDataType) {
		Set<Node> supplementalDataNodes =
				node.getChildNodes(currSupplementalDataTemplateId).collect(Collectors.toSet());
		Set<SupplementalData> codes = SupplementalData.getSupplementalDataSetByType(supplementalDataType);

		for (SupplementalData supplementalData : codes) {
			Node validatedSupplementalNode = filterCorrectNode(
					supplementalDataType, supplementalDataNodes, supplementalData);

			if (validatedSupplementalNode == null) {
				addSupplementalValidationError(node, supplementalData);
			}
		}
		validateSupplementalDataNodeCounts(node, supplementalDataType, supplementalDataNodes);
	}

	/**
	 * Filters out the specified Supplemental Data Node to validate
	 *
	 * @param supplementalDataType Supplemental value name for the current Node value
	 * @param supplementalDataNodes List of nodes to filter
	 * @param supplementalData Current Supplemental Data to validate against
	 * @return
	 */
	private Node filterCorrectNode(String supplementalDataType,
								   Set<Node> supplementalDataNodes,
								   SupplementalData supplementalData) {
		return supplementalDataNodes.stream()
				.filter(filterDataBySupplementalCode(supplementalDataType, supplementalData.getCode()))
				.findFirst()
				.orElse(null);
	}

	/**
	 * Predicate for filtering the correct Supplemental Data node.
	 *
	 * @param nodeValueName Object that maps to the current node value
	 * @param code Required code to be validate against
	 * @return
	 */
	private Predicate<Node> filterDataBySupplementalCode(String nodeValueName, String code) {
		return thisNode -> code.equalsIgnoreCase(thisNode.getValue(nodeValueName));
	}

	/**
	 * Adds a Supplemental Validation Error upon failure
	 *
	 * @param node Object being validated
	 * @param supplementalData Object holding the current code that was validated
	 */
	private void addSupplementalValidationError(Node node, SupplementalData supplementalData) {
		MeasureConfig config =
				MeasureConfigs.getConfigurationMap()
						.get(node.getParent().getValue(QualityMeasureIdValidator.MEASURE_ID));
		LocalizedError error =
				ErrorCode.CPC_PLUS_MISSING_SUPPLEMENTAL_CODE.format(supplementalData.getCode(),
						config.getElectronicMeasureId(), node.getValue(MeasureDataDecoder.MEASURE_TYPE));
		addValidationError(Detail.forErrorAndNode(error, node));
	}

	/**
	 * Validates all Supplemental Data nodes for an Aggregate count
	 *
	 * @param node parent node
	 * @param supplementalDataType type of supplemental nodes
	 * @param supplementalDataNodes supplemental nodes to be validated
	 */
	private void validateSupplementalDataNodeCounts(Node node,
													String supplementalDataType, Set<Node> supplementalDataNodes) {
		supplementalDataNodes.forEach(thisNode -> {
			LocalizedError error = makeIncorrectCountSizeLocalizedError(node, supplementalDataType, thisNode);
			check(thisNode).childMinimum(error, 1, TemplateId.ACI_AGGREGATE_COUNT)
					.childMaximum(error, 1, TemplateId.ACI_AGGREGATE_COUNT);
		});
	}

	/**
	 * Creates a localized error for an invalid number of aggregate counts
	 *
	 * @param node parent node
	 * @param supplementalDataType type of supplemental nodes
	 * @param thisNode current supplemental node to be validated
	 * @return
	 */
	private LocalizedError makeIncorrectCountSizeLocalizedError(Node node, String supplementalDataType, Node thisNode) {
		MeasureConfig config = MeasureConfigs.getConfigurationMap().get(
				node.getParent().getValue(QualityMeasureIdValidator.MEASURE_ID));

		return ErrorCode.CPC_PLUS_SUPPLEMENTAL_DATA_MISSING_COUNT.format(
				thisNode.getValue(supplementalDataType), node.getValue(MeasureDataDecoder.MEASURE_TYPE),
				config.getElectronicMeasureId());
	}
}
