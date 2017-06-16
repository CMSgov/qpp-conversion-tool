package gov.cms.qpp.conversion.validate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import gov.cms.qpp.conversion.decode.AggregateCountDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;

/**
 * Created by clydetedrick on 4/6/17.
 */
public class AggregateCountValidatorTest {

    @Test
    public void testIsAggregateCount() {
        Node aggregateCountNode = new Node(TemplateId.ACI_AGGREGATE_COUNT);

        AggregateCountValidator validator = new AggregateCountValidator();

        assertEquals("validator and node are compatible", validator.getTemplateId(), aggregateCountNode.getType());
    }

    @Test
    public void testValueAbsenceFailure() {
        Node aggregateCountNode = new Node(TemplateId.ACI_AGGREGATE_COUNT);

        AggregateCountValidator validator = new AggregateCountValidator();
        validator.internalValidateSingleNode( aggregateCountNode );
        List<Detail> errors = validator.getDetails();

        assertFalse("there's an error", errors.isEmpty());
        assertEquals(AggregateCountValidator.VALUE_ERROR, errors.get(0).getMessage());
    }

    @Test
    public void testValueTypeFailure() {
        Node aggregateCountNode = new Node(TemplateId.ACI_AGGREGATE_COUNT);
        aggregateCountNode.putValue(AggregateCountDecoder.AGGREGATE_COUNT, "meep");

        AggregateCountValidator validator = new AggregateCountValidator();
        validator.internalValidateSingleNode(aggregateCountNode);
        List<Detail> errors = validator.getDetails();

        assertFalse("there's an error", errors.isEmpty());
        assertEquals(AggregateCountValidator.TYPE_ERROR, errors.get(0).getMessage());
    }

    @Test
    public void testValueTypeSuccess() {
        Node aggregateCountNode = new Node(TemplateId.ACI_AGGREGATE_COUNT);
        aggregateCountNode.putValue(AggregateCountDecoder.AGGREGATE_COUNT, "7");

        AggregateCountValidator validator = new AggregateCountValidator();
        validator.internalValidateSingleNode(aggregateCountNode);
        List<Detail> errors = validator.getDetails();

        assertTrue("there are no errors", errors.isEmpty());
    }
}
