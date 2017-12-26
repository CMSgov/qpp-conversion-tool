package gov.cms.qpp.conversion.validate;

import static com.google.common.truth.Truth.assertWithMessage;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ErrorCode;
import gov.cms.qpp.conversion.model.error.correspondence.DetailsErrorEquals;

class AciSectionValidatorTest {

	private static final String VALID_ACI_MEASURE = "ACI_EP_1";
	private Node reportingParamNode;
	private Node aciNumeratorDenominatorNode;
	private Node measureNode;
	private Node aciSectionNode;

	@BeforeEach
	void setUpAciSectionNode() {
		reportingParamNode = new Node(TemplateId.REPORTING_PARAMETERS_ACT);
		aciNumeratorDenominatorNode = new Node(TemplateId.ACI_NUMERATOR_DENOMINATOR);
		measureNode = new Node(TemplateId.MEASURE_PERFORMED);
		measureNode.putValue("measureId", VALID_ACI_MEASURE);

		aciSectionNode = new Node(TemplateId.ACI_SECTION);
		aciSectionNode.putValue("category", "aci");
	}

	@Test
	void testNoReportingParamPresent() {
		aciSectionNode.addChildNodes(aciNumeratorDenominatorNode, measureNode);

		AciSectionValidator aciSectionValidator = new AciSectionValidator();

		Set<Detail> errors = aciSectionValidator.validateSingleNode(aciSectionNode);

		assertWithMessage("error should be about missing proportion node")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.ACI_SECTION_MISSING_REPORTING_PARAMETER_ACT);
	}

	@Test
	void testTooManyReportingParams() {
		Node invalidReportingParamNode = new Node(TemplateId.REPORTING_PARAMETERS_ACT);
		aciSectionNode.addChildNodes(reportingParamNode, invalidReportingParamNode, aciNumeratorDenominatorNode, measureNode);

		AciSectionValidator aciSectionValidator = new AciSectionValidator();

		Set<Detail> errors = aciSectionValidator.validateSingleNode(aciSectionNode);

		assertWithMessage("error should be about missing required Measure")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.ACI_SECTION_MISSING_REPORTING_PARAMETER_ACT);
	}
}