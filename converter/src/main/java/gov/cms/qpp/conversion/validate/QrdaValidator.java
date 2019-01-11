package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Registry;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ValidationResult;
import gov.cms.qpp.conversion.segmentation.QrdaScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

/**
 * The engine that executes the VALIDATORS on the entire hierarchy of {@link gov.cms.qpp.conversion.model.Node}s.
 */
public class QrdaValidator {
	private static final Logger DEV_LOG = LoggerFactory.getLogger(QrdaValidator.class);

	private final List<Detail> errors = new ArrayList<>();
	private final List<Detail> warnings = new ArrayList<>();
	private final Set<TemplateId> scope;
	private final Registry<NodeValidator> validators;

	/**
	 * Constructs and instance from the Context which contains all
	 * necessary properties for validation.
	 * @param context
	 */
	public QrdaValidator(Context context) {
		this.validators = context.getRegistry(Validator.class);
		this.scope = context.hasScope() ? QrdaScope.getTemplates(context.getScope()) : null;
	}

	/**
	 * Validate all the {@link gov.cms.qpp.conversion.model.Node}s from the passed in Node and on down.
	 *
	 * @param rootNode The root node that all other nodes descend from.
	 * @return The list of validation errors for the entire tree of nodes.
	 */
	public ValidationResult validate(Node rootNode) {
		DEV_LOG.info("Validating all nodes in the tree");

		//validate each node while traversing the tree
		validateTree(rootNode);

		return new ValidationResult(errors, warnings);
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
		getValidators(node.getType())
			.filter(this::isValidationRequired)
			.forEach(validatorForNode -> {
				ValidationResult problems = validatorForNode.validateSingleNode(node);
				errors.addAll(problems.getErrors());
				warnings.addAll(problems.getWarnings());
			});
	}

	/**
	 * Retrieve permitted {@link Validator}s. {@link #scope} is used to determine which VALIDATORS are allowable.
	 *
	 * @param templateId string representation of a would be validator's template id
	 * @return validators that correspond to the given template id
	 */
	private Stream<NodeValidator> getValidators(TemplateId templateId) {
		Set<NodeValidator> nodeValidators = validators.inclusiveGet(templateId);
		return nodeValidators.stream()
				.filter(Objects::nonNull)
				.filter(nodeValidator -> {
					Validator validator = nodeValidator.getClass().getAnnotation(Validator.class);
					TemplateId template = validator == null ? TemplateId.DEFAULT : validator.value();
					return scope == null || scope.contains(template);
				})
				.distinct();
	}

	/**
	 * Determines whether the validation the {@link gov.cms.qpp.conversion.validate.NodeValidator} does is required.
	 *
	 * @param validatorForNode The NodeValidator
	 * @return Whether the validation the NodeValidator does is required.
	 */
	private boolean isValidationRequired(NodeValidator validatorForNode) {
		Validator validator = getAnnotation(validatorForNode);
		return validator != null && !validator.optional();
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
				.filter(Node::isNotValidated)
				.forEach(this::validateTree);
	}
}
