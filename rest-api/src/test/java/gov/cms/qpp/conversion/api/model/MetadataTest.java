package gov.cms.qpp.conversion.api.model;


import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Consumer;

import static junit.framework.TestCase.fail;

public class MetadataTest {

	@Test
	public void equalsContract() {
		EqualsVerifier.forClass(Metadata.class)
				.usingGetClass()
				.suppress(Warning.NONFINAL_FIELDS)
				.verify();
	}

	@Test
	public void plumbing() {
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
		return (T) Array.get(Array.newInstance(clazz, 1), 0);
	}
}
