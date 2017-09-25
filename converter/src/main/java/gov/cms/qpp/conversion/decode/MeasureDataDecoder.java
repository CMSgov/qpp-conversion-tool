package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.filter.Filters;

import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Decoder for CMS V2 Measure Data eCQM
 */
@Decoder(TemplateId.MEASURE_DATA_CMS_V2)
public class MeasureDataDecoder extends QppXmlDecoder {

	protected static final Set<String> MEASURES = Stream.of("IPP", "IPOP", "DENEX", "DENOM", "DENEXCEP", "NUMER")
			.collect(Collectors.toSet());

	public static final String MEASURE_TYPE = "type";
	public static final String MEASURE_POPULATION = "populationId";

	public MeasureDataDecoder(Context context) {
		super(context);
	}

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
		setPopulationId(element, thisNode);
		return thisNode.hasValue(MEASURE_TYPE) ? DecodeResult.TREE_CONTINUE : DecodeResult.TREE_ESCAPED;
	}

	/**
	 * Locate measure code value in element and set on node.
	 *
	 * @param element Object that holds the XML representation of measure id
	 * @param thisNode Holder for decoded data
	 */
	private void setMeasure(Element element, Node thisNode) {
		String expressionStr = getXpath(MEASURE_TYPE);
		Consumer<? super Attribute> consumer = attr -> {
			String code = attr.getValue();
			if (MEASURES.contains(code)) {
				thisNode.putValue(MEASURE_TYPE, code, false);
			}
		};
		setOnNode(element, expressionStr, consumer, Filters.attribute(), false);
	}

	/**
	 * Locate measure sub-population GUUID from the element and set on node.
	 *
	 * @param element Object that holds the XML representation of measure id
	 * @param thisNode Holder for decoded data
	 */
	private void setPopulationId(Element element, Node thisNode) {
		String expressionStr = getXpath(MEASURE_POPULATION);
		Consumer<? super Attribute> consumer = attr ->
				thisNode.putValue(MEASURE_POPULATION, attr.getValue(), false);
		setOnNode(element, expressionStr, consumer, Filters.attribute(), false);
	}
}
