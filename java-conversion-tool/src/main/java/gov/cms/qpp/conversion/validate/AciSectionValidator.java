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

@Validator(templateId = TemplateId.ACI_SECTION, required = true)
public class AciSectionValidator extends NodeValidator {

	protected static final String ACI_NUMERATOR_DENOMINATOR_NODE_REQUIRED =
		"At least one Aci Numerator Denominator Measure Node is required";
	protected static final String NO_REQUIRED_MEASURE =
		"The required measure ''{0}'' is not present in the source file. "
			+ "Please add the ACI measure and try again.";
	private static final String MEASURE_DATA_FILE_NAME = "measures-data-aci-short.json";

	private MeasureConfigs measureConfigs;

	@Override
	protected void internalValidateSingleNode(final Node node) {
		initMeasureConfigs();

		thoroughlyCheck(node).childMinimum(ACI_NUMERATOR_DENOMINATOR_NODE_REQUIRED, 1, TemplateId.ACI_NUMERATOR_DENOMINATOR);

		validateMeasureConfigs(node);
	}

	@Override
	protected void internalValidateSameTemplateIdNodes(final List<Node> nodes) {
		//no cross-node validations
	}

	/**
	 * Initialize all measure configurations
	 */
	private void initMeasureConfigs() {

		ObjectMapper mapper = new ObjectMapper();

		ClassPathResource measuresConfigResource = new ClassPathResource(MEASURE_DATA_FILE_NAME);

		try {
			measureConfigs = mapper.treeToValue(mapper.readTree(measuresConfigResource.getInputStream()),
				MeasureConfigs.class);
		} catch (IOException e) {
			throw new IllegalArgumentException("failure to correctly read measures config json", e);
		}
	}

	private void validateMeasureConfigs(final Node node) {
		for (MeasureConfig config : measureConfigs.getMeasureConfigs()) {
			if (config.isRequired()) {
				String expectedMeasureId = config.getMeasureId();
				thoroughlyCheck(node).hasMeasures(MessageFormat.format(NO_REQUIRED_MEASURE, expectedMeasureId), expectedMeasureId);
			}
		}
	}
}
