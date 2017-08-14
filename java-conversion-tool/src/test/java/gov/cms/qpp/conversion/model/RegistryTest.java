package gov.cms.qpp.conversion.model;

import gov.cms.qpp.conversion.decode.AggregateCountDecoder;
import gov.cms.qpp.conversion.decode.InputDecoder;
import gov.cms.qpp.conversion.encode.AggregateCountEncoder;
import org.jdom2.Element;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.PrintStream;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class RegistryTest {

	private Registry<InputDecoder> registry;
	private PrintStream err;

	@Before
	public void before() {
		registry = new Registry<>(Decoder.class);
		err = System.err;
	}

	@After
	public void tearDown() {
		System.setErr(err);
	}

	@Test
	public void testRegistryExistsByDefault() throws Exception {
		try {
			registry.register(new ComponentKey(TemplateId.PLACEHOLDER, Program.ALL), Placeholder.class);
		} catch (NullPointerException e) {
			fail("Registry should always exist.");
		}
		assertTrue("Registry exists", true);
	}

	@Test
	public void testRegistryInit() throws Exception {
		registry.register(new ComponentKey(TemplateId.PLACEHOLDER, Program.ALL), Placeholder.class);
		registry.init();
		InputDecoder decoder = registry.get(TemplateId.PLACEHOLDER);
		assertNull("Registry should have been reset.", decoder);
	}

	@Test
	public void testRegistryGetConverterHandler() throws Exception {
		registry.register(new ComponentKey(TemplateId.PLACEHOLDER, Program.ALL), Placeholder.class);
		InputDecoder decoder = registry.get(TemplateId.PLACEHOLDER);
		assertTrue("Registry should have been reset.", decoder instanceof Placeholder);
	}

	// This test must reside here in order to call the protected methods on the
	// registry
	@Test
	public void testRegistry_placeAndFetch() throws Exception {
		Set<ComponentKey> componentKeys = registry.getComponentKeys(AggregateCountDecoder.class);
		assertThat(componentKeys, hasSize(1));
		for (ComponentKey componentKey : componentKeys) {
			InputDecoder decoder = registry.get(componentKey.getTemplate());

			assertNotNull("A handler is expected", decoder);
			assertEquals("Handler should be an instance of the handler for the given XPATH", AggregateCountDecoder.class,
					decoder.getClass());
		}
	}

	@Test
	public void testRegistry_getTemplateIds() throws Exception {
		Set<ComponentKey> componentKeys = registry.getComponentKeys(AggregateCountDecoder.class);
		assertThat("A componentKey is expected", componentKeys, hasSize(1));
		for (ComponentKey componentKey : componentKeys) {
			assertEquals("The templateId should be",
					TemplateId.ACI_AGGREGATE_COUNT, componentKey.getTemplate());
		}

		componentKeys = new Registry<>(Encoder.class).getComponentKeys(AggregateCountEncoder.class);
		assertThat("A componentKey is expected", componentKeys, hasSize(1));
		for (ComponentKey componentKey : componentKeys) {
			assertEquals("The templateId should be",
					TemplateId.ACI_AGGREGATE_COUNT, componentKey.getTemplate());
		}
	}

	@Test
	public void testRegistry_getTemplateIds_NullReturn() throws Exception {
		Set<ComponentKey> componentKeys = new Registry<Encoder>(SuppressWarnings.class).getComponentKeys(Placeholder.class);
		assertThat("A componentKey is not expected", componentKeys, empty());
	}

	@Test
	public void testRegistryGetHandlerThatFailsConstruction() throws Exception {
		registry.register(new ComponentKey(TemplateId.PLACEHOLDER, Program.ALL), PrivateConstructor.class);
		InputDecoder decoder = registry.get(TemplateId.PLACEHOLDER);
		assertThat("Registry should return null for failed construction not an exception.", decoder, is(nullValue()));
	}

	@Test
	public void testRegistryAddDuplicate() throws Exception {
		registry.register(new ComponentKey(TemplateId.PLACEHOLDER, Program.ALL), Placeholder.class);
		registry.register(new ComponentKey(TemplateId.PLACEHOLDER, Program.ALL), AnotherPlaceholder.class);
		InputDecoder decoder = registry.get(TemplateId.PLACEHOLDER);
		assertTrue("Registry should have overwritten id with the second one.", decoder instanceof AnotherPlaceholder);
	}

	@Test
	public void testSize() {
		assertTrue("Registry does not have contents", registry.size() > 0);
	}
}

@SuppressWarnings("unused") // this is here for a the annotation tests
class Placeholder implements InputDecoder {

	private String unused;

	public Placeholder() {
	}

	@Override
	public Node decode(Element xmlDoc) {
		return null;
	}
}

@SuppressWarnings("unused") // this is here for a the annotation tests
class AnotherPlaceholder implements InputDecoder {

	private String unused;

	public AnotherPlaceholder() {
	}

	@Override
	public Node decode(Element xmlDoc) {
		return null;
	}
}

class PrivateConstructor implements InputDecoder {

	private PrivateConstructor() {
	}

	@Override
	public Node decode(Element xmlDoc) {
		return null;
	}
}
