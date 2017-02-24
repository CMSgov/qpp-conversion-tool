package gov.cms.qpp.conversion.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import gov.cms.qpp.conversion.decode.AciNumeratorDenominatorDecoder;
import gov.cms.qpp.conversion.decode.DecodeException;
import gov.cms.qpp.conversion.decode.InputDecoder;

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
		String templateId = registry.getAnnotationParams(AciNumeratorDenominatorDecoder.class);
		InputDecoder decoder = (InputDecoder) registry.get(templateId);

		assertNotNull("A handler is expected", decoder);
		assertEquals("Handler should be an instance of the handler for the given XPATH",
				AciNumeratorDenominatorDecoder.class, decoder.getClass());
	}
}

class Placeholder implements InputDecoder {
	public Placeholder() {
		// required
	}

	@Override
	public String toString() {
		return "PLACEHOLDER";
	}

	@Override
	public Node decode() throws DecodeException {
		return null;
	}
};
