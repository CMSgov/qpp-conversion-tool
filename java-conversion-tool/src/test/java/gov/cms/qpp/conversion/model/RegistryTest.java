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
			registry.register(TemplateId.PLACEHOLDER, Placeholder.class);
		} catch (NullPointerException e) {
			fail("Registry should always exist.");
		}
		assertTrue("Registry exists", true);
	}

	@Test
	public void testRegistryInit() throws Exception {
		registry.register(TemplateId.PLACEHOLDER, Placeholder.class);
		registry.init();
		InputDecoder decoder = registry.get(TemplateId.PLACEHOLDER);
		assertTrue("Registry should have been reset.", decoder == null);
	}

	@Test
	public void testRegistryGetConverterHandler() throws Exception {
		registry.register(TemplateId.PLACEHOLDER, Placeholder.class);
		InputDecoder decoder = registry.get(TemplateId.PLACEHOLDER);
		assertTrue("Registry should have been reset.", decoder instanceof Placeholder);
	}

	// This test must reside here in order to call the protected methods on the
	// registry
	@Test
	public void testRegistry_placeAndFetch() throws Exception {
		Set<TemplateId> templateIds = registry.getTemplateIds(AggregateCountDecoder.class);
		assertThat(templateIds, hasSize(1));
		for (TemplateId templateId : templateIds) {
			InputDecoder decoder = registry.get(templateId);

			assertNotNull("A handler is expected", decoder);
			assertEquals("Handler should be an instance of the handler for the given XPATH", AggregateCountDecoder.class,
					decoder.getClass());
		}
	}

	@Test
	public void testRegistry_getTemplateIds() throws Exception {
		Set<TemplateId> templateIds = registry.getTemplateIds(AggregateCountDecoder.class);
		assertThat("A templateId is expected", templateIds, hasSize(1));
		for (TemplateId templateId : templateIds) {
			assertEquals("The templateId should be", TemplateId.ACI_AGGREGATE_COUNT, templateId);
		}

		templateIds = new Registry<>(Encoder.class).getTemplateIds(AggregateCountEncoder.class);
		assertThat("A templateId is expected", templateIds, hasSize(1));
		for (TemplateId templateId : templateIds) {
			assertEquals("The templateId should be", TemplateId.ACI_AGGREGATE_COUNT, templateId);
		}
	}

	@Test
	public void testRegistry_getTemplateIds_NullReturn() throws Exception {
		Set<TemplateId> templateIds = new Registry<Encoder>(SuppressWarnings.class).getTemplateIds(Placeholder.class);
		assertThat("A templateId is not expected", templateIds, empty());
	}

	@Test
	public void testRegistryGetHandlerThatFailsConstruction() throws Exception {
		registry.register(TemplateId.PLACEHOLDER, PrivateConstructor.class);
		InputDecoder decoder = registry.get(TemplateId.PLACEHOLDER);
		assertThat("Registry should return null for faile construction not an exception.", decoder, is(nullValue()));
	}

	@Test
	public void testRegistryAddDuplicate() throws Exception {
		registry.register(TemplateId.PLACEHOLDER, Placeholder.class);
		registry.register(TemplateId.PLACEHOLDER, AnotherPlaceholder.class);
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
