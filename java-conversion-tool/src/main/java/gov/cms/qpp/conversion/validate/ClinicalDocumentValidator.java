package gov.cms.qpp.conversion.validate;

import java.util.List;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.ValidationError;
import gov.cms.qpp.conversion.model.Validator;

@Validator(templateId = "2.16.840.1.113883.10.20.27.1.2", required = true)
public class ClinicalDocumentValidator extends QrdaValidator {

	public ClinicalDocumentValidator() {

	}

	@Override
	protected List<ValidationError> internalValidate(Node node) {

		Validator thisAnnotation = this.getClass().getAnnotation(Validator.class);

		List<Node> nodes = node.findNode(thisAnnotation.templateId());

		if (thisAnnotation.required()) {
			if (null == nodes || nodes.size() == 0) {
				this.addValidationError(new ValidationError("Clinical Document Node is required"));
			}
		}

		return this.getValidationErrors();
	}

}
