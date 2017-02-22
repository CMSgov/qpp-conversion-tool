package gov.cms.qpp.conversion.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.jdom2.Element;
import org.junit.Before;
import org.junit.Test;
//import org.w3c.dom.Node;

import gov.cms.qpp.conversion.parser.XmlInputParser;

public class ConverterRegistryTest {

	String key = "observation[templateId/@root = '2.16.840.1.113883.10.20.27.3.32:2016-09-01']";
	
	
	ConverterRegistry<XmlInputParser> registry;
	
	XmlInputParser placeholder = new XmlInputParser() {
		@Override
		public String toString() {
			return "PLACEHOLDER";
		}
		@Override
		protected Node parse(Element element, Node parent) {
			return null;
		}
		@Override
		protected Node internalParse(Element element) {
			return null;
		}
	};
	
	@Before
	public void before() {
		registry = new ConverterRegistry<>();
	}
	
	@Test
	public void testRegistryExistsByDefault() throws Exception {
		try {
			registry.register("el", "id", placeholder);
		} catch (NullPointerException e) {
			fail("Registry should always exist.");
		}
		assertTrue("Registry exists", true);
	}
	
	@Test
	public void testRegistryInit() throws Exception {
		registry.register("el", "id", placeholder);
		registry.init();
		XmlInputParser parser = registry.getConverter("el", "id");
		assertTrue("Registry should have been reset.", parser==null);
	}
	
	@Test
	public void testRegistryGetConverterHandler() throws Exception {
		registry.register("el", "id", placeholder);
		XmlInputParser parser = registry.getConverter("el", "id");
		assertEquals("Registry should have been reset.", placeholder, parser);
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
