package gov.cms.qpp.conversion.decode;

import org.jdom2.Element;
import org.jdom2.Namespace;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Registry;
import gov.cms.qpp.conversion.model.XmlRootDecoder;

public abstract class XmlInputDecoder implements InputDecoder {

	protected static Registry<String, XmlInputDecoder> rootDecoders = new Registry<String, XmlInputDecoder>(XmlRootDecoder.class);
	protected Element rootElement;
	protected Namespace namespace = Namespace.NO_NAMESPACE;
	protected Element xmlDoc;
	
	public XmlInputDecoder() {
	}

	public void setDom(Element xmlDoc) {
		this.xmlDoc = xmlDoc;
		rootElement = xmlDoc.getDocument().getRootElement();
	}
	
	/**
	 * Decode a document into a Node
	 */
	public Node decode() {

		XmlInputDecoder decoder = rootDecoders.get(rootElement.getName());
		
		if (null != decoder) {
			return decoder.internalDecode(xmlDoc, new Node());
		}
		
		Node rootParentNode = new Node();
		rootParentNode.setId("placeholder");

		return decode(xmlDoc, rootParentNode);
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
