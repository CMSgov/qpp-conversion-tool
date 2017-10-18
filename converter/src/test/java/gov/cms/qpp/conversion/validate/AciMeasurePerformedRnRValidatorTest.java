package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.correspondence.DetailsMessageEquals;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static com.google.common.truth.Truth.assertWithMessage;

public class AciMeasurePerformedRnRValidatorTest {

	private AciMeasurePerformedRnRValidator validator;
	private Node aciMeasurePerformedRnRNode;

	@Before
	public void init() {
		validator = new AciMeasurePerformedRnRValidator();

		aciMeasurePerformedRnRNode = new Node(TemplateId.ACI_MEASURE_PERFORMED_REFERENCE_AND_RESULTS);
		aciMeasurePerformedRnRNode.putValue("measureId", "ACI_INFBLO_1");

		Node measurePerformed = new Node(TemplateId.MEASURE_PERFORMED);
		aciMeasurePerformedRnRNode.addChildNode(measurePerformed);
	}

	@Test
	public void testValidateGoodData() throws Exception {
		Set<Detail> errors = run();
		assertWithMessage("no errors should be present")
				.that(errors).isEmpty();
	}

	@Test
	public void testWithNoMeasureId() throws Exception {
		aciMeasurePerformedRnRNode.removeValue("measureId");
		Set<Detail> errors = run();
		assertWithMessage("Should result in a MEASURE_ID_IS_REQUIRED error")
				.that(errors).comparingElementsUsing(DetailsMessageEquals.INSTANCE)
				.containsExactly(AciMeasurePerformedRnRValidator.MEASURE_ID_IS_REQUIRED);
	}

	@Test
	public void testWithNoChildren() throws Exception {
		aciMeasurePerformedRnRNode.getChildNodes().clear();
		Set<Detail> errors = run();
		assertWithMessage("Validation error size should be 1")
				.that(errors).comparingElementsUsing(DetailsMessageEquals.INSTANCE)
				.containsExactly(AciMeasurePerformedRnRValidator.MEASURE_PERFORMED_IS_REQUIRED);
	}

	private Set<Detail> run() {
		return validator.validateSingleNode(aciMeasurePerformedRnRNode);
	}

}