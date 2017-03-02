package gov.cms.qpp.conversion.xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class XmlUtils {
	
	public static Element stringToDOM(String xml) throws XmlException {
		if (xml == null) {
			return null;
		}
		
		return parseXmlStream(new ByteArrayInputStream(xml.getBytes()));
	}

	public static Element fileToDOM(String filename) throws XmlException {
		if (filename == null) {
			return null;
		}
		
		return fileToDOM(new File(filename));
	}
	
	public static Element fileToDOM(File file) throws XmlException {
		try {
			return parseXmlStream(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			throw new XmlException("File '" + file.getName() + "' Cannot be found", e);
		}
	}
	
	protected static Element parseXmlStream(InputStream xmlStream) throws XmlException {
		Document dom;
		try {
			SAXBuilder saxBuilder = new SAXBuilder();
			dom = saxBuilder.build(xmlStream);
		} catch (JDOMException | IOException e) {
			throw new XmlException("Failed to process XML String into DOM Element", e);
		}
		return dom.getRootElement();
	}
	
	public static String buildString(String ... parts) {
		StringBuilder buff = new StringBuilder();
		
		for (String part : parts) {
			buff.append(part);
		}
		
		return buff.toString();
	}
	
}
