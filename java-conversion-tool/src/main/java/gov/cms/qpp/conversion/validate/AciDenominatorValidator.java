package gov.cms.qpp.conversion.validate;


import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.ValidationError;
import gov.cms.qpp.conversion.model.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This Validator checks that exactly one Aggregate Count Child exists,
 * and that its aggregate count value is a positive integer value greater than zero.
 */
@Validator(templateId = TemplateId.ACI_DENOMINATOR, required = true)
public class AciDenominatorValidator extends CommonNumeratorDenominatorValidator {
	private static final Logger DEV_LOG = LoggerFactory.getLogger(AciDenominatorValidator.class);
	protected static final String DENOMINATOR_CANNOT_BE_ZERO = "The ACI Denominator's Aggregate Value can not be zero";
	protected static final String DENOMINATOR_NAME = "Denominator";

	/**
	 * Public constructor sets the node name for this class
	 */
	public AciDenominatorValidator() {
		nodeName = DENOMINATOR_NAME;
	}

	/**
	 * internalValidateSingleNode Checks that this node has a child and that the child is a Denominator
	 * and that the denominator integer value is > 0
	 *
	 * @param node Node parsed xml fragment under consideration
	 */
	@Override
	protected void internalValidateSingleNode(Node node) {
		super.internalValidateSingleNode(node);
		if ( ! getValidationErrors().isEmpty() ){
			return;
		}
		Node aggregateCountNode = node.findFirstNode(TemplateId.ACI_AGGREGATE_COUNT.getTemplateId());

		int value = Integer.parseInt(aggregateCountNode.getValue("aggregateCount"));
		if (value == 0) {
			this.addValidationError(new ValidationError(DENOMINATOR_CANNOT_BE_ZERO, aggregateCountNode.getPath()));
			DEV_LOG.error(DENOMINATOR_CANNOT_BE_ZERO);
		}
	}
}