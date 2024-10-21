package gov.cms.qpp.conversion.validate;

import static com.google.common.truth.Truth.assertWithMessage;
import static gov.cms.qpp.conversion.model.Constants.AGGREGATE_COUNT;

import java.util.List;

import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ProblemCode;
import gov.cms.qpp.conversion.model.error.correspondence.DetailsErrorEquals;


class AggregateCountValidatorTest {

	@Test
	void testIsAggregateCount() {
			Node aggregateCountNode = new Node(TemplateId.PI_AGGREGATE_COUNT);

		AggregateCountValidator validator = new AggregateCountValidator();

		assertWithMessage("validator and node are compatible")
				.that(validator.getClass().getAnnotation(Validator.class).value()).isEqualTo(aggregateCountNode.getType());
	}

	@Test
	void testValueAbsenceFailure() {
		Node aggregateCountNode = new Node(TemplateId.PI_AGGREGATE_COUNT);
		aggregateCountNode.setParent(new Node(TemplateId.PI_NUMERATOR));

		AggregateCountValidator validator = new AggregateCountValidator();
		List<Detail> errors = validator.validateSingleNode(aggregateCountNode).getErrors();

		assertWithMessage("Should result in a value error")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ProblemCode.AGGREGATE_COUNT_VALUE_NOT_SINGULAR.format(TemplateId.PI_NUMERATOR.name(), 0));
	}

	@Test
	void testValueTypeFailure() {
		Node aggregateCountNode = new Node(TemplateId.PI_AGGREGATE_COUNT);
		aggregateCountNode.setParent(new Node(TemplateId.PI_NUMERATOR));
		aggregateCountNode.putValue(AGGREGATE_COUNT, "meep");

		AggregateCountValidator validator = new AggregateCountValidator();
		List<Detail> errors = validator.validateSingleNode(aggregateCountNode).getErrors();

		assertWithMessage("Should result in a type error")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ProblemCode.AGGREGATE_COUNT_VALUE_NOT_INTEGER);
	}

	@Test
	void testValueTypeSuccess() {
		Node aggregateCountNode = new Node(TemplateId.PI_AGGREGATE_COUNT);
		aggregateCountNode.setParent(new Node(TemplateId.PI_NUMERATOR));
		aggregateCountNode.putValue(AGGREGATE_COUNT, "7");

		AggregateCountValidator validator = new AggregateCountValidator();
		List<Detail> errors = validator.validateSingleNode(aggregateCountNode).getErrors();

		assertWithMessage("there are no errors")
				.that(errors).isEmpty();
	}
}
