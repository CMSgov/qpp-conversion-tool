package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.model.AnnotationMockHelper;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Validations;
import org.jdom2.Element;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.mockpolicies.Slf4jMockPolicy;
import org.powermock.core.classloader.annotations.MockPolicy;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.slf4j.Logger;

import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;

@RunWith(PowerMockRunner.class)
@MockPolicy(Slf4jMockPolicy.class)
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

	@Test
	@PrepareForTest(QppXmlDecoder.class)
	public void testThatDefaultCaseReturnsNoAction() {
		AnnotationMockHelper.mockDecoder("errorDecoder", TestChildDecodeError.class);
		AnnotationMockHelper.mockDecoder("noActionDecoder", TestChildNoAction.class);

		Element testElement = new Element("testElement");
		Element testChildElement = new Element("templateId");
		testChildElement.setAttribute("root", "noActionDecoder");

		Logger logger = mock(Logger.class);

		testElement.getChildren().add(testChildElement);
		Node testNode = new Node();

		Whitebox.setInternalState(QppXmlDecoder.class, "CLIENT_LOG", logger);

		QppXmlDecoder objectUnderTest = new QppXmlDecoderTest();
		objectUnderTest.decode(testElement, testNode);

		verify(logger).error(eq("We need to define a default case. Could be TreeContinue?"));
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
