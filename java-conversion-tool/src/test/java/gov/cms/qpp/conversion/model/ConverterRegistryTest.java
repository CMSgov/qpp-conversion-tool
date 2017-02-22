package gov.cms.qpp.conversion.model;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
//import org.w3c.dom.Node;

import gov.cms.qpp.conversion.parser.DecodeEception;
import gov.cms.qpp.conversion.parser.InputParser;

public class ConverterRegistryTest {

	ConverterRegistry registry;
	
	@Before
	public void before() {
		registry = new ConverterRegistry();
	}
	
	@Test
	public void testRegistryExistsByDefault() throws Exception {
		try {
			registry.register("el", "id", Placeholder.class);
		} catch (NullPointerException e) {
			fail("Registry should always exist.");
		}
		assertTrue("Registry exists", true);
	}
	
	@Test
	public void testRegistryInit() throws Exception {
		registry.register("el", "id", Placeholder.class);
		registry.init();
		InputParser parser = registry.getConverter("el", "id");
		assertTrue("Registry should have been reset.", parser==null);
	}
	
	@Test
	public void testRegistryGetConverterHandler() throws Exception {
		registry.register("el", "id", Placeholder.class);
		InputParser parser = registry.getConverter("el", "id");
		assertTrue("Registry should have been reset.", parser instanceof Placeholder);
	}
/*
	// This test must reside here in order to call the protected methods on the registry
	@Test
	public void testRegistry_placeAndFetch() throws Exception {
		ConverterRegistry.reset();
		ConverterRegistry.registerAnnotatedHandlers();
		
		String xpath = ConverterRegistry.getHanlderXpath(ACINumeratorDenominatorValue.class);
		Handler handler = registry.getConverter(xpath);
		
		assertNotNull("A handler is expected", handler);
		assertEquals("Handler should be an instance of the handler for the given XPATH", ACINumeratorDenominatorValue.class, handler.getClass());
	}
*/	
}

class Placeholder implements InputParser  {
	public Placeholder() {
		// required
	}
	
	@Override
	public String toString() {
		return "PLACEHOLDER";
	}

	@Override
	public Node parse() throws DecodeEception {
		return null;
	}
};

