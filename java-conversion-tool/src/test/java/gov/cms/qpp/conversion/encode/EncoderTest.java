package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.Registry;
import gov.cms.qpp.conversion.model.TemplateId;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * This class tests basic Encoder functionality for the defined list of tmeplateId's
 */
public class EncoderTest {

	private final List<String> templateIDs = Arrays.asList(
			TemplateId.CLINICAL_DOCUMENT.getTemplateId(),
			TemplateId.MEASURE_SECTION_V2.getTemplateId(),
			TemplateId.IA_SECTION.getTemplateId(),
			TemplateId.ACI_SECTION.getTemplateId(),
			TemplateId.ACI_AGGREGATE_COUNT.getTemplateId(),
			TemplateId.MEASURE_DATA_CMS_V2.getTemplateId(),
			TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2.getTemplateId(),
			TemplateId.REPORTING_STRATUM_CMS.getTemplateId(),
			TemplateId.REPORTING_PARAMETERS_ACT.getTemplateId(),
			TemplateId.CMS_AGGREGATE_COUNT.getTemplateId(),
			TemplateId.PERFORMANCE_RATE_PROPORTION_MEASURE_CMS_V2.getTemplateId(),
			TemplateId.CONTINUOUS_VARIABLE_MEASURE_VALUE_CMS.getTemplateId(),
			TemplateId.MEASURE_PERFORMED.getTemplateId(),
			TemplateId.ACI_NUMERATOR_DENOMINATOR.getTemplateId(),
			TemplateId.ACI_MEASURE_PERFORMED_REFERENCE_AND_RESULTS.getTemplateId(),
			TemplateId.PERFORMANCE_RATE.getTemplateId(),
			TemplateId.ACI_NUMERATOR.getTemplateId(),
			TemplateId.ACI_DENOMINATOR.getTemplateId(),
			TemplateId.IA_MEASURE.getTemplateId()
	);

	/**
	 * decodeTemplateIds for each TemplateId in the list ensure that it exists in
	 * the registry.
	 * @throws Exception
	 */
	@Test
	public void decodeTemplateIds() throws Exception {
		Registry<String, OutputEncoder> registry = new Registry<>(Encoder.class);
		for (String templateId : templateIDs) {
			OutputEncoder encoder = registry.get(templateId);
			assertThat(templateId + " returned node should not be null", encoder, is(not(nullValue())));
		}
	}
}
