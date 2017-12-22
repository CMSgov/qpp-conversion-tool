package gov.cms.qpp.conversion.api.model;


import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.function.Consumer;

import static com.google.common.truth.Truth.assertThat;
import static junit.framework.TestCase.fail;

class MetadataTest {

	@Test
	void equalsContract() {
		EqualsVerifier.forClass(Metadata.class)
				.usingGetClass()
				.suppress(Warning.NONFINAL_FIELDS)
				.verify();
	}

	@Test
	void testDateCreatedOnConstruction() {
		Metadata metadata = new Metadata();
		assertThat(metadata.getCreatedDate()).isNotNull();
	}

	@Test
	void testGetCpcProcessedCreateDateWithNullProcessed() {
		Metadata metadata = new Metadata();
		assertThat(metadata.getCpcProcessedCreateDate()).isNull();
	}

	@Test
	void testGetCpcProcessedCreateDateWithNonNullProcessed() {
		Metadata metadata = new Metadata();
		Boolean processed = false;
		metadata.setCpcProcessed(processed);
		assertThat(metadata.getCpcProcessedCreateDate()).startsWith(processed + "#");
	}

	@Test
	void testSetCpcProcessedCreateDateWithoutHash() {
		Metadata metadata = new Metadata();
		Boolean processedBefore = metadata.getCpcProcessed();
		Date createDateBefore = metadata.getCreatedDate();

		metadata.setCpcProcessedCreateDate("DogCow");

		assertThat(metadata.getCpcProcessed()).isEqualTo(processedBefore);
		assertThat(metadata.getCreatedDate()).isEqualTo(createDateBefore);
	}

	@Test
	void testSetCpcProcessedCreateDateWithHash() {
		Metadata metadata = new Metadata();
		metadata.setCpcProcessed(false);
		Date createDateBefore = metadata.getCreatedDate();

		metadata.setCpcProcessedCreateDate("true#2017-12-08T18:32:54.846Z");

		assertThat(metadata.getCpcProcessed()).isTrue();
		assertThat(metadata.getCreatedDate()).isLessThan(createDateBefore);
	}

	@Test
	void plumbing() {
		Consumer<Method> consumer = harness(new Metadata());

		Arrays.stream(Metadata.class.getDeclaredMethods())
				.filter(this::junk)
				.forEach(consumer);
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
