package gov.cms.qpp.conversion.decode;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.jdom2.Element;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.xml.XmlUtils;

public class ClinicalDocumentDecoderTest {

	@Test
	public void decodeClinicalDocumentDecoderAsNode() throws Exception {
		ClassPathResource xmlResource = new ClassPathResource("valid-QRDA-III.xml");
		String xmlString = IOUtils.toString(xmlResource.getInputStream(), Charset.defaultCharset());
		Element dom = XmlUtils.stringToDOM(xmlString);

		QppXmlDecoder decoder = new QppXmlDecoder();
		decoder.setDom(dom);

		Node root = decoder.decode();

		assertThat("returned node should not be null", decoder, is(not(nullValue())));
		
		assertThat("template ID is correct", root.getId(), is("2.16.840.1.113883.10.20.27.1.2"));
		assertThat("programName is correct", root.getValue("programName"), is("mips"));
		assertThat("nationalProviderIdentifier correct", root.getValue("nationalProviderIdentifier"), is("2567891421"));
		assertThat("taxpayerIdentificationNumber correct", root.getValue("taxpayerIdentificationNumber"), is("123456789"));
		assertThat("performanceStart  correct", root.getValue("performanceStart"), is("20170101"));
		assertThat("performanceEnd correct", root.getValue("performanceEnd"), is("20171231"));

	}
	

}
