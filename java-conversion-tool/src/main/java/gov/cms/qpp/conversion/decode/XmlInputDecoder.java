package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.model.Node;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filter;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Abstraction to parse XML files within the decoder structure.
 */
public abstract class XmlInputDecoder implements InputDecoder {
	private static final Logger DEV_LOG = LoggerFactory.getLogger(XmlInputDecoder.class);
	Namespace defaultNs;
	Namespace xpathNs;

	/**
	 * decodeXml Determines what formats of xml we accept and decode to
	 *
	 * @param xmlDoc XML document whose format is to be determined
	 * @return Root intermediate format node
	 */
	public static Node decodeXml(Element xmlDoc) {
		List<XmlInputDecoder> xmlDecoders = Arrays.asList(new QppXmlDecoder());

		for  (XmlInputDecoder decoder : xmlDecoders) {
			if (decoder.accepts(xmlDoc)) {
				return decoder.decode(xmlDoc);
			}
		}

		DEV_LOG.error("The XML file is an unknown document");

		return null;
	}

	/**
	 * Decode a document into a Node
	 *
	 * @param xmlDoc XML Document to be decoded
	 * @return Decoded root node
	 */
	@Override
	public Node decode(Element xmlDoc) {
		return decodeRoot(xmlDoc);
	}

	/**
	 * Convenient way to pass a list into sub decoders.
	 * 
	 * @param elements List of elements to be decoded
	 * @param parent Parent node that all child elements will be decoded into
	 */
	protected void decode(List<Element> elements, Node parent) {
		for (Element element : elements) {
			decode(element, parent);
		}
	}

	/**
	 * Abstraction of decode for an element to a node
	 * 
	 * @param element Element to be decoded
	 * @param parent Node to be decoded into
 	 * @return Action to take after decode
	 */
	protected abstract DecodeResult decode(Element element, Node parent);

	/**
	 * Sets xml namespace
	 *
	 * @param element Element that hold the namespace
	 * @param decoder Decoder to configure
	 */
	void setNamespace(Element element, XmlInputDecoder decoder) {
		decoder.defaultNs = element.getNamespace();

		// this handle the case where there is no URI for a default namespace (test)
		try {
			Constructor<Namespace> constructor = Namespace.class.getDeclaredConstructor(String.class, String.class);
			constructor.setAccessible(true);
			decoder.xpathNs = constructor.newInstance("ns", decoder.defaultNs.getURI());
		} catch (NoSuchMethodException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			String message = "Cannot construct special Xpath namespace";
			throw new IllegalArgumentException(message, e);
		}
	}

	/**
	 * Executes an Xpath for an element and executes the consumer
	 *
	 * @param element Element the xpath is executed against
	 * @param expressionStr Xpath
	 * @param consumer Consumer to execute if the xpath matches
	 * @param filter Filter to apply for the xpath
	 * @param selectOne Whether to execute for the first match or multiple matches
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void setOnNode(Element element, String expressionStr,
					Consumer consumer, Filter<?> filter, boolean selectOne) {
		XPathExpression<?> expression = XPathFactory.instance().compile(expressionStr, filter, null,  xpathNs);

		if (selectOne) {
			Optional.ofNullable(expression.evaluateFirst(element)).ifPresent(consumer);
		} else {
			List<?> elems = expression.evaluate(element);
			Optional.ofNullable(elems)
					.ifPresent(notNullElems -> notNullElems.forEach(consumer));
		}
	}

	/**
	 * Top level element to decode
	 *
	 * @param xmlDoc Element to be decoded
	 * @return Root node
	 */
	protected abstract Node decodeRoot(Element xmlDoc);

	/**
	 * Represents an internal parsing of an element
	 * 
	 * @param element Element to be decoded
	 * @param thisNode Node to be decoded into
	 * @return Action to take after decode
	 */
	protected abstract DecodeResult internalDecode(Element element, Node thisNode);

	/**
	 * Determines if the Decoder can handle the input
	 *
	 * @param xmlDoc XML document
	 * @return Whether or not the decoder can handle the element
	 */
	protected abstract boolean accepts(Element xmlDoc);
}
