package gov.cms.qpp.test.enums;

import org.junit.jupiter.api.Test;

public interface EnumContract {

	@Test
	default void testName() {
		Enum<?> value = getEnumType().getEnumConstants()[0];
		value.name(); // code coverage
	}

	Class<? extends Enum<?>> getEnumType();

}
