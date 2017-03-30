package gov.cms.qpp.conversion.decode;

import org.jdom2.Element;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.XmlDecoder;

/**
 * Decoder to parse Advancing Care Information Numerator Denominator Type Measure Denominator Data.
 * @author David Uselmann
 *
 */
@XmlDecoder(templateId="2.16.840.1.113883.10.20.27.3.32")
public class AciProportionDenominatorDecoder extends QppXmlDecoder {
	@Override
	protected DecodeResult internalDecode(Element element, Node thisnode) {
		thisnode.putValue("name", "aciProportionDenominator");
		putDenominator(element, thisnode);
		return DecodeResult.TreeContinue;
	}

	private void putDenominator(Element element, Node thisnode) {
		Element entryRelationship = getEntryRelationship(element);
		if (entryRelationship == null) {
			return;
		}

		Element value = getValue(entryRelationship);
		if (value == null) {
			return;
		}

		thisnode.putValue("denominator", value.getAttributeValue("value"));
	}
	
	private Element getEntryRelationship(Element element) {
		return getChild(element, "entryRelationship");
	}

	private Element getValue(Element element) {
		return getChild(element, "value");
	}

	private Element getChild(Element element, String name) {
		return element.getChild(name, element.getNamespace());
	}
}
