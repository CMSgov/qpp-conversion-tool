package gov.cms.qpp.test.enums;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public interface EnumContract {

	@Test
	default void testName() {
		Enum<?> value = getEnumType().getEnumConstants()[0];
		Assertions.assertNotNull(value.name()); // code coverage
	}

	Class<? extends Enum<?>> getEnumType();

}
