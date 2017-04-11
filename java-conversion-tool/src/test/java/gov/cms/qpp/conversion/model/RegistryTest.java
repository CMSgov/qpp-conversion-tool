package gov.cms.qpp.conversion.model;

import gov.cms.qpp.conversion.decode.AggregateCountDecoder;
import gov.cms.qpp.conversion.decode.DecodeException;
import gov.cms.qpp.conversion.decode.InputDecoder;
import gov.cms.qpp.conversion.encode.AggregateCountEncoder;

import org.apache.commons.io.output.NullOutputStream;
import org.jdom2.Element;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.PrintStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class RegistryTest {

	private Registry<String, InputDecoder> registry;
	private PrintStream err;

	@Before
	public void before() {
		registry = new Registry<>(XmlDecoder.class);
		err = System.err;
	}

	@After
	public void tearDown() {
		System.setErr(err);
	}

	@Test
	public void testRegistryExistsByDefault() throws Exception {
		try {
			registry.register("id", Placeholder.class);
		} catch (NullPointerException e) {
			fail("Registry should always exist.");
		}
		assertTrue("Registry exists", true);
	}

	@Test
	public void testRegistryInit() throws Exception {
		registry.register("id", Placeholder.class);
		registry.init();
		InputDecoder decoder = registry.get("id");
		assertTrue("Registry should have been reset.", decoder == null);
	}

	@Test
	public void testRegistryGetConverterHandler() throws Exception {
		registry.register("id", Placeholder.class);
		InputDecoder decoder = registry.get("id");
		assertTrue("Registry should have been reset.", decoder instanceof Placeholder);
	}

	// This test must reside here in order to call the protected methods on the
	// registry
	@Test
	public void testRegistry_placeAndFetch() throws Exception {
		String templateId = registry.getAnnotationParam(AggregateCountDecoder.class);
		InputDecoder decoder = registry.get(templateId);

		assertNotNull("A handler is expected", decoder);
		assertEquals("Handler should be an instance of the handler for the given XPATH", AggregateCountDecoder.class,
				decoder.getClass());
	}

	@Test
	public void testRegistry_getAnnotationParam() throws Exception {
		String templateId = registry.getAnnotationParam(AggregateCountDecoder.class);
		assertNotNull("A templateId is expected", templateId);
		assertEquals("The templateId should be", TemplateId.ACI_AGGREGATE_COUNT.getTemplateId(), templateId);

		templateId = new Registry<String, Encoder>(Encoder.class, EncoderNew.class).getAnnotationParam(AggregateCountEncoder.class);
		assertNotNull("A templateId is expected", templateId);
		assertEquals("The templateId should be", TemplateId.ACI_AGGREGATE_COUNT.getTemplateId(), templateId);
	}

	@Test
	public void testRegistry_getAnnotationParam_NullReturn() throws Exception {
		String templateId = new Registry<String, Encoder>(SuppressWarnings.class).getAnnotationParam(Placeholder.class);
		assertTrue("A templateId is expected", templateId == null);
	}

	@Test
	public void testRegistryGetHandlerThatFailsConstruction() throws Exception {
		registry.register("id", PrivateConstructor.class);
		InputDecoder decoder = registry.get("id");
		assertThat("Registry should return null for faile construction not an exception.", decoder, is(nullValue()));
	}

	@Test
	public void testClassNotFoundCausesMissingEntriesInRegistry_throwsNoException() {
		Registry<String, XmlDecoder> registryA = new Registry<>(XmlDecoder.class);

		// Mock the condition where a class is not found during registry
		// building
		Registry<String, XmlDecoder> registryB = new Registry<String, XmlDecoder>(XmlDecoder.class) {
			@Override
			protected Class<?> getAnnotatedClass(String className) throws ClassNotFoundException {
				if ("gov.cms.qpp.conversion.decode.AggregateCountDecoder".equals(className)) {
					System.setErr(new PrintStream(NullOutputStream.NULL_OUTPUT_STREAM));
					throw new ClassNotFoundException();
				}
				return Class.forName(className);
			}
		};
		assertEquals("The class was not found in the Decoder registry", registryA.size(),
				registryB.size() + 1);
	}

	@Test
	public void testRegistryAddDuplicate() throws Exception {
		registry.register("id", Placeholder.class);
		registry.register("id", AnotherPlaceholder.class);
		InputDecoder decoder = registry.get("id");
		assertTrue("Registry should have overwritten id with the second one.", decoder instanceof AnotherPlaceholder);
	}
}

@SuppressWarnings("unused") // this is here for a the annotation tests
class Placeholder implements InputDecoder {

	private String unused;

	public Placeholder() {
	}

	@Override
	public Node decode(Element xmlDoc) throws DecodeException {
		return null;
	}
}

@SuppressWarnings("unused") // this is here for a the annotation tests
class AnotherPlaceholder implements InputDecoder {

	private String unused;

	public AnotherPlaceholder() {
	}

	@Override
	public Node decode(Element xmlDoc) throws DecodeException {
		return null;
	}
}

class PrivateConstructor implements InputDecoder {

	private PrivateConstructor() {
	}

	@Override
	public Node decode(Element xmlDoc) throws DecodeException {
		return null;
	}
}
