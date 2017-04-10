package gov.cms.qpp.conversion.model;

import gov.cms.qpp.conversion.validate.NodeValidator;
import gov.cms.qpp.conversion.validate.QrdaValidator;
import org.mockito.Matchers;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.reflect.Whitebox;

public class AnnotationMockHelper {

	private static final String METHOD_IS_VALIDATION_REQUIRED = "isValidationRequired";
	private static final String METHOD_GET_TEMPLATE_ID = "getTemplateId";

	public static QrdaValidator mockValidator(final String templateId, final Class<? extends NodeValidator> validator, final boolean required) throws Exception {
		return mockValidator(templateId, validator, required, null);
	}

	public static QrdaValidator mockValidator(final String templateId, final Class<? extends NodeValidator> validator, final boolean required,  QrdaValidator spy) throws Exception {
		registerValidator(templateId, validator);

		if(null == spy) {
			spy = PowerMockito.spy(new QrdaValidator());
		}

		mockQrdaValidator(spy, templateId, validator, required);

		return spy;
	}

	private static void registerValidator(final String templateId, final Class<? extends NodeValidator> validator) {
		final Registry<String, NodeValidator> registry = Whitebox.getInternalState(QrdaValidator.class, Registry.class);
		registry.register(templateId, validator);
	}

	private static void mockQrdaValidator(final QrdaValidator spy, final String templateId, final Class<? extends NodeValidator> validator, final boolean required) throws Exception {
		PowerMockito.doReturn(required).when(spy, METHOD_IS_VALIDATION_REQUIRED, Matchers.isA(validator));
		PowerMockito.doReturn(templateId).when(spy, METHOD_GET_TEMPLATE_ID, Matchers.isA(validator));
	}
}
