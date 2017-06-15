package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class AciSectionValidatorTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testNoMeasurePresent() {
		Node aciSectionNode = new Node(TemplateId.ACI_SECTION);
		Node reportingParamNode = new Node(TemplateId.REPORTING_PARAMETERS_ACT);
		aciSectionNode.addChildNode(reportingParamNode);
		aciSectionNode.putValue("category", "aci");

		AciSectionValidator measureVal = new AciSectionValidator();

		List<Detail> errors = measureVal.validateSingleNode(aciSectionNode);

		assertThat("there should be 2 error", errors, hasSize(2));
		assertThat("error should be about missing proportion node", errors.get(0).getMessage(),
			is(AciSectionValidator.ACI_NUMERATOR_DENOMINATOR_NODE_REQUIRED));
		assertThat("error should be about missing required Measure", errors.get(1).getMessage(),
			is(MessageFormat.format(AciSectionValidator.NO_REQUIRED_MEASURE, "ACI_EP_1")));
	}

	@Test
	public void testWrongMeasurePresent() {

		Node aciSectionNode = new Node(TemplateId.ACI_SECTION);
		Node reportingParamNode = new Node(TemplateId.REPORTING_PARAMETERS_ACT);
		aciSectionNode.putValue("category", "aci");
		aciSectionNode.addChildNode(reportingParamNode);

		Node aciNumeratorDenominatorNode = new Node(TemplateId.ACI_NUMERATOR_DENOMINATOR, aciSectionNode);
		aciNumeratorDenominatorNode.putValue("measureId", "TEST_MEASURE");

		aciSectionNode.addChildNode(aciNumeratorDenominatorNode);

		Node aciDenominatorNode = new Node(TemplateId.ACI_DENOMINATOR, aciNumeratorDenominatorNode);
		Node aciNumeratorNode = new Node(TemplateId.ACI_NUMERATOR, aciNumeratorDenominatorNode);

		aciNumeratorDenominatorNode.addChildNode(aciNumeratorNode);
		aciNumeratorDenominatorNode.addChildNode(aciDenominatorNode);

		AciSectionValidator measureval = new AciSectionValidator();
		List<Detail> errors = measureval.validateSingleNode(aciSectionNode);

		assertThat("there should be 1 error", errors, hasSize(1));
		assertThat("error should be about the required measure not present", errors.get(0).getMessage(),
			is(MessageFormat.format(AciSectionValidator.NO_REQUIRED_MEASURE, "ACI_EP_1")));
	}

	@Test
	public void testNoCrossCuttingErrors() {
		Node aciSectionNode = new Node(TemplateId.ACI_SECTION);
		aciSectionNode.putValue("category", "aci");

		AciSectionValidator measureVal = new AciSectionValidator();
		List<Detail> errors = measureVal.validateSameTemplateIdNodes(Arrays.asList(aciSectionNode));

		assertThat("there should be 0 errors", errors, empty());
	}
}