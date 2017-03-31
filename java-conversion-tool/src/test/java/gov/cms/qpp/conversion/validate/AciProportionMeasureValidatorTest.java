package gov.cms.qpp.conversion.validate;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIterableWithSize.iterableWithSize;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.ValidationError;

public class AciProportionMeasureValidatorTest {

	private static final String EXPECTED_TEXT = "At least one Aci Proportion Measure Node is required";
	private static final String EXPECTED_WRONG_PARENT = "This ACI Measure Node should have an ACI Section Node as a parent";
	private static final String EXPECTED_NO_CHILD_NODES = "This ACI Measure Node does not have any child Nodes";
	private static final String EXPECTED_NO_NUMERATOR = "This ACI Measure Node does not contain a Numerator Node child";
	private static final String EXPECTED_NO_DENOMINATOR = "This ACI Measure Node does not contain a Denominator Node child";
	private static final String EXPECTED_TOO_MANY_NUMERATORS = "This ACI Measure Node contains too many Numerator Node children";
	private static final String EXPECTED_TOO_MANY_DENOMINATORS = "This ACI Measure Node contains too many Denominator Node children";
	private static final String EXPECTED_MEASURE_NOT_PRESENT = "The required measure 'ACI_EP_1' is not present in the source file. Please add the ACI measure and try again.";

	// required measures: ACI_EP_1

	@Test
	public void testMeasurePresent() {
		Node clinicalDocumentNode = new Node("2.16.840.1.113883.10.20.27.1.2");
		clinicalDocumentNode.putValue("programName", "mips");
		clinicalDocumentNode.putValue("taxpayerIdentificationNumber", "123456789");
		clinicalDocumentNode.putValue("nationalProviderIdentifier", "2567891421");
		clinicalDocumentNode.putValue("performanceStart", "20170101");
		clinicalDocumentNode.putValue("performanceEnd", "20171231");

		Node aciSectionNode = new Node(clinicalDocumentNode, "2.16.840.1.113883.10.20.27.2.5");
		aciSectionNode.putValue("category", "aci");

		clinicalDocumentNode.addChildNode(aciSectionNode);

		Node aciProportionMeasureNode = new Node(aciSectionNode, "2.16.840.1.113883.10.20.27.3.28");
		aciProportionMeasureNode.putValue("measureId", "ACI_EP_1");

		aciSectionNode.addChildNode(aciProportionMeasureNode);

		Node aciDenominatorNode = new Node(aciProportionMeasureNode, "2.16.840.1.113883.10.20.27.3.32");
		Node aciNumeratorNode = new Node(aciProportionMeasureNode, "2.16.840.1.113883.10.20.27.3.31");

		aciProportionMeasureNode.addChildNode(aciNumeratorNode);
		aciProportionMeasureNode.addChildNode(aciDenominatorNode);

		AciProportionMeasureValidator measureval = new AciProportionMeasureValidator();
		List<ValidationError> errors = measureval.internalValidate(clinicalDocumentNode);

		assertThat("no errors should be present", errors, empty());
	}

	@Test
	public void testMeasureNotPresent() {
		Node aciSectionNode = new Node();
		aciSectionNode.setId("2.16.840.1.113883.10.20.27.2.5");
		aciSectionNode.putValue("category", "aci");

		Node clinicalDocumentNode = new Node();
		clinicalDocumentNode.setId("2.16.840.1.113883.10.20.27.1.2");
		clinicalDocumentNode.putValue("programName", "mips");
		clinicalDocumentNode.putValue("taxpayerIdentificationNumber", "123456789");
		clinicalDocumentNode.putValue("nationalProviderIdentifier", "2567891421");
		clinicalDocumentNode.putValue("performanceStart", "20170101");
		clinicalDocumentNode.putValue("performanceEnd", "20171231");
		clinicalDocumentNode.addChildNode(aciSectionNode);

		AciProportionMeasureValidator measureval = new AciProportionMeasureValidator();
		List<ValidationError> errors = measureval.internalValidate(clinicalDocumentNode);

		assertThat("there should be 1 error", errors, iterableWithSize(1));
		assertThat("error should be about missing Measure node", errors.get(0).getErrorText(), is(EXPECTED_TEXT));
	}

