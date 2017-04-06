package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.NodeType;
import gov.cms.qpp.conversion.model.ValidationError;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.Assert.*;

/**
 * Class to test the AciNumeratorValidator
 */
public class AciNumeratorValidatorTest {
	@Test
	public void internalValidateSingleNode() throws Exception {
		Node aciNumeratorNode = new Node( NodeType.ACI_NUMERATOR.getTemplateId());
		Node aggregateCountNode = new Node( NodeType.ACI_AGGREGATE_COUNT.getTemplateId());

		aggregateCountNode.putValue("value","100");
		aciNumeratorNode.addChildNode(aggregateCountNode);

		AciNumeratorValidator validator = new AciNumeratorValidator();
		List<ValidationError> errors = validator.validateSingleNode(aciNumeratorNode);
		errors.addAll(validator.validateSameTemplateIdNodes(Arrays.asList(aciNumeratorNode)));

		assertThat("no errors should be present", errors, empty());

	}

	@Test
	public void internalValidateSameTemplateIdNodes() throws Exception {
	}

}