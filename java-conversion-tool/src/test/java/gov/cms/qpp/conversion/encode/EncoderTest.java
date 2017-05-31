package gov.cms.qpp.conversion.encode;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.EnumSet;
import java.util.Set;

import org.junit.Test;

import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Registry;
import gov.cms.qpp.conversion.model.TemplateId;

/**
 * This class tests basic Encoder functionality for the defined list of tmeplateId's
 */
public class EncoderTest {

	private final Set<TemplateId> templateIds = EnumSet.of(
			TemplateId.MEASURE_SECTION,
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
			TemplateId.CMS_AGGREGATE_COUNT,
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
	public void decodeTemplateIds() throws Exception {
		Registry<OutputEncoder> registry = new Registry<>(Encoder.class);
		for (TemplateId templateId : templateIds) {
			OutputEncoder encoder = registry.get(templateId);
			assertThat(templateId + " returned node should not be null", encoder, is(not(nullValue())));
		}
	}
}
