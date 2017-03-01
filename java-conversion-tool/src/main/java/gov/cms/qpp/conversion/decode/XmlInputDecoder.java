package gov.cms.qpp.conversion.decode;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filter;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import gov.cms.qpp.conversion.Validatable;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Registry;
import gov.cms.qpp.conversion.model.XmlRootDecoder;

public abstract class XmlInputDecoder implements InputDecoder, Validatable<String, String> {
	protected static Registry<String, XmlInputDecoder> rootDecoders = new Registry<String, XmlInputDecoder>(XmlRootDecoder.class);
	protected Namespace defaultNs; 
	protected Namespace xpathNs;
	
	public XmlInputDecoder() {
	}

	/**
	 * Decode a document into a Node
	 */
	public Node decode(Element xmlDoc) {
		
		XmlInputDecoder decoder = rootDecoders.get(xmlDoc.getDocument().getRootElement().getName());
		
		if (null != decoder) {
			setNamespace(xmlDoc, decoder);
			Node parsedNode = decoder.internalDecode(xmlDoc, new Node());
			
			if (null == parsedNode.getId()) {
				throw new XmlInputFileException("ClinicalDocument templateId cannot be found.");
			}
			
			return parsedNode;
		} else {
			throw new XmlInputFileException("ClinicalDocument node cannot be parsed.");
		}
		
	}
	
	/**
	 * Decode a XML fragment into a Node
	 */
	public Node decodeFragment(Element xmlDoc) {
		
		Node rootParentNode = new Node();
		rootParentNode.setId("placeholder");

		return decode(xmlDoc, rootParentNode);
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
		decoder.xpathNs = Namespace.getNamespace("ns", decoder.defaultNs.getURI());
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
	abstract protected Node decode(Element element, Node parent);
	

	/**
	 * Represents an internal parsing of an element
	 * 
	 * @param element
	 * @param thisNode created for this decode
	 * @return
	 */
	abstract protected Node internalDecode(Element element, Node thisnode);
}
