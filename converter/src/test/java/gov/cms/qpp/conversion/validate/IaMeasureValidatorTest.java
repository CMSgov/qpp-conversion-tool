package gov.cms.qpp.conversion.validate;

import static com.google.common.truth.Truth.assertWithMessage;

import java.util.Collection;
import java.util.Set;

import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ErrorCode;
import gov.cms.qpp.conversion.model.error.correspondence.DetailsErrorEquals;

/**
 * Test class for IaMeasureValidator
 */
class IaMeasureValidatorTest {

	/**
	 * Validate a correct set of Nodes
	 *
	 * @throws Exception on test error
	 */
	@Test
	void internalValidateSingleNodeY() throws Exception {
		Node measureNode = new Node(TemplateId.IA_MEASURE);
		Node measurePerformedNode = new Node(TemplateId.MEASURE_PERFORMED, measureNode);
		measureNode.addChildNode(measurePerformedNode);
		measurePerformedNode.putValue("measurePerformed", "Y");

		IaMeasureValidator validator = new IaMeasureValidator();
		Collection<Detail> errors = validator.validateSingleNode(measureNode);
		assertWithMessage("no errors should be present")
				.that(errors).isEmpty();
	}

	@Test
	void internalValidateSingleNodeN() throws Exception {
		Node measureNode = new Node(TemplateId.IA_MEASURE);
		Node measurePerformedNode = new Node(TemplateId.MEASURE_PERFORMED, measureNode);
		measureNode.addChildNode(measurePerformedNode);
		measurePerformedNode.putValue("measurePerformed", "N");

		IaMeasureValidator validator = new IaMeasureValidator();
		Collection<Detail> errors = validator.validateSingleNode(measureNode);
		assertWithMessage("no errors should be present")
				.that(errors).isEmpty();
	}


	/**
	 * Validate a missing child
	 *
	 * @throws Exception on test error
	 */
	@Test
	void testMissingNode() throws Exception {
		Node measureNode = new Node(TemplateId.IA_MEASURE);
		IaMeasureValidator validator = new IaMeasureValidator();
		Set<Detail> errors = validator.validateSingleNode(measureNode);

		assertWithMessage("The INCORRECT_CHILDREN_COUNT Error is expected")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.IA_MEASURE_INCORRECT_CHILDREN_COUNT);
	}

	/**
	 * Validate a missing child
	 *
	 * @throws Exception on test error
	 */
	@Test
	void testTooManyChildren() throws Exception {
		Node measureNode = new Node(TemplateId.IA_MEASURE);
		Node measurePerformedNode1 = new Node(TemplateId.MEASURE_PERFORMED, measureNode);
		Node measurePerformedNode2 = new Node(TemplateId.MEASURE_PERFORMED, measureNode);
		measureNode.addChildNode(measurePerformedNode1);
		measurePerformedNode1.putValue("measurePerformed", "Y");
		measureNode.addChildNode(measurePerformedNode2);
		measurePerformedNode1.putValue("measurePerformed", "N");

		IaMeasureValidator validator = new IaMeasureValidator();
		Set<Detail> errors = validator.validateSingleNode(measureNode);

		assertWithMessage("The INCORRECT_CHILDREN_COUNT Error is expected")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.IA_MEASURE_INCORRECT_CHILDREN_COUNT);
	}
}