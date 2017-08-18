package gov.cms.qpp.conversion.decode;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.EnumSet;
import java.util.Set;

import org.junit.Test;

import gov.cms.qpp.ConverterTestHelper;
import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.Registry;
import gov.cms.qpp.conversion.model.TemplateId;

public class DecoderTest {

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

	@Test
	public void decodeTemplateIds() throws Exception {
		Registry<InputDecoder> registry = new Registry<>(ConverterTestHelper.newMockConverter(), Decoder.class);

		for (TemplateId templateId : templateIds) {
			InputDecoder decoder = registry.get(templateId);
			assertThat(templateId + " returned node should not be null", decoder, is(not(nullValue())));
		}
	}
}
