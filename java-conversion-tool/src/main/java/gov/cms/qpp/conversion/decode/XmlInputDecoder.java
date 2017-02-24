package gov.cms.qpp.conversion.decode;

import org.jdom2.Element;

import gov.cms.qpp.conversion.model.Node;

public abstract class XmlInputDecoder implements InputDecoder {

	protected Element xmlDoc;
	
	public XmlInputDecoder() {
	}

	public void setDom(Element xmlDoc) {
		this.xmlDoc = xmlDoc;
	}
	
	/**
	 * Decode a document into a Node
	 */
	public Node decode() {

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
