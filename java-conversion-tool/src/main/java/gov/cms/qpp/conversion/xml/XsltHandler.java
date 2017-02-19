package gov.cms.qpp.conversion.xml;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

public class XsltHandler extends BaseNodeHandler {
    private static final Logger LOG = LoggerFactory.getLogger(XsltHandler.class);
	
	public XsltHandler(final String handledElementName) {
		super(handledElementName);
	}

	@Override
	public void doHandle(XMLStreamReader reader) throws XMLStreamException {
		LOG.info(reader.getLocalName());
		
		ClassPathResource xslResource = new ClassPathResource("xml2json_a.xsl");
		TransformerFactory tFactory = TransformerFactory.newInstance();
		try {
			Transformer transformer = tFactory.newTransformer(new StreamSource(xslResource.getFile()));
			transformer.transform(new StAXSource(reader), new StreamResult(System.out));
			
		} catch (TransformerException| IOException e) {
			throw new RuntimeException(e);
		}

	}

}
