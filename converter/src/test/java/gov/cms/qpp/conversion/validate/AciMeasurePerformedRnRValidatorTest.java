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

class AciMeasurePerformedRnRValidatorTest {

	private AciMeasurePerformedRnRValidator validator;
	private Node aciMeasurePerformedRnRNode;

	@BeforeEach
	void init() {
		validator = new AciMeasurePerformedRnRValidator();

		aciMeasurePerformedRnRNode = new Node(TemplateId.PI_MEASURE_PERFORMED_REFERENCE_AND_RESULTS);
		aciMeasurePerformedRnRNode.putValue("measureId", "ACI_INFBLO_1");

		Node measurePerformed = new Node(TemplateId.MEASURE_PERFORMED);
		aciMeasurePerformedRnRNode.addChildNode(measurePerformed);
	}

	@Test
	void testValidateGoodData() {
		Set<Detail> errors = run();
		assertWithMessage("no errors should be present")
				.that(errors).isEmpty();
	}

	@Test
	void testWithNoMeasureId(){
		aciMeasurePerformedRnRNode.removeValue("measureId");
		Set<Detail> errors = run();
		assertWithMessage("Should result in a MEASURE_ID_IS_REQUIRED error")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.PI_MEASURE_PERFORMED_RNR_MEASURE_ID_NOT_SINGULAR);
	}

	@Test
	void testWithNoChildren() {
		aciMeasurePerformedRnRNode.getChildNodes().clear();
		Set<Detail> errors = run();
		assertWithMessage("Validation error size should be 2")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.PI_MEASURE_PERFORMED_RNR_MEASURE_PERFORMED_EXACT);
	}

	private Set<Detail> run() {
		return validator.validateSingleNode(aciMeasurePerformedRnRNode);
	}

}