package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.decode.AggregateCountDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.correspondence.DetailsMessageEquals;
import org.junit.Test;

import java.util.Set;

import static com.google.common.truth.Truth.assertWithMessage;


public class AggregateCountValidatorTest {

    @Test
    public void testIsAggregateCount() {
        Node aggregateCountNode = new Node(TemplateId.ACI_AGGREGATE_COUNT);

        AggregateCountValidator validator = new AggregateCountValidator();

        assertWithMessage("validator and node are compatible")
                .that(validator.getTemplateId()).isEqualTo(aggregateCountNode.getType());
    }

    @Test
    public void testValueAbsenceFailure() {
        Node aggregateCountNode = new Node(TemplateId.ACI_AGGREGATE_COUNT);

        AggregateCountValidator validator = new AggregateCountValidator();
        validator.internalValidateSingleNode( aggregateCountNode );
        Set<Detail> errors = validator.getDetails();

        assertWithMessage("Should result in a value error")
                .that(errors).comparingElementsUsing(DetailsMessageEquals.INSTANCE)
                .containsExactly(AggregateCountValidator.VALUE_ERROR);
    }

    @Test
    public void testValueTypeFailure() {
        Node aggregateCountNode = new Node(TemplateId.ACI_AGGREGATE_COUNT);
        aggregateCountNode.putValue(AggregateCountDecoder.AGGREGATE_COUNT, "meep");

        AggregateCountValidator validator = new AggregateCountValidator();
        validator.internalValidateSingleNode(aggregateCountNode);
        Set<Detail> errors = validator.getDetails();

        assertWithMessage("Should result in a type error")
                .that(errors).comparingElementsUsing(DetailsMessageEquals.INSTANCE)
                .containsExactly(AggregateCountValidator.TYPE_ERROR);
    }

    @Test
    public void testValueTypeSuccess() {
        Node aggregateCountNode = new Node(TemplateId.ACI_AGGREGATE_COUNT);
        aggregateCountNode.putValue(AggregateCountDecoder.AGGREGATE_COUNT, "7");

        AggregateCountValidator validator = new AggregateCountValidator();
        validator.internalValidateSingleNode(aggregateCountNode);
        Set<Detail> errors = validator.getDetails();

        assertWithMessage("there are no errors")
                .that(errors).isEmpty();
    }
}
