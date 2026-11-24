package gov.cms.qpp.conversion.api.model;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;
import gov.cms.qpp.conversion.model.error.Detail;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

class MetadataTest {

	private static final String EXPECTED_DATE = "2018-12-12T08:30:30.285Z";

	@Test
	void equalsContract() {
		EqualsVerifier.forClass(Metadata.class)
				.usingGetClass()
				.suppress(Warning.NONFINAL_FIELDS)
				.verify();
	}

	@Test
	void testDateCreatedOnConstruction() {
		Metadata metadata = Metadata.create();
		assertThat(metadata.getCreatedDate()).isNotNull();
	}

	@Test
	void enhancedClientSchemaCanBeCreated() {
		assertThat(TableSchema.fromBean(Metadata.class)).isNotNull();
	}

	@Test
	void testGetCpcProcessedCreateDateWithNullProcessed() {
		Metadata metadata = Metadata.create();
		assertThat(metadata.getCpcProcessedCreateDate()).isNull();
	}

	@Test
	void testGetCpcProcessedCreateDateWithNonNullProcessed() {
		Metadata metadata = Metadata.create();
		Boolean processed = false;
		metadata.setCpcProcessed(processed);
		assertThat(metadata.getCpcProcessedCreateDate()).startsWith(processed + "#");
	}

	@Test
	void testSetCpcProcessedCreateDateWithoutHash() {
		Metadata metadata = Metadata.create();
		Boolean processedBefore = metadata.getCpcProcessed();
		Instant createDateBefore = metadata.getCreatedDate();

		metadata.setCpcProcessedCreateDate("DogCow");

		assertThat(metadata.getCpcProcessed()).isEqualTo(processedBefore);
		assertThat(metadata.getCreatedDate()).isEqualTo(createDateBefore);
	}

	@Test
	void testSetCpcProcessedCreateDateWithHash() {
		Metadata metadata = Metadata.create();
		metadata.setCpcProcessed(false);
		Instant createDateBefore = metadata.getCreatedDate();

		metadata.setCpcProcessedCreateDate("true#2017-12-08T18:32:54.846Z");

		assertThat(metadata.getCpcProcessed()).isTrue();
		assertThat(metadata.getCreatedDate()).isLessThan(createDateBefore);
	}

	@Test
	void plumbing() {
		Consumer<Method> consumer = harness(Metadata.create());

		Arrays.stream(Metadata.class.getDeclaredMethods())
				.filter(this::junk)
				.forEach(consumer);
	}

	@Test
	void testConvertInstantToString() {
		Metadata.InstantConverter instantConverter = new Metadata.InstantConverter();
		Instant testInstant = Instant.parse(EXPECTED_DATE);

		assertThat(instantConverter.transformFrom(testInstant).s()).isEqualTo(EXPECTED_DATE);
	}

	@Test
	void testConvertStringToInstant() {
		Instant expected = Instant.parse(EXPECTED_DATE);
		Metadata.InstantConverter instantConverter = new Metadata.InstantConverter();
		Instant outcome = instantConverter.transformTo(AttributeValue.builder().s(EXPECTED_DATE).build());

		assertThat(outcome).isEquivalentAccordingToCompareTo(expected);
	}

	@Test
	void detailsConverterRoundTripsJson() {
		Detail detail = new Detail();
		detail.setMessage("validation detail");
		Details details = new Details();
		details.setDetails(List.of(detail));

		Metadata.DetailsConverter converter = new Metadata.DetailsConverter();
		Details decoded = converter.transformTo(converter.transformFrom(details));

		assertThat(decoded).isEqualTo(details);
	}

	private Consumer<Method> harness(Metadata meta) {
		return method -> {
			try {
				Class<?>[] paramTypes = method.getParameterTypes();
				if (paramTypes.length > 0) {
					method.invoke(meta, getDefaultValue(paramTypes[0]));
				} else {
					method.invoke(meta);
				}
			} catch (IllegalAccessException | InvocationTargetException e) {
				fail("Could not interact with metadata model using " + method.getName());
			}
		};
	}

	private boolean junk(Method method) {
		return !method.getName().matches("(.*)jacoco(.*)");
	}

	@SuppressWarnings("unchecked")
	private static <T> T getDefaultValue(Class<T> clazz) {
		T defaultValue = null;

		try {
			defaultValue = clazz.getConstructor().newInstance();
		}
		catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			defaultValue = (T) Array.get(Array.newInstance(clazz, 1), 0);
		}

		return defaultValue;
	}
}
