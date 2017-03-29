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
	/**
         * internalDecode reads the xml fragment "aciProportionDenominator"
         * parses into gov.cms.qpp.conversion.model.Node
         * @param element Element
         * @param thisnode Node enclosing parent node xml fragment
         * @return DecodeResult
         */
        @Override
	protected DecodeResult internalDecode(Element element, Node thisnode) {
		thisnode.putValue("name", "aciProportionDenominator");
		return DecodeResult.TreeContinue;
	}
}
