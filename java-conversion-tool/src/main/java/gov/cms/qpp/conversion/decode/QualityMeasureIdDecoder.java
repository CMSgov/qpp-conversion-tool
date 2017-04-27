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
import java.util.Optional;
import java.util.Set;

/**
 * Decoder to read XML Data for an Quality Measure Identifier (eCQM).
 */
@Decoder(TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2)
public class QualityMeasureIdDecoder extends QppXmlDecoder {

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
		String measureGuid = getMeasureGuid(element);

		DecodeResult decodeResult = DecodeResult.TREE_CONTINUE;

		if (MEASURE_ID_CONTAINING_STRATUM.contains(measureGuid)) {
			decodeResult = DecodeResult.TREE_ESCAPED;
		} else {
			thisNode.putValue("measureId", measureGuid);
		}

		return decodeResult;
	}

	/**
	 * Obtains the measure GUID.
	 *
	 * @param element XML element that represents the Quality Measure Identifier
	 * @return The measure GUID in the Quality Measure Identifier
	 */
	private String getMeasureGuid(final Element element) {
		String expressionStr = "./ns:reference/ns:externalDocument/ns:id[@root='2.16.840.1.113883.4.738']/@extension";

		XPathExpression<Attribute> expression = XPathFactory.instance().compile(expressionStr, Filters.attribute(), null,
			xpathNs);
		return Optional.ofNullable(expression.evaluateFirst(element)).map(attribute -> attribute.getValue())
			.orElse(null);
	}
}
