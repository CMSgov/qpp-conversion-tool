package gov.cms.qpp.conversion.validate;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIterableWithSize.iterableWithSize;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.After;
import org.junit.Test;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.ValidationError;

public class AciProportionMeasureValidatorTest {

	private static final String EXPECTED_TEXT = "At least one Aci Proportion Measure Node is required";

	@After
	public void tearDown() {
		AciProportionMeasureValidator.resetValidationErrors();
	}

	@Test
	public void testMeasurePresent() {

		Node aciProportionMeasureNode = new Node();
		aciProportionMeasureNode.setId("2.16.840.1.113883.10.20.27.3.28");
		aciProportionMeasureNode.putValue("measureId", "ACI-PEA-1");

		Node aciSectionNode = new Node();
		aciSectionNode.setId("2.16.840.1.113883.10.20.27.2.5");
		aciSectionNode.putValue("category", "aci");
		aciSectionNode.addChildNode(aciProportionMeasureNode);

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

		assertThat("there should be one error", errors, iterableWithSize(1));
		assertThat("error should be about missing Measure node", errors.get(0).getErrorText(), is(EXPECTED_TEXT));

	}

}
