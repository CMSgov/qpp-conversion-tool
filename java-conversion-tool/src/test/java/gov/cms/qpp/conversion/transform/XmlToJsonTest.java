package gov.cms.qpp.conversion.transform;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

public class XmlToJsonTest {

	@Test
	public void TransformTest() throws Exception {
		ClassPathResource xmlResource = new ClassPathResource("QRDA_III_1.xml");
		ClassPathResource xslResource = new ClassPathResource("xml2json.xsl");
		
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer(new StreamSource(xslResource.getFile()));
		transformer.transform(new StreamSource(xmlResource.getInputStream()), new StreamResult(System.out));
	}

}
