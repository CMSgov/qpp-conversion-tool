package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.NodeType;
import gov.cms.qpp.conversion.model.ValidationError;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Node checker DSL to help abbreviate / simplify single node validations
 */
class Checker {
	private Node node;
	private List<ValidationError> validationErrors;
	private boolean anded;
	private Map<NodeType, Long> nodeCount;

	/**
	 * static factory that returns a shortcut validator
	 *
	 * @param node
	 * @param validationErrors
	 * @return
	 */
	static Checker check( Node node, List<ValidationError> validationErrors ){
		return new Checker( node, validationErrors, true);
	}

	/**
	 * static factory that returns a non-shortcut validator
	 *
	 * @param node
	 * @param validationErrors
	 * @return
	 */
	static Checker thoroughlyCheck( Node node, List<ValidationError> validationErrors ){
		return new Checker( node, validationErrors, false);
	}

	private Checker( Node node, List<ValidationError> validationErrors, boolean anded ){
		this.node = node;
		this.validationErrors = validationErrors;
		this.anded = anded;
		this.nodeCount = node.getChildNodes().stream().collect(
				Collectors.groupingBy( Node::getType, Collectors.counting() )
		);
	}

	private boolean shouldShortcut() {
		return anded && !validationErrors.isEmpty();
	}

	/**
	 * checks target node for the existence of a value with the given name key
	 *
	 * @param message error message if searched value is not found
	 * @param name
	 * @return
	 */
	Checker value( String message, String name ) {
		if ( !shouldShortcut() ) {
			if (node.getValue(name) == null) {
				validationErrors.add(new ValidationError(message));
			}
		}
		return this;
	}

	/**
	 * checks target node for the existence of an integer value with the given name key
	 *
	 * @param message error message if searched value is not found or is not appropriately typed
	 * @param name
	 * @return
	 */
	Checker intValue( String message, String name ) {
		if ( !shouldShortcut() ) {
			try {
				Integer.parseInt(node.getValue(name));
			} catch (NumberFormatException ex) {
				validationErrors.add(new ValidationError(message));
			}
		}
		return this;
	}

	/**
	 * checks target node for the existence of any child nodes
	 *
	 * @param message
	 * @return
	 */
	Checker hasChildren(String message ) {
		if ( !shouldShortcut() ) {
			if (node.getChildNodes().isEmpty()) {
				validationErrors.add(new ValidationError(message));
			}
		}
		return this;
	}

	/**
	 * verifies that the target node has more than the given minimum of the given {@link NodeType}s
	 *
	 * @param message
	 * @param minimum
	 * @param types
	 * @return
	 */
	Checker childMinimum( String message, int minimum, NodeType... types  ) {
		if ( !shouldShortcut() ) {
			long count = tallyNodes(types);
			if (count < minimum) {
				validationErrors.add(new ValidationError(message));
			}
		}
		return this;
	}

	/**
	 * verifies that the target node has less than the given maximum of the given {@link NodeType}s
	 *
	 * @param message
	 * @param maximum
	 * @param types
	 * @return
	 */
	Checker childMaximum( String message, int maximum, NodeType... types ) {
		if ( !shouldShortcut() ) {
			long count = tallyNodes(types);
			if (count > maximum) {
				validationErrors.add(new ValidationError(message));
			}
		}
		return this;
	}

	private long tallyNodes( NodeType... types ) {
		return Arrays.stream( types )
			.mapToLong( type -> ( nodeCount.get( type ) == null ) ? 0 : nodeCount.get( type ) )
			.sum();
	}
}
