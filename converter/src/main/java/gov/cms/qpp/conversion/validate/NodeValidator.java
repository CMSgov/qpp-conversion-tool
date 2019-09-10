package gov.cms.qpp.conversion.validate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.cms.qpp.conversion.Context;
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
	protected final Context context;

	public NodeValidator() {
		this(null);
	}

	public NodeValidator(Context context) {
		this.context = context;
	}

	public List<Detail> viewErrors() {
		return Collections.unmodifiableList(errors);
	}

	public List<Detail> viewWarnings() {
		return Collections.unmodifiableList(warnings);
	}

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
	 * @param error The error to add to the list.
	 */
	public final void addError(Detail error) {
		errors.add(error);
	}

	/**
	 * Used by child classes to add a {@link Detail}.
	 *
	 * @param warning The warning to add to the list.
	 */
	public final void addWarning(Detail warning) {
		warnings.add(warning);
	}

	/**
	 * Used to determine if a error detail has been added.
	 * @param detail the error to check exists
	 * @return True if detail is contained in the errors collection
	 */
	public boolean containsError(Detail detail) {
		return errors.contains(detail);
	}

	/**
	 * Returns a checker for implementations of performValidation(Node)
	 * @param node the node for the checker to visit
	 * @return an instance of Checker for the node and the continuing collection of errors.
	 */
	protected final Checker checkErrors(Node node) {
		return Checker.check(node, errors);
	}

	/**
	 * Identical use as checkErrors except that the Checker instance is in force mode.
	 * @param node the node for the checker to visit
	 * @return an instance of Checker for the node and the continuing collection of errors.
	 */
	protected final Checker forceCheckErrors(Node node) {
		return Checker.forceCheck(node, errors);
	}

	/**
	 * Returns a checker for implementations of performValidation(Node)
	 * @param node the node for the checker to visit
	 * @return an instance of Checker for the node and the continuing collection of warnings.
	 */
	protected final Checker checkWarnings(Node node) {
		return Checker.check(node, warnings);
	}
}
