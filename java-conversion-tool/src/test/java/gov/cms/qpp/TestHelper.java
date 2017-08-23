package gov.cms.qpp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.mockito.ArgumentMatchers;
import org.powermock.api.mockito.PowerMockito;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.validate.NodeValidator;
import gov.cms.qpp.conversion.validate.QrdaValidator;

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
	 * Allows an additional {@link gov.cms.qpp.conversion.validate.NodeValidator} to validate a specified templateId.
	 *
	 * This can be used to simulate the @Validator annotation in a test.
	 *
	 * @param templateId The templateId that the test validator will validate.
	 * @param validator The class of the validator.
	 * @param required Whether the validator is required.
	 * @param spy An existing spied QrdaValidator that will be extended.
	 * @throws Exception If the mocking fails.
	 */
	public static QrdaValidator mockValidator(Context context, Class<? extends NodeValidator> validator, boolean required) throws Exception {
		return mockValidator(context, validator, required, null);
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
	public static QrdaValidator mockValidator(Context context, Class<? extends NodeValidator> validator, boolean required,  QrdaValidator spy) throws Exception {
		if (spy == null) {
			spy = PowerMockito.spy(new QrdaValidator(context));
		}

		mockQrdaValidator(spy, validator, required);

		return spy;
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
	private static void mockQrdaValidator(QrdaValidator spy, Class<? extends NodeValidator> validator, boolean required) throws Exception {
		PowerMockito.doReturn(required).when(spy, METHOD_IS_VALIDATION_REQUIRED, ArgumentMatchers.isA(validator));
	}

	private TestHelper() {
	}

}
