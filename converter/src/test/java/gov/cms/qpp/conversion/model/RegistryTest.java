package gov.cms.qpp.conversion.model;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.decode.AggregateCountDecoder;
import gov.cms.qpp.conversion.decode.InputDecoder;
import gov.cms.qpp.conversion.encode.AggregateCountEncoder;
import org.jdom2.Element;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.Set;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static org.junit.Assert.fail;

public class RegistryTest {

	private Context context;
	private Registry<InputDecoder> registry;
	private PrintStream err;

	@Before
	public void before() {
		context = new Context();
		registry = context.getRegistry(Decoder.class);
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
	}

	@Test
	public void testRegistryGetDefaultConverterHandler() throws Exception {
		context.setProgram(Program.CPC);
		registry.register(new ComponentKey(TemplateId.PLACEHOLDER, Program.ALL), Placeholder.class);
		InputDecoder decoder = registry.get(TemplateId.PLACEHOLDER);

		assertWithMessage("Registry should return %s instance.", Placeholder.class.getName())
				.that(decoder).isInstanceOf(Placeholder.class);
	}

	@Test
	public void testRegistryGetProgramSpecificConverterHandler() throws Exception {
		context.setProgram(Program.CPC);
		registry.register(new ComponentKey(TemplateId.PLACEHOLDER, Program.ALL), Placeholder.class);
		registry.register(new ComponentKey(TemplateId.PLACEHOLDER, Program.CPC), AnotherPlaceholder.class);
		InputDecoder decoder = registry.get(TemplateId.PLACEHOLDER);

		assertWithMessage("Registry should return %s instance.", AnotherPlaceholder.class.getName())
				.that(decoder).isInstanceOf(AnotherPlaceholder.class);
	}

	@Test
	public void testRegistryInclusiveGetDefaultConverterHandler() throws Exception {
		context.setProgram(Program.CPC);
		registry.register(new ComponentKey(TemplateId.PLACEHOLDER, Program.ALL), Placeholder.class);
		Set<InputDecoder> decoders = registry.inclusiveGet(TemplateId.PLACEHOLDER);

		assertWithMessage("Registry should return %s instance.", Placeholder.class.getName())
				.that(decoders.iterator().next()).isInstanceOf(Placeholder.class);
	}

	@Test
	public void testRegistryInclusiveGetProgramSpecificConverterHandler() throws Exception {
		context.setProgram(Program.CPC);
		registry.register(new ComponentKey(TemplateId.PLACEHOLDER, Program.ALL), Placeholder.class);
		registry.register(new ComponentKey(TemplateId.PLACEHOLDER, Program.CPC), AnotherPlaceholder.class);
		Set<InputDecoder> decoders = registry.inclusiveGet(TemplateId.PLACEHOLDER);

		assertWithMessage("Should return two decoders")
				.that(decoders).hasSize(2);
	}

	@Test
	public void testRegistryInclusiveGetPrioritizesGeneral() throws Exception {
		context.setProgram(Program.CPC);
		registry.register(new ComponentKey(TemplateId.PLACEHOLDER, Program.CPC), AnotherPlaceholder.class);
		registry.register(new ComponentKey(TemplateId.PLACEHOLDER, Program.ALL), Placeholder.class);
		Set<InputDecoder> decoders = registry.inclusiveGet(TemplateId.PLACEHOLDER);
		Iterator<InputDecoder> iterator = decoders.iterator();

		assertWithMessage("First Registry entry should be a %s instance.", Placeholder.class.getName())
				.that(iterator.next()).isInstanceOf(Placeholder.class);
		assertWithMessage("Second Registry entry should be a %s instance.", AnotherPlaceholder.class.getName())
				.that(iterator.next()).isInstanceOf(AnotherPlaceholder.class);
	}

	// This test must reside here in order to call the protected methods on the
	// registry
	@Test
	public void testRegistry_placeAndFetch() throws Exception {
		Set<ComponentKey> componentKeys = registry.getComponentKeys(AggregateCountDecoder.class);

		assertThat(componentKeys).hasSize(1);
		for (ComponentKey componentKey : componentKeys) {
			InputDecoder decoder = registry.get(componentKey.getTemplate());

			assertWithMessage("A handler is expected")
					.that(decoder).isNotNull();
			assertWithMessage("Handler should be an instance of the handler for the given XPATH")
					.that(decoder).isInstanceOf(AggregateCountDecoder.class);
		}
	}

