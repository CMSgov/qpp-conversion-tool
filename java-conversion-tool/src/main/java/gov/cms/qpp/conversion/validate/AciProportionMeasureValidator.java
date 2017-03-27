package gov.cms.qpp.conversion.validate;

import java.io.IOException;
import java.util.List;

import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.NodeType;
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

		// Most likely, this "required" validation can be moved into the
		// QrdaValidator superclass
		if (thisAnnotation.required()) {
			if (nodes.isEmpty()) {
				this.addValidationError(new ValidationError("At least one Aci Proportion Measure Node is required"));
			}
		}

		// if we did not find any measure nodes, just return right now because
		// there's nothing else to verify
		if (!this.getValidationErrors().isEmpty()) {
			return this.getValidationErrors();
		}

		// the aci measure node should have an aci section node as parent
		// it can have a numerator node and a denominator node as children

		for (Node aNode : nodes) {
			if (NodeType.ACI_SECTION != aNode.getParent().getType()) {
				this.addValidationError(
						new ValidationError("This ACI Measure Node should have an ACI Section Node as a parent"));
			}

			List<Node> children = aNode.getChildNodes();

			if (!children.isEmpty()) {
				int numeratorCount = 0;
				int denominatorCount = 0;

				for (Node child : children) {
					if (NodeType.ACI_DENOMINATOR == child.getType()) {
						denominatorCount++;
					}
					if (NodeType.ACI_NUMERATOR == child.getType()) {
						numeratorCount++;
					}
				}

				if (numeratorCount == 0) {
					this.addValidationError(
							new ValidationError("This ACI Measure Node does not contain a Numerator Node child"));
				}

				if (numeratorCount > 1) {
					this.addValidationError(
							new ValidationError("This ACI Measure Node contains too many Numerator Node children"));
				}

				if (denominatorCount == 0) {
					this.addValidationError(
							new ValidationError("This ACI Measure Node does not contain a Denominator Node child"));
				}

				if (denominatorCount > 1) {
					this.addValidationError(
							new ValidationError("This ACI Measure Node contains too many Denominator Node children"));
				}

			} else {
				this.addValidationError(new ValidationError("This ACI Measure Node does not have any child Nodes"));
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
