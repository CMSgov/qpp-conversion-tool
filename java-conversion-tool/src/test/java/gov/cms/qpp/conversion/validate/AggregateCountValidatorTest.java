package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.decode.AggregateCountDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.NodeType;
import gov.cms.qpp.conversion.model.ValidationError;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by clydetedrick on 4/6/17.
 */
public class AggregateCountValidatorTest {

    @Test
    public void testIsAggregateCount() {
        Node aggregateCountNode = new Node( NodeType.ACI_AGGREGATE_COUNT.getTemplateId() );

        AggregateCountValidator validator = new AggregateCountValidator();

        assertEquals("validator and node are compatible", validator.getTemplateId(), aggregateCountNode.getId());
    }

    @Test
    public void testValueAbsenceFailure() {
        Node aggregateCountNode = new Node( NodeType.ACI_AGGREGATE_COUNT.getTemplateId() );

        AggregateCountValidator validator = new AggregateCountValidator();
        validator.internalValidateSingleNode( aggregateCountNode );
        List<ValidationError> errors = validator.getValidationErrors();

        assertFalse("there's an error", errors.isEmpty());
        assertEquals( AggregateCountValidator.VALUE_ERROR, errors.get( 0 ).getErrorText() );
    }

    @Test
    public void testValueTypeFailure() {
        Node aggregateCountNode = new Node( NodeType.ACI_AGGREGATE_COUNT.getTemplateId() );
        aggregateCountNode.putValue( AggregateCountDecoder.AGGREGATE_COUNT, "meep" );

        AggregateCountValidator validator = new AggregateCountValidator();
        validator.internalValidateSingleNode( aggregateCountNode );
        List<ValidationError> errors = validator.getValidationErrors();

        assertFalse("there's an error", errors.isEmpty());
        assertEquals( AggregateCountValidator.TYPE_ERROR, errors.get( 0 ).getErrorText() );
    }

    @Test
    public void testValueTypeSuccess() {
        Node aggregateCountNode = new Node( NodeType.ACI_AGGREGATE_COUNT.getTemplateId() );
        aggregateCountNode.putValue( AggregateCountDecoder.AGGREGATE_COUNT, "7" );

        AggregateCountValidator validator = new AggregateCountValidator();
        validator.internalValidateSingleNode( aggregateCountNode );
        List<ValidationError> errors = validator.getValidationErrors();

        assertTrue("there are no errors", errors.isEmpty());
    }
}
