package gov.cms.qpp.conversion.validate;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.NodeType;
import gov.cms.qpp.conversion.model.ValidationError;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.validation.MeasureConfig;
import gov.cms.qpp.conversion.model.validation.MeasureConfigs;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

/**
 * Validate all ACI Proportion Type Measures
 */
@Validator(templateId = "2.16.840.1.113883.10.20.27.3.28", required = true)
public class AciProportionMeasureValidator extends QrdaValidator {

	private MeasureConfigs measureConfigs;

	protected static final String ACI_PROPORTION_NODE_REQUIRED = "At least one Aci Proportion Measure Node is required";
	protected static final String NO_PARENT_SECTION = "This ACI Measure Node should have an ACI Section Node as a parent";
	protected static final String NO_NUMERATOR = "This ACI Measure Node does not contain a Numerator Node child";
	protected static final String TOO_MANY_NUMERATORS = "This ACI Measure Node contains too many Numerator Node children";
	protected static final String NO_DENOMINATOR = "This ACI Measure Node does not contain a Denominator Node child";
	protected static final String TOO_MANY_DENOMINATORS = "This ACI Measure Node contains too many Denominator Node children";
	protected static final String NO_CHILDREN = "This ACI Measure Node does not have any child Nodes";
	protected static final String NO_REQUIRED_MEASURE = "The required measure ''{0}'' is not present in the source file. Please add the ACI measure and try again.";

	/**
	 * Constructs a new {@code AciProportionMeasureValidator}.
	 */
	public AciProportionMeasureValidator() {
		initMeasureConfigs();
	}

	/**
	 * Validates all the ACI Proportion Type Measures that may be in the hierarchy of
	 * {@link gov.cms.qpp.conversion.model.Node}.
	 *
	 * Validates the following.
	 * <ul>
	 *     <li>One ACI Proportion Type Measure node exists</li>
	 *     <li>ACI Proportion Type Measure nodes have an ACI section as a parent</li>
	 *     <li>ACI Proportion Type Measure nodes have one and only one numerator node</li>
	 *     <li>ACI Proportion Type Measure nodes have one and only one denominator node</li>
	 *     <li>All the required measures are represented in at least one ACI Proportion Type Measure</li>
	 * </ul>
	 *
	 * @param node The top level node.
	 * @return A list of errors in converting ACI Proportion Type Measure.
	 */
	@Override
	protected List<ValidationError> internalValidate(Node node) {

		Validator thisAnnotation = this.getClass().getAnnotation(Validator.class);

		List<Node> aciProportionNodes = node.findNode(thisAnnotation.templateId());

		//Most likely, this "required" validation can be moved into the
		//QrdaValidator superclass
		if (!validateOneAciProportionExists(thisAnnotation, aciProportionNodes)) {
			return this.getValidationErrors();
		}

		for (Node currentNode : aciProportionNodes) {
			validateNode(currentNode);
		}

		List<MeasureConfig> configs = measureConfigs.getMeasureConfigs();

		for (MeasureConfig config : configs) {
			validateMeasureConfig(config, aciProportionNodes);
		}

		return this.getValidationErrors();
	}

	private boolean validateOneAciProportionExists(final Validator thisAnnotation, final List<Node> aciProportionNodes) {

		if (thisAnnotation.required() && aciProportionNodes.isEmpty()) {
			this.addValidationError(new ValidationError(ACI_PROPORTION_NODE_REQUIRED));
			// if we did not find any measure nodes, just return right now because
			// there's nothing else to verify
			return false;
		}
		return true;
	}

	private void validateMeasureConfig(final MeasureConfig measureConfig, final List<Node> aciProportionNodes) {

		if (measureConfig.isRequired()) {
			boolean foundMeasure = false;
			for (Node aNode : aciProportionNodes) {
				if (aNode.getValue("measureId").equals(measureConfig.getMeasureId())) {
					foundMeasure = true;
					break;
				}
			}

			if (!foundMeasure) {
				this.addValidationError(new ValidationError(MessageFormat.format(NO_REQUIRED_MEASURE, measureConfig.getMeasureId())));
			}
		}
	}

	private void validateNode(final Node node) {

		//the aci measure node should have an aci section node as parent
		//it can have a numerator node and a denominator node as children

		validateParentIsAciSection(node);
		validateChildren(node);
	}

	private void validateChildren(final Node node) {

		List<Node> children = node.getChildNodes();

		if (!children.isEmpty()) {
			int numeratorCount = 0;
			int denominatorCount = 0;

			for (Node child : children) {
				if (NodeType.ACI_DENOMINATOR.equals(child.getType())) {
					denominatorCount++;
				}
				if (NodeType.ACI_NUMERATOR.equals(child.getType())) {
					numeratorCount++;
				}
			}

			validateNumeratorCount(numeratorCount);
			validateDenominatorCount(denominatorCount);
		} else {
			this.addValidationError(new ValidationError(NO_CHILDREN));
		}
	}

	private void validateDenominatorCount(final int denominatorCount) {

		if (denominatorCount == 0) {
			this.addValidationError(
					new ValidationError(NO_DENOMINATOR));
		} else if (denominatorCount > 1) {
			this.addValidationError(
					new ValidationError(TOO_MANY_DENOMINATORS));
		}
	}

	private void validateNumeratorCount(final int numeratorCount) {

		if (numeratorCount == 0) {
			this.addValidationError(
					new ValidationError(NO_NUMERATOR));
		} else if (numeratorCount > 1) {
			this.addValidationError(
					new ValidationError(TOO_MANY_NUMERATORS));
		}
	}

	private void validateParentIsAciSection(final Node node) {

		if (NodeType.ACI_SECTION != node.getParent().getType()) {
			this.addValidationError(
					new ValidationError(NO_PARENT_SECTION));
		}
	}

	private void initMeasureConfigs() {

		ObjectMapper mapper = new ObjectMapper();

		ClassPathResource measuresConfigResource = new ClassPathResource("measures-data-aci-short.json");

		try {
			measureConfigs = mapper.treeToValue(mapper.readTree(measuresConfigResource.getInputStream()),
					MeasureConfigs.class);
		} catch (IOException e) {
			throw new IllegalArgumentException("failure to correctly read measures config json", e);
		}
	}
}
