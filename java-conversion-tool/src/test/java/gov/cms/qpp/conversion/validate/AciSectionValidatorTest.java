package gov.cms.qpp.conversion.validate;

import com.fasterxml.jackson.core.JsonParseException;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.ValidationError;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.Is.isA;
import static org.junit.Assert.assertThat;

public class AciSectionValidatorTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testNoMeasurePresent() {
		Node aciSectionNode = new Node();
		aciSectionNode.setId(TemplateId.ACI_SECTION.getTemplateId());
		aciSectionNode.putValue("category", "aci");

		AciSectionValidator measureVal = new AciSectionValidator();

		List<ValidationError> errors = measureVal.validateSingleNode(aciSectionNode);

		assertThat("there should be 2 error", errors, hasSize(2));
		assertThat("error should be about missing proportion node", errors.get(0).getErrorText(),
			is(AciSectionValidator.ACI_NUMERATOR_DENOMINATOR_NODE_REQUIRED));
		assertThat("error should be about missing required Measure", errors.get(1).getErrorText(),
			is(MessageFormat.format(AciSectionValidator.NO_REQUIRED_MEASURE, "ACI_EP_1")));
	}

	@Test
	public void testWrongMeasurePresent() {

		Node aciSectionNode = new Node(TemplateId.ACI_SECTION.getTemplateId());
		aciSectionNode.putValue("category", "aci");

		Node aciNumeratorDenominatorNode = new Node(aciSectionNode, TemplateId.ACI_NUMERATOR_DENOMINATOR.getTemplateId());
		aciNumeratorDenominatorNode.putValue("measureId", "TEST_MEASURE");

		aciSectionNode.addChildNode(aciNumeratorDenominatorNode);

		Node aciDenominatorNode = new Node(aciNumeratorDenominatorNode, TemplateId.ACI_DENOMINATOR.getTemplateId());
		Node aciNumeratorNode = new Node(aciNumeratorDenominatorNode, TemplateId.ACI_NUMERATOR.getTemplateId());

		aciNumeratorDenominatorNode.addChildNode(aciNumeratorNode);
		aciNumeratorDenominatorNode.addChildNode(aciDenominatorNode);

		AciSectionValidator measureval = new AciSectionValidator();
		List<ValidationError> errors = measureval.validateSingleNode(aciSectionNode);

		assertThat("there should be 1 error", errors, hasSize(1));
		assertThat("error should be about the required measure not present", errors.get(0).getErrorText(),
			is(MessageFormat.format(AciSectionValidator.NO_REQUIRED_MEASURE, "ACI_EP_1")));
	}

	@Test
	public void testNoCrossCuttingErrors() {
		Node aciSectionNode = new Node(TemplateId.ACI_SECTION.getTemplateId());
		aciSectionNode.putValue("category", "aci");

		AciSectionValidator measureVal = new AciSectionValidator();
		List<ValidationError> errors = measureVal.validateSameTemplateIdNodes(Arrays.asList(aciSectionNode));

		assertThat("there should be 0 errors", errors, empty());
	}

	@Test
	public void testGoodMeasureDataFile() {

		AciSectionValidator validator = new AciSectionValidator();
		validator.setMeasureDataFile("measures-data-aci-short.json");
		//no exception thrown
	}

	@Test
	public void testNonExistingMeasureDataFile() {

		//set-up
		thrown.expect(IllegalArgumentException.class);
		thrown.expectCause(isA(IOException.class));

		//execute
		AciSectionValidator validator = new AciSectionValidator();
		validator.setMeasureDataFile("Bogus file name");
	}

	@Test
	public void testBadFormattedMeasureDataFile() {

		//set-up
		thrown.expect(IllegalArgumentException.class);
		thrown.expectCause(isA(JsonParseException.class));

		//execute
		AciSectionValidator validator = new AciSectionValidator();
		validator.setMeasureDataFile("bad_formatted_measures_data.json");
	}
}