package gov.cms.qpp.conversion.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.jaxen.Context;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.SimpleFunctionContext;
import org.jaxen.XPathFunctionContext;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.located.LocatedJDOMFactory;

/**
 * Utility for parsing various input types into a JDom Element.
 */
public class XmlUtils {
	private static final String DISALLOW_DTD = "http://apache.org/xml/features/disallow-doctype-decl";
	private static final String EXT_GENERAL_ENTITIES = "http://xml.org/sax/features/external-general-entities";
	private static final String EXT_PARAM_ENTITIES = "http://xml.org/sax/features/external-parameter-entities";
	private static final String ACCESS_EXTERNAL_DTD = "javax.xml.accessExternalDTD";
	private static final String ACCESS_EXTERNAL_SCHEMA = "javax.xml.accessExternalSchema";

	/**
	 * Replacement for Jaxen's XSLT document() function. The converter never
	 * dereferences URIs from xpath; document() coupled with jdom2's unhardened
	 * navigator-side parser creates an attack vector, so it is disabled outright.
	 */
	@SuppressWarnings("rawtypes")
	private static final Function DISABLED_DOCUMENT = new Function() {
		@Override
		public Object call(Context context, List args) throws FunctionCallException {
			throw new FunctionCallException(
					"document() is disabled: URI dereferencing from XPath is not permitted");
		}
	};

	static {
		installSecurityHardening();
	}

	/**
	 * Process-wide xpath/xml hardening. Also invoked from application entry points
	 * (rest-api, commandline) so the policy does not depend on class-load order.
	 * Idempotent.
	 */
	public static void installSecurityHardening() {
		((SimpleFunctionContext) XPathFunctionContext.getInstance())
				.registerFunction(null, "document", DISABLED_DOCUMENT);
		System.setProperty(ACCESS_EXTERNAL_DTD, "");
		System.setProperty(ACCESS_EXTERNAL_SCHEMA, "");
	}

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
	public static Element stringToDom(String xml) {
		if (xml == null) {
			return null;
		}

		return parseXmlStream(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
	}

	/**
	 * Parses a stream of XML into a tree of XML elements.
	 *
	 * @param xmlStream The XML.
	 * @return The root element of the XML tree.
	 * @throws XmlException When a failure to parse the XML.
	 */
	public static Element parseXmlStream(InputStream xmlStream) {
		try {
			SAXBuilder saxBuilder = new SAXBuilder();
			saxBuilder.setFeature(DISALLOW_DTD,true);
			saxBuilder.setFeature(EXT_GENERAL_ENTITIES, false);
			saxBuilder.setFeature(EXT_PARAM_ENTITIES, false);
			saxBuilder.setJDOMFactory(new LocatedJDOMFactory());

			return saxBuilder.build(xmlStream).getRootElement();
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
	public static String buildString(String... parts) {
		return Arrays.stream(parts)
				.collect(Collectors.joining());
	}

}
