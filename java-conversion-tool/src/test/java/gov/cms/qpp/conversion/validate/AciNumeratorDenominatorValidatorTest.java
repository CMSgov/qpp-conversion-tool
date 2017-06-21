package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class AciNumeratorDenominatorValidatorTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testMeasurePresent() {
		Node clinicalDocumentNode = new Node(TemplateId.CLINICAL_DOCUMENT);
		clinicalDocumentNode.putValue("programName", "mips");
		clinicalDocumentNode.putValue("taxpayerIdentificationNumber", "123456789");
		clinicalDocumentNode.putValue("nationalProviderIdentifier", "2567891421");
		clinicalDocumentNode.putValue("performanceStart", "20170101");
		clinicalDocumentNode.putValue("performanceEnd", "20171231");

		Node aciSectionNode = new Node(TemplateId.ACI_SECTION, clinicalDocumentNode);
		aciSectionNode.putValue("category", "aci");

		clinicalDocumentNode.addChildNode(aciSectionNode);

		Node aciNumeratorDenominatorNode = new Node(TemplateId.ACI_NUMERATOR_DENOMINATOR, aciSectionNode);
		aciNumeratorDenominatorNode.putValue("measureId", "ACI_EP_1");

		aciSectionNode.addChildNode(aciNumeratorDenominatorNode);

		Node aciDenominatorNode = new Node(TemplateId.ACI_DENOMINATOR, aciNumeratorDenominatorNode);
		Node aciNumeratorNode = new Node(TemplateId.ACI_NUMERATOR, aciNumeratorDenominatorNode);

		aciNumeratorDenominatorNode.addChildNode(aciNumeratorNode);
		aciNumeratorDenominatorNode.addChildNode(aciDenominatorNode);

		AciNumeratorDenominatorValidator measureVal = new AciNumeratorDenominatorValidator();
		List<Detail> errors = measureVal.validateSingleNode(aciNumeratorDenominatorNode);

		assertThat("no errors should be present", errors, empty());
	}

	@Test
	public void testNumerateDenominatorMissingMeasureId() {
		Node aciSectionNode = new Node(TemplateId.ACI_SECTION);
		Node aciNumeratorDenominatorNode = new Node(TemplateId.ACI_NUMERATOR_DENOMINATOR, aciSectionNode);
		Node aciDenominatorNode = new Node(TemplateId.ACI_DENOMINATOR, aciNumeratorDenominatorNode);
		Node aciNumeratorNode = new Node(TemplateId.ACI_NUMERATOR, aciNumeratorDenominatorNode);

		aciNumeratorDenominatorNode.addChildNode(aciNumeratorNode);
		aciNumeratorDenominatorNode.addChildNode(aciDenominatorNode);

		aciSectionNode.putValue("category", "aci");
		aciSectionNode.addChildNode(aciNumeratorDenominatorNode);

		AciNumeratorDenominatorValidator measureVal = new AciNumeratorDenominatorValidator();

		List<Detail> errors = measureVal.validateSingleNode(aciNumeratorDenominatorNode);

		assertThat("there should be 1 error", errors, hasSize(1));
		assertThat("error should be about missing numerator denominator measure name", errors.get(0).getMessage(),
				is(AciNumeratorDenominatorValidator.NO_MEASURE_NAME));
	}

	@Test
	public void testMeasureNodeInvalidParent() {
		Node clinicalDocumentNode = new Node(TemplateId.CLINICAL_DOCUMENT);
		clinicalDocumentNode.putValue("programName", "mips");
		clinicalDocumentNode.putValue("taxpayerIdentificationNumber", "123456789");
		clinicalDocumentNode.putValue("nationalProviderIdentifier", "2567891421");
		clinicalDocumentNode.putValue("performanceStart", "20170101");
		clinicalDocumentNode.putValue("performanceEnd", "20171231");

		Node aciNumeratorDenominatorNode = new Node(TemplateId.ACI_NUMERATOR_DENOMINATOR, clinicalDocumentNode);
		aciNumeratorDenominatorNode.putValue("measureId", "ACI_EP_1");

		clinicalDocumentNode.addChildNode(aciNumeratorDenominatorNode);

		Node aciDenominatorNode = new Node(TemplateId.ACI_DENOMINATOR, aciNumeratorDenominatorNode);
		Node aciNumeratorNode = new Node(TemplateId.ACI_NUMERATOR, aciNumeratorDenominatorNode);

		aciNumeratorDenominatorNode.addChildNode(aciNumeratorNode);
		aciNumeratorDenominatorNode.addChildNode(aciDenominatorNode);

		AciNumeratorDenominatorValidator measureVal = new AciNumeratorDenominatorValidator();
		List<Detail> errors = measureVal.validateSingleNode(aciNumeratorDenominatorNode);

		assertThat("there should be 1 error", errors, hasSize(1));
		assertThat("error should be about invalid parent node", errors.get(0).getMessage(),
				is(AciNumeratorDenominatorValidator.NO_PARENT_SECTION));
	}

	@Test
	public void testNoChildNodes() {

		Node aciSectionNode = new Node(TemplateId.ACI_SECTION);
		aciSectionNode.putValue("category", "aci");

		Node aciNumeratorDenominatorNode = new Node(TemplateId.ACI_NUMERATOR_DENOMINATOR, aciSectionNode);
		aciNumeratorDenominatorNode.putValue("measureId", "ACI_EP_1");

		aciSectionNode.addChildNode(aciNumeratorDenominatorNode);

		AciNumeratorDenominatorValidator measureval = new AciNumeratorDenominatorValidator();
		List<Detail> errors = measureval.validateSingleNode(aciNumeratorDenominatorNode);

		assertThat("there should be 1 error", errors, hasSize(1));
		assertThat("error should be about no child nodes", errors.get(0).getMessage(), is(AciNumeratorDenominatorValidator.NO_CHILDREN));
	}

	@Test
	public void testNoNumerator() {

		Node aciSectionNode = new Node(TemplateId.ACI_SECTION);
		aciSectionNode.putValue("category", "aci");

		Node aciNumeratorDenominatorNode = new Node(TemplateId.ACI_NUMERATOR_DENOMINATOR, aciSectionNode);
		aciNumeratorDenominatorNode.putValue("measureId", "ACI_EP_1");

		aciSectionNode.addChildNode(aciNumeratorDenominatorNode);

		Node aciDenominatorNode = new Node(TemplateId.ACI_DENOMINATOR, aciNumeratorDenominatorNode);
		Node aciNumeratorPlaceholder = new Node(TemplateId.PLACEHOLDER, aciNumeratorDenominatorNode);

		aciNumeratorDenominatorNode.addChildNode(aciDenominatorNode);
		aciNumeratorDenominatorNode.addChildNode(aciNumeratorPlaceholder);

		AciNumeratorDenominatorValidator measureval = new AciNumeratorDenominatorValidator();
		List<Detail> errors = measureval.validateSingleNode(aciNumeratorDenominatorNode);

		assertThat("there should be 1 error", errors, hasSize(1));
		assertThat("error should be about missing Numerator node", errors.get(0).getMessage(),
				is(AciNumeratorDenominatorValidator.NO_NUMERATOR));
	}

	@Test
	public void testNoDenominator() {

		Node aciSectionNode = new Node(TemplateId.ACI_SECTION);
		aciSectionNode.putValue("category", "aci");

		Node aciNumeratorDenominatorNode = new Node(TemplateId.ACI_NUMERATOR_DENOMINATOR, aciSectionNode);
		aciNumeratorDenominatorNode.putValue("measureId", "ACI_EP_1");

		aciSectionNode.addChildNode(aciNumeratorDenominatorNode);

		Node aciDenominatorPlaceholder = new Node(TemplateId.PLACEHOLDER, aciNumeratorDenominatorNode);
		Node aciNumeratorNode = new Node(TemplateId.ACI_NUMERATOR, aciNumeratorDenominatorNode);

		aciNumeratorDenominatorNode.addChildNode(aciDenominatorPlaceholder);
		aciNumeratorDenominatorNode.addChildNode(aciNumeratorNode);

		AciNumeratorDenominatorValidator measureval = new AciNumeratorDenominatorValidator();
		List<Detail> errors = measureval.validateSingleNode(aciNumeratorDenominatorNode);

		assertThat("there should be 1 error", errors, hasSize(1));
		assertThat("error should be about missing Denominator node", errors.get(0).getMessage(),
				is(AciNumeratorDenominatorValidator.NO_DENOMINATOR));
	}

	@Test
	public void testTooManyNumerators() {

		Node aciSectionNode = new Node(TemplateId.ACI_SECTION);
		aciSectionNode.putValue("category", "aci");

		Node aciNumeratorDenominatorNode = new Node(TemplateId.ACI_NUMERATOR_DENOMINATOR, aciSectionNode);
		aciNumeratorDenominatorNode.putValue("measureId", "ACI_EP_1");

		aciSectionNode.addChildNode(aciNumeratorDenominatorNode);

		Node aciDenominatorNode = new Node(TemplateId.ACI_DENOMINATOR, aciNumeratorDenominatorNode);
		Node aciNumeratorNode = new Node(TemplateId.ACI_NUMERATOR, aciNumeratorDenominatorNode);
		Node aciNumeratorNode2 = new Node(TemplateId.ACI_NUMERATOR, aciNumeratorDenominatorNode);

		aciNumeratorDenominatorNode.addChildNode(aciDenominatorNode);
		aciNumeratorDenominatorNode.addChildNode(aciNumeratorNode);
		aciNumeratorDenominatorNode.addChildNode(aciNumeratorNode2);

		AciNumeratorDenominatorValidator measureval = new AciNumeratorDenominatorValidator();
		List<Detail> errors = measureval.validateSingleNode(aciNumeratorDenominatorNode);

		assertThat("there should be 1 error", errors, hasSize(1));
		assertThat("error should be about too many Numerator nodes", errors.get(0).getMessage(),
				is(AciNumeratorDenominatorValidator.TOO_MANY_NUMERATORS));
	}

	@Test
	public void testTooManyDenominators() {

		Node aciSectionNode = new Node(TemplateId.ACI_SECTION);
		aciSectionNode.putValue("category", "aci");

		Node aciNumeratorDenominatorNode = new Node(TemplateId.ACI_NUMERATOR_DENOMINATOR, aciSectionNode);
		aciNumeratorDenominatorNode.putValue("measureId", "ACI_EP_1");

		aciSectionNode.addChildNode(aciNumeratorDenominatorNode);

		Node aciDenominatorNode = new Node(TemplateId.ACI_DENOMINATOR, aciNumeratorDenominatorNode);
		Node aciDenominatorNode2 = new Node(TemplateId.ACI_DENOMINATOR, aciNumeratorDenominatorNode);
		Node aciNumeratorNode = new Node(TemplateId.ACI_NUMERATOR, aciNumeratorDenominatorNode);

		aciNumeratorDenominatorNode.addChildNode(aciDenominatorNode);
		aciNumeratorDenominatorNode.addChildNode(aciDenominatorNode2);
		aciNumeratorDenominatorNode.addChildNode(aciNumeratorNode);

		AciNumeratorDenominatorValidator measureval = new AciNumeratorDenominatorValidator();
		List<Detail> errors = measureval.validateSingleNode(aciNumeratorDenominatorNode);

		assertThat("there should be 1 error", errors, hasSize(1));
		assertThat("error should be about too many Denominator nodes", errors.get(0).getMessage(),
				is(AciNumeratorDenominatorValidator.TOO_MANY_DENOMINATORS));
	}
}
