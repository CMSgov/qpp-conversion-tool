package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.XmlDecoder;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.filter.Filters;

import java.util.function.Consumer;

/**
 * Decoder to parse Advancing Care Information Numerator Denominator Type
 * Measure Reference and Results.
 *
 * @author David Uselmann
 *
 */
@XmlDecoder(templateId = "2.16.840.1.113883.10.20.27.3.28")
public class AciProportionMeasureDecoder extends QppXmlDecoder {

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

		setMeasureIdOnNode(element, thisnode);

		return DecodeResult.TreeContinue;
	}

	/**
	 * setMeasureIdOnNode parses the xml fragment and sets the @extension
	 *
	 * @param element Element
	 * @param thisnode Node gov.cms.qpp.conversion.model.Node;
	 */
	protected void setMeasureIdOnNode(Element element, Node thisnode) {
		String expressionStr = "./ns:reference/ns:externalDocument/ns:id/@extension";
		Consumer<? super Attribute> consumer = p -> thisnode.putValue("measureId", p.getValue());
		setOnNode(element, expressionStr, consumer, Filters.attribute(), true);
	}

}
