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

	private RegistryHelper() {
		//Private constructor Helper class has only public static methods
	}

	public static void setEncoderRegistry(Registry<String, JsonOutputEncoder> newRegistry)throws Exception  {
		final Field field = QppOutputEncoder.class.getDeclaredField("ENCODERS");
		field.setAccessible(true);
		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		modifiersField.setInt(field, field.getModifiers()& ~Modifier.FINAL );
		field.set(null, newRegistry);

	}

	public static Registry<String, JsonOutputEncoder> makeInvalidRegistry(String missingClassName) {
		return new Registry<String, JsonOutputEncoder>(Encoder.class) {
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
