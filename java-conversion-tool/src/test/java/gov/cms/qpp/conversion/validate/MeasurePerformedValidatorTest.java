package gov.cms.qpp.conversion.validate;

import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.hamcrest.core.Is;
import org.junit.Test;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;

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
		List<Detail> errors = validator.validateSingleNode(measurePerformedNode);
		assertThat("no errors should be present", errors, empty());
	}

	@Test
	public void internalValidateSingleNodeN() throws Exception {
		Node measurePerformedNode = new Node(TemplateId.MEASURE_PERFORMED);
		measurePerformedNode.putValue("measurePerformed", "N");

		MeasurePerformedValidator validator = new MeasurePerformedValidator();
		List<Detail> errors = validator.validateSingleNode(measurePerformedNode);
		assertThat("no errors should be present", errors, empty());
	}

	@Test
	public void internalValidateSingleNodeInvalid() throws Exception {
		Node measurePerformedNode = new Node(TemplateId.MEASURE_PERFORMED);
		measurePerformedNode.putValue("measurePerformed", "wrong value");

		MeasurePerformedValidator validator = new MeasurePerformedValidator();
		List<Detail> errors = validator.validateSingleNode(measurePerformedNode);
		assertThat("no errors should be present", errors.size(), Is.is(1));
	}
}
