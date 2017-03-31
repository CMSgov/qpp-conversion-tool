package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Registry;
import gov.cms.qpp.conversion.model.ValidationError;
import gov.cms.qpp.conversion.model.Validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class QrdaValidator {

	private final Registry<String, NodeValidator> validators = new Registry<>(Validator.class);

	private final Map<String, List<Node>> nodesForTemplateIds = new HashMap<>();
	private final List<ValidationError> validationErrors = new ArrayList<>();

	public List<ValidationError> validate(Node rootNode) {

		//validate each node while traversing the tree
		validateTree(rootNode);

		validateNodesByTemplateId();

		collectValidationErrors();

		// iterate through all of the known validators
		// each validator understands if the node it's for is required
		// it also can do other validations
		/*for (String key : validators.getKeys()) {

			NodeValidator aValidator = validators.get(key);
			aValidator.validateNode(rootNode);
			validationErrors.addAll(aValidator.getValidationErrors());
		}*/

		// do we need to do anything with any Nodes that weren't validated?
		// if they are extraneous Nodes, is that an issue?
		// the encoder will only encode the same Nodes we're validating

		return validationErrors;
	}

	private void validateTree(final Node node) {

		validateSingleNode(node);

		validateChildren(node);
	}

	private void validateSingleNode(final Node node) {

		final String templateId = node.getId();
		NodeValidator validatorForNode = validators.get(templateId);

		if (null != validatorForNode) {
			addNodeToTemplateMap(node);
			validatorForNode.validateNode(node);
		}
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

	private void validateNodesByTemplateId() {

		final Set<Map.Entry<String, List<Node>>> entriesPerTemplate = nodesForTemplateIds.entrySet();
		for (Map.Entry<String, List<Node>> entryForTemplate : entriesPerTemplate) {

			validateSimilarTemplateIdNodes(entryForTemplate);
		}
	}

	private void validateSimilarTemplateIdNodes(final Map.Entry<String, List<Node>> entryForTemplate) {

		final String templateId = entryForTemplate.getKey();
		final NodeValidator validatorForNodes = validators.get(templateId);

		if (null != validatorForNodes) {
			validatorForNodes.validateNodes(entryForTemplate.getValue());
		}
	}

	private void collectValidationErrors() {

		for (String key : validators.getKeys()) {

			NodeValidator validator = validators.get(key);
			validationErrors.addAll(validator.getValidationErrors());
		}
	}
}
