package gov.cms.qpp.conversion.xml;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Utility for parsing various input types into a JDom Element.
 */
public class XmlUtils {

	/**
	 * Private constructor so utility class cannot be instantiated
	 */
	private XmlUtils() {
		//private constructor so utility class cannot be instantiated
	}

	/**
	 * Parses the string as XML.
	 *
	 * @param xml A string of XML.
	 * @return The root element of the XML tree.
	 * @throws XmlException When a failure to parse the XML.
	 */
	public static Element stringToDOM(String xml) throws XmlException {
		if (xml == null) {
			return null;
		}
		
		return parseXmlStream(new ByteArrayInputStream(xml.getBytes()));
	}

	/**
	 * Parses an XML file specified by a path or filename.
	 *
	 * @param filename A path or filename of an XML file.
	 * @return The root element of the XML tree.
	 * @throws XmlException When a failure to parse the XML or open and read the file.
	 */
	public static Element fileToDOM(String filename) throws XmlException {
		if (filename == null) {
			return null;
		}
		
		return fileToDOM(Paths.get(filename));
	}

	/**
	 * Parses an XML file specified by the Path.
	 *
	 * @param file An XML file.
	 * @return The root element of the XML tree.
	 * @throws XmlException When a failure to parse the XML or open and read the file.
	 */
	public static Element fileToDOM(Path file) throws XmlException {
		try (InputStream xmlStream = Files.newInputStream(file)) {
			return parseXmlStream(xmlStream);
		} catch (IOException e) {
			throw new XmlException("File '" + file + "' Cannot be parsed", e);
		}
	}

	/**
	 * Parses a stream of XML into a tree of XML elements.
	 *
	 * @param xmlStream The XML.
	 * @return The root element of the XML tree.
	 * @throws XmlException When a failure to parse the XML.
	 */
	protected static Element parseXmlStream(InputStream xmlStream) throws XmlException {
		try {
			SAXBuilder saxBuilder = new SAXBuilder();
			Document dom = saxBuilder.build(xmlStream);
			return dom.getRootElement();
		} catch (JDOMException | IOException e) {
			throw new XmlException("Failed to process XML String into DOM Element", e);
		}
	}

	/**
	 * Concatenates the parameters.
	 *
	 * @param parts The strings to concatenate.
	 * @return A concatenation of the parts.
	 */
	public static String buildString(String ... parts) {
		return Arrays.stream(parts)
				.collect(Collectors.joining());
	}
	
}
