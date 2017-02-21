package gov.cms.qpp.conversion.parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom2.Element;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.NodeId;

/**
 * Top level parser for parsing into QPP format. Contains a map of child parsers
 * that can parse an element.
 */
public class QppXmlInputParser extends XmlInputParser {

	protected Map<NodeId, QppXmlInputParser> parserMap;

	public QppXmlInputParser() {
		System.out.println("default constructor");
	}

	public QppXmlInputParser(boolean initMap) {
		if (initMap) {
			System.out.println("in qpp parser constructor");
			parserMap = new HashMap<>();
			System.out.println(parserMap);

			parserMap.put(new NodeId("observation", "2.16.840.1.113883.10.20.27.3.3"),
					new RateAggregationInputParser());
			System.out.println("finished with qpp parser constructor");
		}
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

					QppXmlInputParser childParser = parserMap.get(new NodeId(elementName, templateId));

					if (null != childParser) {
						Node childNodeValue = childParser.internalParse(ele);

						if (null != childNodeValue) {
							parentNode.addChildNode(childNodeValue);

							// recursively call parse(element, node) with this
							// child as parent
							parse(ele, childNodeValue);
						} else {
							// recursively call parse(element, node) with a
							// placeholder node as parent

							Node placeholderNode = new Node();
							placeholderNode.setInternalId(new NodeId(elementName, "placeholder"));

							parse(ele, placeholderNode);

						}
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
