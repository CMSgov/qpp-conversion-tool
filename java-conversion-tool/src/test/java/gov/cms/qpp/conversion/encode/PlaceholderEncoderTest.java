package gov.cms.qpp.conversion.encode;


import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class PlaceholderEncoderTest {

	@Test
<<<<<<< HEAD
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
=======
	public void encodePlaceholderNodeNegative() throws EncodeException {
		//setup
		Node placeHolder = new Node(TemplateId.PLACEHOLDER.getTemplateId());
		placeHolder.addChildNode(new Node("meep"));
		JsonWrapper wrapper = new JsonWrapper();
		PlaceholderEncoder encoder = new PlaceholderEncoder();

		//when
		encoder.internalEncode(wrapper, placeHolder);

		//then
		assertThat(encoder.getValidationErrors().size(), is(1));
>>>>>>> 882897d9420680d6b881505a8292857495843042
	}
}
