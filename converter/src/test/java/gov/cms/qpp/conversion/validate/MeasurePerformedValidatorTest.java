package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ErrorCode;
import gov.cms.qpp.conversion.model.error.correspondence.DetailsErrorEquals;
import org.junit.Test;

import java.util.Set;

import static com.google.common.truth.Truth.assertWithMessage;

/**
 * Test class for MeasurePerformedValidator
 */

public class MeasurePerformedValidatorTest {

	/**
	 * Validate a correct set of Nodes
	 *
	 * @throws Exception on test error
	 */
	@Test
	public void internalValidateSingleNodeY() throws Exception {
		Node measurePerformedNode = new Node(TemplateId.MEASURE_PERFORMED);
		measurePerformedNode.putValue("measurePerformed", "Y");

		MeasurePerformedValidator validator = new MeasurePerformedValidator();
		Set<Detail> errors = validator.validateSingleNode(measurePerformedNode);
		assertWithMessage("no errors should be present")
				.that(errors).isEmpty();
	}

	@Test
	public void internalValidateSingleNodeN() throws Exception {
		Node measurePerformedNode = new Node(TemplateId.MEASURE_PERFORMED);
		measurePerformedNode.putValue("measurePerformed", "N");

		MeasurePerformedValidator validator = new MeasurePerformedValidator();
		Set<Detail> errors = validator.validateSingleNode(measurePerformedNode);
		assertWithMessage("no errors should be present")
				.that(errors).isEmpty();
	}

	@Test
	public void internalValidateSingleNodeInvalid() throws Exception {
		Node measurePerformedNode = new Node(TemplateId.MEASURE_PERFORMED);
		measurePerformedNode.putValue("measurePerformed", "wrong value");

		MeasurePerformedValidator validator = new MeasurePerformedValidator();
		Set<Detail> errors = validator.validateSingleNode(measurePerformedNode);
		assertWithMessage("Should result in a single type error")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.IA_MEASURE_INVALID_TYPE);
	}
}
