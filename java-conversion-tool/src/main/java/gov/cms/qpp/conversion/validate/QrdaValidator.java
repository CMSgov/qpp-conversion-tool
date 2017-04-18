package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Registry;
import gov.cms.qpp.conversion.model.ValidationError;
import gov.cms.qpp.conversion.model.Validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The engine that executes the validators on the entire hierarchy of {@link gov.cms.qpp.conversion.model.Node}s.
 */
public class QrdaValidator {

	private static final Registry<String, NodeValidator> VALIDATORS = new Registry<>(Validator.class);

	private final Map<String, List<Node>> nodesForTemplateIds = new HashMap<>();
	private final List<ValidationError> validationErrors = new ArrayList<>();

	/**
	 * Validate all the {@link gov.cms.qpp.conversion.model.Node}s from the passed in Node and on down.
	 *
	 * @param rootNode The root node that all other nodes descend from.
	 * @return The list of validation errors for the entire tree of nodes.
	 */
	public List<ValidationError> validate(Node rootNode) {
		Converter.CLIENT_LOG.info("Validating all nodes in the tree");

		//validate each node while traversing the tree
		validateTree(rootNode);

		//validate lists of nodes grouped by templateId
		validateTemplateIds();

		return validationErrors;
	}

	/**
	 * Validates the {@link gov.cms.qpp.conversion.model.Node} and all of its children.
	 *
	 * @param node The root node to start validating from.
	 */
	private void validateTree(final Node node) {
		validateSingleNode(node);

		validateChildren(node);
	}

	/**
	 * Validates a single {@link gov.cms.qpp.conversion.model.Node} based on its ID.
	 *
	 * @param node The node to validate.
	 */
	private void validateSingleNode(final Node node) {
		final String templateId = node.getId();
		NodeValidator validatorForNode = VALIDATORS.get(templateId);

		if (null == validatorForNode) {
			return;
		}

		boolean isRequired = isValidationRequired(validatorForNode);
		if (!isRequired) {
			return;
		}

		addNodeToTemplateMap(node);
		List<ValidationError> nodeErrors = validatorForNode.validateSingleNode(node);
		validationErrors.addAll(nodeErrors);
	}

	/**
	 * Determines whether the validation the {@link gov.cms.qpp.conversion.validate.NodeValidator} does is required.
	 *
	 * @param validatorForNode The NodeValidator
	 * @return Whether the validation the NodeValidator does is required.
	 */
	private boolean isValidationRequired(final NodeValidator validatorForNode) {
		return getAnnotation(validatorForNode).required();
	}

	/**
	 * Returns the {@link gov.cms.qpp.conversion.model.Validator} that is used on the
	 * {@link gov.cms.qpp.conversion.validate.NodeValidator}.
	 *
	 * @param validator The NodeValidator that has the @Validator annotation
	 * @return The @Validator annotation
	 */
	private Validator getAnnotation(final NodeValidator validator) {
		return validator.getClass().getAnnotation(Validator.class);
	}

	/**
	 * Adds the node to a map of template IDs to {@link gov.cms.qpp.conversion.model.Node}s that are of that
	 * template ID.
	 *
	 * @param node The node to add to the map.
	 */
	private void addNodeToTemplateMap(final Node node) {
		nodesForTemplateIds.putIfAbsent(node.getId(), new ArrayList<>());

		nodesForTemplateIds.get(node.getId()).add(node);
	}

	/**
	 * Validates all the children of the passed in {@link gov.cms.qpp.conversion.model.Node}.
	 *
	 * @param parentNode The children of this node are validated.
	 */
	private void validateChildren(final Node parentNode) {
		for (Node childNode: parentNode.getChildNodes()) {

			validateTree(childNode);
		}
	}

	/**
	 * Iterates over all the validators to have them validate similar nodes.
	 */
	private void validateTemplateIds() {
		Converter.CLIENT_LOG.info("Validating all nodes by templateId");

		for (String validatorKey : VALIDATORS.getKeys()) {
			validateSingleTemplateId(VALIDATORS.get(validatorKey));
		}
	}

	/**
	 * Validates all the disparate nodes of the same template ID given the
	 * {@link gov.cms.qpp.conversion.validate.NodeValidator}.
	 *
	 * @param validator The validator that should be called.
	 */
	private void validateSingleTemplateId(final NodeValidator validator) {
		boolean isRequired = isValidationRequired(validator);
		if (!isRequired) {
			return;
		}

		final String templateId = getTemplateId(validator);

		Converter.CLIENT_LOG.debug("Validating nodes associated with templateId {}", templateId);

		List<Node> nodesForTemplateId = nodesForTemplateIds.getOrDefault(templateId, Arrays.asList());

		List<ValidationError> nodesErrors = validator.validateSameTemplateIdNodes(nodesForTemplateId);
		validationErrors.addAll(nodesErrors);
	}

	/**
	 * Gets the template ID that the {@link gov.cms.qpp.conversion.validate.NodeValidator} validates.
	 *
	 * @param validatorForNode The NodeValidator that has the @Validator annotation
	 * @return The templateId that the NodeValidator will validate
	 */
	private String getTemplateId(final NodeValidator validatorForNode) {
		return getAnnotation(validatorForNode).templateId().getTemplateId();
	}
}
