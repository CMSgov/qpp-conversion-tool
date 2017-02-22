package gov.cms.qpp.conversion.parser;

import org.jdom2.Document;
import org.jdom2.Element;

import gov.cms.qpp.conversion.model.Node;

public abstract class XmlInputParser {

	protected Document xmlDoc;

	/**
	 * Parse a file into a Node
	 */
	public Node parse(Element dom) {

		Node rootParentNode = new Node();
		rootParentNode.setId(dom.getName(), "placeholder");

		return parse(dom, rootParentNode);
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
