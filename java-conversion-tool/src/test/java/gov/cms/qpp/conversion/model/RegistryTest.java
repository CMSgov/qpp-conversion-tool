package gov.cms.qpp.conversion.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import gov.cms.qpp.conversion.decode.AciNumeratorDenominatorDecoder;
import gov.cms.qpp.conversion.decode.DecodeException;
import gov.cms.qpp.conversion.decode.InputDecoder;
import gov.cms.qpp.conversion.encode.AciNumeratorDenominatorEncoder;

public class RegistryTest {

	Registry<InputDecoder> registry;

	@Before
	public void before() {
		registry = new Registry<>(XmlDecoder.class);
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
		InputDecoder decoder = (InputDecoder) registry.get("id");
		assertTrue("Registry should have been reset.", decoder == null);
	}

	@Test
	public void testRegistryGetConverterHandler() throws Exception {
		registry.register("id", Placeholder.class);
		InputDecoder decoder = (InputDecoder) registry.get("id");
		assertTrue("Registry should have been reset.", decoder instanceof Placeholder);
	}

	// This test must reside here in order to call the protected methods on the
	// registry
	@Test
	public void testRegistry_placeAndFetch() throws Exception {
		String templateId = registry.getAnnotationParam(AciNumeratorDenominatorDecoder.class);
		InputDecoder decoder = (InputDecoder) registry.get(templateId);

		assertNotNull("A handler is expected", decoder);
		assertEquals("Handler should be an instance of the handler for the given XPATH",
				AciNumeratorDenominatorDecoder.class, decoder.getClass());
	}
	
	@Test
	public void testRegistry_getAnnotationParam() throws Exception {
		String templateId = registry.getAnnotationParam(AciNumeratorDenominatorDecoder.class);
		assertNotNull("A templateId is expected", templateId);
		assertEquals("The templateId should be",
				"2.16.840.1.113883.10.20.27.3.3", templateId);
		

		templateId = new Registry<>(JsonEncoder.class).getAnnotationParam(AciNumeratorDenominatorEncoder.class);
		assertNotNull("A templateId is expected", templateId);
		assertEquals("The templateId should be",
				"2.16.840.1.113883.10.20.27.3.3", templateId);
	}
	@Test
	public void testRegistry_getAnnotationParam_NullReturn() throws Exception {
		String templateId = new Registry<>(SuppressWarnings.class).getAnnotationParam(Placeholder.class);
		assertTrue("A templateId is expected", templateId==null);
	}
	
	@Test
	public void testRegistryGetHandlerThatFailsConstruction() throws Exception {
		registry.register("id", PrivateConstructor.class);
		InputDecoder decoder = (InputDecoder) registry.get("id");
		assertThat("Registry should return null for faile construction not an exception.",
				decoder, is(nullValue()) );
	}
	
}

@SuppressWarnings("unused") // this is here for a the annotation tests
class Placeholder implements InputDecoder {
	private String unused;
	
	public Placeholder() {}

	@Override
	public Node decode() throws DecodeException {
		return null;
	}
};

class PrivateConstructor implements InputDecoder {
	private PrivateConstructor() {}

	@Override
	public Node decode() throws DecodeException {
		return null;
	}
};
