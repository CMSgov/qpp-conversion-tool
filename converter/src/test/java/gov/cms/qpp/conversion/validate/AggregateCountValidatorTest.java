package gov.cms.qpp.conversion.validate;

import static com.google.common.truth.Truth.assertWithMessage;

import java.util.Set;

import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.decode.AggregateCountDecoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ErrorCode;
import gov.cms.qpp.conversion.model.error.correspondence.DetailsErrorEquals;


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
                .that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
                .containsExactly(ErrorCode.AGGREGATE_COUNT_VALUE_NOT_SINGULAR);
    }

    @Test
    public void testValueTypeFailure() {
        Node aggregateCountNode = new Node(TemplateId.ACI_AGGREGATE_COUNT);
        aggregateCountNode.putValue(AggregateCountDecoder.AGGREGATE_COUNT, "meep");

        AggregateCountValidator validator = new AggregateCountValidator();
        validator.internalValidateSingleNode(aggregateCountNode);
        Set<Detail> errors = validator.getDetails();

        assertWithMessage("Should result in a type error")
                .that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
                .containsExactly(ErrorCode.AGGREGATE_COUNT_VALUE_NOT_INTEGER);
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
