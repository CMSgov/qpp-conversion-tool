package gov.cms.qpp.conversion.parser;

import java.util.List;

import org.jdom2.Element;

import gov.cms.qpp.conversion.model.ConverterRegistry;
import gov.cms.qpp.conversion.model.Node;

/**
 * Top level parser for parsing into QPP format. Contains a map of child parsers
 * that can parse an element.
 */
public class QppXmlInputParser extends XmlInputParser {
	
	public static String TEMPLATE_ID = "templateId";

	protected static ConverterRegistry<QppXmlInputParser> parsers = new ConverterRegistry<>();

	public QppXmlInputParser() {
		System.out.println("default constructor");
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

		List<Element> childElements = element.getChildren();

		String elementName = element.getName();
		String templateId;


			for (Element ele : childElements) {
				if ("templateId".equals(ele.getName())) {
					templateId = ele.getAttributeValue("root");

					// at this point we have both an element name and a child
					// element of template id
					// create a NodeId and see if we get a match inside
					// parserMap

					QppXmlInputParser childParser = parsers.getConverter(elementName, templateId);

					if (null != childParser) {
						Node childNodeValue = childParser.internalParse(element);

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

		return returnNode;
	}

	@Override
	protected Node internalParse(Element element) {
		// this is the top level, so just return null

		return null;
	}

}
