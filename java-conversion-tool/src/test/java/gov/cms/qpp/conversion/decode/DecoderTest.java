package gov.cms.qpp.conversion.decode;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import gov.cms.qpp.conversion.model.Registry;
import gov.cms.qpp.conversion.model.Validations;
import gov.cms.qpp.conversion.model.XmlDecoder;

public class DecoderTest {

	private final List<String> templateIDs = Arrays.asList(
			"2.16.840.1.113883.10.20.24.2.2",
			
			"2.16.840.1.113883.10.20.27.1.2",
			"2.16.840.1.113883.10.20.27.2.3",
			"2.16.840.1.113883.10.20.27.2.4",
			"2.16.840.1.113883.10.20.27.2.5",
			"2.16.840.1.113883.10.20.27.2.6",
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

	@Test
	public void decodeTemplateIds() throws Exception {
		Registry<String, InputDecoder> registry;
		registry = new Registry<>(XmlDecoder.class);
		
		for (String templateId : templateIDs) {
			InputDecoder decoder = registry.get(templateId);
			assertThat(templateId + " returned node should not be null", decoder, is(not(nullValue())));
		}
	}


}