	@Test
	public void testMeasureNodeInvalidParent() {
		Node clinicalDocumentNode = new Node();
		clinicalDocumentNode.setId("2.16.840.1.113883.10.20.27.1.2");
		clinicalDocumentNode.putValue("programName", "mips");
		clinicalDocumentNode.putValue("taxpayerIdentificationNumber", "123456789");
		clinicalDocumentNode.putValue("nationalProviderIdentifier", "2567891421");
		clinicalDocumentNode.putValue("performanceStart", "20170101");
		clinicalDocumentNode.putValue("performanceEnd", "20171231");

		Node aciProportionMeasureNode = new Node(clinicalDocumentNode, "2.16.840.1.113883.10.20.27.3.28");
		aciProportionMeasureNode.putValue("measureId", "ACI_EP_1");

		clinicalDocumentNode.addChildNode(aciProportionMeasureNode);

		Node aciDenominatorNode = new Node(aciProportionMeasureNode, "2.16.840.1.113883.10.20.27.3.32");
		Node aciNumeratorNode = new Node(aciProportionMeasureNode, "2.16.840.1.113883.10.20.27.3.31");

		aciProportionMeasureNode.addChildNode(aciNumeratorNode);
		aciProportionMeasureNode.addChildNode(aciDenominatorNode);

		AciProportionMeasureValidator measureval = new AciProportionMeasureValidator();
		List<ValidationError> errors = measureval.internalValidate(clinicalDocumentNode);

		assertThat("there should be 1 error", errors, iterableWithSize(1));
		assertThat("error should be about invalid parent node", errors.get(0).getErrorText(),
				is(EXPECTED_WRONG_PARENT));
	}

	@Test
	public void testNoChildNodes() {
		Node clinicalDocumentNode = new Node("2.16.840.1.113883.10.20.27.1.2");
		clinicalDocumentNode.putValue("programName", "mips");
		clinicalDocumentNode.putValue("taxpayerIdentificationNumber", "123456789");
		clinicalDocumentNode.putValue("nationalProviderIdentifier", "2567891421");
		clinicalDocumentNode.putValue("performanceStart", "20170101");
		clinicalDocumentNode.putValue("performanceEnd", "20171231");

		Node aciSectionNode = new Node(clinicalDocumentNode, "2.16.840.1.113883.10.20.27.2.5");
		aciSectionNode.putValue("category", "aci");

		clinicalDocumentNode.addChildNode(aciSectionNode);

		Node aciProportionMeasureNode = new Node(aciSectionNode, "2.16.840.1.113883.10.20.27.3.28");
		aciProportionMeasureNode.putValue("measureId", "ACI_EP_1");

		aciSectionNode.addChildNode(aciProportionMeasureNode);

		AciProportionMeasureValidator measureval = new AciProportionMeasureValidator();
		List<ValidationError> errors = measureval.internalValidate(clinicalDocumentNode);

		assertThat("there should be 1 error", errors, iterableWithSize(1));
		assertThat("error should be about no child nodes", errors.get(0).getErrorText(), is(EXPECTED_NO_CHILD_NODES));
	}

	@Test
	public void testNoNumerator() {
		Node clinicalDocumentNode = new Node("2.16.840.1.113883.10.20.27.1.2");
		clinicalDocumentNode.putValue("programName", "mips");
		clinicalDocumentNode.putValue("taxpayerIdentificationNumber", "123456789");
		clinicalDocumentNode.putValue("nationalProviderIdentifier", "2567891421");
		clinicalDocumentNode.putValue("performanceStart", "20170101");
		clinicalDocumentNode.putValue("performanceEnd", "20171231");

		Node aciSectionNode = new Node(clinicalDocumentNode, "2.16.840.1.113883.10.20.27.2.5");
		aciSectionNode.putValue("category", "aci");

		clinicalDocumentNode.addChildNode(aciSectionNode);

		Node aciProportionMeasureNode = new Node(aciSectionNode, "2.16.840.1.113883.10.20.27.3.28");
		aciProportionMeasureNode.putValue("measureId", "ACI_EP_1");

		aciSectionNode.addChildNode(aciProportionMeasureNode);

		Node aciDenominatorNode = new Node(aciProportionMeasureNode, "2.16.840.1.113883.10.20.27.3.32");
		Node aciNumeratorPlaceholder = new Node(aciProportionMeasureNode, "placeholder");

		aciProportionMeasureNode.addChildNode(aciDenominatorNode);
		aciProportionMeasureNode.addChildNode(aciNumeratorPlaceholder);

		AciProportionMeasureValidator measureval = new AciProportionMeasureValidator();
		List<ValidationError> errors = measureval.internalValidate(clinicalDocumentNode);

		assertThat("there should be 1 error", errors, iterableWithSize(1));
		assertThat("error should be about missing Numerator node", errors.get(0).getErrorText(),
				is(EXPECTED_NO_NUMERATOR));
	}

