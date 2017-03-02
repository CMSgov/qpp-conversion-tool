package gov.cms.qpp.conversion.validate;

import java.io.IOException;
import java.util.List;

import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.ValidationError;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.validation.MeasureConfig;
import gov.cms.qpp.conversion.model.validation.MeasureConfigs;

@Validator(templateId = "2.16.840.1.113883.10.20.27.3.28", required = true)
public class AciProportionMeasureValidator extends QrdaValidator {

	private MeasureConfigs measureConfigs;

	public AciProportionMeasureValidator() {
		initMeasureConfigs();
	}

	@Override
	protected List<ValidationError> internalValidate(Node node) {

		Validator thisAnnotation = this.getClass().getAnnotation(Validator.class);

		List<Node> nodes = node.findNode(thisAnnotation.templateId());

		if (thisAnnotation.required()) {
			if (null == nodes || nodes.size() == 0) {
				this.addValidationError(new ValidationError("At least one Aci Proportion Measure Node is required"));
			}
		}

		List<MeasureConfig> configs = measureConfigs.getMeasureConfigs();

		boolean foundMeasure = false;

		for (MeasureConfig config : configs) {
			if (config.isRequired()) {
				foundMeasure = false;
				for (Node aNode : nodes) {
					if (aNode.getValue("measureId").equals(config.getMeasureId())) {
						foundMeasure = true;
						break;
					}
				}

				if (!foundMeasure) {
					this.addValidationError(new ValidationError("The required measure '" + config.getMeasureId()
							+ "' is not present in the source file. Please add the ACI measure and try again."));
				}
			}
		}

		return this.getValidationErrors();
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
