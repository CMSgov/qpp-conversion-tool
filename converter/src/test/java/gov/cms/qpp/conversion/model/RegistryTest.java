package gov.cms.qpp.conversion.model;

import org.jdom2.Element;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.decode.AggregateCountDecoder;
import gov.cms.qpp.conversion.decode.DecodeResult;
import gov.cms.qpp.conversion.decode.QrdaDecoder;
import gov.cms.qpp.conversion.encode.AggregateCountEncoder;

import java.util.Iterator;
import java.util.Set;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

class RegistryTest {

	private Context context;
	private Registry<QrdaDecoder> registry;

	@BeforeEach
	void before() {
		context = new Context();
		registry = context.getRegistry(Decoder.class);
	}

	@Test
	void testRegistryExistsByDefault() {
		try {
			registry.register(new ComponentKey(TemplateId.PLACEHOLDER, Program.ALL), Placeholder.class);
		} catch (NullPointerException e) {
			Assertions.fail("Registry should always exist.");
		}
	}

	@Test
	void testRegistryGetDefaultConverterHandler() {
		context.setProgram(Program.CPC);
		registry.register(new ComponentKey(TemplateId.PLACEHOLDER, Program.ALL), Placeholder.class);
		QrdaDecoder decoder = registry.get(TemplateId.PLACEHOLDER);

		assertWithMessage("Registry should return %s instance.", Placeholder.class.getName())
				.that(decoder).isInstanceOf(Placeholder.class);
	}

	@Test
	void testRegistryGetProgramSpecificConverterHandler() {
		context.setProgram(Program.CPC);
		registry.register(new ComponentKey(TemplateId.PLACEHOLDER, Program.ALL), Placeholder.class);
		registry.register(new ComponentKey(TemplateId.PLACEHOLDER, Program.CPC), AnotherPlaceholder.class);
		QrdaDecoder decoder = registry.get(TemplateId.PLACEHOLDER);

		assertWithMessage("Registry should return %s instance.", AnotherPlaceholder.class.getName())
				.that(decoder).isInstanceOf(AnotherPlaceholder.class);
	}

	@Test
	void testRegistryInclusiveGetDefaultConverterHandler() {
		context.setProgram(Program.CPC);
		registry.register(new ComponentKey(TemplateId.PLACEHOLDER, Program.ALL), Placeholder.class);
		Set<QrdaDecoder> decoders = registry.inclusiveGet(TemplateId.PLACEHOLDER);

		assertWithMessage("Registry should return %s instance.", Placeholder.class.getName())
				.that(decoders.iterator().next()).isInstanceOf(Placeholder.class);
	}

	@Test
	void testRegistryInclusiveGetProgramSpecificConverterHandler() {
		context.setProgram(Program.CPC);
		registry.register(new ComponentKey(TemplateId.PLACEHOLDER, Program.ALL), Placeholder.class);
		registry.register(new ComponentKey(TemplateId.PLACEHOLDER, Program.CPC), AnotherPlaceholder.class);
		Set<QrdaDecoder> decoders = registry.inclusiveGet(TemplateId.PLACEHOLDER);

		assertWithMessage("Should return two decoders")
				.that(decoders).hasSize(2);
	}

	@Test
	void testRegistryInclusiveGetPrioritizesGeneral() {
		context.setProgram(Program.CPC);
		registry.register(new ComponentKey(TemplateId.PLACEHOLDER, Program.CPC), AnotherPlaceholder.class);
		registry.register(new ComponentKey(TemplateId.PLACEHOLDER, Program.ALL), Placeholder.class);
		Set<QrdaDecoder> decoders = registry.inclusiveGet(TemplateId.PLACEHOLDER);
		Iterator<QrdaDecoder> iterator = decoders.iterator();

		assertWithMessage("First Registry entry should be a %s instance.", Placeholder.class.getName())
				.that(iterator.next()).isInstanceOf(Placeholder.class);
		assertWithMessage("Second Registry entry should be a %s instance.", AnotherPlaceholder.class.getName())
				.that(iterator.next()).isInstanceOf(AnotherPlaceholder.class);
	}

	// This test must reside here in order to call the protected methods on the
	// registry
	@Test
	void testRegistry_placeAndFetch() {
		Set<ComponentKey> componentKeys = registry.getComponentKeys(AggregateCountDecoder.class);

		assertThat(componentKeys).hasSize(1);
		for (ComponentKey componentKey : componentKeys) {
			QrdaDecoder decoder = registry.get(componentKey.getTemplate());

			assertWithMessage("A handler is expected")
					.that(decoder).isNotNull();
			assertWithMessage("Handler should be an instance of the handler for the given XPATH")
					.that(decoder).isInstanceOf(AggregateCountDecoder.class);
		}
	}

