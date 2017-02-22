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
		
		buff.append("<observation classCode=\"OBS\" moodCode=\"EVN\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">")
			.append("  <templateId root=\"2.16.840.1.113883.10.20.27.3.3\"/>")
			.append("  <code code=\"MSRAGG\" codeSystem=\"2.16.840.1.113883.5.4\" codeSystemName=\"ActCode\" displayName=\"rate aggregation\"/>")
			.append("  <statusCode code=\"completed\"/>")
			.append("  <value xsi:type=\"INT\" value=\"600\"/>")
			.append("  <methodCode code=\"COUNT\" codeSystem=\"2.16.840.1.113883.5.84\" codeSystemName=\"ObservationMethod\" displayName=\"Count\"/>")
			.append("</observation>");
		return buff.toString();
	}
	
}
