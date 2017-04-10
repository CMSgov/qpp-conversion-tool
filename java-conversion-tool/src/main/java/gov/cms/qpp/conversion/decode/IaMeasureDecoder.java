package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.XmlDecoderNew;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.filter.Filters;

import java.util.function.Consumer;

/**
 * Decoder to parse Improvement Activity Performed Measure Reference and Results.
 * @author David Puglielli
 *
 */
@XmlDecoderNew(TemplateId.IA_MEASURE)
public class IaMeasureDecoder extends QppXmlDecoder {

	@Override
	protected DecodeResult internalDecode(Element element, Node thisnode) {
		setMeasureIdOnNode(element, thisnode);

		decode(element.getChild("component", defaultNs), thisnode);

		return DecodeResult.TREE_FINISHED;
	}

	private void setMeasureIdOnNode(Element element, Node thisnode) {
		String expressionStr = "./ns:reference/ns:externalDocument/ns:id/@extension";
		Consumer<? super Attribute> consumer = p -> thisnode.putValue("measureId", p.getValue());
		setOnNode(element, expressionStr, consumer, Filters.attribute(), true);
	}
}