	@Test
	public void testNoDenominator() {
		Node clinicalDocumentNode = new Node("2.16.840.1.113883.10.20.27.1.2");
		clinicalDocumentNode.putValue("programName", "mips");
		clinicalDocumentNode.putValue("taxpayerIdentificationNumber", "123456789");
		clinicalDocumentNode.putValue("nationalProviderIdentifier", "2567891421");
		clinicalDocumentNode.putValue("performanceStart", "20170101");
		clinicalDocumentNode.putValue("performanceEnd", "20171231");

		Node aciSectionNode = new Node(clinicalDocumentNode, "2.16.840.1.113883.10.20.27.2.5");
		aciSectionNode.putValue("category", "aci");

		clinicalDocumentNode.addChildNode(aciSectionNode);

		Node aciProportionMeasureNode = new Node(aciSectionNode, "2.16.840.1.113883.10.20.27.3.28");
		aciProportionMeasureNode.putValue("measureId", "ACI_EP_1");

		aciSectionNode.addChildNode(aciProportionMeasureNode);

		Node aciDenominatorPlaceholder = new Node(aciProportionMeasureNode, "placeholder");
		Node aciNumeratorNode = new Node(aciProportionMeasureNode, "2.16.840.1.113883.10.20.27.3.31");

		aciProportionMeasureNode.addChildNode(aciDenominatorPlaceholder);
		aciProportionMeasureNode.addChildNode(aciNumeratorNode);

		AciProportionMeasureValidator measureval = new AciProportionMeasureValidator();
		List<ValidationError> errors = measureval.internalValidate(clinicalDocumentNode);

		assertThat("there should be 1 error", errors, iterableWithSize(1));
		assertThat("error should be about missing Denominator node", errors.get(0).getErrorText(),
				is(EXPECTED_NO_DENOMINATOR));
	}

	@Test
	public void testTooManyNumerators() {
		Node clinicalDocumentNode = new Node("2.16.840.1.113883.10.20.27.1.2");
		clinicalDocumentNode.putValue("programName", "mips");
		clinicalDocumentNode.putValue("taxpayerIdentificationNumber", "123456789");
		clinicalDocumentNode.putValue("nationalProviderIdentifier", "2567891421");
		clinicalDocumentNode.putValue("performanceStart", "20170101");
		clinicalDocumentNode.putValue("performanceEnd", "20171231");

		Node aciSectionNode = new Node(clinicalDocumentNode, "2.16.840.1.113883.10.20.27.2.5");
		aciSectionNode.putValue("category", "aci");

		clinicalDocumentNode.addChildNode(aciSectionNode);

		Node aciProportionMeasureNode = new Node(aciSectionNode, "2.16.840.1.113883.10.20.27.3.28");
		aciProportionMeasureNode.putValue("measureId", "ACI_EP_1");

		aciSectionNode.addChildNode(aciProportionMeasureNode);

		Node aciDenominatorNode = new Node(aciProportionMeasureNode, "2.16.840.1.113883.10.20.27.3.32");
		Node aciNumeratorNode = new Node(aciProportionMeasureNode, "2.16.840.1.113883.10.20.27.3.31");
		Node aciNumeratorNode2 = new Node(aciProportionMeasureNode, "2.16.840.1.113883.10.20.27.3.31");

		aciProportionMeasureNode.addChildNode(aciDenominatorNode);
		aciProportionMeasureNode.addChildNode(aciNumeratorNode);
		aciProportionMeasureNode.addChildNode(aciNumeratorNode2);

		AciProportionMeasureValidator measureval = new AciProportionMeasureValidator();
		List<ValidationError> errors = measureval.internalValidate(clinicalDocumentNode);

		assertThat("there should be 1 error", errors, iterableWithSize(1));
		assertThat("error should be about too many Numerator nodes", errors.get(0).getErrorText(),
				is(EXPECTED_TOO_MANY_NUMERATORS));
	}

