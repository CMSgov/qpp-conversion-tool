package gov.cms.qpp.conversion.parser;

import org.jdom2.Element;

import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.Node;

@Decoder(elementName = "observation", templateId = "2.16.840.1.113883.10.20.27.3.3")
public class AciNumeratorDenominatorInputParser extends QppXmlInputParser {

	public AciNumeratorDenominatorInputParser() {
		// if this element parser required children for parsing, then we would
		// just
		// add another NodeId/QppXmlInputParser to the parent class's parserMap
	}

	// we do not override parse(Element, Node)

	// we DO override internalParse(Element)

	@Override
	protected Node internalParse(Element element, Node thisnode) {
		// for the rate aggregation element, we want to get the
		// value element and then the value attribute

		// we'll store the value with the key: rateAggregationDenominator
		// and the value that we find
		String valueText = null;

		Element valueElement = element.getChild("value");

		if (null != valueElement) {
			valueText = valueElement.getAttributeValue("value");

			if (null != valueText) {
				thisnode.putValue("aciNumeratorDenominator", valueText);
			}

		}

		return thisnode;
	}

}
