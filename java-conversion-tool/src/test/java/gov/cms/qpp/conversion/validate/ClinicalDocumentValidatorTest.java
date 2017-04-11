package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
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
		Node clinicalDocumentNode = new Node(TemplateId.CLINICAL_DOCUMENT.getTemplateId());
		clinicalDocumentNode.putValue("programName", "mips");
		clinicalDocumentNode.putValue("taxpayerIdentificationNumber", "123456789");
		clinicalDocumentNode.putValue("nationalProviderIdentifier", "2567891421");
		clinicalDocumentNode.putValue("performanceStart", "20170101");
		clinicalDocumentNode.putValue("performanceEnd", "20171231");

		Node aciSectionNode = new Node(clinicalDocumentNode, TemplateId.ACI_SECTION.getTemplateId());
		aciSectionNode.putValue("category", "aci");

		clinicalDocumentNode.addChildNode(aciSectionNode);

		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		List<ValidationError> errors = validator.validateSingleNode(clinicalDocumentNode);

		assertThat("no errors should be present", errors, empty());
	}

	@Test
	public void testClinicalDocumentPresentIa() {
		Node clinicalDocumentNode = new Node(TemplateId.CLINICAL_DOCUMENT.getTemplateId());
		clinicalDocumentNode.putValue("programName", "mips");
		clinicalDocumentNode.putValue("taxpayerIdentificationNumber", "123456789");
		clinicalDocumentNode.putValue("nationalProviderIdentifier", "2567891421");
		clinicalDocumentNode.putValue("performanceStart", "20170101");
		clinicalDocumentNode.putValue("performanceEnd", "20171231");

		Node iaSectionNode = new Node(clinicalDocumentNode, TemplateId.IA_SECTION.getTemplateId());
		iaSectionNode.putValue("category", "ia");

		clinicalDocumentNode.addChildNode(iaSectionNode);

		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		List<ValidationError> errors = validator.validateSingleNode(clinicalDocumentNode);

		assertThat("no errors should be present", errors, empty());
	}

	@Test
	public void testClinicalDocumentNotPresent() {

		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		List<ValidationError> errors = validator.validateSameTemplateIdNodes(Arrays.asList());

		assertThat("there should be one error", errors, iterableWithSize(1));
		assertThat("error should be about missing Clinical Document node", errors.get(0).getErrorText(),
				is(EXPECTED_TEXT));
	}

	@Test
	public void testTooManyClinicalDocumentNodes() {
		Node clinicalDocumentNode = new Node(TemplateId.CLINICAL_DOCUMENT.getTemplateId());
		Node clinicalDocumentNode2 = new Node(TemplateId.CLINICAL_DOCUMENT.getTemplateId());

		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		List<ValidationError> errors = validator.validateSameTemplateIdNodes(Arrays.asList(clinicalDocumentNode, clinicalDocumentNode2));

		assertThat("there should be one error", errors, iterableWithSize(1));
		assertThat("error should be about too many Clinical Document nodes", errors.get(0).getErrorText(),
				is(EXPECTED_ONE_ALLOWED));
	}

	@Test
	public void testNoSections() {
		Node clinicalDocumentNode = new Node(TemplateId.CLINICAL_DOCUMENT.getTemplateId());
		clinicalDocumentNode.putValue("programName", "mips");
		clinicalDocumentNode.putValue("taxpayerIdentificationNumber", "123456789");
		clinicalDocumentNode.putValue("nationalProviderIdentifier", "2567891421");
		clinicalDocumentNode.putValue("performanceStart", "20170101");
		clinicalDocumentNode.putValue("performanceEnd", "20171231");

		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		List<ValidationError> errors = validator.validateSingleNode(clinicalDocumentNode);

		assertThat("there should be one error", errors, iterableWithSize(1));
		assertThat("error should be about missing section node", errors.get(0).getErrorText(), is(EXPECTED_NO_SECTION));
	}

	@Test
	public void testNoSectionsOtherChildren() {
		Node clinicalDocumentNode = new Node(TemplateId.CLINICAL_DOCUMENT.getTemplateId());
		clinicalDocumentNode.putValue("programName", "mips");
		clinicalDocumentNode.putValue("taxpayerIdentificationNumber", "123456789");
		clinicalDocumentNode.putValue("nationalProviderIdentifier", "2567891421");
		clinicalDocumentNode.putValue("performanceStart", "20170101");
		clinicalDocumentNode.putValue("performanceEnd", "20171231");

		Node placeholderNode = new Node("placeholder");

		clinicalDocumentNode.addChildNode(placeholderNode);

		ClinicalDocumentValidator validator = new ClinicalDocumentValidator();
		List<ValidationError> errors = validator.validateSingleNode(clinicalDocumentNode);

		assertThat("there should be one error", errors, iterableWithSize(1));
		assertThat("error should be about missing section node", errors.get(0).getErrorText(), is(EXPECTED_NO_SECTION));
	}
}
