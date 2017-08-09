package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Registry;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.segmentation.QrdaScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * The engine that executes the VALIDATORS on the entire hierarchy of {@link gov.cms.qpp.conversion.model.Node}s.
 */
public class QrdaValidator {
	private static final Logger DEV_LOG = LoggerFactory.getLogger(QrdaValidator.class);
	private static final Registry<NodeValidator> VALIDATORS = new Registry<>(Validator.class);

	private final List<Detail> details = new ArrayList<>();
	private Set<TemplateId> scope;

	public QrdaValidator() {
		Set<TemplateId> theScope = QrdaScope.getTemplates(Converter.getScope());
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
	public List<Detail> validate(Node rootNode) {
		DEV_LOG.info("Validating all nodes in the tree");

		//validate each node while traversing the tree
		validateTree(rootNode);

		return details;
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
		NodeValidator validatorForNode = getValidator(node.getType());

		if (null == validatorForNode || !isValidationRequired(validatorForNode)) {
			return;
		}

		Set<Detail> nodeErrors = validatorForNode.validateSingleNode(node);
		details.addAll(nodeErrors);
	}

	/**
	 * Retrieve a permitted {@link Validator}. {@link #scope} is used to determine which VALIDATORS are allowable.
	 *
	 * @param templateId string representation of a would be validator's template id
	 * @return validator that corresponds to the given template id
	 */
	private NodeValidator getValidator(TemplateId templateId) {
		NodeValidator nodeValidator = VALIDATORS.get(templateId);
		if (nodeValidator != null) {
			Validator validator = nodeValidator.getClass().getAnnotation(Validator.class);
			return (scope != null && !scope.contains(validator.value())) ? null : nodeValidator;
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
	 * Validates all the children of the passed in {@link gov.cms.qpp.conversion.model.Node}.
	 *
	 * @param parentNode The children of this node are validated.
	 */
	private void validateChildren(final Node parentNode) {
		parentNode.getChildNodes().stream()
				.filter(n -> !n.isValidated())
				.forEach(this::validateTree);
	}
}
