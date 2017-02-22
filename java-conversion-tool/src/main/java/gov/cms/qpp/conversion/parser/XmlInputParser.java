package gov.cms.qpp.conversion.parser;

import java.io.File;
import java.io.IOException;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.NodeId;

public abstract class XmlInputParser implements InputParser {

	protected Document xmlDoc;

	/**
	 * Parse a file into a Node
	 */
	@Override
	public Node parse(File inputFile) {

		SAXBuilder saxBuilder = new SAXBuilder();
		try {
			Document parsedDoc = saxBuilder.build(inputFile);

			this.xmlDoc = parsedDoc;

		} catch (JDOMException | IOException e) {

			e.printStackTrace();
		}

		Element rootElement = xmlDoc.getRootElement();

		Node rootParentNode = new Node();
		rootParentNode.setInternalId(new NodeId(rootElement.getName(), "placeholder"));

		return parse(rootElement, rootParentNode);
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
