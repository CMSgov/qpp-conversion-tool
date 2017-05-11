package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.ConversionEntry;
import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Registry;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.ValidationError;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.segmentation.QrdaScope;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The engine that executes the VALIDATORS on the entire hierarchy of {@link gov.cms.qpp.conversion.model.Node}s.
 */
public class QrdaValidator {

	private static final Registry<String, NodeValidator> VALIDATORS = new Registry<>(Validator.class);

	private final Map<String, List<Node>> nodesForTemplateIds = new HashMap<>();
	private final List<ValidationError> validationErrors = new ArrayList<>();
	private Collection<TemplateId> scope;

	public QrdaValidator() {
		Collection<TemplateId> theScope = QrdaScope.getTemplates(ConversionEntry.getScope());
		if (!theScope.isEmpty()) {
			this.scope = theScope;
		}
	}

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
		NodeValidator validatorForNode = getValidator(templateId);

		if (null == validatorForNode || !isValidationRequired(validatorForNode)) {
			return;
		}

		addNodeToTemplateMap(node);
		List<ValidationError> nodeErrors = validatorForNode.validateSingleNode(node);
		validationErrors.addAll(nodeErrors);
	}

	/**
	 * Retrieve a permitted {@link Validator}. {@link #scope} is used to determine which VALIDATORS are allowable.
	 *
	 * @param templateId string representation of a would be validator's template id
	 * @return validator that corresponds to the given template id
	 */
	private NodeValidator getValidator(String templateId) {
		NodeValidator nodeValidator = VALIDATORS.get(templateId);
		if (nodeValidator != null) {
			Validator validator = AnnotationUtils.findAnnotation(nodeValidator.getClass(), Validator.class);
			return (scope != null && !scope.contains(validator.templateId())) ? null : nodeValidator;
		}

		return null;
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
		parentNode.getChildNodes().stream()
				.filter(n -> !n.isValidated())
				.forEach(this::validateTree);
	}

	/**
	 * Iterates over all the VALIDATORS to have them validate similar nodes.
	 */
	private void validateTemplateIds() {
		Converter.CLIENT_LOG.info("Validating all nodes by templateId");

		for (String validatorKey : VALIDATORS.getKeys()) {
			validateSingleTemplateId(getValidator(validatorKey));
		}
	}

	/**
	 * Validates all the disparate nodes of the same template ID given the
	 * {@link gov.cms.qpp.conversion.validate.NodeValidator}.
	 *
	 * @param validator The validator that should be called.
	 */
	private void validateSingleTemplateId(final NodeValidator validator) {
		if (null == validator || !isValidationRequired(validator)) {
			return;
		}

		final String templateId = getTemplateId(validator);

		Converter.CLIENT_LOG.debug("Validating nodes associated with templateId {}", templateId);

		List<Node> nodesForTemplateId = nodesForTemplateIds.getOrDefault(templateId, Collections.emptyList());

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
