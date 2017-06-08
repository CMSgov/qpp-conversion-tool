package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.Assert.assertThat;

/**
 * Test class for IaMeasureValidator
 */
public class IaMeasureValidatorTest {

	/**
	 * Validate a correct set of Nodes
	 *
	 * @throws Exception on test error
	 */
	@Test
	public void internalValidateSingleNodeY() throws Exception {
		Node measureNode = new Node(TemplateId.IA_MEASURE);
		Node measurePerformedNode = new Node(TemplateId.MEASURE_PERFORMED, measureNode);
		measureNode.addChildNode(measurePerformedNode);
		measurePerformedNode.putValue("measurePerformed", "Y");

		IaMeasureValidator validator = new IaMeasureValidator();
		List<Detail> errors = validator.validateSingleNode(measureNode);
		assertThat("no errors should be present", errors, empty());
	}

	@Test
	public void internalValidateSingleNodeN() throws Exception {
		Node measureNode = new Node(TemplateId.IA_MEASURE);
		Node measurePerformedNode = new Node(TemplateId.MEASURE_PERFORMED, measureNode);
		measureNode.addChildNode(measurePerformedNode);
		measurePerformedNode.putValue("measurePerformed", "N");

		IaMeasureValidator validator = new IaMeasureValidator();
		List<Detail> errors = validator.validateSingleNode(measureNode);
		assertThat("no errors should be present", errors, empty());
	}


	/**
	 * Validate a missing child
	 *
	 * @throws Exception on test error
	 */
	@Test
	public void testMissingNode() throws Exception {
		Node measureNode = new Node(TemplateId.IA_MEASURE);

		IaMeasureValidator validator = new IaMeasureValidator();
		List<Detail> errors = validator.validateSingleNode(measureNode);
		assertThat("A missing child errors should be present", errors.size(), is(1));
		String error = errors.get(0).getMessage();
		assertThat("The INCORRECT_CHILDREN_COUNT Error is expected", error,
				is(IaMeasureValidator.INCORRECT_CHILDREN_COUNT));
	}

	/**
	 * Validate a missing child
	 *
	 * @throws Exception on test error
	 */
	@Test
	public void testTooManyChildren() throws Exception {
		Node measureNode = new Node(TemplateId.IA_MEASURE);
		Node measurePerformedNode1 = new Node(TemplateId.MEASURE_PERFORMED, measureNode);
		Node measurePerformedNode2 = new Node(TemplateId.MEASURE_PERFORMED, measureNode);
		measureNode.addChildNode(measurePerformedNode1);
		measurePerformedNode1.putValue("measurePerformed", "Y");
		measureNode.addChildNode(measurePerformedNode2);
		measurePerformedNode1.putValue("measurePerformed", "N");

		IaMeasureValidator validator = new IaMeasureValidator();
		List<Detail> errors = validator.validateSingleNode(measureNode);
		assertThat("A Too Many children errors should be present", errors.size(), is(1));
		String error = errors.get(0).getMessage();
		assertThat("The INCORRECT_CHILDREN_COUNT Error is expected", error, is(IaMeasureValidator.INCORRECT_CHILDREN_COUNT));
	}
}