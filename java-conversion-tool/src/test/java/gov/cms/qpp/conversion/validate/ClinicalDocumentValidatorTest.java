package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.ValidationError;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIterableWithSize.iterableWithSize;
import static org.junit.Assert.assertThat;

public class ClinicalDocumentValidatorTest {

	private static final String EXPECTED_TEXT = "Clinical Document Node is required";
	private static final String EXPECTED_ONE_ALLOWED = "Only one Clinical Document Node is allowed";
	private static final String EXPECTED_NO_SECTION = "Clinical Document Node must have at least one Aci or Ia Section Node as a child";

	@Test
	public void testClinicalDocumentPresent() {

		Node clinicalDocumentNode = new Node("2.16.840.1.113883.10.20.27.1.2");
		clinicalDocumentNode.putValue("programName", "mips");
		clinicalDocumentNode.putValue("taxpayerIdentificationNumber", "123456789");
		clinicalDocumentNode.putValue("nationalProviderIdentifier", "2567891421");
		clinicalDocumentNode.putValue("performanceStart", "20170101");
		clinicalDocumentNode.putValue("performanceEnd", "20171231");

		Node aciSectionNode = new Node(clinicalDocumentNode, "2.16.840.1.113883.10.20.27.2.5");
		aciSectionNode.putValue("category", "aci");

		clinicalDocumentNode.addChildNode(aciSectionNode);

		ClinicalDocumentValidator cdval = new ClinicalDocumentValidator();
		List<ValidationError> errors = cdval.validateNode(clinicalDocumentNode);

		assertThat("no errors should be present", errors, empty());

	}

	@Test
	public void testClinicalDocumentPresentIa() {

		Node clinicalDocumentNode = new Node("2.16.840.1.113883.10.20.27.1.2");
		clinicalDocumentNode.putValue("programName", "mips");
		clinicalDocumentNode.putValue("taxpayerIdentificationNumber", "123456789");
		clinicalDocumentNode.putValue("nationalProviderIdentifier", "2567891421");
		clinicalDocumentNode.putValue("performanceStart", "20170101");
		clinicalDocumentNode.putValue("performanceEnd", "20171231");

		Node iaSectionNode = new Node(clinicalDocumentNode, "2.16.840.1.113883.10.20.27.2.4");
		iaSectionNode.putValue("category", "ia");

		clinicalDocumentNode.addChildNode(iaSectionNode);

		ClinicalDocumentValidator cdval = new ClinicalDocumentValidator();
		List<ValidationError> errors = cdval.validateNode(clinicalDocumentNode);

		assertThat("no errors should be present", errors, empty());

	}

	@Test
	public void testClinicalDocumentNotPresent() {

		ClinicalDocumentValidator cdval = new ClinicalDocumentValidator();
		List<ValidationError> errors = cdval.validateNodes(Arrays.asList());

		assertThat("there should be one error", errors, iterableWithSize(1));
		assertThat("error should be about missing Clinical Document node", errors.get(0).getErrorText(),
				is(EXPECTED_TEXT));

	}

	@Test
	public void testTooManyClinicalDocumentNodes() {

		Node clinicalDocumentNode = new Node("2.16.840.1.113883.10.20.27.1.2");
		Node clinicalDocumentNode2 = new Node("2.16.840.1.113883.10.20.27.1.2");

		ClinicalDocumentValidator cdval = new ClinicalDocumentValidator();
		List<ValidationError> errors = cdval.validateNodes(Arrays.asList(clinicalDocumentNode, clinicalDocumentNode2));

		assertThat("there should be one error", errors, iterableWithSize(1));
		assertThat("error should be about too many Clinical Document nodes", errors.get(0).getErrorText(),
				is(EXPECTED_ONE_ALLOWED));

	}

	@Test
	public void testNoSections() {

		Node clinicalDocumentNode = new Node("2.16.840.1.113883.10.20.27.1.2");
		clinicalDocumentNode.putValue("programName", "mips");
		clinicalDocumentNode.putValue("taxpayerIdentificationNumber", "123456789");
		clinicalDocumentNode.putValue("nationalProviderIdentifier", "2567891421");
		clinicalDocumentNode.putValue("performanceStart", "20170101");
		clinicalDocumentNode.putValue("performanceEnd", "20171231");

		ClinicalDocumentValidator cdval = new ClinicalDocumentValidator();
		List<ValidationError> errors = cdval.validateNode(clinicalDocumentNode);

		assertThat("there should be one error", errors, iterableWithSize(1));
		assertThat("error should be about missing section node", errors.get(0).getErrorText(), is(EXPECTED_NO_SECTION));

	}

	@Test
	public void testNoSectionsOtherChildren() {

		Node clinicalDocumentNode = new Node("2.16.840.1.113883.10.20.27.1.2");
		clinicalDocumentNode.putValue("programName", "mips");
		clinicalDocumentNode.putValue("taxpayerIdentificationNumber", "123456789");
		clinicalDocumentNode.putValue("nationalProviderIdentifier", "2567891421");
		clinicalDocumentNode.putValue("performanceStart", "20170101");
		clinicalDocumentNode.putValue("performanceEnd", "20171231");

		Node placeholderNode = new Node("placeholder");

		clinicalDocumentNode.addChildNode(placeholderNode);

		ClinicalDocumentValidator cdval = new ClinicalDocumentValidator();
		List<ValidationError> errors = cdval.validateNode(clinicalDocumentNode);

		assertThat("there should be one error", errors, iterableWithSize(1));
		assertThat("error should be about missing section node", errors.get(0).getErrorText(), is(EXPECTED_NO_SECTION));

	}

}