	@Test
	public void testRegistry_getTemplateIds() throws Exception {
		Set<ComponentKey> componentKeys = registry.getComponentKeys(AggregateCountDecoder.class);
		assertWithMessage("A componentKey is expected")
				.that(componentKeys).hasSize(1);
		for (ComponentKey componentKey : componentKeys) {
			assertWithMessage("The templateId should be")
					.that(componentKey.getTemplate()).isSameAs(TemplateId.ACI_AGGREGATE_COUNT);
		}

		componentKeys = context.getRegistry(Encoder.class).getComponentKeys(AggregateCountEncoder.class);
		assertWithMessage("A componentKey is expected")
				.that(componentKeys).hasSize(1);
		for (ComponentKey componentKey : componentKeys) {
			assertWithMessage("The templateId should be")
					.that(componentKey.getTemplate()).isSameAs(TemplateId.ACI_AGGREGATE_COUNT);
		}
	}

	@Test
	public void testRegistry_getTemplateIds_NullReturn() throws Exception {
		Set<ComponentKey> componentKeys = context.getRegistry(SuppressWarnings.class).getComponentKeys(Placeholder.class);
		assertWithMessage("A componentKey is not expected")
				.that(componentKeys).isEmpty();
	}

	@Test
	public void testRegistryGetHandlerThatFailsConstruction() throws Exception {
		registry.register(new ComponentKey(TemplateId.PLACEHOLDER, Program.ALL), PrivateConstructor.class);
		InputDecoder decoder = registry.get(TemplateId.PLACEHOLDER);
		assertWithMessage("Registry with a private constructor should be constructable")
				.that(decoder).isNotNull();
	}

	@Test
	public void testRegistryGetHandlerWithNoDefaultConstructor() throws Exception {
		registry.register(new ComponentKey(TemplateId.PLACEHOLDER, Program.ALL), NoDefaultConstructor.class);
		InputDecoder decoder = registry.get(TemplateId.PLACEHOLDER);
		assertWithMessage("Registry without a default constructor should not be constructable")
				.that(decoder).isNull();
	}

	@Test
	public void testRegistryGetHandlerWithMalcontentedConstructor() throws Exception {
		registry.register(new ComponentKey(TemplateId.PLACEHOLDER, Program.ALL), MalcontentedConstructor.class);
		InputDecoder decoder = registry.get(TemplateId.PLACEHOLDER);
		assertThat(decoder).isNull();
	}

	@Test(expected = SevereRuntimeException.class)
	public void testRegistryGetHandlerWithThrowableConstructor() throws Exception {
		registry.register(new ComponentKey(TemplateId.PLACEHOLDER, Program.ALL), ThrowableConstructor.class);
		registry.get(TemplateId.PLACEHOLDER);
	}

	@Test
	public void testRegistryGetHandlerWithNoArgMalcontentedConstructor() throws Exception {
		registry.register(new ComponentKey(TemplateId.PLACEHOLDER, Program.ALL), NoArgMalcontentedConstructor.class);
		InputDecoder decoder = registry.get(TemplateId.PLACEHOLDER);
		assertThat(decoder).isNull();
	}

	@Test(expected = SevereRuntimeException.class)
	public void testRegistryGetHandlerWithNoArgThrowableConstructor() throws Exception {
		registry.register(new ComponentKey(TemplateId.PLACEHOLDER, Program.ALL), NoArgThrowableConstructor.class);
		registry.get(TemplateId.PLACEHOLDER);
	}

	@Test
	public void testRegistryAddDuplicate() throws Exception {
		registry.register(new ComponentKey(TemplateId.PLACEHOLDER, Program.ALL), Placeholder.class);
		registry.register(new ComponentKey(TemplateId.PLACEHOLDER, Program.ALL), AnotherPlaceholder.class);
		InputDecoder decoder = registry.get(TemplateId.PLACEHOLDER);
		assertWithMessage("Registry should have overwritten id with the second one.")
				.that(decoder).isInstanceOf(AnotherPlaceholder.class);
	}

	@Test
	public void testSize() {
		assertWithMessage("Registry does not have contents")
				.that(registry.size()).isGreaterThan(0);
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

class NoDefaultConstructor implements InputDecoder {

	public NoDefaultConstructor(String meep) {
	}

	@Override
	public Node decode(Element xmlDoc) {
		return null;
	}
}

class MalcontentedConstructor implements InputDecoder {

	public MalcontentedConstructor(Context meep) {
		throw new RuntimeException("just cause");
	}

	@Override
	public Node decode(Element xmlDoc) {
		return null;
	}
}

class ThrowableConstructor implements InputDecoder {

	public ThrowableConstructor(Context meep) throws Throwable{
		throw new Throwable("just cause");
	}

	@Override
	public Node decode(Element xmlDoc) {
		return null;
	}
}

class NoArgMalcontentedConstructor implements InputDecoder {

	public NoArgMalcontentedConstructor() {
		throw new RuntimeException("just cause");
	}

	@Override
	public Node decode(Element xmlDoc) {
		return null;
	}
}

class NoArgThrowableConstructor implements InputDecoder {

	public NoArgThrowableConstructor() throws Throwable{
		throw new Throwable("just cause");
	}

	@Override
	public Node decode(Element xmlDoc) {
		return null;
	}
}