	@Test
	void testRegistry_getTemplateIds() {
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
	void testRegistry_getTemplateIds_NullReturn() {
		Set<ComponentKey> componentKeys = context.getRegistry(SuppressWarnings.class).getComponentKeys(Placeholder.class);
		assertWithMessage("A componentKey is not expected")
				.that(componentKeys).isEmpty();
	}

	@Test
	void testRegistryGetHandlerThatFailsConstruction() {
		registry.register(new ComponentKey(TemplateId.PLACEHOLDER, Program.ALL), PrivateConstructor.class);
		QrdaDecoder decoder = registry.get(TemplateId.PLACEHOLDER);
		assertWithMessage("Registry with a private constructor should be constructable")
				.that(decoder).isNotNull();
	}

	@Test
	void testRegistryGetHandlerWithNoDefaultConstructor() {
		registry.register(new ComponentKey(TemplateId.PLACEHOLDER, Program.ALL), NoDefaultConstructor.class);
		QrdaDecoder decoder = registry.get(TemplateId.PLACEHOLDER);
		assertWithMessage("Registry without a default constructor should not be constructable")
				.that(decoder).isNull();
	}

	@Test
	void testRegistryGetHandlerWithMalcontentedConstructor() {
		registry.register(new ComponentKey(TemplateId.PLACEHOLDER, Program.ALL), MalcontentedConstructor.class);
		QrdaDecoder decoder = registry.get(TemplateId.PLACEHOLDER);
		assertThat(decoder).isNull();
	}

	@Test
	void testRegistryGetHandlerWithThrowableConstructor() {
		Assertions.assertThrows(SevereRuntimeException.class, () -> {
			registry.register(new ComponentKey(TemplateId.PLACEHOLDER, Program.ALL), ThrowableConstructor.class);
			registry.get(TemplateId.PLACEHOLDER);
		});
	}

	@Test
	void testRegistryGetHandlerWithNoArgMalcontentedConstructor() {
		registry.register(new ComponentKey(TemplateId.PLACEHOLDER, Program.ALL), NoArgMalcontentedConstructor.class);
		QrdaDecoder decoder = registry.get(TemplateId.PLACEHOLDER);
		assertThat(decoder).isNull();
	}

	@Test
	void testRegistryGetHandlerWithNoArgThrowableConstructor() {
		Assertions.assertThrows(SevereRuntimeException.class, () -> {
			registry.register(new ComponentKey(TemplateId.PLACEHOLDER, Program.ALL), NoArgThrowableConstructor.class);
			registry.get(TemplateId.PLACEHOLDER);
		});
	}

	@Test
	void testRegistryAddDuplicate() {
		registry.register(new ComponentKey(TemplateId.PLACEHOLDER, Program.ALL), Placeholder.class);
		registry.register(new ComponentKey(TemplateId.PLACEHOLDER, Program.ALL), AnotherPlaceholder.class);
		QrdaDecoder decoder = registry.get(TemplateId.PLACEHOLDER);
		assertWithMessage("Registry should have overwritten id with the second one.")
				.that(decoder).isInstanceOf(AnotherPlaceholder.class);
	}

	@Test
	void testSize() {
		assertThat(registry.size()).isGreaterThan(0);
	}
}

@SuppressWarnings("unused") // this is here for a the annotation tests
class Placeholder extends QrdaDecoder {

	private String unused;

	public Placeholder() {
		super(null);
	}

	@Override
	public DecodeResult decode(Element xmlDoc, Node node) {
		return null;
	}
}

@SuppressWarnings("unused") // this is here for a the annotation tests
class AnotherPlaceholder extends QrdaDecoder {

	private String unused;

	public AnotherPlaceholder() {
		super(null);
	}

	@Override
	public DecodeResult decode(Element xmlDoc, Node node) {
		return null;
	}
}

class PrivateConstructor extends QrdaDecoder {

	private PrivateConstructor() {
		super(null);
	}

	@Override
	public DecodeResult decode(Element xmlDoc, Node node) {
		return null;
	}
}

class NoDefaultConstructor extends QrdaDecoder {

	public NoDefaultConstructor(String meep) {
		super(null);
	}

	@Override
	public DecodeResult decode(Element xmlDoc, Node node) {
		return null;
	}
}

class MalcontentedConstructor extends QrdaDecoder {

	public MalcontentedConstructor(Context meep) {
		super(null);
		throw new RuntimeException("just cause");
	}

	@Override
	public DecodeResult decode(Element xmlDoc, Node node) {
		return null;
	}
}

class ThrowableConstructor extends QrdaDecoder {

	public ThrowableConstructor(Context meep) throws Throwable {
		super(null);
		throw new Throwable("just cause");
	}

	@Override
	public DecodeResult decode(Element xmlDoc, Node node) {
		return null;
	}
}

class NoArgMalcontentedConstructor extends QrdaDecoder {

	public NoArgMalcontentedConstructor() {
		super(null);
		throw new RuntimeException("just cause");
	}

	@Override
	public DecodeResult decode(Element xmlDoc, Node node) {
		return null;
	}
}

class NoArgThrowableConstructor extends QrdaDecoder {

	public NoArgThrowableConstructor() throws Throwable {
		super(null);
		throw new Throwable("just cause");
	}

	@Override
	public DecodeResult decode(Element xmlDoc, Node node) {
		return null;
	}
}
