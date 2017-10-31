package gov.cms.qpp.conversion.validate;

import static com.google.common.truth.Truth.assertWithMessage;

import java.util.Set;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.ErrorCode;
import gov.cms.qpp.conversion.model.error.correspondence.DetailsErrorEquals;

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
		Set<Detail> errors = measureVal.validateSingleNode(aciNumeratorDenominatorNode);

		assertWithMessage("no errors should be present")
				.that(errors).isEmpty();
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

		Set<Detail> errors = measureVal.validateSingleNode(aciNumeratorDenominatorNode);

		assertWithMessage("error should be about missing numerator denominator measure name")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.ACI_NUMERATOR_DENOMINATOR_MISSING_MEASURE_ID);
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
		Set<Detail> errors = measureVal.validateSingleNode(aciNumeratorDenominatorNode);

		assertWithMessage("error should be about invalid parent node")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.ACI_NUMERATOR_DENOMINATOR_PARENT_NOT_ACI_SECTION);
	}

	@Test
	public void testNoChildNodes() {

		Node aciSectionNode = new Node(TemplateId.ACI_SECTION);
		aciSectionNode.putValue("category", "aci");

		Node aciNumeratorDenominatorNode = new Node(TemplateId.ACI_NUMERATOR_DENOMINATOR, aciSectionNode);
		aciNumeratorDenominatorNode.putValue("measureId", "ACI_EP_1");

		aciSectionNode.addChildNode(aciNumeratorDenominatorNode);

		AciNumeratorDenominatorValidator measureval = new AciNumeratorDenominatorValidator();
		Set<Detail> errors = measureval.validateSingleNode(aciNumeratorDenominatorNode);

		assertWithMessage("error should be about no child nodes")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.ACI_NUMERATOR_DENOMINATOR_MISSING_CHILDREN);
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
		Set<Detail> errors = measureval.validateSingleNode(aciNumeratorDenominatorNode);

		assertWithMessage("error should be about missing Numerator node")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.ACI_NUMERATOR_DENOMINATOR_VALIDATOR_MISSING_NUMERATOR_CHILD_NODE);
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
		Set<Detail> errors = measureval.validateSingleNode(aciNumeratorDenominatorNode);

		assertWithMessage("error should be about missing Denominator node")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.ACI_NUMERATOR_DENOMINATOR_VALIDATOR_MISSING_DENOMINATOR_CHILD_NODE);
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
		Set<Detail> errors = measureval.validateSingleNode(aciNumeratorDenominatorNode);

		assertWithMessage("error should be about too many Numerator nodes")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.ACI_NUMERATOR_DENOMINATOR_VALIDATOR_TOO_MANY_NUMERATORS);
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
		Set<Detail> errors = measureval.validateSingleNode(aciNumeratorDenominatorNode);

		assertWithMessage("error should be about too many Denominator nodes")
				.that(errors).comparingElementsUsing(DetailsErrorEquals.INSTANCE)
				.containsExactly(ErrorCode.ACI_NUMERATOR_DENOMINATOR_VALIDATOR_TOO_MANY_DENOMINATORS);
	}
}
