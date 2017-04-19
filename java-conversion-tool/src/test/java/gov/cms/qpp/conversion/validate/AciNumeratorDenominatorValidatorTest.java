package gov.cms.qpp.conversion.validate;

import com.fasterxml.jackson.core.JsonParseException;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.ValidationError;
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

public class AciNumeratorDenominatorValidatorTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testMeasurePresent() {
		Node clinicalDocumentNode = new Node(TemplateId.CLINICAL_DOCUMENT.getTemplateId());
		clinicalDocumentNode.putValue("programName", "mips");
		clinicalDocumentNode.putValue("taxpayerIdentificationNumber", "123456789");
		clinicalDocumentNode.putValue("nationalProviderIdentifier", "2567891421");
		clinicalDocumentNode.putValue("performanceStart", "20170101");
		clinicalDocumentNode.putValue("performanceEnd", "20171231");

		Node aciSectionNode = new Node(clinicalDocumentNode, TemplateId.ACI_SECTION.getTemplateId());
		aciSectionNode.putValue("category", "aci");

		clinicalDocumentNode.addChildNode(aciSectionNode);

		Node aciNumeratorDenominatorNode = new Node(aciSectionNode, TemplateId.ACI_NUMERATOR_DENOMINATOR.getTemplateId());
		aciNumeratorDenominatorNode.putValue("measureId", "ACI_EP_1");

		aciSectionNode.addChildNode(aciNumeratorDenominatorNode);

		Node aciDenominatorNode = new Node(aciNumeratorDenominatorNode, TemplateId.ACI_DENOMINATOR.getTemplateId());
		Node aciNumeratorNode = new Node(aciNumeratorDenominatorNode, TemplateId.ACI_NUMERATOR.getTemplateId());

		aciNumeratorDenominatorNode.addChildNode(aciNumeratorNode);
		aciNumeratorDenominatorNode.addChildNode(aciDenominatorNode);

		AciNumeratorDenominatorValidator measureVal = new AciNumeratorDenominatorValidator();
		List<ValidationError> errors = measureVal.validateSingleNode(aciNumeratorDenominatorNode);
		errors.addAll(measureVal.validateSameTemplateIdNodes(Arrays.asList(aciNumeratorDenominatorNode)));

