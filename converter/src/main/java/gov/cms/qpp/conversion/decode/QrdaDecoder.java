package gov.cms.qpp.conversion.decode;

import com.google.common.base.Strings;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filter;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.correlation.PathCorrelator;
import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.validation.SupplementalData;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static gov.cms.qpp.conversion.decode.SupplementalDataEthnicityDecoder.SUPPLEMENTAL_DATA_CODE;
import static gov.cms.qpp.conversion.decode.SupplementalDataEthnicityDecoder.SUPPLEMENTAL_DATA_KEY;
import static gov.cms.qpp.conversion.decode.SupplementalDataPayerDecoder.SUPPLEMENTAL_DATA_PAYER_CODE;

public abstract class QrdaDecoder {

	protected final Context context;
	private Namespace defaultNs;
	protected Namespace xpathNs;

	public QrdaDecoder(final Context context) {
		this.context = context;
	}

	protected abstract DecodeResult decode(Element element, Node thisNode);

	/**
	 * Sets xml namespace
	 */
	public void setNamespace(Namespace defaultNs) {
		this.defaultNs = defaultNs;
		String defaultNsUri = defaultNs.getURI();
		xpathNs = Strings.isNullOrEmpty(defaultNsUri) ? Namespace.NO_NAMESPACE : Namespace.getNamespace("ns", defaultNsUri);
	}

	/**
	 * Returns the xpath from the path-correlation.json meta data
	 *
	 * @param attribute Key to the correlation data
	 * @return xpath expression as a string
	 */
	protected String getXpath(String attribute) {
		String template = this.getClass().getAnnotation(Decoder.class).value().name();
		return PathCorrelator.getXpath(template, attribute, defaultNs.getURI());
	}

	/**
	 * Executes an Xpath for an element and executes the consumer
	 *
	 * @param element Element the xpath is executed against
	 * @param expressionStr Xpath
	 * @param consumer Consumer to execute if the xpath matches
	 * @param filter Filter to apply for the xpath
	 * @param selectOne Whether to execute for the first match or multiple matches
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void setOnNode(Element element, String expressionStr,
		Consumer consumer, Filter<?> filter, boolean selectOne) {
		XPathExpression<?> expression = XPathFactory.instance().compile(expressionStr, filter, null,  xpathNs);

		if (selectOne) {
			Optional.ofNullable(expression.evaluateFirst(element)).ifPresent(consumer);
		} else {
			List<?> elems = expression.evaluate(element);
			Optional.ofNullable(elems)
				.ifPresent(notNullElems -> notNullElems.forEach(consumer));
		}
	}

	/**
	 * Sets a given Supplemental Data by type in the current Node
	 *
	 * @param element XML element that represents SupplementalDataCode
	 * @param thisNode Current Node to decode into
	 * @param type Current Supplemental Type to put onto this node
	 */
	public void setSupplementalDataOnNode(Element element, Node thisNode, SupplementalData.SupplementalType type) {
		String supplementalXpathCode = type.equals(SupplementalData.SupplementalType.PAYER) ?
			SUPPLEMENTAL_DATA_PAYER_CODE :  SUPPLEMENTAL_DATA_CODE;
		String expressionStr = getXpath(supplementalXpathCode);
		Consumer<? super Attribute> consumer = attr -> {
			String code = attr.getValue();
			thisNode.putValue(SUPPLEMENTAL_DATA_KEY, code, false);
		};
		setOnNode(element, expressionStr, consumer, Filters.attribute(), false);
	}
}
