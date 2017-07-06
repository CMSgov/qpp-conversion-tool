package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class AciSectionValidatorTest {
	private static final String VALID_ACI_MEASURE = "ACI_EP_1";
	private Node reportingParamNode;
	private Node aciNumeratorDenominatorNode;
	private Node measureNode;
	private Node aciSectionNode;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setUpAciSectionNode() {
		reportingParamNode = new Node(TemplateId.REPORTING_PARAMETERS_ACT);
		aciNumeratorDenominatorNode = new Node(TemplateId.ACI_NUMERATOR_DENOMINATOR);
		measureNode = new Node(TemplateId.MEASURE_PERFORMED);
		measureNode.putValue("measureId", VALID_ACI_MEASURE);

		aciSectionNode = new Node(TemplateId.ACI_SECTION);
		aciSectionNode.putValue("category", "aci");
	}

	@Test
	public void testNoReportingParamPresent() {
		aciSectionNode.addChildNodes(aciNumeratorDenominatorNode, measureNode);

		AciSectionValidator aciSectionValidator = new AciSectionValidator();

		Set<Detail> errors = aciSectionValidator.validateSingleNode(aciSectionNode);

		assertThat("error should be about missing proportion node",
				errors.iterator().next().getMessage(),
				is(AciSectionValidator.MINIMUM_REPORTING_PARAM_REQUIREMENT_ERROR));
	}

	@Test
	public void testTooManyReportingParams() {
		Node invalidReportingParamNode = new Node(TemplateId.REPORTING_PARAMETERS_ACT);
		aciSectionNode.addChildNodes(reportingParamNode, invalidReportingParamNode, aciNumeratorDenominatorNode, measureNode);

		AciSectionValidator aciSectionValidator = new AciSectionValidator();

		Set<Detail> errors = aciSectionValidator.validateSingleNode(aciSectionNode);

		assertThat("error should be about missing required Measure",
				errors.iterator().next().getMessage(),
				is(AciSectionValidator.MINIMUM_REPORTING_PARAM_REQUIREMENT_ERROR));
	}
}