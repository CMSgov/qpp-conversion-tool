package gov.cms.qpp.conversion.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import gov.cms.qpp.conversion.parser.AciNumeratorDenominatorInputParser;
import gov.cms.qpp.conversion.parser.DecodeException;
import gov.cms.qpp.conversion.parser.InputParser;

public class RegistryTest {

	Registry<InputParser> registry;

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
		InputParser parser = (InputParser) registry.get("id");
		assertTrue("Registry should have been reset.", parser == null);
	}

	@Test
	public void testRegistryGetConverterHandler() throws Exception {
		registry.register("id", Placeholder.class);
		InputParser parser = (InputParser) registry.get("id");
		assertTrue("Registry should have been reset.", parser instanceof Placeholder);
	}

	// This test must reside here in order to call the protected methods on the
	// registry
	@Test
	public void testRegistry_placeAndFetch() throws Exception {
		String templateId = registry.getAnnotationParams(AciNumeratorDenominatorInputParser.class);
		InputParser parser = (InputParser) registry.get(templateId);

		assertNotNull("A handler is expected", parser);
		assertEquals("Handler should be an instance of the handler for the given XPATH",
				AciNumeratorDenominatorInputParser.class, parser.getClass());
	}
}

class Placeholder implements InputParser {
	public Placeholder() {
		// required
	}

	@Override
	public String toString() {
		return "PLACEHOLDER";
	}

	@Override
	public Node parse() throws DecodeException {
		return null;
	}
};
