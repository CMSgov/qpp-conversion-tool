package gov.cms.qpp.conversion.parser;

import org.jdom2.Element;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TransformHandler;



@TransformHandler(elementName="observation", templateId="2.16.840.1.113883.10.20.27.3.3")
public class RateAggregationInputParser extends QppXmlInputParser {

	public RateAggregationInputParser() {
		// if this element parser required children for parsing, then we would just
		// add another NodeId/QppXmlInputParser to the parent class's parserMap
	}

	// we do not override parse(Element, Node)

	// we DO override internalParse(Element)

	@Override
	protected Node internalParse(Element element) {
		// for the rate aggregation element, we want to get the
		// value element and then the value attribute

		Node returnNode = new Node();

		// we'll store the value with the key: rateAggregationDenominator
		// and the value that we find
		String valueText = null;
		String templateId = null;

		Element valueElement = element.getChild("value");

		// we will also need the template id
		Element templateIdElement = element.getChild("templateId");

		if (null != templateIdElement) {
			templateId = templateIdElement.getAttributeValue("root");
		}

		returnNode.setId(element.getName(), templateId);


		if (null != valueElement) {
			valueText = valueElement.getAttributeValue("value");

			if (null != valueText) {
				returnNode.putValue("rateAggregationDenominator", valueText);
			}

		}

		return returnNode;
	}

}
