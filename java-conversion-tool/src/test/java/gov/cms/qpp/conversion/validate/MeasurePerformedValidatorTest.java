package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.ValidationError;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.Assert.assertThat;

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
		Node measureNode = new Node(TemplateId.IA_MEASURE.getTemplateId());
		Node measurePerformedNode = new Node(measureNode, TemplateId.MEASURE_PERFORMED.getTemplateId());
		measureNode.addChildNode(measurePerformedNode);
		measurePerformedNode.putValue("measurePerformed", "Y");

		MeasurePerformedValidator validator = new MeasurePerformedValidator();
		List<ValidationError> errors = validator.validateSingleNode(measureNode);
		assertThat("no errors should be present", errors, empty());
	}

	@Test
	public void internalValidateSingleNodeN() throws Exception {
		Node measureNode = new Node(TemplateId.IA_MEASURE.getTemplateId());
		Node measurePerformedNode = new Node(measureNode, TemplateId.MEASURE_PERFORMED.getTemplateId());
		measureNode.addChildNode(measurePerformedNode);
		measurePerformedNode.putValue("measurePerformed", "N");

		MeasurePerformedValidator validator = new MeasurePerformedValidator();
		List<ValidationError> errors = validator.validateSingleNode(measureNode);
		assertThat("no errors should be present", errors, empty());
	}
	/**
	 * Validate an invalid value child Node
	 *
	 * @throws Exception on test error
	 */
	@Test
	public void internalValidateSingleInvalidValueNode() throws Exception {
		Node measureNode = new Node(TemplateId.IA_MEASURE.getTemplateId());
		Node measurePerformedNode = new Node(measureNode, TemplateId.MEASURE_PERFORMED.getTemplateId());
		measureNode.addChildNode(measurePerformedNode);
		measurePerformedNode.putValue("measurePerformed", "abc");

		MeasurePerformedValidator validator = new MeasurePerformedValidator();
		List<ValidationError> errors = validator.validateSingleNode(measureNode);
		assertThat("An invalid value error should be present", errors.size(), is(1));
		String error = errors.get(0).getErrorText();
		assertThat("The Invalid value Error is expected", error, is(MeasurePerformedValidator.TYPE_ERROR));
	}

	/**
	 * Validate a missing child
	 *
	 * @throws Exception on test error
	 */
	@Test
	public void testMissingNode() throws Exception {
		Node measureNode = new Node(TemplateId.IA_MEASURE.getTemplateId());

		MeasurePerformedValidator validator = new MeasurePerformedValidator();
		List<ValidationError> errors = validator.validateSingleNode(measureNode);
		assertThat("A missing child errors should be present", errors.size(), is(1));
		String error = errors.get(0).getErrorText();
		assertThat("The INCORRECT_CHILDREN_COUNT Error is expected", error,
				is(MeasurePerformedValidator.INCORRECT_CHILDREN_COUNT));
	}

	/**
	 * Validate a missing child
	 *
	 * @throws Exception on test error
	 */
	@Test
	public void testTooManyChildren() throws Exception {
		Node measureNode = new Node(TemplateId.IA_MEASURE.getTemplateId());
		Node measurePerformedNode1 = new Node(measureNode, TemplateId.MEASURE_PERFORMED.getTemplateId());
		Node measurePerformedNode2 = new Node(measureNode, TemplateId.MEASURE_PERFORMED.getTemplateId());
		measureNode.addChildNode(measurePerformedNode1);
		measurePerformedNode1.putValue("measurePerformed", "Y");
		measureNode.addChildNode(measurePerformedNode2);
		measurePerformedNode1.putValue("measurePerformed", "N");

		MeasurePerformedValidator validator = new MeasurePerformedValidator();
		List<ValidationError> errors = validator.validateSingleNode(measureNode);
		assertThat("A Too Many children errors should be present", errors.size(), is(1));
		String error = errors.get(0).getErrorText();
		assertThat("The INCORRECT_CHILDREN_COUNT Error is expected", error, is(MeasurePerformedValidator.INCORRECT_CHILDREN_COUNT));
	}
}