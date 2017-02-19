package gov.cms.qpp.conversion.xml;

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XmlToJsonConverter  {
    private static final Logger LOG = LoggerFactory.getLogger(XmlToJsonConverter.class);
    private final String elementName;

	public XmlToJsonConverter(final String elementName) {
		super();
		this.elementName = elementName;
	}
	
	public void convert(InputStream xmlStream, OutputStream jsonStream, NodeHandler handler) throws XMLStreamException {
		// get a factory instance
		XMLInputFactory myFactory = XMLInputFactory.newInstance();
		// set error reporter (similar to setting ErrorReporter in SAX)
		// myFactory.setXMLReporter(myXMLReporter);
		// set resolver (similar to setting EntityResolver in SAX)
		// myFactory.setXMLResolver(myXMLResolver);
		// configure the factory, e.g. validating or non-validating
		// myFactory.setProperty(..., ...);
		// create new XMLStreamReader
		XMLStreamReader myReader = myFactory.createXMLStreamReader(xmlStream);
		// document encoding from the XML declaration
		String encoding = myReader.getEncoding();
		// loop through document for XML constructs of interest
		
		handler.handle(myReader);
	}
	
	public String getElementName() {
		return elementName;
	}

}
