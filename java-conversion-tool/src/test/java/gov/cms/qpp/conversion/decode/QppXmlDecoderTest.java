package gov.cms.qpp.conversion.decode;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import gov.cms.qpp.conversion.model.Validations;
import gov.cms.qpp.conversion.xml.XmlException;
import gov.cms.qpp.conversion.xml.XmlUtils;

public class QppXmlDecoderTest extends QppXmlDecoder {
	
	@Before
	public void setup() throws Exception {
		Validations.init();
	}
	
	@After
	public void teardown() throws Exception {
		Validations.clear();
	}
	
	@Test
	public void validationFormatTest() throws Exception {
		XmlInputDecoder target = new QppXmlDecoder();
		
		target.addValidation("templateid.1", "validation.1");
		target.addValidation("templateid.1", "validation.2");
		target.addValidation("templateid.3", "validation.3");
		
		List<String> checkList = Arrays.asList("templateid.1 - validation.1",
												"templateid.1 - validation.2",
												"templateid.3 - validation.3");
		int count = 0;
		for (String validation : target.validations()) {
			assertThat("Expected validation", checkList.contains(validation), is(true));
			count++;
		}
		
		assertThat("Expected count", count, is(3));
		
		checkList = Arrays.asList("validation.1", "validation.2");
		count = 0;
		for (String validation : target.getValidationsById("templateid.1")) {
			assertThat("Expected validation", checkList.contains(validation), is(true));
			count++;
		}
		
		assertThat("Expected count", count, is(2));
	}
	
	@Test
	public void validationFileTest() throws Exception {
		ClassPathResource xmlResource = new ClassPathResource("bogus-QDRA-III");
		String xmlFragment = IOUtils.toString(xmlResource.getInputStream(), Charset.defaultCharset());
		
		try {
			new QppXmlDecoder().decode(XmlUtils.stringToDOM(xmlFragment));
		} catch (Exception e) {
			assertThat("Expected XmlInputFileException", e instanceof XmlInputFileException, is(true));
		}
		
		xmlResource = new ClassPathResource("bogus-QDRA-III-root");
		xmlFragment = IOUtils.toString(xmlResource.getInputStream(), Charset.defaultCharset());
		
		try {
			new QppXmlDecoder().decode(XmlUtils.stringToDOM(xmlFragment));
		} catch (Exception e) {
			assertThat("Expected XmlInputFileException", e instanceof XmlInputFileException, is(true));
		}
		
		xmlResource = new ClassPathResource("non-xml-file.xml");
		xmlFragment = IOUtils.toString(xmlResource.getInputStream(), Charset.defaultCharset());
		
		try {
			new QppXmlDecoder().decode(XmlUtils.stringToDOM(xmlFragment));
		} catch (Exception e) {
			assertThat("Expected XmlException", e instanceof XmlException, is(true));
		}
		
	}

	
	@Test
	public void sillyCoverageTest() throws Exception {
		assertThat("Should be benign", new QppXmlDecoder().internalDecode(null, null), is(nullValue()));
	}
}
