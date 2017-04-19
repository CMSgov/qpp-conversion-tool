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

	protected static final String ACI_NUMERATOR_DENOMINATOR_NODE_REQUIRED =
			"At least one Aci Numerator Denominator Measure Node is required";
	protected static final String NO_PARENT_SECTION =
			"This ACI Numerator Denominator Node should have an ACI Section Node as a parent";
	protected static final String NO_MEASURE_NAME =
			"This ACI Numerator Denominator Node does not contain a measure name ID";
	protected static final String NO_NUMERATOR =
			"This ACI Numerator Denominator Node does not contain a Numerator Node child";
	protected static final String TOO_MANY_NUMERATORS =
			"This ACI Numerator Denominator Node contains too many Numerator Node children";
	protected static final String NO_DENOMINATOR =
			"This ACI Numerator Denominator Node does not contain a Denominator Node child";
	protected static final String TOO_MANY_DENOMINATORS =
			"This ACI Numerator Denominator Node contains too many Denominator Node children";
	protected static final String NO_CHILDREN =
			"This ACI Numerator Denominator Node does not have any child Nodes";
	protected static final String NO_REQUIRED_MEASURE =
			"The required measure ''{0}'' is not present in the source file. "
			+ "Please add the ACI measure and try again.";

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
		Checker nodeChecker = check(node).hasParent(NO_PARENT_SECTION, TemplateId.ACI_SECTION);
		//the aci numerator denominator measure node must have a numerator node and a denominator node as children
		validateChildren(nodeChecker);
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
	protected void internalValidateSameTemplateIdNodes(List<Node> nodes) {

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

	/**
	 * Validates all of the given nodes children.
	 *
	 * @param nodeChecker for a node that represents the ACI Numerator Denominator Measure Section
	 */
	private void validateChildren(Checker nodeChecker) {
		nodeChecker.value(NO_MEASURE_NAME, "measureId")
				.hasChildren(NO_CHILDREN)
				.childMinimum(NO_DENOMINATOR, 1, TemplateId.ACI_DENOMINATOR)
				.childMinimum(NO_NUMERATOR, 1, TemplateId.ACI_NUMERATOR)
				.childMaximum(TOO_MANY_DENOMINATORS, 1, TemplateId.ACI_DENOMINATOR)
				.childMaximum(TOO_MANY_NUMERATORS, 1, TemplateId.ACI_NUMERATOR);
	}

	/**
	 * Validates that an Aci Numerator Denominator Section Exists
	 *
	 * @param aciNumeratorDenominatorNodes List of nodes to validate
	 */
	private void validateOneAciNumeratorDenominatorExists(List<Node> aciNumeratorDenominatorNodes) {

		if (aciNumeratorDenominatorNodes.isEmpty()) {
			this.addValidationError(new ValidationError(ACI_NUMERATOR_DENOMINATOR_NODE_REQUIRED));
		}
	}

	/**
	 * Validates all required measure configurations
	 *
	 * @param measureConfig Object that holds the measure configuration
	 * @param aciNumeratorDenominatorNodes List of nodes to validate
	 */
	private void validateMeasureConfig(MeasureConfig measureConfig, List<Node> aciNumeratorDenominatorNodes) {

		if (measureConfig.isRequired()) {
			for (Node aciNode : aciNumeratorDenominatorNodes) {
				if (Objects.equals(aciNode.getValue("measureId"), measureConfig.getMeasureId())) {
					return;
				}
			}

			String message = MessageFormat.format(NO_REQUIRED_MEASURE, measureConfig.getMeasureId());
			ValidationError error = new ValidationError(message);
			this.addValidationError(error);
		}


	}

	/**
	 * Initialize all measure configurationscle
	 */
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
