package gov.cms.qpp.conversion.decode;


import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.filter.Filters;

import java.util.function.Consumer;

/**
 * Decoder to read XML Data for an Quality Measure Identifier (eCQM).
 */
@Decoder(TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2)
public class QualityMeasureIdDecoder extends QppXmlDecoder {

	/**
	 * Decodes an Quality Measure Identifier into the intermediate Node format
	 *
	 * @param element  XML element that represents the Quality Measure Identifier
	 * @param thisNode Node represents the quality measure identifier
	 * @return {@code DecodeResult.TREE_CONTINUE} to continue down the parsed XML
	 */
	@Override
	protected DecodeResult internalDecode(Element element, Node thisNode) {
		String expressionStr = "./ns:reference/ns:externalDocument/ns:id/@extension";
		Consumer<? super Attribute> consumer = p -> thisNode.putValue("measureId", p.getValue());
		setOnNode(element, expressionStr, consumer, Filters.attribute(), true);
		return DecodeResult.TREE_CONTINUE;
	}
}
