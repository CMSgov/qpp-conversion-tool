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
			if (null == nodes || nodes.isEmpty()) {
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

		if (null != nodes && !nodes.isEmpty()) {
			for (Node aNode : nodes) {
				if (NodeType.ACI_SECTION != aNode.getParent().getType()) {
					this.addValidationError(
							new ValidationError("This ACI Measure Node should have an ACI Section Node as a parent"));
				}

				List<Node> children = aNode.getChildNodes();

				if (null != children && children.size() == 2) {
					boolean hasNumerator = false;
					boolean hasDenominator = false;

					if (NodeType.ACI_DENOMINATOR == children.get(0).getType()
							|| NodeType.ACI_DENOMINATOR == children.get(1).getType()) {
						hasDenominator = true;
					}

					if (NodeType.ACI_NUMERATOR == children.get(0).getType()
							|| NodeType.ACI_NUMERATOR == children.get(1).getType()) {
						hasNumerator = true;
					}

					if (!hasNumerator) {
						this.addValidationError(
								new ValidationError("This ACI Measure Node does not contain a Numerator Node child"));
					}

					if (!hasDenominator) {
						this.addValidationError(
								new ValidationError("This ACI Measure Node does not contain a Denominator Node child"));
					}

				} else {
					this.addValidationError(new ValidationError(
							"This ACI Measure Node does not have a Numerator and Denominator Node as children"));
				}
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
