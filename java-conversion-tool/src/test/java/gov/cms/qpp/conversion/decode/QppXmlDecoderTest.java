package gov.cms.qpp.conversion.decode;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.jdom2.Element;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import gov.cms.qpp.conversion.model.AnnotationMockHelper;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Validations;

public class QppXmlDecoderTest extends QppXmlDecoder {

	@Before
	public void setup() throws Exception {
		Validations.init();
	}

	@After
	public void teardown() throws Exception {
		Validations.clear();
	}

	@Test
	public void validationFormatTest() throws Exception {
		XmlInputDecoder objectUnderTest = new QppXmlDecoder();

		objectUnderTest.addValidation("templateid.1", "validation.1");
		objectUnderTest.addValidation("templateid.1", "validation.2");
		objectUnderTest.addValidation("templateid.3", "validation.3");

		List<String> validations = (List<String>) objectUnderTest.validations();
		assertThat("Expected count", validations, hasSize(3));
		assertThat("Expected validation", validations, hasItems("templateid.1 - validation.1",
				"templateid.1 - validation.2",
				"templateid.3 - validation.3"));
	}

	@Test
	public void validationFormatTestById() {
		QppXmlDecoder objectUnderTest = new QppXmlDecoder();
		objectUnderTest.addValidation("templateid.1", "validation.1");
		objectUnderTest.addValidation("templateid.1", "validation.2");

		List<String> validations = objectUnderTest.getValidationsById("templateid.1");
		assertThat("Expected count", validations, hasSize(2));
		assertThat("Expected validation", validations, hasItems("validation.1", "validation.2"));
	}

	@Test
	public void decodeResultNoAction() throws Exception {
		assertThat("DecodeResult is incorrect", new QppXmlDecoder().internalDecode(null, null),
				is(DecodeResult.NO_ACTION));
	}

	@Test
	public void nullElementDecodeReturnsError() {
		//Element nullElement = null;
		assertThat("DecodeResult is incorrect", new QppXmlDecoder().decode((Element)null, null),
				is(DecodeResult.ERROR));
	}

	@Test
	public void decodeInvalidChildReturnsError() {
		AnnotationMockHelper.mockDecoder("errorDecoder", TestChildDecodeError.class);
		AnnotationMockHelper.mockDecoder("noActionDecoder", TestChildNoAction.class);

		Element testElement = new Element("testElement");
		Element testChildElement = new Element("templateId");
		testChildElement.setAttribute("root", "errorDecoder");

		testElement.getChildren().add(testChildElement);
		Node testNode = new Node();

		QppXmlDecoder objectUnderTest = new QppXmlDecoderTest();
		objectUnderTest.decode(testElement, testNode);

		List<String> validations = objectUnderTest.getValidationsById("errorDecoder");

		assertThat("Child Node did not return " + DecodeResult.ERROR , validations, hasItem("Failed to decode."));
	}

	public static class TestChildDecodeError extends QppXmlDecoder{

		@Override
		public DecodeResult internalDecode(Element element, Node childNode) {
			return DecodeResult.ERROR;
		}
	}

	public static class TestChildNoAction extends QppXmlDecoder{

		@Override
		public DecodeResult internalDecode(Element element, Node childNode) {
			return DecodeResult.NO_ACTION;
		}
	}
}
