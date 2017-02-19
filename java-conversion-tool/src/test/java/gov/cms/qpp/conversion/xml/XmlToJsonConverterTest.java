package gov.cms.qpp.conversion.xml;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import gov.cms.qpp.conversion.io.ByteCounterOutputStream;

public class XmlToJsonConverterTest {

	@Test
	public void convertTest() throws Exception {
		ClassPathResource xmlResource = new ClassPathResource("QRDA_III_1.xml");
		
		XmlToJsonConverter converter = new XmlToJsonConverter("ClinicalDocument");
		
		ByteCounterOutputStream out = new ByteCounterOutputStream();
		converter.convert(xmlResource.getInputStream(), out, new ClinicalDocumentHandler());
	}

}
