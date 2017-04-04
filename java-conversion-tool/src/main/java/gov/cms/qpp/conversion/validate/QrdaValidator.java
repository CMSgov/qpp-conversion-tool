package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Registry;
import gov.cms.qpp.conversion.model.ValidationError;
import gov.cms.qpp.conversion.model.Validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QrdaValidator {

	private static final Registry<String, NodeValidator> VALIDATORS = new Registry<>(Validator.class);

	private final Map<String, List<Node>> nodesForTemplateIds = new HashMap<>();
	private final List<ValidationError> validationErrors = new ArrayList<>();

	public List<ValidationError> validate(Node rootNode) {

		//validate each node while traversing the tree
		validateTree(rootNode);

		//validate lists of nodes grouped by templateId
		validateTemplateIds();

		return validationErrors;
	}

	private void validateTree(final Node node) {

		validateSingleNode(node);

		validateChildren(node);
	}

	private void validateSingleNode(final Node node) {

		final String templateId = node.getId();
		NodeValidator validatorForNode = VALIDATORS.get(templateId);

		if (null == validatorForNode) {
			return;
		}

		boolean isRequired = validatorForNode.getClass().getAnnotation(Validator.class).required();
		if(!isRequired) {
			return;
		}

		addNodeToTemplateMap(node);
		List<ValidationError> nodeErrors = validatorForNode.validateSingleNode(node);
		validationErrors.addAll(nodeErrors);
	}

	private void addNodeToTemplateMap(final Node node) {

		nodesForTemplateIds.putIfAbsent(node.getId(), new ArrayList<>());

		nodesForTemplateIds.get(node.getId()).add(node);
	}

	private void validateChildren(final Node parentNode) {

		for (Node childNode: parentNode.getChildNodes()) {

			validateTree(childNode);
		}
	}

	private void validateTemplateIds() {

		for (String validatorKey : VALIDATORS.getKeys()) {
			validateSingleTemplateId(VALIDATORS.get(validatorKey));
		}
	}

	private void validateSingleTemplateId(final NodeValidator validator) {

		Validator validatorAnnotation = validator.getClass().getAnnotation(Validator.class);

		boolean isRequired = validatorAnnotation.required();
		if(!isRequired) {
			return;
		}

		final String templateId = validatorAnnotation.templateId();
		List<Node> nodesForTemplateId = nodesForTemplateIds.getOrDefault(templateId, Arrays.asList());

		List<ValidationError> nodesErrors = validator.validateSameTemplateIdNodes(nodesForTemplateId);
		validationErrors.addAll(nodesErrors);
	}
}
