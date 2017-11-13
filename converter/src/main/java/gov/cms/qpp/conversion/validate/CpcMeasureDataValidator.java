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
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Validates a Sub population's Measure Data for the CPC Plus program entity
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
		Map<String, TemplateId> nodeTypeToTemplateIdMap = SupplementalData.getSupplementalTypeMapToTemplateId();
		for (String supplementalDataType: nodeTypeToTemplateIdMap.keySet()) {
			validateAllSupplementalNodesOfSpecifiedType(
					node, nodeTypeToTemplateIdMap.get(supplementalDataType), supplementalDataType);
		}
	}

	/**
	 * Validates all of the nodes under one specified Supplemental Type
	 *
	 * @param node
	 * @param currentTemplateId
	 * @param supplementalDataType
	 */
	private void validateAllSupplementalNodesOfSpecifiedType(Node node,
															 TemplateId currentTemplateId,
															 String supplementalDataType) {
		List<Node> supplementalDataNodes =
				node.getChildNodes(currentTemplateId).collect(Collectors.toList());
		List<SupplementalData> codes = SupplementalData.getSupplementalDataListByType(supplementalDataType);

		for (SupplementalData supplementalData : codes) {
			Node validateSupplementalNode = filterCorrectNode(
					supplementalDataType, supplementalDataNodes, supplementalData);

			if (validateSupplementalNode == null) {
				addSupplementalValidationError(node, supplementalData);
			}
		}
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
								   List<Node> supplementalDataNodes,
								   SupplementalData supplementalData) {
		return supplementalDataNodes.stream()
				.filter(makeValidatorBySupplementalTypeAndCode(supplementalDataType, supplementalData.getCode()))
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
	private Predicate<Node> makeValidatorBySupplementalTypeAndCode(String nodeValueName, String code) {
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
}