		assertThat("no errors should be present", errors, empty());
	}

	@Test
	public void testRequiredMeasureNotPresent() {
		Node aciSectionNode = new Node();
		aciSectionNode.setId(TemplateId.ACI_SECTION.getTemplateId());
		aciSectionNode.putValue("category", "aci");

		Node clinicalDocumentNode = new Node();
		clinicalDocumentNode.setId(TemplateId.CLINICAL_DOCUMENT.getTemplateId());
		clinicalDocumentNode.putValue("programName", "mips");
		clinicalDocumentNode.putValue("taxpayerIdentificationNumber", "123456789");
		clinicalDocumentNode.putValue("nationalProviderIdentifier", "2567891421");
		clinicalDocumentNode.putValue("performanceStart", "20170101");
		clinicalDocumentNode.putValue("performanceEnd", "20171231");
		clinicalDocumentNode.addChildNode(aciSectionNode);

		AciNumeratorDenominatorValidator measureVal = new AciNumeratorDenominatorValidator();

		List<ValidationError> errors = measureVal.validateSameTemplateIdNodes(Arrays.asList());

		assertThat("there should be 2 error", errors, hasSize(2));
		assertThat("error should be about missing proportion node", errors.get(0).getErrorText(),
		           is(AciNumeratorDenominatorValidator.ACI_NUMERATOR_DENOMINATOR_NODE_REQUIRED));
		assertThat("error should be about missing required Measure", errors.get(1).getErrorText(),
		           is(MessageFormat.format(AciNumeratorDenominatorValidator.NO_REQUIRED_MEASURE, "ACI_EP_1")));
	}

	@Test
	public void testNumerateDenominatorMissingMeasureId() {
		Node aciSectionNode = new Node(TemplateId.ACI_SECTION.getTemplateId());
		Node aciNumeratorDenominatorNode = new Node(aciSectionNode, TemplateId.ACI_NUMERATOR_DENOMINATOR.getTemplateId());
		Node aciDenominatorNode = new Node(aciNumeratorDenominatorNode, TemplateId.ACI_DENOMINATOR.getTemplateId());
		Node aciNumeratorNode = new Node(aciNumeratorDenominatorNode, TemplateId.ACI_NUMERATOR.getTemplateId());

		aciNumeratorDenominatorNode.addChildNode(aciNumeratorNode);
		aciNumeratorDenominatorNode.addChildNode(aciDenominatorNode);

		aciSectionNode.putValue("category", "aci");
		aciSectionNode.addChildNode(aciNumeratorDenominatorNode);

		AciNumeratorDenominatorValidator measureVal = new AciNumeratorDenominatorValidator();

		List<ValidationError> errors = measureVal.validateSingleNode(aciNumeratorDenominatorNode);

		assertThat("there should be 1 error", errors, hasSize(1));
		assertThat("error should be about missing numerator denominator measure name", errors.get(0).getErrorText(),
				is(AciNumeratorDenominatorValidator.NO_MEASURE_NAME));
	}

	@Test
	public void testMeasureNodeInvalidParent() {
		Node clinicalDocumentNode = new Node();
		clinicalDocumentNode.setId(TemplateId.CLINICAL_DOCUMENT.getTemplateId());
		clinicalDocumentNode.putValue("programName", "mips");
		clinicalDocumentNode.putValue("taxpayerIdentificationNumber", "123456789");
		clinicalDocumentNode.putValue("nationalProviderIdentifier", "2567891421");
		clinicalDocumentNode.putValue("performanceStart", "20170101");
		clinicalDocumentNode.putValue("performanceEnd", "20171231");

		Node aciNumeratorDenominatorNode = new Node(clinicalDocumentNode, TemplateId.ACI_NUMERATOR_DENOMINATOR.getTemplateId());
		aciNumeratorDenominatorNode.putValue("measureId", "ACI_EP_1");

		clinicalDocumentNode.addChildNode(aciNumeratorDenominatorNode);

		Node aciDenominatorNode = new Node(aciNumeratorDenominatorNode, TemplateId.ACI_DENOMINATOR.getTemplateId());
		Node aciNumeratorNode = new Node(aciNumeratorDenominatorNode, TemplateId.ACI_NUMERATOR.getTemplateId());

		aciNumeratorDenominatorNode.addChildNode(aciNumeratorNode);
		aciNumeratorDenominatorNode.addChildNode(aciDenominatorNode);

		AciNumeratorDenominatorValidator measureVal = new AciNumeratorDenominatorValidator();
		List<ValidationError> errors = measureVal.validateSingleNode(aciNumeratorDenominatorNode);

		assertThat("there should be 1 error", errors, hasSize(1));
		assertThat("error should be about invalid parent node", errors.get(0).getErrorText(),
				is(AciNumeratorDenominatorValidator.NO_PARENT_SECTION));
	}

	@Test
	public void testNoChildNodes() {

		Node aciSectionNode = new Node(TemplateId.ACI_SECTION.getTemplateId());
		aciSectionNode.putValue("category", "aci");

		Node aciNumeratorDenominatorNode = new Node(aciSectionNode, TemplateId.ACI_NUMERATOR_DENOMINATOR.getTemplateId());
		aciNumeratorDenominatorNode.putValue("measureId", "ACI_EP_1");

		aciSectionNode.addChildNode(aciNumeratorDenominatorNode);

		AciNumeratorDenominatorValidator measureval = new AciNumeratorDenominatorValidator();
		List<ValidationError> errors = measureval.validateSingleNode(aciNumeratorDenominatorNode);

		assertThat("there should be 1 error", errors, hasSize(1));
		assertThat("error should be about no child nodes", errors.get(0).getErrorText(), is(AciNumeratorDenominatorValidator.NO_CHILDREN));
	}

	@Test
	public void testNoNumerator() {

		Node aciSectionNode = new Node(TemplateId.ACI_SECTION.getTemplateId());
		aciSectionNode.putValue("category", "aci");

		Node aciNumeratorDenominatorNode = new Node(aciSectionNode, TemplateId.ACI_NUMERATOR_DENOMINATOR.getTemplateId());
		aciNumeratorDenominatorNode.putValue("measureId", "ACI_EP_1");

		aciSectionNode.addChildNode(aciNumeratorDenominatorNode);

		Node aciDenominatorNode = new Node(aciNumeratorDenominatorNode, TemplateId.ACI_DENOMINATOR.getTemplateId());
		Node aciNumeratorPlaceholder = new Node(aciNumeratorDenominatorNode, TemplateId.PLACEHOLDER.getTemplateId());

		aciNumeratorDenominatorNode.addChildNode(aciDenominatorNode);
		aciNumeratorDenominatorNode.addChildNode(aciNumeratorPlaceholder);

		AciNumeratorDenominatorValidator measureval = new AciNumeratorDenominatorValidator();
		List<ValidationError> errors = measureval.validateSingleNode(aciNumeratorDenominatorNode);

		assertThat("there should be 1 error", errors, hasSize(1));
		assertThat("error should be about missing Numerator node", errors.get(0).getErrorText(),
				is(AciNumeratorDenominatorValidator.NO_NUMERATOR));
	}

	@Test
	public void testNoDenominator() {

		Node aciSectionNode = new Node(TemplateId.ACI_SECTION.getTemplateId());
		aciSectionNode.putValue("category", "aci");

		Node aciNumeratorDenominatorNode = new Node(aciSectionNode, TemplateId.ACI_NUMERATOR_DENOMINATOR.getTemplateId());
		aciNumeratorDenominatorNode.putValue("measureId", "ACI_EP_1");

		aciSectionNode.addChildNode(aciNumeratorDenominatorNode);

		Node aciDenominatorPlaceholder = new Node(aciNumeratorDenominatorNode, TemplateId.PLACEHOLDER.getTemplateId());
		Node aciNumeratorNode = new Node(aciNumeratorDenominatorNode, TemplateId.ACI_NUMERATOR.getTemplateId());

		aciNumeratorDenominatorNode.addChildNode(aciDenominatorPlaceholder);
		aciNumeratorDenominatorNode.addChildNode(aciNumeratorNode);

		AciNumeratorDenominatorValidator measureval = new AciNumeratorDenominatorValidator();
		List<ValidationError> errors = measureval.validateSingleNode(aciNumeratorDenominatorNode);

		assertThat("there should be 1 error", errors, hasSize(1));
		assertThat("error should be about missing Denominator node", errors.get(0).getErrorText(),
				is(AciNumeratorDenominatorValidator.NO_DENOMINATOR));
	}

	@Test
	public void testTooManyNumerators() {

		Node aciSectionNode = new Node(TemplateId.ACI_SECTION.getTemplateId());
		aciSectionNode.putValue("category", "aci");

		Node aciNumeratorDenominatorNode = new Node(aciSectionNode, TemplateId.ACI_NUMERATOR_DENOMINATOR.getTemplateId());
		aciNumeratorDenominatorNode.putValue("measureId", "ACI_EP_1");

		aciSectionNode.addChildNode(aciNumeratorDenominatorNode);

		Node aciDenominatorNode = new Node(aciNumeratorDenominatorNode, TemplateId.ACI_DENOMINATOR.getTemplateId());
		Node aciNumeratorNode = new Node(aciNumeratorDenominatorNode, TemplateId.ACI_NUMERATOR.getTemplateId());
		Node aciNumeratorNode2 = new Node(aciNumeratorDenominatorNode, TemplateId.ACI_NUMERATOR.getTemplateId());

		aciNumeratorDenominatorNode.addChildNode(aciDenominatorNode);
		aciNumeratorDenominatorNode.addChildNode(aciNumeratorNode);
		aciNumeratorDenominatorNode.addChildNode(aciNumeratorNode2);

		AciNumeratorDenominatorValidator measureval = new AciNumeratorDenominatorValidator();
		List<ValidationError> errors = measureval.validateSingleNode(aciNumeratorDenominatorNode);

		assertThat("there should be 1 error", errors, hasSize(1));
		assertThat("error should be about too many Numerator nodes", errors.get(0).getErrorText(),
				is(AciNumeratorDenominatorValidator.TOO_MANY_NUMERATORS));
	}

	@Test
	public void testTooManyDenominators() {

		Node aciSectionNode = new Node(TemplateId.ACI_SECTION.getTemplateId());
		aciSectionNode.putValue("category", "aci");

		Node aciNumeratorDenominatorNode = new Node(aciSectionNode, TemplateId.ACI_NUMERATOR_DENOMINATOR.getTemplateId());
		aciNumeratorDenominatorNode.putValue("measureId", "ACI_EP_1");

		aciSectionNode.addChildNode(aciNumeratorDenominatorNode);

		Node aciDenominatorNode = new Node(aciNumeratorDenominatorNode, TemplateId.ACI_DENOMINATOR.getTemplateId());
		Node aciDenominatorNode2 = new Node(aciNumeratorDenominatorNode, TemplateId.ACI_DENOMINATOR.getTemplateId());
		Node aciNumeratorNode = new Node(aciNumeratorDenominatorNode, TemplateId.ACI_NUMERATOR.getTemplateId());

		aciNumeratorDenominatorNode.addChildNode(aciDenominatorNode);
		aciNumeratorDenominatorNode.addChildNode(aciDenominatorNode2);
		aciNumeratorDenominatorNode.addChildNode(aciNumeratorNode);

		AciNumeratorDenominatorValidator measureval = new AciNumeratorDenominatorValidator();
		List<ValidationError> errors = measureval.validateSingleNode(aciNumeratorDenominatorNode);

		assertThat("there should be 1 error", errors, hasSize(1));
		assertThat("error should be about too many Denominator nodes", errors.get(0).getErrorText(),
				is(AciNumeratorDenominatorValidator.TOO_MANY_DENOMINATORS));
	}

	@Test
	public void testWrongMeasurePresent() {
		Node clinicalDocumentNode = new Node(TemplateId.CLINICAL_DOCUMENT.getTemplateId());
		clinicalDocumentNode.putValue("programName", "mips");
		clinicalDocumentNode.putValue("taxpayerIdentificationNumber", "123456789");
		clinicalDocumentNode.putValue("nationalProviderIdentifier", "2567891421");
		clinicalDocumentNode.putValue("performanceStart", "20170101");
		clinicalDocumentNode.putValue("performanceEnd", "20171231");

		Node aciSectionNode = new Node(clinicalDocumentNode, TemplateId.ACI_SECTION.getTemplateId());
		aciSectionNode.putValue("category", "aci");

		clinicalDocumentNode.addChildNode(aciSectionNode);

		Node aciNumeratorDenominatorNode = new Node(aciSectionNode, TemplateId.ACI_NUMERATOR_DENOMINATOR.getTemplateId());
		aciNumeratorDenominatorNode.putValue("measureId", "TEST_MEASURE"); // ACI_EP_1
																		// required

		aciSectionNode.addChildNode(aciNumeratorDenominatorNode);

		Node aciDenominatorNode = new Node(aciNumeratorDenominatorNode, TemplateId.ACI_DENOMINATOR.getTemplateId());
		Node aciNumeratorNode = new Node(aciNumeratorDenominatorNode, TemplateId.ACI_NUMERATOR.getTemplateId());

		aciNumeratorDenominatorNode.addChildNode(aciNumeratorNode);
		aciNumeratorDenominatorNode.addChildNode(aciDenominatorNode);

		AciNumeratorDenominatorValidator measureval = new AciNumeratorDenominatorValidator();
		measureval.validateSameTemplateIdNodes(Arrays.asList(aciNumeratorDenominatorNode));
		List<ValidationError> errors = measureval.getValidationErrors();

		assertThat("there should be 1 error", errors, hasSize(1));
		assertThat("error should be about the required measure not present", errors.get(0).getErrorText(),
				is(MessageFormat.format(AciNumeratorDenominatorValidator.NO_REQUIRED_MEASURE, "ACI_EP_1")));
	}

	@Test
	public void testGoodMeasureDataFile() {

		AciNumeratorDenominatorValidator validator = new AciNumeratorDenominatorValidator();
		validator.setMeasureDataFile("measures-data-aci-short.json");
		//no exception thrown
	}

	@Test
	public void testNonExistingMeasureDataFile() {

		//set-up
		thrown.expect(IllegalArgumentException.class);
		thrown.expectCause(isA(IOException.class));

		//execute
		AciNumeratorDenominatorValidator validator = new AciNumeratorDenominatorValidator();
		validator.setMeasureDataFile("Bogus file name");
	}

	@Test
	public void testBadFormattedMeasureDataFile() {

		//set-up
		thrown.expect(IllegalArgumentException.class);
		thrown.expectCause(isA(JsonParseException.class));

		//execute
		AciNumeratorDenominatorValidator validator = new AciNumeratorDenominatorValidator();
		validator.setMeasureDataFile("bad_formatted_measures_data.json");
	}
}
