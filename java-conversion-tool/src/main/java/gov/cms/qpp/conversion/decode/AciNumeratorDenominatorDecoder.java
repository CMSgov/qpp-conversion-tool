package gov.cms.qpp.conversion.decode;

import org.jdom2.Element;

import gov.cms.qpp.conversion.model.XmlDecoder;
import gov.cms.qpp.conversion.model.Node;

@XmlDecoder(templateId = "2.16.840.1.113883.10.20.27.3.3")
public class AciNumeratorDenominatorDecoder extends QppXmlDecoder {

	public AciNumeratorDenominatorDecoder() {
		// if this element decoder required children for parsing, 
		// then we would just
		// add another NodeId/QppXmlInputDecoder to the parent class's decoder registry
	}

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
