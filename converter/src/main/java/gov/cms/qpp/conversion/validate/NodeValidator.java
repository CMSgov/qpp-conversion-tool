package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.error.Detail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * The parent class that all validators must inherit from.
 */
public abstract class NodeValidator {

	private static final Logger DEV_LOG = LoggerFactory.getLogger(NodeValidator.class);
	private Set<Detail> details = new LinkedHashSet<>();

	/**
	 * Validates a single {@link gov.cms.qpp.conversion.model.Node} and returns the list
	 * of {@link Detail}s for that node.
	 *
	 * @param node The node to validate.
	 * @return List of errors determined for the node paramter.
	 * @see #internalValidateSingleNode(Node)
	 */
	public Set<Detail> validateSingleNode(final Node node) {
		DEV_LOG.debug("Using {} to validate {}", this.getClass().getName(), node);
		internalValidateSingleNode(node);
		return getDetails();
	}

	/**
	 * Used by child classes to get the current list of validation errors they have added.
	 *
	 * @return The current list of validation errors.
	 */
	protected Set<Detail> getDetails() {
		return details;
	}

	/**
	 * Used by child classes to add a {@link Detail}.
	 *
	 * @param newError The error to add to the list.
	 */
	protected void addValidationError(final Detail newError) {
		getDetails().add(newError);
	}

	/**
	 * Overridden by child classes to validate a specific {@link gov.cms.qpp.conversion.model.Node}.
	 *
	 * <p>
	 * The implementation should validate the {@link gov.cms.qpp.conversion.model.Node} passed in.  If an error is
	 * found, the child class must call {@link #addValidationError(Detail)} for it to be reported.  The
	 * Node argument will have the same ID as the templateId of the
	 * {@link gov.cms.qpp.conversion.model.Validator}.
	 * </p>
	 *
	 * @param node The node to validate.
	 */
	protected abstract void internalValidateSingleNode(final Node node);

	protected Checker check(Node node) {
		return Checker.check(node, this.getDetails());
	}

	Checker thoroughlyCheck(Node node) {
		return Checker.thoroughlyCheck(node, this.getDetails());
	}
}
