package gov.cms.qpp.conversion.encode;

import gov.cms.qpp.conversion.model.Encoder;
import gov.cms.qpp.conversion.model.EncoderNew;
import gov.cms.qpp.conversion.model.Registry;
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
			"2.16.840.1.113883.10.20.24.2.2",
			
			"2.16.840.1.113883.10.20.27.1.2",
			"2.16.840.1.113883.10.20.27.2.3",
			"2.16.840.1.113883.10.20.27.2.4",
			"2.16.840.1.113883.10.20.27.2.5",
			"2.16.840.1.113883.10.20.27.3.3",
			
			"2.16.840.1.113883.10.20.27.3.16",
			"2.16.840.1.113883.10.20.27.3.17",
			"2.16.840.1.113883.10.20.27.3.18",
			"2.16.840.1.113883.10.20.27.3.19",
			"2.16.840.1.113883.10.20.27.3.20",
			"2.16.840.1.113883.10.20.27.3.21",
			"2.16.840.1.113883.10.20.27.3.22",
			"2.16.840.1.113883.10.20.27.3.23",
			// this seems to be handled by 2.16.840.1.113883.10.20.27.3.3
			"2.16.840.1.113883.10.20.27.3.24",
			"2.16.840.1.113883.10.20.27.3.25",
			"2.16.840.1.113883.10.20.27.3.26",
			"2.16.840.1.113883.10.20.27.3.27",
			"2.16.840.1.113883.10.20.27.3.28",
			"2.16.840.1.113883.10.20.27.3.29",
			"2.16.840.1.113883.10.20.27.3.30",
			"2.16.840.1.113883.10.20.27.3.31",
			"2.16.840.1.113883.10.20.27.3.32",
			"2.16.840.1.113883.10.20.27.3.33"
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
