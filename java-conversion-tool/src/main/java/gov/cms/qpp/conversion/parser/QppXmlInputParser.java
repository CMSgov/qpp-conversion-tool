package gov.cms.qpp.conversion.parser;

import java.util.List;

import org.jdom2.Element;

import gov.cms.qpp.conversion.model.Registry;
import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.Node;

/**
 * Top level parser for parsing into QPP format. Contains a map of child parsers
 * that can parse an element.
 */
public class QppXmlInputParser extends XmlInputParser {
	

	protected static Registry parsers = new Registry(Decoder.class);

	public QppXmlInputParser() {
	}

	/**
	 * Iterates over the element to find all child elements. Finds any elements
	 * that match a NodeId in the parserMap. If there are any matches, calls
	 * internalParse with that Element on the parser class. Aggregates Nodes
	 * that are returned.
	 * 
	 */
	@Override
	protected Node parse(Element element, Node parentNode) {

		Node returnNode = parentNode;
		
		if (null != element) {
			

			List<Element> childElements = element.getChildren();
	
			String elementName;
			String templateId;
	
			for (Element ele : childElements) {
				elementName = ele.getName();
	
				// should any of the child elements be parsed with one of our
				// parsers?
				List<Element> chchildElements = ele.getChildren();
	
				for (Element eleele : chchildElements) {
					if ("templateId".equals(eleele.getName())) {
						templateId = eleele.getAttributeValue("root");
	
						// at this point we have both an element name and a child
						// element of template id
						// create a NodeId and see if we get a match inside
						// parserMap
	
						QppXmlInputParser childParser = (QppXmlInputParser) parsers.get(elementName, templateId);
	
						if (null != childParser) {
							Node childNodeValue = childParser.internalParse(ele, createNode(ele));
	
							if (null != childNodeValue) {
								parentNode.addChildNode(childNodeValue);
	
								// recursively call parse(element, node) with this
								// child as parent
								parse(ele, childNodeValue);
							} else {
								// recursively call parse(element, node) with a
								// placeholder node as parent
	
								Node placeholderNode = new Node();
								placeholderNode.setId(elementName, "placeholder");
	
								parse(ele, placeholderNode);
	
							}
						}
	
					}
				}
			}
		}

		return returnNode;
	}

	@Override
	protected Node internalParse(Element element, Node thisnode) {
		// this is the top level, so just return null

		return null;
	}
	
	private Node createNode(Element element) {
		Node thisNode = new Node();

		String templateId = null;
		// we will also need the template id
		Element templateIdElement = element.getChild("templateId");

		if (null != templateIdElement) {
			templateId = templateIdElement.getAttributeValue("root");
		}

		thisNode.setId(element.getName(), templateId);
		return thisNode;
	}

}
