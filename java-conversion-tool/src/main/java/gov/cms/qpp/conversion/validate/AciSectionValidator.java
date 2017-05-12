package gov.cms.qpp.conversion.validate;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.validation.MeasureConfig;
import gov.cms.qpp.conversion.model.validation.MeasureConfigs;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

/**
 * Validate the ACI Section.
 */
@Validator(templateId = TemplateId.ACI_SECTION, required = true)
public class AciSectionValidator extends NodeValidator {

	protected static final String ACI_NUMERATOR_DENOMINATOR_NODE_REQUIRED =
		"At least one Aci Numerator Denominator Measure Node is required";
	protected static final String NO_REQUIRED_MEASURE =
		"The required measure ''{0}'' is not present in the source file. "
			+ "Please add the ACI measure and try again.";

	private MeasureConfigs measureConfigs;
	private String measureDataFileName = "measures-data-short.json";

	/**
	 * Validates the ACI Section.
	 * <p>
	 * Validates the following.
	 * <ul>
	 * <li>One ACI Numerator Denominator Type Measure node exists</li>
	 * <li>All the required measures are represented in at least one ACI Numerator Denominator Type Measure</li>
	 * </ul>
	 *
	 * @param node An ACI section node.
	 */
	@Override
	protected void internalValidateSingleNode(final Node node) {
		initMeasureConfigs();

		thoroughlyCheck(node).childMinimum(ACI_NUMERATOR_DENOMINATOR_NODE_REQUIRED, 1, TemplateId.ACI_NUMERATOR_DENOMINATOR);

		validateMeasureConfigs(node);
	}

	/**
	 * Does nothing.
	 *
	 * @param nodes The list of nodes to validate.
	 */
	@Override
	protected void internalValidateSameTemplateIdNodes(final List<Node> nodes) {
		//no cross-node validations
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
	 * Initialize all measure configurations
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

	/**
	 * Validates all required measure configurations exist in the ACI section.
	 *
	 * @param node An ACI section node.
	 */
	private void validateMeasureConfigs(final Node node) {
		for (MeasureConfig config : measureConfigs.getMeasureConfigs()) {
			if (config.isRequired()) {
				String expectedMeasureId = config.getMeasureId();
				thoroughlyCheck(node).hasMeasures(MessageFormat.format(NO_REQUIRED_MEASURE, expectedMeasureId),
					expectedMeasureId);
			}
		}
	}
}
