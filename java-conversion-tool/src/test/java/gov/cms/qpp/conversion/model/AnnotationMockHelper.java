package gov.cms.qpp.conversion.model;

import gov.cms.qpp.conversion.decode.QppXmlDecoder;
import gov.cms.qpp.conversion.encode.JsonOutputEncoder;
import gov.cms.qpp.conversion.encode.QppOutputEncoder;
import gov.cms.qpp.conversion.validate.NodeValidator;
import gov.cms.qpp.conversion.validate.QrdaValidator;
import org.mockito.ArgumentMatchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.reflect.Whitebox;

/**
 * A utility class to help mock annotations
 */
public class AnnotationMockHelper {

	private static final String METHOD_IS_VALIDATION_REQUIRED = "isValidationRequired";

	/**
	 * Private and empty because we do not want this class instantiated.
	 */
	private AnnotationMockHelper() {
		//private and empty because we do not want this class instantiated.
	}

	/**
	 * Allows a {@link gov.cms.qpp.conversion.validate.NodeValidator} to validate a specified templateId.
	 *
	 * This can be used to simulate the @Validator annotation in a test.
	 *
	 * @param templateId The templateId that the test validator will validate.
	 * @param validator The class of the validator.
	 * @param required Whether the validator is required.
	 * @return A spied QrdaValidator that has all the appropriate hooks in place to validate a test validator
	 * @throws Exception If the mocking fails.
	 */
	public static QrdaValidator mockValidator(final TemplateId templateId, final Class<? extends NodeValidator> validator,
	                                          final boolean required) throws Exception {
		return mockValidator(templateId, validator, required, null);
	}

	/**
	 * Allows an additional {@link gov.cms.qpp.conversion.validate.NodeValidator} to validate a specified templateId.
	 *
	 * This can be used to simulate the @Validator annotation in a test.  Passing in a previously spied
	 * {@link gov.cms.qpp.conversion.validate.QrdaValidator} allows additional NodeValidators.
	 *
	 * @param templateId The templateId that the test validator will validate.
	 * @param validator The class of the validator.
	 * @param required Whether the validator is required.
	 * @param spy An existing spied QrdaValidator that will be extended.
	 * @return A spied QrdaValidator that has all the appropriate hooks in place to validate a test validator
	 * @throws Exception If the mocking fails.
	 */
	public static QrdaValidator mockValidator(final TemplateId templateId, final Class<? extends NodeValidator> validator,
	                                          final boolean required,  QrdaValidator spy) throws Exception {
		registerValidator(templateId, validator);

		if(null == spy) {
			spy = PowerMockito.spy(new QrdaValidator());
		}

		mockQrdaValidator(spy, templateId, validator, required);

		return spy;
	}

	/**
	 * Registers the specified {@link gov.cms.qpp.conversion.validate.NodeValidator} in the Registry for the specified
	 * templateId.
	 *
	 * @param templateId The templateId string that you want the NodeValidator to validate.
	 * @param validator The NodeValidator that is stored in the registry.
	 */
	private static void registerValidator(TemplateId templateId, Class<? extends NodeValidator> validator) {
		Registry<NodeValidator> registry = Whitebox.getInternalState(QrdaValidator.class, Registry.class);
		registry.register(new ComponentKey(templateId, Program.ALL), validator);
	}

	/**
	 * Mocks the {@link gov.cms.qpp.conversion.validate.QrdaValidator} spy such that the @Validator annotation is
	 * simulated.
	 *
	 * @param spy The QrdaValidator spy.
	 * @param templateId The templateId to be mocked.
	 * @param validator The NodeValidator to be used to validate the templateId.
	 * @param required Whether the validation is required.
	 * @throws Exception If the mocking fails.
	 */
	private static void mockQrdaValidator(QrdaValidator spy, TemplateId templateId,
	                                      Class<? extends NodeValidator> validator, boolean required)
		throws Exception {
		PowerMockito.doReturn(required).when(spy, METHOD_IS_VALIDATION_REQUIRED, ArgumentMatchers.isA(validator));
	}

	/**
	 * Registers the specified {@link gov.cms.qpp.conversion.decode.QppXmlDecoder} in the Registry for the specified
	 * templateId.
	 *
	 * @param templateId The templateId string that you want the decoder to decode.
	 * @param decoder The decoder to be stored in the registry.
	 */
	public static void mockDecoder(final TemplateId templateId, final Class<? extends QppXmlDecoder> decoder) {
		final Registry<QppXmlDecoder> registry = Whitebox.getInternalState(QppXmlDecoder.class, Registry.class);
		registry.register(new ComponentKey(templateId, Program.ALL), decoder);
	}

	/**
	 * Registers the specified {@link gov.cms.qpp.conversion.encode.JsonOutputEncoder} in the Registry for the specified
	 * templateId.
	 *
	 * @param templateId The templateId string that you want the decoder to decode.
	 * @param encoder The decoder to be stored in the registry.
	 */
	public static void mockEncoder(final TemplateId templateId, final Class<? extends JsonOutputEncoder> encoder) {
		final Registry<JsonOutputEncoder> registry = Whitebox.getInternalState(QppOutputEncoder.class, Registry.class);
		registry.register(new ComponentKey(templateId, Program.ALL), encoder);
	}
}
