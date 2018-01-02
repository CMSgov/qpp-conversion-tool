package gov.cms.qpp.test.enums;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public interface EnumContract {

	@Test
	default void testValueOfNullThrows() throws NoSuchMethodException, SecurityException {
		Class<? extends Enum<?>> type = getEnumType();
		Method valueOf = type.getMethod("valueOf", String.class);
		valueOf.setAccessible(true);
		Assertions.assertThrows(IllegalArgumentException.class, () -> valueOf.invoke(null, null));
	}

	@Test
	default void testValueOfInvalidThrows() throws NoSuchMethodException, SecurityException {
		Class<? extends Enum<?>> type = getEnumType();
		Method valueOf = type.getMethod("valueOf", String.class);
		valueOf.setAccessible(true);
		Assertions.assertThrows(InvocationTargetException.class, () -> valueOf.invoke(null, UUID.randomUUID().toString()));
	}

	@Test
	default void testValueOfValid() throws Exception {
		Class<? extends Enum<?>> type = getEnumType();
		Enum<?> value = type.getEnumConstants()[0];
		Method valueOf = type.getMethod("valueOf", String.class);
		valueOf.setAccessible(true);
		valueOf.invoke(null, value.name());
	}

	@Test
	default void testName() {
		Enum<?> value = getEnumType().getEnumConstants()[0];
		value.name(); // code coverage
	}

	@Test
	default void testValues() throws Exception {
		Class<? extends Enum<?>> type = getEnumType();
		Method values = type.getMethod("values");
		values.setAccessible(true);
		values.invoke(null);
	}

	Class<? extends Enum<?>> getEnumType();

}
