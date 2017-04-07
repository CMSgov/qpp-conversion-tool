package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.NodeType;
import gov.cms.qpp.conversion.model.ValidationError;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by clydetedrick on 4/7/17.
 */
class Checker {
	private Node node;
	private List<ValidationError> validationErrors;
	private boolean anded;
	private Map<NodeType, Long> nodeCount;

	static Checker check( Node node, List<ValidationError> validationErrors ){
		return new Checker( node, validationErrors, true);
	}

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

	Checker value( String message, String name ) {
		if ( shouldShortcut() ) {
			return this;
		}
		if ( node.getValue( name ) == null ) {
			validationErrors.add( new ValidationError( message ));
		}
		return this;
	}

	Checker intValue( String message, String name ) {
		if ( shouldShortcut() ) {
			return this;
		}
		try{
			Integer.parseInt( node.getValue( name ) );
		} catch (NumberFormatException ex) {
			validationErrors.add( new ValidationError( message ));
		}
		return this;
	}

	Checker children( String message ) {
		if ( shouldShortcut() ) {
			return this;
		}
		if ( node.getChildNodes().isEmpty() ) {
			validationErrors.add( new ValidationError( message ));
		}
		return this;
	}

	Checker childMinimum( String message, int minimum, NodeType... types  ) {
		if ( shouldShortcut() ) {
			return this;
		}

		long count = Arrays
				.stream( types )
				.mapToLong( type -> ( nodeCount.get( type ) == null ) ? 0 : nodeCount.get( type ) )
				.sum();
		if ( count < minimum ) {
			validationErrors.add( new ValidationError( message ));
		}
		return this;
	}

	Checker childMaximum( String message, int maximum, NodeType... types ) {
		if ( shouldShortcut() ) {
			return this;
		}
		long count = Arrays
				.stream( types )
				.mapToLong( type -> ( nodeCount.get( type ) == null ) ? 0 : nodeCount.get( type ) )
				.sum();
		if ( count > maximum ) {
			validationErrors.add( new ValidationError( message ));
		}
		return this;
	}
}
