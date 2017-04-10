package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.XmlDecoder;
import org.jdom2.Element;
/**
 * Decoder to parse Advancing Care Information Numerator Denominator Type
 * Measure Denominator Data.
 *
 * @author David Uselmann
 *
 */
@XmlDecoder(templateId = TemplateId.ACI_DENOMINATOR)
public class AciProportionDenominatorDecoder extends QppXmlDecoder {

	/**
	 * internalDecode reads the xml fragment "aciProportionDenominator" parses
	 * into gov.cms.qpp.conversion.model.Node
	 *
	 * @param element Element
	 * @param thisnode Node enclosing parent node xml fragment
	 * @return DecodeResult
	 */
	@Override
	protected DecodeResult internalDecode(Element element, Node thisnode) {
		thisnode.putValue("name", "aciProportionDenominator");
		return DecodeResult.TREE_CONTINUE;
	}
}
