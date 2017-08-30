package gov.cms.qpp;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.decode.InputDecoder;
import gov.cms.qpp.conversion.encode.OutputEncoder;
import gov.cms.qpp.conversion.encode.QppOutputEncoder;
import gov.cms.qpp.conversion.model.ComponentKey;
import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Validator;
import gov.cms.qpp.conversion.validate.NodeValidator;
import gov.cms.qpp.conversion.validate.QrdaValidator;
import org.mockito.ArgumentMatchers;
import org.powermock.api.mockito.PowerMockito;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestHelper {

	private static final String METHOD_IS_VALIDATION_REQUIRED = "isValidationRequired";

	/**
	 * Retrieve fixture file content using "src/test/resources/fixtures/" as a base directory
	 *
	 * @param name file name
	 * @return file content
	 * @throws IOException when it can't locate / read the file
	 */
	public static String getFixture(final String name) throws IOException {
		Path path = Paths.get("src/test/resources/fixtures/" + name);
		return new String(Files.readAllBytes(path));
	}

	/**
	 * Registers the {@link InputDecoder} in the {@link gov.cms.qpp.conversion.model.Registry}.
	 *
	 * @param context The context that contains the Registry.
	 * @param decoder The decoder to register.
	 * @param componentKey The combination of a TemplateId and Program that the decoder will be registered for.
	 */
	public static void mockDecoder(Context context, Class<? extends InputDecoder> decoder, ComponentKey componentKey) {
		context.getRegistry(Decoder.class).register(componentKey, decoder);
	}

	/**
	 * Allows an additional {@link gov.cms.qpp.conversion.validate.NodeValidator} to validate a specified templateId.
	 *
	 * This can be used to simulate the @Validator annotation in a test.
	 *
	 * @param context Holder of contextual information that qualifies the validation.
	 * @param validator The class of the validator.
	 * @param componentKey The combination of TemplateId and Program that the validator validates.
	 * @param required Whether the validator is required.
	 * @return A spied QrdaValidator that has all the appropriate hooks in place to validate a test validator
	 * @throws Exception If the mocking fails.
	 */
	public static QrdaValidator mockValidator(Context context, Class<? extends NodeValidator> validator, final ComponentKey componentKey, boolean required) throws Exception {
		return mockValidator(context, validator, componentKey, required, null);
	}

	/**
	 * Allows an additional {@link gov.cms.qpp.conversion.validate.NodeValidator} to validate a specified templateId.
	 *
	 * This can be used to simulate the @Validator annotation in a test.  Passing in a previously spied
	 * {@link gov.cms.qpp.conversion.validate.QrdaValidator} allows additional NodeValidators.
	 *
	 * @param context Holder of contextual information that qualifies the validation.
	 * @param validator The class of the validator.
	 * @param componentKey The combination of TemplateId and Program that the validator validates.
	 * @param required Whether the validator is required.
	 * @param spy An existing spied QrdaValidator that will be extended.
	 * @return A spied QrdaValidator that has all the appropriate hooks in place to validate a test validator
	 * @throws Exception If the mocking fails.
	 */
	public static QrdaValidator mockValidator(Context context, Class<? extends NodeValidator> validator, final ComponentKey componentKey, boolean required, QrdaValidator spy) throws Exception {
		if (spy == null) {
			spy = PowerMockito.spy(new QrdaValidator(context));
		}

		registerValidator(context, validator, componentKey);
		mockQrdaValidator(spy, validator, required);

		return spy;
	}

	/**
	 * Registers the {@link QppOutputEncoder} in the {@link gov.cms.qpp.conversion.model.Registry}.
	 *
	 * @param context The context that contains the Registry.
	 * @param encoder The encoder to register.
	 * @param componentKey The combination of a TemplateId and Program that the encoder will be registered for.
	 */
	public static void mockEncoder(Context context, Class<? extends OutputEncoder> encoder, ComponentKey componentKey) {
		context.getRegistry(Encoder.class).register(componentKey, encoder);
	}

	/**
	 * Registers the {@link gov.cms.qpp.conversion.validate.NodeValidator} in the {@link gov.cms.qpp.conversion.model.Registry}.
	 *
	 * @param context The context that contains the Registry.
	 * @param validator The validator to register.
	 * @param componentKey The combination of a TemplateId and Program that the validator will be registered for.
	 */
	private static void registerValidator(Context context, Class<? extends NodeValidator> validator, final ComponentKey componentKey) {
		context.getRegistry(Validator.class).register(componentKey, validator);
	}

	/**
	 * Mocks the {@link gov.cms.qpp.conversion.validate.QrdaValidator} spy such that the @Validator annotation is
	 * simulated.
	 *
	 * @param spy The QrdaValidator spy.
	 * @param validator The NodeValidator to be used to validate the templateId.
	 * @param required Whether the validation is required.
	 * @throws Exception If the mocking fails.
	 */
	private static void mockQrdaValidator(QrdaValidator spy, Class<? extends NodeValidator> validator, boolean required) throws Exception {
		PowerMockito.doReturn(required).when(spy, METHOD_IS_VALIDATION_REQUIRED, ArgumentMatchers.isA(validator));
	}

	private TestHelper() {
	}

}