	@Test
	public void testTooManyDenominators() {
		Node clinicalDocumentNode = new Node("2.16.840.1.113883.10.20.27.1.2");
		clinicalDocumentNode.putValue("programName", "mips");
		clinicalDocumentNode.putValue("taxpayerIdentificationNumber", "123456789");
		clinicalDocumentNode.putValue("nationalProviderIdentifier", "2567891421");
		clinicalDocumentNode.putValue("performanceStart", "20170101");
		clinicalDocumentNode.putValue("performanceEnd", "20171231");

		Node aciSectionNode = new Node(clinicalDocumentNode, "2.16.840.1.113883.10.20.27.2.5");
		aciSectionNode.putValue("category", "aci");

		clinicalDocumentNode.addChildNode(aciSectionNode);

		Node aciProportionMeasureNode = new Node(aciSectionNode, "2.16.840.1.113883.10.20.27.3.28");
		aciProportionMeasureNode.putValue("measureId", "ACI_EP_1");

		aciSectionNode.addChildNode(aciProportionMeasureNode);

		Node aciDenominatorNode = new Node(aciProportionMeasureNode, "2.16.840.1.113883.10.20.27.3.32");
		Node aciDenominatorNode2 = new Node(aciProportionMeasureNode, "2.16.840.1.113883.10.20.27.3.32");
		Node aciNumeratorNode = new Node(aciProportionMeasureNode, "2.16.840.1.113883.10.20.27.3.31");

		aciProportionMeasureNode.addChildNode(aciDenominatorNode);
		aciProportionMeasureNode.addChildNode(aciDenominatorNode2);
		aciProportionMeasureNode.addChildNode(aciNumeratorNode);

		AciProportionMeasureValidator measureval = new AciProportionMeasureValidator();
		List<ValidationError> errors = measureval.internalValidate(clinicalDocumentNode);

		assertThat("there should be 1 error", errors, iterableWithSize(1));
		assertThat("error should be about too many Denominator nodes", errors.get(0).getErrorText(),
				is(EXPECTED_TOO_MANY_DENOMINATORS));
	}

	@Test
	public void testWrongMeasurePresent() {
		Node clinicalDocumentNode = new Node("2.16.840.1.113883.10.20.27.1.2");
		clinicalDocumentNode.putValue("programName", "mips");
		clinicalDocumentNode.putValue("taxpayerIdentificationNumber", "123456789");
		clinicalDocumentNode.putValue("nationalProviderIdentifier", "2567891421");
		clinicalDocumentNode.putValue("performanceStart", "20170101");
		clinicalDocumentNode.putValue("performanceEnd", "20171231");

		Node aciSectionNode = new Node(clinicalDocumentNode, "2.16.840.1.113883.10.20.27.2.5");
		aciSectionNode.putValue("category", "aci");

		clinicalDocumentNode.addChildNode(aciSectionNode);

		Node aciProportionMeasureNode = new Node(aciSectionNode, "2.16.840.1.113883.10.20.27.3.28");
		aciProportionMeasureNode.putValue("measureId", "TEST_MEASURE"); // ACI_EP_1
																		// required

		aciSectionNode.addChildNode(aciProportionMeasureNode);

		Node aciDenominatorNode = new Node(aciProportionMeasureNode, "2.16.840.1.113883.10.20.27.3.32");
		Node aciNumeratorNode = new Node(aciProportionMeasureNode, "2.16.840.1.113883.10.20.27.3.31");

		aciProportionMeasureNode.addChildNode(aciNumeratorNode);
		aciProportionMeasureNode.addChildNode(aciDenominatorNode);

		AciProportionMeasureValidator measureval = new AciProportionMeasureValidator();
		List<ValidationError> errors = measureval.internalValidate(clinicalDocumentNode);

		assertThat("there should be 1 error", errors, iterableWithSize(1));
		assertThat("error should be about the required measure not present", errors.get(0).getErrorText(),
				is(EXPECTED_MEASURE_NOT_PRESENT));
	}
}
