package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.encode.helper.RegistryHelper;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Registry;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.ValidationError;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests the Placeholder adds coverage for CircleCI
 */
public class PlaceholderEncoderTest extends PlaceholderEncoder {
	@Test
	public void internalEncodeMissingEncoderTest() throws Exception {

		Registry<JsonOutputEncoder> invalidRegistry =
				RegistryHelper.makeInvalidRegistry(PlaceholderEncoder.class.getName());
		Registry<JsonOutputEncoder> validRegistry = QppOutputEncoder.ENCODERS;

		Node root = new Node(TemplateId.DEFAULT);
		Node placeHolderNode = new Node(TemplateId.PLACEHOLDER, root);
		root.addChildNode(placeHolderNode);
		JsonWrapper testJsonWrapper = new JsonWrapper();
		PlaceholderEncoder placeHolderEncoder = new PlaceholderEncoder();

		RegistryHelper.setEncoderRegistry(invalidRegistry); //Set Registry with missing class

		placeHolderEncoder.internalEncode(testJsonWrapper, root);
		List<ValidationError> errors = placeHolderEncoder.getValidationErrors();
		assertThat("Expecting Encode Exception", errors.size(), is(1));

		RegistryHelper.setEncoderRegistry(validRegistry); //Restore Registry
	}

}

