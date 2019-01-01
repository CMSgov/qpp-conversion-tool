package gov.cms.qpp.conversion.validate;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ValidationResult;

/**
 * The parent class that all validators must inherit from.
 */
public abstract class NodeValidator {

	private static final Logger DEV_LOG = LoggerFactory.getLogger(NodeValidator.class);
	private final List<Detail> errors = new ArrayList<>();
	private final List<Detail> warnings = new ArrayList<>();

	/**
	 * Validates a single {@link gov.cms.qpp.conversion.model.Node} and returns the list
	 * of {@link Detail}s for that node.
	 *
	 * @param node The node to validate.
	 * @return List of errors determined for the node paramter.
	 * @see #performValidation(Node)
	 */
	public final ValidationResult validateSingleNode(Node node) {
		DEV_LOG.debug("Using {} to validate {}", this.getClass().getName(), node);
		performValidation(node);
		return new ValidationResult(errors, warnings);
	}

	/**
	 * Overridden by child classes to validate a specific {@link gov.cms.qpp.conversion.model.Node}.
	 *
	 * <p>
	 * The implementation should validate the {@link gov.cms.qpp.conversion.model.Node} passed in.  If an error is
	 * found, the child class must call {@link #addError(Detail)} for it to be reported.  The
	 * Node argument will have the same ID as the templateId of the
	 * {@link gov.cms.qpp.conversion.model.Validator}.
	 * </p>
	 *
	 * @param node The node to validate.
	 */
	protected abstract void performValidation(Node node);

	/**
	 * Used by child classes to add a {@link Detail}.
	 *
	 * @param newError The error to add to the list.
	 */
	protected final void addError(Detail error) {
		errors.add(error);
	}

	public boolean containsError(Detail detail) {
		return errors.contains(detail);
	}

	protected final Checker checkErrors(Node node) {
		return Checker.check(node, errors);
	}

	protected final Checker forceCheckErrors(Node node) {
		return Checker.forceCheck(node, errors);
	}
}
