package gov.cms.qpp.conversion.parser;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.jdom2.Element;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.NodeId;

public class RateAggregationInputParser extends QppXmlInputParser {

	public RateAggregationInputParser() {
		super();
		// if this element parser required children for parsing, then we would
		// just
		// add another NodeId/QppXmlInputParser to the parent class's parserMap
		System.out.println("in rate aggr constructor");
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

		returnNode.setInternalId(new NodeId(element.getName(), templateId));

		Map<String, Serializable> valueMap = new HashMap<>();

		if (null != valueElement) {
			valueText = valueElement.getAttributeValue("value");

			if (null != valueText) {
				valueMap.put("rateAggregationDenominator", valueText);
			}

		}

		returnNode.setData(valueMap);

		return returnNode;
	}

}
