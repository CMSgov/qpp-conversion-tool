package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.decode.MeasureDataDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.ValidationError;
import gov.cms.qpp.conversion.model.Validator;

import java.util.List;

/**
 * Validates Measure Data - an Aggregate Count child
 */
@Validator(templateId = TemplateId.MEASURE_DATA_CMS_V2, required = true)
public class MeasureDataValidator extends NodeValidator {

	public static final String TYPE_ERROR = "Measure data value is required.";
	public static final String MISSING_AGGREGATE_COUNT  = "Measure performed must have exactly one Aggregate Count.";
	public static final String INCORRECT_CHILDREN_COUNT  = "Measure performed must have exactly one child Aggregate Count.";
	public static final String INVALID_VALUE = "Measure data must be a positive integer value";

	/**
	 * Validates a single Measure Data Value {@link Node}.
	 *
	 * Validates the following.
	 * <ul>
	 *    <li>An integer value with a name in the list from MeasureDataDecoder.MEASURES</li>
	 *    <li>The string value is an integer/li>
	 *</ul>
	 *
	 * @param node Node that represents a IA Measure Performed.
	 */
	@Override
	protected void internalValidateSingleNode(Node node) {
		List<Node> children = node.getChildNodes();
		if (children == null || children.isEmpty() ) {
			addValidationError(new ValidationError(INCORRECT_CHILDREN_COUNT, node.getPath()));
			return;
		}
		int dataCount = 0; //Make sure we find at least one data value
		int number; // Used to parse Integer into
		List<Node> nodesList = null; //Get the children list of AggregateCount nodes expect one
		for(Node child : children ) {
			String value = child.getValue("type"); //One of MeasureDataDecoder.MEASURES.contains
			if ( MeasureDataDecoder.MEASURES.contains(value)){
				nodesList = child.findNode(TemplateId.ACI_AGGREGATE_COUNT.getTemplateId());
				if ( nodesList == null ) {
					addValidationError(new ValidationError(MISSING_AGGREGATE_COUNT, child.getPath()));
				}
				if ( nodesList.size() > 1){
					addValidationError(new ValidationError(INCORRECT_CHILDREN_COUNT, child.getPath()));
				}

				String nodeValue =  nodesList.get(0).getValue("aggregateCount");
				if (nodeValue != null) {
					try {
						number = Integer.parseInt(nodeValue);
						if (number < 0) {
							addValidationError(new ValidationError(INVALID_VALUE, child.getPath()));
						} else {
							dataCount++;
						}
					} catch (NumberFormatException nfe) {
						addValidationError(new ValidationError(INVALID_VALUE, child.getPath()));
					}
				}
			}
		}
		if ( dataCount == 0 ){
			addValidationError(new ValidationError(TYPE_ERROR, node.getPath()));
		}
	}

	/**
	 * Checks the interdependancy of nodes in the parsed tree.
	 * IA Measure Performed has no dependencies on other nodes in the document.
	 * @param nodes The list of nodes to validate.
	 */
	@Override
	protected void internalValidateSameTemplateIdNodes(List<Node> nodes) {
		// No current cross node Aggregate Count validations
	}
}