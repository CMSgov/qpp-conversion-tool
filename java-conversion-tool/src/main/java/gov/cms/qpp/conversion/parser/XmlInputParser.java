package gov.cms.qpp.conversion.parser;

import org.jdom2.Element;

import gov.cms.qpp.conversion.model.Node;

public abstract class XmlInputParser implements InputParser {

	protected Element xmlDoc;
	
	public XmlInputParser() {
	}

	public void setDom(Element xmlDoc) {
		this.xmlDoc = xmlDoc;
	}
	
	/**
	 * Parse a file into a Node
	 */
	public Node parse() {

		Node rootParentNode = new Node();
		rootParentNode.setId(xmlDoc.getName(), "placeholder");

		return parse(xmlDoc, rootParentNode);
	}

	/**
	 * Represents some sort of higher level parse of an element
	 * 
	 * @param element
	 * @return
	 */
	abstract protected Node parse(Element element, Node parent);

	/**
	 * Represents an internal parsing of an element
	 * 
	 * @param element
	 * @return
	 */
	abstract protected Node internalParse(Element element);
}
