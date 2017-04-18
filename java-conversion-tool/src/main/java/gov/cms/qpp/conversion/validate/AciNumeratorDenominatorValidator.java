package gov.cms.qpp.conversion.validate;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.ValidationError;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.validation.MeasureConfig;
import gov.cms.qpp.conversion.model.validation.MeasureConfigs;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;

/**
 * Validate all ACI Numerator Denominator Type Measures.
 */
@Validator(templateId = TemplateId.ACI_NUMERATOR_DENOMINATOR, required = true)
public class AciNumeratorDenominatorValidator extends NodeValidator {

	private MeasureConfigs measureConfigs;
	private String measureDataFileName = "measures-data-aci-short.json";

	protected static final String ACI_NUMERATOR_DENOMINATOR_NODE_REQUIRED = "At least one Aci Numerator Denominator Measure Node is required";
	protected static final String NO_PARENT_SECTION = "This ACI Numerator Denominator Node should have an ACI Section Node as a parent";
	protected static final String NO_MEASURE_NAME = "This ACI Numerator Denominator Node does not contain a measure name ID";
	protected static final String NO_NUMERATOR = "This ACI Numerator Denominator Node does not contain a Numerator Node child";
	protected static final String TOO_MANY_NUMERATORS = "This ACI Numerator Denominator Node contains too many Numerator Node children";
	protected static final String NO_DENOMINATOR = "This ACI Numerator Denominator Node does not contain a Denominator Node child";
	protected static final String TOO_MANY_DENOMINATORS = "This ACI Numerator Denominator Node contains too many Denominator Node children";
	protected static final String NO_CHILDREN = "This ACI Numerator Denominator Node does not have any child Nodes";
	protected static final String NO_REQUIRED_MEASURE = "The required measure ''{0}'' is not present in the source file. Please add the ACI measure and try again.";

	/**
	 * Constructs a new {@code AciNumeratorDenominatorValidator}.
	 */
	public AciNumeratorDenominatorValidator() {
		initMeasureConfigs();
	}

	/**
	 * Validates a single ACI Numerator Denominator Type Measure.
	 *
	 * Validates the following.
	 * <ul>
	 *     <li>ACI Numerator Denominator Type Measure nodes have an ACI section as a parent.</li>
	 *     <li>ACI Numerator Denominator Type Measure nodes have one and only one numerator node.</li>
	 *     <li>ACI Numerator Denominator Type Measure nodes have one and only one denominator node.</li>
	 * </ul>
	 *
	 * @param node The node that represents an ACI Numerator Denominator Type Measure.
	 */
	@Override
	protected void internalValidateSingleNode(Node node) {

		//the aci numerator denominator measure node must have an aci section node as parent
		validateParentIsAciSection(node);
		//the aci numerator denominator measure node must have a numerator node and a denominator node as children
		validateChildren(node);
	}

	/**
	 * Validates all the ACI Numerator Denominator Type Measures.
	 *
	 * Validates the following.
	 * <ul>
	 *     <li>One ACI Numerator Denominator Type Measure node exists</li>
	 *     <li>All the required measures are represented in at least one ACI Numerator Denominator Type Measure</li>
	 * </ul>
	 *
	 * @param nodes A list of all the ACI Numerator Denominator Type Measure nodes.
	 */
	@Override
	protected void internalValidateSameTemplateIdNodes(final List<Node> nodes) {

		validateOneAciNumeratorDenominatorExists(nodes);

		List<MeasureConfig> configs = measureConfigs.getMeasureConfigs();

		for (MeasureConfig config : configs) {
			validateMeasureConfig(config, nodes);
		}
	}

	/**
	 * Changes the measure data file used to validate the measures.
	 *
	 * @param fileName The file name found in the classpath to parse.
	 */
	public void setMeasureDataFile(String fileName) {
		measureDataFileName = fileName;
		initMeasureConfigs();
	}

	private void validateParentIsAciSection(final Node node) {

		if (TemplateId.ACI_SECTION != node.getParent().getType()) {
			this.addValidationError(
				new ValidationError(NO_PARENT_SECTION));
		}
	}

	private void validateChildren(final Node node) {

		List<Node> children = node.getChildNodes();

		if (null == node.getValue("measureId")) {
			this.addValidationError(
					new ValidationError(NO_MEASURE_NAME));
		}

		if (!children.isEmpty()) {
			int numeratorCount = 0;
			int denominatorCount = 0;

			for (Node child : children) {
				if (TemplateId.ACI_DENOMINATOR == child.getType()) {
					denominatorCount++;
				} else if (TemplateId.ACI_NUMERATOR == child.getType()) {
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

	private void validateOneAciNumeratorDenominatorExists(final List<Node> aciNumeratorDenominatorNodes) {

		if (aciNumeratorDenominatorNodes.isEmpty()) {
			this.addValidationError(new ValidationError(ACI_NUMERATOR_DENOMINATOR_NODE_REQUIRED));
		}
	}

	private void validateMeasureConfig(final MeasureConfig measureConfig, final List<Node> aciNumeratorDenominatorNodes) {

		if (measureConfig.isRequired()) {
			for (Node aNode : aciNumeratorDenominatorNodes) {
				if (Objects.equals(aNode.getValue("measureId"), measureConfig.getMeasureId())) {
					return;
				}
			}

			this.addValidationError(new ValidationError(MessageFormat.format(NO_REQUIRED_MEASURE, measureConfig.getMeasureId())));
		}


	}

	private void initMeasureConfigs() {

		ObjectMapper mapper = new ObjectMapper();

		ClassPathResource measuresConfigResource = new ClassPathResource(measureDataFileName);

		try {
			measureConfigs = mapper.treeToValue(mapper.readTree(measuresConfigResource.getInputStream()),
			                                    MeasureConfigs.class);
		} catch (IOException e) {
			throw new IllegalArgumentException("failure to correctly read measures config json", e);
		}
	}
}
