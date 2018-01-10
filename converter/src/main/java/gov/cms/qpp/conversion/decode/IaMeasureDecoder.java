package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.filter.Filters;

import java.util.function.Consumer;

/**
 * Decoder to parse Improvement Activity Performed Measure Reference and Results.
 *
 */
@Decoder(TemplateId.IA_MEASURE)
public class IaMeasureDecoder extends QrdaXmlDecoder {

	public IaMeasureDecoder(Context context) {
		super(context);
	}

	/**
	 * Parses element containing a IA Measure into a node
	 *
	 * @param element Top element in the XML document
	 * @param thisNode Top node created in the XML document
	 * @return result that the decoder is finished for this node
	 */
	@Override
	protected DecodeResult internalDecode(Element element, Node thisNode) {
		String expressionStr = getXpath("measureId");
		Consumer<? super Attribute> consumer = p -> thisNode.putValue("measureId", p.getValue());
		setOnNode(element, expressionStr, consumer, Filters.attribute(), true);

		decode(element.getChild("component", defaultNs), thisNode);

		return DecodeResult.TREE_FINISHED;
	}
}
