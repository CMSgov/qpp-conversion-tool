package gov.cms.qpp.conversion.decode;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.correlation.PathCorrelator;
import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filter;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

public abstract class QrdaDecoder {

	private static final String NSURI_VARIABLE = "nsuri";

	protected final Context context;
	protected Namespace xpathNs = Namespace.NO_NAMESPACE;

	/**
	 * Construct a QRDA decoder with the given context.
	 *
	 * @param context conversion context
	 */
	@SuppressFBWarnings("EI_EXPOSE_REP2")
	public QrdaDecoder(Context context) {
		this.context = context;
	}

	protected abstract DecodeResult decode(Element element, Node thisNode);

	/**
	 * Sets the xml namespace.
	 *
	 * Required so that we find the correct elements.  For example, XPath evaluation needs to know namespaces.
	 *
	 * @param defaultNs namespace assigned to decoder
	 */
	public void setNamespace(Namespace defaultNs) {
		String defaultNsUri = defaultNs.getURI();
		xpathNs = StringUtils.isEmpty(defaultNsUri) ?
				Namespace.NO_NAMESPACE : Namespace.getNamespace("ns", defaultNsUri);
	}

	/**
	 * Variables bound for every converter xpath. The namespace URI is data, never
	 * expression text, which makes xpath injection through xmlns impossible.
	 */
	private Map<String, Object> xpathVariables() {
		return Collections.singletonMap(NSURI_VARIABLE, xpathNs.getURI());
	}

	/**
	 * Returns the xpath from the path-correlation.json meta data
	 *
	 * @param attribute Key to the correlation data
	 * @return xpath expression as a string
	 */
	protected String getXpath(String attribute) {
		String template = this.getClass()
				.getAnnotation(Decoder.class)
				.value().name();
		return PathCorrelator.getXpath(template, attribute);
	}

	/**
	 * Executes an XPath for an element and executes the consumer
	 *
	 * @param element      Element the xpath is executed against
	 * @param expressionStr Xpath
	 * @param consumer     Consumer to execute if the xpath matches
	 * @param filter       Filter to apply for the xpath
	 * @param selectOne    Whether to execute for the first match or multiple matches
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void setOnNode(Element element, String expressionStr,
							 Consumer consumer, Filter<?> filter, boolean selectOne) {
		XPathExpression<?> expression = XPathFactory.instance()
				.compile(expressionStr, filter, xpathVariables(), xpathNs);

		if (selectOne) {
			Optional.ofNullable(expression.evaluateFirst(element)).ifPresent(consumer);
		} else {
			List<?> elems = expression.evaluate(element);
			Optional.ofNullable(elems)
					.ifPresent(notNullElems -> notNullElems.forEach(consumer));
		}
	}

	/**
	 * Executes an XPath for attributes and passes values to the consumer
	 *
	 * @param element       Element the xpath is executed against
	 * @param expressionStr Xpath
	 * @param consumer      Consumer to execute with list of string values
	 * @param filter        Filter to apply for the xpath
	 */
	protected void setMultipleAttributesOnNode(Element element, String expressionStr,
											   Consumer<List<String>> consumer, Filter<Attribute> filter) {
		XPathExpression<Attribute> expression = XPathFactory.instance()
				.compile(expressionStr, filter, xpathVariables(), xpathNs);
		List<Attribute> elems = expression.evaluate(element);
		List<String> values = new ArrayList<>();
		elems.forEach(attr -> values.add(attr.getValue()));
		consumer.accept(values);
	}
}
