package gov.cms.qpp.conversion.encode;

import static com.google.common.truth.Truth.assertWithMessage;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;

import java.util.EnumSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Registry;
import gov.cms.qpp.conversion.model.TemplateId;

/**
 * This class tests basic Encoder functionality for the defined list of tmeplateId's
 */
class EncoderTest {

	private final Set<TemplateId> templateIds = EnumSet.of(
			TemplateId.CLINICAL_DOCUMENT,
			TemplateId.MEASURE_SECTION_V2,
			TemplateId.IA_SECTION,
			TemplateId.ACI_SECTION,
			TemplateId.ACI_AGGREGATE_COUNT,
			TemplateId.MEASURE_DATA_CMS_V2,
			TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2,
			TemplateId.PAYER_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2,
			TemplateId.RACE_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2,
			TemplateId.REPORTING_STRATUM_CMS,
			TemplateId.SEX_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2,
			TemplateId.ETHNICITY_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2,
			TemplateId.REPORTING_PARAMETERS_ACT,
			TemplateId.CONTINUOUS_VARIABLE_MEASURE_VALUE_CMS,
			TemplateId.MEASURE_PERFORMED,
			TemplateId.ACI_NUMERATOR_DENOMINATOR,
			TemplateId.ACI_MEASURE_PERFORMED_REFERENCE_AND_RESULTS,
			TemplateId.ACI_NUMERATOR,
			TemplateId.ACI_DENOMINATOR,
			TemplateId.IA_MEASURE);

	/**
	 * decodeTemplateIds for each TemplateId in the list ensure that it exists in
	 * the registry.
	 * @throws Exception
	 */
	@Test
	void decodeTemplateIds() throws Exception {
		Registry<OutputEncoder> registry = new Registry<>(new Context(), Encoder.class);
		for (TemplateId templateId : templateIds) {
			OutputEncoder encoder = registry.get(templateId);
			assertWithMessage(templateId + " returned node should not be null")
					.that(encoder)
					.isNotNull();
		}
	}
}
