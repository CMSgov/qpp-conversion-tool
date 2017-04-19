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
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Utility for parsing various input types into a JDom Element.
 */
public class XmlUtils {
	/**
	 * Private constructor so utility class cannot be instantiated.
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
	public static Element stringToDom(String xml) throws XmlException {
		if (xml == null) {
			return null;
		}

		return parseXmlStream(new ByteArrayInputStream(xml.getBytes()));
	}

	/**
	 * Returns an InputStream sourced by the given path.
	 *
	 * @param file An XML file.
	 * @return InputStream for the file's content
	 * @throws IOException When a failure to open and read the file.
	 */
	public static InputStream fileToStream(Path file) throws IOException {
		return Files.newInputStream(file);
	}

	/**
	 * Parses a stream of XML into a tree of XML elements.
	 *
	 * @param xmlStream The XML.
	 * @return The root element of the XML tree.
	 * @throws XmlException When a failure to parse the XML.
	 */
	public static Element parseXmlStream(InputStream xmlStream) throws XmlException {
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
