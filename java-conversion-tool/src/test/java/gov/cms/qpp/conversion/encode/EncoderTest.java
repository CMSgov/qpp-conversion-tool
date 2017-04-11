package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.EncoderNew;
import gov.cms.qpp.conversion.model.Registry;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validations;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class EncoderTest {

	private final List<String> templateIDs = Arrays.asList(
			TemplateId.MEASURE_SECTION.getTemplateId(),

			TemplateId.CLINICAL_DOCUMENT.getTemplateId(),
	TemplateId.MEASURE_SECTION1.getTemplateId(),
	TemplateId.IA_SECTION.getTemplateId(),
	TemplateId.ACI_SECTION.getTemplateId(),
//			"2.16.840.1.113883.10.20.27.2.6", // this one is handled internally
	TemplateId.ACI_AGGREGATE_COUNT.getTemplateId(),
	TemplateId.IPP_POPULATION.getTemplateId(),
	TemplateId.MEASURE_ENTRY_NQF.getTemplateId(),
	TemplateId.PAYER_SUPPLEMENT.getTemplateId(),
	TemplateId.RACE_SUPPLEMENT.getTemplateId(),
	TemplateId.UNKNOWN.getTemplateId(),
	TemplateId.GENDER_MALE.getTemplateId(),
	TemplateId.ETHNICITY_SUPPLEMENT.getTemplateId(),
	TemplateId.REPORTING_NODE_DRIV.getTemplateId(),
			// this seems to be handled by 2.16.840.1.113883.10.20.27.3.3
	TemplateId.IPP_POPULATION_1.getTemplateId(),
	TemplateId.PERFORMANCE_RATE_1.getTemplateId(),
	TemplateId.UNKNOWN_1.getTemplateId(),
	//TemplateId..getTemplateId(),
			"2.16.840.1.113883.10.20.27.3.27",
	TemplateId.ACI_PROPORTION.getTemplateId(),
	TemplateId.UNKNOWN_2.getTemplateId(),
	TemplateId.PERFORMANCE_RATE.getTemplateId(),
	TemplateId.ACI_NUMERATOR.getTemplateId(),
	TemplateId.ACI_DENOMINATOR.getTemplateId(),
	TemplateId.IA_MEASURE.getTemplateId()
	);
	
	
	@Before
	public void before() {
		Validations.init();
	}
	@After
	public void after() {
		Validations.clear();
	}
	
//	private String makeXml(String templateId) {
//		String xmlFragment = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
//				+ "<component xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"urn:hl7-org:v3\">\n"
//				+ "	<observation classCode=\"OBS\" moodCode=\"EVN\">\n"
//				+ " 	<templateId root=\"2.16.840.1.113883.10.20.24.3.98\"/>\n" 
//				+ "		<templateId root=\""+templateId+"\" extension=\"2016-09-01\" />\n"
//				+ "	</observation>\n"
//				+ "</component>";
//		return xmlFragment;
//	}

	@Test
	public void decodeTemplateIds() throws Exception {
		Registry<String, OutputEncoder> registry;
		registry = new Registry<>(Encoder.class, EncoderNew.class);
		
		for (String templateId : templateIDs) {
			OutputEncoder encoder = registry.get(templateId);
			assertThat(templateId + " returned node should not be null", encoder, is(not(nullValue())));
		}
	}


}
