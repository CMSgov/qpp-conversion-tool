package gov.cms.qpp.conversion.decode;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filter;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.cms.qpp.conversion.Validatable;
import gov.cms.qpp.conversion.model.Node;

public abstract class XmlInputDecoder implements InputDecoder, Validatable<String, String> {
    static final Logger LOG = LoggerFactory.getLogger(XmlInputDecoder.class);
	protected Namespace defaultNs; 
	protected Namespace xpathNs;
	
	public XmlInputDecoder() {
	}

	/**
	 * Decode a document into a Node
	 */
	public static Node decodeXml(Element xmlDoc) {
		List<XmlInputDecoder> xmlDecoders = Arrays.asList(new QppXmlDecoder());
		for  (XmlInputDecoder decoder : xmlDecoders) {
			if (decoder.accepts(xmlDoc)) {
				return decoder.decode(xmlDoc);
			}
		}
		
		LOG.error("The file is an unknown XML document");
		
		return null;
	}
	
	/**
	 * Decode a document into a Node
	 */
	public Node decode(Element xmlDoc) {
		return decodeRoot(xmlDoc);
	}


	/**
	 * Convenient way to pass a list into sub decoders.
	 * 
	 * @param element
	 * @return
	 */
	protected void decode(List<Element> elements, Node parent) {
		for (Element element : elements) {
			decode(element, parent);
		}
	};
	
	
	protected void setNamespace(Element el, XmlInputDecoder decoder) {
		decoder.defaultNs = el.getNamespace();
		
		// this handle the case where there is no URI for a default namespace (test)
		try {
			Constructor<Namespace> constructor = Namespace.class.getDeclaredConstructor(String.class, String.class);
			constructor.setAccessible(true);
			decoder.xpathNs = constructor.newInstance("ns", decoder.defaultNs.getURI());
		} catch (NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new IllegalArgumentException("Cannot construct special Xpath namespace", e);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void setOnNode(Element element, String expressionStr, Consumer consumer, Filter<?> filter, boolean selectOne) {
		XPathExpression<?> expression = XPathFactory.instance().compile(expressionStr, filter, null,  xpathNs);
		
		if (selectOne) {
			Optional.ofNullable(expression.evaluateFirst(element)).ifPresent(consumer);
		} else {
			Optional.ofNullable(expression.evaluate(element)).ifPresent(consumer);
		}
	}

	/**
	 * Represents some sort of higher level decode of an element
	 * 
	 * @param element
	 * @return
	 */
	abstract protected DecodeResult decode(Element element, Node parent);

	/**
	 * When you have no parent
	 * @param xmlDoc
	 * @return
	 */
	abstract protected Node decodeRoot(Element xmlDoc);

	/**
	 * Represents an internal parsing of an element
	 * 
	 * @param element
	 * @param thisNode created for this decode
	 * @return
	 */
	abstract protected DecodeResult internalDecode(Element element, Node thisnode);
	
	/**
	 * See if the Decoder can handle the input
	 * @param xmlDoc
	 * @return true/false
	 */
	protected abstract boolean accepts(Element xmlDoc);

}
