package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.XmlDecoder;
import org.jdom2.Element;

/**
 * Decoder to parse Advancing Care Information Numerator Denominator Type Measure Numerator Data.
 * @author David Uselmann
 *
 */
@XmlDecoder(templateId="2.16.840.1.113883.10.20.27.3.31")
public class AciProportionNumeratorDecoder extends QppXmlDecoder {
	@Override
	protected DecodeResult internalDecode(Element element, Node thisnode) {
		thisnode.putValue("name", "aciProportionNumerator");
		return DecodeResult.TreeContinue;
	}
}
