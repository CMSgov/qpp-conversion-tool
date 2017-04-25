package gov.cms.qpp.conversion.decode;


import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.filter.Filters;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

@Decoder(TemplateId.MEASURE_DATA_CMS_V2)
public class MeasureDataDecoder extends QppXmlDecoder {
	private final static List<String> MEASURES = Collections.singletonList("DENOM");
	public final static String MEASURE_TYPE = "type";


	/**
	 * Decodes V2 CMS Measure Data into an intermediate node
	 *
	 * @param element Element XML element that represents V2 CMS Measure Data
	 * @param thisNode Node that represents V2 CMS Measure Data.
	 * @return DecodeResult
	 */
	@Override
	protected DecodeResult internalDecode(Element element, Node thisNode) {
		setMeasure(element, thisNode);

		return thisNode.hasValue(MEASURE_TYPE) ? DecodeResult.TREE_CONTINUE : DecodeResult.PARENT_FINISHED;
	}

	/**
	 * Locate measure code value in element and set on node.
	 *
	 * @param element Object that holds the XML representation of measure id
	 * @param thisNode Holder for decoded data
	 */
	private void setMeasure(Element element, Node thisNode) {
		String expressionStr = "./ns:value/@code";
		Consumer<? super Attribute> consumer = attr -> {
			String code = attr.getValue();
			if (MEASURES.contains(code)) {
				thisNode.putValue(MEASURE_TYPE, code);
			}
		};
		setOnNode(element, expressionStr, consumer, Filters.attribute(), true);
	}
}
