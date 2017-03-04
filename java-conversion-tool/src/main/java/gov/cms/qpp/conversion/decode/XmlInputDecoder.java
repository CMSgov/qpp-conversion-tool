package gov.cms.qpp.conversion.decode;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
import gov.cms.qpp.conversion.model.Registry;
import gov.cms.qpp.conversion.model.XmlRootDecoder;

public abstract class XmlInputDecoder implements InputDecoder, Validatable<String, String> {
    static final Logger LOG = LoggerFactory.getLogger(XmlInputDecoder.class);
	protected static Registry<String, XmlInputDecoder> rootDecoders = new Registry<String, XmlInputDecoder>(XmlRootDecoder.class);
	protected Namespace defaultNs; 
	protected Namespace xpathNs;
	
	public XmlInputDecoder() {
	}

	/**
	 * Decode a document into a Node
	 */
	public static Node decodeAll(Element xmlDoc) {
		
		XmlInputDecoder decoder = rootDecoders.get(xmlDoc.getDocument().getRootElement().getName());
		
		if (decoder instanceof QppXmlDecoder) {
			Node node = new Node();
			DecodeResult result = decoder.internalDecode(xmlDoc, node);
			
			// TODO handle result
			
			if (null == node.getId()) {
				LOG.error("The file is not a QDRA-III xml document");
			}
			
			return node;
		} 
		// else if (decoder instanceof <Other decoder>) {
		//  Validation
		//
		else {
			LOG.error("The file is an unknown XML document");
			return null;
		}
		
	}
	
	/**
	 * Decode a document into a Node
	 */
	public Node decode(Element xmlDoc) {
		return decodeAll(xmlDoc);
	}
	
	/**
	 * Decode a XML fragment into a Node
	 */
	public Node decodeFragment(Element xmlDoc) {
		
		Node rootParentNode = new Node("placeholder");

		decode(xmlDoc, rootParentNode);
		
		return rootParentNode;
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
	 * Represents an internal parsing of an element
	 * 
	 * @param element
	 * @param thisNode created for this decode
	 * @return
	 */
	abstract protected DecodeResult internalDecode(Element element, Node thisnode);
}
