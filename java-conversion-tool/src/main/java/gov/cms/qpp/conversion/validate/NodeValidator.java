package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * The parent class that all validators must inherit from.
 */
public abstract class NodeValidator {

	private static final Logger DEV_LOG = LoggerFactory.getLogger(NodeValidator.class);

	private List<Detail> details = new ArrayList<>();

	/**
	 * Validates a single {@link gov.cms.qpp.conversion.model.Node} and returns the list
	 * of {@link Detail}s for that node.
	 *
	 * @param node The node to validate.
	 * @return List of errors determined for the node paramter.
	 * @see #internalValidateSingleNode(Node)
	 */
	public List<Detail> validateSingleNode(final Node node) {
		internalValidateSingleNode(node);
		return getDetails();
	}

	/**
	 * Validates a list of {@link gov.cms.qpp.conversion.model.Node}s that all have the same ID.
	 *
	 * <p>
	 * Some validators need a list of all the {@link gov.cms.qpp.conversion.model.Node}s of the same ID to validate
	 * certain aspects.  For example, validate that at least one node in the QRDA3 document contains a
	 * {@code measureId} of {@code ACI_PHCDRR_3}.
	 * </p>
	 *
	 * @param nodes A list of nodes that all have the same ID.
	 * @return List of errors determined from the list of nodes.
	 * @see #internalValidateSameTemplateIdNodes(List)
	 */
	public List<Detail> validateSameTemplateIdNodes(final List<Node> nodes) {
		internalValidateSameTemplateIdNodes(nodes);
		return getDetails();
	}

	/**
	 * Used by child classes to get the current list of validation errors they have added.
	 *
	 * @return The current list of validation errors.
	 */
	protected List<Detail> getDetails() {
		return details;
	}

	/**
	 * Used by child classes to add a {@link Detail}.
	 *
	 * @param newError The error to add to the list.
	 */
	protected void addValidationError(final Detail newError) {
		logValidationError(newError);
		details.add(newError);
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

	/**
	 * Overridden by child classes to validate a list of {@link gov.cms.qpp.conversion.model.Node}s.
	 *
	 * <p>
	 * The implementation should validate the list of {@link gov.cms.qpp.conversion.model.Node}s as a whole. 
	 * An implementation must not do single node validations on each element of the list.  Single node
	 * validations are done exclusively in {@link #internalValidateSingleNode(Node)}. Instead, an implementation
	 * must validate things that can only be validated given all the Nodes with the same ID - that ID being
	 * the templateId of the {@link gov.cms.qpp.conversion.model.Validator} - in the QRDA3 file. 
	 * For example, validate that at least one node in the QRDA3 document contains a {@code measureId}
	 * of {@code ACI_PHCDRR_3}.
	 * </p>
	 *
	 * @param nodes The list of nodes to validate.
	 */
	protected abstract void internalValidateSameTemplateIdNodes(final List<Node> nodes);

	private void logValidationError(final Detail newError) {
		DEV_LOG.debug("Error '{}' added for templateId {}", newError, getTemplateId());
	}

	/**
	 * Get the node validator's corresponding template id.
	 *
	 * @return templateId
	 */
	protected TemplateId getTemplateId() {
		final Validator validator = this.getClass().getAnnotation(Validator.class);
		return (null != validator) ? validator.value() : null;
	}

	protected Checker check(Node node) {
		return Checker.check(node, this.getDetails());
	}

	protected Checker thoroughlyCheck(Node node) {
		return Checker.thoroughlyCheck(node, this.getDetails());
	}
}
