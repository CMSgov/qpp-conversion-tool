package gov.cms.qpp.conversion.encode.helper;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import gov.cms.qpp.conversion.encode.JsonOutputEncoder;
import gov.cms.qpp.conversion.encode.QppOutputEncoder;
import gov.cms.qpp.conversion.model.Registry;

/**
 * This helper class helps tests with creating invalid encoder registries.
 */
public class RegistryHelper {

	/**
	 * Private constructor on helper class with only static methods
	 */
	private RegistryHelper() {
		//Private constructor Helper class has only public static methods
	}

	/**
	 * This will set a Registry onto the QppEncoder class
	 * Used by tests trying to cover missing encoder conditions
	 *
	 * @param newRegistry Encoder registry to replace on QppEncoder
	 * @throws NoSuchFieldException   Java Reflection API
	 * @throws IllegalAccessException Can be caused if a Security manager is in place
	 */
	public static void setEncoderRegistry(Registry<JsonOutputEncoder> newRegistry) throws NoSuchFieldException, IllegalAccessException {
		final Field field = QppOutputEncoder.class.getDeclaredField("ENCODERS");
		field.setAccessible(true);
		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
		field.set(null, newRegistry);

	}
}
