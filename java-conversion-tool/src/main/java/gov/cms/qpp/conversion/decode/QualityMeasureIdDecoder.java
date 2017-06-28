package gov.cms.qpp.conversion.decode;


import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Decoder to read XML Data for an Quality Measure Identifier (eCQM).
 */
@Decoder(TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2)
public class QualityMeasureIdDecoder extends QppXmlDecoder {

	private static final String MEASURE_ID = "measureId";
	private static final Set<String> MEASURE_ID_CONTAINING_STRATUM = new HashSet<>(Arrays.asList(
		"40280381-528a-60ff-0152-8e089ed20376", "40280381-51f0-825b-0152-22b52da917ba",
		"40280381-51f0-825b-0152-22b695b217dc", "40280381-52fc-3a32-0153-1f6962df0f9c"));

	/**
	 * Decodes an Quality Measure Identifier into the intermediate Node format.
	 *
	 * Currently, only decodes measures that do not contain stratum.
	 *
	 * @param element  XML element that represents the Quality Measure Identifier
	 * @param thisNode Node represents the quality measure identifier
	 * @return {@code DecodeResult.TREE_CONTINUE} to continue down the parsed XML
	 */
	@Override
	protected DecodeResult internalDecode(Element element, Node thisNode) {
		DecodeResult decodeResult = DecodeResult.TREE_CONTINUE;
		List<String> measureGuids = getMeasureGuid(element);

		if (measureGuids.stream().anyMatch(MEASURE_ID_CONTAINING_STRATUM::contains)) {
			decodeResult = DecodeResult.TREE_ESCAPED;
		} else {
			measureGuids.forEach(measureGuid ->
				thisNode.putValue(MEASURE_ID, measureGuid, false));
		}

		return decodeResult;
	}

	/**
	 * Obtains the measure GUID.
	 *
	 * @param element XML element that represents the Quality Measure Identifier
	 * @return The measure GUID in the Quality Measure Identifier
	 */
	private List<String> getMeasureGuid(final Element element) {
		String expressionStr = getXpath(MEASURE_ID);

		XPathExpression<Attribute> expression = XPathFactory.instance()
			.compile(expressionStr, Filters.attribute(), null, xpathNs);
		return expression.evaluate(element).stream()
			.map(Attribute::getValue)
			.collect(Collectors.toList());
	}
}
