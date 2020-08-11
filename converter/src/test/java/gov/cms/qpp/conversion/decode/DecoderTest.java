package gov.cms.qpp.conversion.decode;

import org.junit.jupiter.api.Test;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.Registry;
import gov.cms.qpp.conversion.model.TemplateId;

import java.util.EnumSet;
import java.util.Set;

import static com.google.common.truth.Truth.assertWithMessage;

class DecoderTest {

	private final Set<TemplateId> templateIds = EnumSet.of(
			TemplateId.CLINICAL_DOCUMENT,
			TemplateId.MEASURE_SECTION_V4,
			TemplateId.IA_SECTION,
			TemplateId.PI_SECTION_V2,
			TemplateId.PI_AGGREGATE_COUNT,
			TemplateId.MEASURE_DATA_CMS_V2,
			TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V4,
			TemplateId.PAYER_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2,
			TemplateId.RACE_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2,
			TemplateId.REPORTING_STRATUM_CMS,
			TemplateId.SEX_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2,
			TemplateId.ETHNICITY_SUPPLEMENTAL_DATA_ELEMENT_CMS_V2,
			TemplateId.REPORTING_PARAMETERS_ACT,
			TemplateId.MEASURE_PERFORMED,
			TemplateId.PI_NUMERATOR_DENOMINATOR,
			TemplateId.PI_MEASURE_PERFORMED_REFERENCE_AND_RESULTS,
			TemplateId.PI_NUMERATOR,
			TemplateId.PI_DENOMINATOR,
			TemplateId.IA_MEASURE);

	@Test
	void decodeTemplateIds() throws Exception {
		Registry<QrdaDecoder> registry = new Registry<>(new Context(), Decoder.class);

		for (TemplateId templateId : templateIds) {
			QrdaDecoder decoder = registry.get(templateId);
			assertWithMessage("%s returned node should not be null", templateId.name())
					.that(decoder)
					.isNotNull();
		}
	}
}
