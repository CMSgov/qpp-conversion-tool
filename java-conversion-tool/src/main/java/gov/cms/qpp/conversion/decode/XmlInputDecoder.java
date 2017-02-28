package gov.cms.qpp.conversion.decode;

import java.util.List;

import org.jdom2.Element;
import org.jdom2.Namespace;

import gov.cms.qpp.conversion.Validatable;
import gov.cms.qpp.conversion.Validations;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Registry;
import gov.cms.qpp.conversion.model.XmlRootDecoder;

public abstract class XmlInputDecoder implements InputDecoder, Validatable<String, String> {
	// keep it ordered since we can only 
	// use this storage method on a single threaded app anyway
	protected static ThreadLocal<Validations<String, String>> validations = new ThreadLocal<>();

	protected static Registry<String, XmlInputDecoder> rootDecoders = new Registry<String, XmlInputDecoder>(XmlRootDecoder.class);
	protected Element xmlDoc;
	protected Namespace defaultNs; 
	protected Namespace xpathNs;
	
	public XmlInputDecoder() {
	}

	public void setDom(Element xmlDoc) {
		this.xmlDoc = xmlDoc;
	}
	
	/**
	 * Decode a document into a Node
	 */
	public Node decode() {
		
		try {
			
			validations.set(new Validations<>());
			
			XmlInputDecoder decoder = rootDecoders.get(xmlDoc.getDocument().getRootElement().getName());
			
			if (null != decoder) {
				setNamespace(xmlDoc, decoder);
				return decoder.internalDecode(xmlDoc, new Node());
			}
			
			Node rootParentNode = new Node();
			rootParentNode.setId("placeholder");
	
			return decode(xmlDoc, rootParentNode);
		} finally {
			validations.set(null);
		}
		
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
