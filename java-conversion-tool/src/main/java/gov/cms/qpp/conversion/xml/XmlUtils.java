package gov.cms.qpp.conversion.xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class XmlUtils {
	
	public static Element stringToDOM(String xml) throws XmlException {
		
		Document dom;
		try {
			SAXBuilder saxBuilder = new SAXBuilder();
			dom = saxBuilder.build(new ByteArrayInputStream(xml.getBytes()));
		} catch (JDOMException | IOException e) {
			throw new XmlException("Failed to process XML String into DOM Element", e);
		}

		return dom.getRootElement();
	}
	
	
	public static Element fileToDom(String filename) throws XmlException {
		return fileToDom(new File(filename));
	}
	
	public static Element fileToDom(File file) throws XmlException {
		
		Document dom;
		try {
			SAXBuilder saxBuilder = new SAXBuilder();
			dom = saxBuilder.build(file);
		} catch (JDOMException | IOException e) {
			throw new XmlException("Failed to load/process XML file into DOM Element", e);
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
