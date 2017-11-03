package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.ErrorCode;
import gov.cms.qpp.conversion.model.error.correspondence.DetailsErrorEquals;
import org.junit.Before;
import org.junit.Test;

import static com.google.common.truth.Truth.assertWithMessage;

public class CpcNpiTinCombinationValidationTest {

	private CpcNpiTinCombinationValidation cpcValidator;
	private Node multipleTinNpiNode;

	@Before
	public void beforeEachTest() {
		cpcValidator = new CpcNpiTinCombinationValidation();
		multipleTinNpiNode = new Node(TemplateId.QRDA_CATEGORY_III_REPORT_V3);
	}

	@Test
	public void testNoNpiTinCombination() {
		cpcValidator.internalValidateSingleNode(multipleTinNpiNode);

		assertWithMessage("Must validate with the correct error")
				.that(cpcValidator.getDetails()).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.CPC_NPI_TIN_COMBINATION_MISSING_NPI_TIN_COMBINATION);
	}

	@Test
	public void testOneNpiTinCombination() {
		Node tinNpiNode = new Node(TemplateId.NPI_TIN_ID);
		multipleTinNpiNode.addChildNode(tinNpiNode);

		cpcValidator.internalValidateSingleNode(multipleTinNpiNode);

		assertWithMessage("There must be no errors")
				.that(cpcValidator.getDetails()).isEmpty();
	}

	@Test
	public void testMultipleNpiTinCombination() {
		Node tinNpiNode1 = new Node(TemplateId.NPI_TIN_ID);
		Node tinNpiNode2 = new Node(TemplateId.NPI_TIN_ID);
		multipleTinNpiNode.addChildNode(tinNpiNode1);
		multipleTinNpiNode.addChildNode(tinNpiNode2);

		cpcValidator.internalValidateSingleNode(multipleTinNpiNode);

		assertWithMessage("There must be no errors")
				.that(cpcValidator.getDetails()).isEmpty();
	}
}