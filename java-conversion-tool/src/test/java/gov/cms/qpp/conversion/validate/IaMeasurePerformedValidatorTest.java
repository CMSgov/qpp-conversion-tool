package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.ValidationError;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.Assert.assertThat;

/**
 * Test class for IaMeasurePerformedValidator
 */
public class IaMeasurePerformedValidatorTest {

	/**
	 * Validate a correct set of Nodes
	 *
	 * @throws Exception
	 */
	@Test
	public void internalValidateSingleNode() throws Exception {
		Node iaMeasureNode = new Node(TemplateId.IA_MEASURE.getTemplateId());
		Node iaMeasurePerformedNode = new Node(iaMeasureNode, TemplateId.ACI_MEASURE_PERFORMED.getTemplateId());
		iaMeasureNode.addChildNode(iaMeasurePerformedNode);
		iaMeasurePerformedNode.putValue("measurePerformed", "Y");

		IaMeasurePerformedValidator validator = new IaMeasurePerformedValidator();
		List<ValidationError> errors = validator.validateSingleNode(iaMeasureNode);
		assertThat("no errors should be present", errors, empty());
	}

	/**
	 * Validate an invalid value child Node
	 *
	 * @throws Exception
	 */
	@Test
	public void internalValidateSingleInvalidValueNode() throws Exception {
		Node iaMeasureNode = new Node(TemplateId.IA_MEASURE.getTemplateId());
		Node iaMeasurePerformedNode = new Node(iaMeasureNode, TemplateId.ACI_MEASURE_PERFORMED.getTemplateId());
		iaMeasureNode.addChildNode(iaMeasurePerformedNode);
		iaMeasurePerformedNode.putValue("measurePerformed", "abc");

		IaMeasurePerformedValidator validator = new IaMeasurePerformedValidator();
		List<ValidationError> errors = validator.validateSingleNode(iaMeasureNode);
		assertThat("An invalid value error should be present", errors.size(), is(1));
		String errSubstring = IaMeasurePerformedValidator.TYPE_ERROR.substring(0,
				IaMeasurePerformedValidator.TYPE_ERROR.indexOf("."));
		String error = errors.get(0).getErrorText();
		String actualErrSubstring = error.substring(0, error.indexOf("."));
		assertThat("The Invalid value Error is expected", errSubstring, is(actualErrSubstring));
	}

	/**
	 * Validate a missing child
	 *
	 * @throws Exception
	 */
	@Test
	public void testMissingNode() throws Exception {
		Node iaMeasureNode = new Node(TemplateId.IA_MEASURE.getTemplateId());

		IaMeasurePerformedValidator validator = new IaMeasurePerformedValidator();
		List<ValidationError> errors = validator.validateSingleNode(iaMeasureNode);
		assertThat("A missing child errors should be present", errors.size(), is(1));
		String errSubstring = IaMeasurePerformedValidator.INCORRECT_CHILDREN_COUNT.substring(0,
				IaMeasurePerformedValidator.INCORRECT_CHILDREN_COUNT.indexOf("."));
		String error = errors.get(0).getErrorText();
		String actualErrSubstring = error.substring(0, error.indexOf("."));
		assertThat("The INCORRECT_CHILDREN_COUNT Error is expected", errSubstring, is(actualErrSubstring));
	}

	/**
	 * Validate a missing child
	 *
	 * @throws Exception
	 */
	@Test
	public void testTooManyChildren() throws Exception {
		Node iaMeasureNode = new Node(TemplateId.IA_MEASURE.getTemplateId());
		Node iaMeasurePerformedNode1 = new Node(iaMeasureNode, TemplateId.ACI_MEASURE_PERFORMED.getTemplateId());
		Node iaMeasurePerformedNode2 = new Node(iaMeasureNode, TemplateId.ACI_MEASURE_PERFORMED.getTemplateId());
		iaMeasureNode.addChildNode(iaMeasurePerformedNode1);
		iaMeasurePerformedNode1.putValue("measurePerformed", "Y");
		iaMeasureNode.addChildNode(iaMeasurePerformedNode2);
		iaMeasurePerformedNode1.putValue("measurePerformed", "N");

		IaMeasurePerformedValidator validator = new IaMeasurePerformedValidator();
		List<ValidationError> errors = validator.validateSingleNode(iaMeasureNode);
		assertThat("A Too Many children errors should be present", errors.size(), is(1));
		String errSubstring = IaMeasurePerformedValidator.INCORRECT_CHILDREN_COUNT.substring(0,
				IaMeasurePerformedValidator.INCORRECT_CHILDREN_COUNT.indexOf("."));
		String error = errors.get(0).getErrorText();
		String actualErrSubstring = error.substring(0, error.indexOf("."));
		assertThat("The INCORRECT_CHILDREN_COUNT Error is expected", errSubstring, is(actualErrSubstring));
	}

}