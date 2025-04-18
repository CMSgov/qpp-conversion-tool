package gov.cms.qpp.conversion.decode;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static gov.cms.qpp.conversion.model.Constants.MEASURE_ID;

/**
 * Decoder to read XML Data for an Quality Measure Identifier (eCQM).
 */
@Decoder(TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V5)
public class QualityMeasureIdDecoder extends QrdaDecoder {

	public QualityMeasureIdDecoder(Context context) {
		super(context);
	}

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
	protected DecodeResult decode(Element element, Node thisNode) {
		List<String> measureGuids = getMeasureGuid(element);

		measureGuids.forEach(measureGuid ->
			thisNode.putValue(MEASURE_ID, measureGuid.toLowerCase(Locale.ENGLISH), false));

		return DecodeResult.TREE_CONTINUE;
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
