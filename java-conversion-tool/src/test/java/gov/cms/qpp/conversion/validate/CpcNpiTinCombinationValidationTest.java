package gov.cms.qpp.conversion.validate;

import static gov.cms.qpp.conversion.model.error.ValidationErrorMatcher.hasValidationErrorsIgnoringPath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;

import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.PathQrdaSource;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.TransformException;

public class CpcNpiTinCombinationValidationTest {

	private static final Path DIR =
			Paths.get("src/test/resources/cpc_plus/");
	private static final Path TOO_LONG =
			DIR.resolve("CPCPlus_TINs_IncorrectDigits_TooLong_SampleQRDA-III.xml");
	private static final Path TOO_SHORT =
			DIR.resolve("CPCPlus_TINs_IncorrectDigits_TooShort_SampleQRDA-III.xml");

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

	@Test(expected = TransformException.class)
	public void testTinTooShort() {
		Converter converter = new Converter(new PathQrdaSource(TOO_SHORT));
		converter.transform();
	}

	@Test(expected = TransformException.class)
	public void testTinTooLong() {
		Converter converter = new Converter(new PathQrdaSource(TOO_LONG));
		converter.transform();
	}

}