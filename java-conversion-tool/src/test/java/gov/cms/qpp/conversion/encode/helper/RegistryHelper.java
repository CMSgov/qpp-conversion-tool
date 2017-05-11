package gov.cms.qpp.conversion.encode.helper;

import gov.cms.qpp.conversion.encode.JsonOutputEncoder;
import gov.cms.qpp.conversion.encode.QppOutputEncoder;
import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Registry;
import org.apache.commons.io.output.NullOutputStream;

import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

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
	public static void setEncoderRegistry(Registry<String, JsonOutputEncoder> newRegistry) throws NoSuchFieldException, IllegalAccessException {
		final Field field = QppOutputEncoder.class.getDeclaredField("encoders");
		field.setAccessible(true);
		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
		field.set(null, newRegistry);

	}

	/**
	 * This helper method will prevent an Encoder from being placed into the Registry
	 * This is useful for testing Invalid Encoders
	 *
	 * @param missingClassName The class of the item to keep out of the registry
	 * @return The newly created registry usually passed to SetEncoderRegistry
	 */
	public static Registry<String, JsonOutputEncoder> makeInvalidRegistry(String missingClassName) {
		return new Registry<String, JsonOutputEncoder>(Encoder.class) {
			/**
			 * Overrides the creation of the Registry method of QppEncoder
			 * @param className
			 * @return The Class of the newly added Encoder. An Item with the Encoder Annotation.
			 * @throws ClassNotFoundException thrown when the class under observation matches the name to
			 * keep out of the registry
			 */
			@Override
			protected Class<?> getAnnotatedClass(String className) throws ClassNotFoundException {
				if (missingClassName.equals(className)) {
					System.setErr(new PrintStream(NullOutputStream.NULL_OUTPUT_STREAM));
					throw new ClassNotFoundException();
				}
				return Class.forName(className);
			}
		};
	}
}
