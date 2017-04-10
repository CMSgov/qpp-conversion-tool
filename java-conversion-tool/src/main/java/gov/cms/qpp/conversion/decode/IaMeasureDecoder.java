package gov.cms.qpp.conversion.decode;

import java.util.function.Consumer;

import gov.cms.qpp.conversion.model.TemplateId;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.filter.Filters;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.XmlDecoder;

/**
 * Decoder to parse Improvement Activity Performed Measure Reference and Results.
 * @author David Puglielli
 *
 */
@XmlDecoder(templateId = TemplateId.IA_MEASURE)
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
