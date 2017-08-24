package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import org.junit.Before;
import org.junit.Test;

import static gov.cms.qpp.conversion.model.error.ValidationErrorMatcher.hasValidationErrorsIgnoringPath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

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

		assertThat("Must validate with the correct error",
			cpcValidator.getDetails(),
			hasValidationErrorsIgnoringPath(CpcNpiTinCombinationValidation.AT_LEAST_ONE_NPI_TIN_COMBINATION));
	}

	@Test
	public void testOneNpiTinCombination() {
		Node tinNpiNode = new Node(TemplateId.NPI_TIN_ID);
		multipleTinNpiNode.addChildNode(tinNpiNode);

		cpcValidator.internalValidateSingleNode(multipleTinNpiNode);

		assertThat("There must be no errors",
			cpcValidator.getDetails(),
			hasSize(0));
	}

	@Test
	public void testMultipleNpiTinCombination() {
		Node tinNpiNode1 = new Node(TemplateId.NPI_TIN_ID);
		Node tinNpiNode2 = new Node(TemplateId.NPI_TIN_ID);
		multipleTinNpiNode.addChildNode(tinNpiNode1);
		multipleTinNpiNode.addChildNode(tinNpiNode2);

		cpcValidator.internalValidateSingleNode(multipleTinNpiNode);

		assertThat("There must be no errors",
			cpcValidator.getDetails(),
			hasSize(0));
	}
}