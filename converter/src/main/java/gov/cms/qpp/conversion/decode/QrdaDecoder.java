package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.correlation.PathCorrelator;
import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.Node;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import org.apache.commons.lang3.StringUtils;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.Filter;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

/**
 * Base class for QRDA decoders.
 */
public abstract class QrdaDecoder {

	protected final Context context;
	protected Namespace xpathNs = Namespace.NO_NAMESPACE;
	private Namespace defaultNs = Namespace.NO_NAMESPACE;

	public QrdaDecoder(Context context) {
		// Defensive copy of incoming Context
		Context ctxCopy = new Context();
		ctxCopy.setDoValidation(context.isDoValidation());
		ctxCopy.setHistorical(context.isHistorical());
		this.context = ctxCopy;
	}

	protected abstract DecodeResult decode(Element element, Node thisNode);

	/**
	 * Sets the XML namespace. Uses a new Namespace instance to avoid storing the caller’s reference directly.
	 *
	 * @param defaultNs namespace assigned to decoder
	 */
	public void setNamespace(Namespace defaultNs) {
		if (defaultNs == null) {
			this.defaultNs = Namespace.NO_NAMESPACE;
			this.xpathNs = Namespace.NO_NAMESPACE;
		} else {
			// Create a new Namespace with the same prefix and URI to avoid exposing the original
			String prefix = defaultNs.getPrefix();
			String uri = defaultNs.getURI();
			this.defaultNs = Namespace.getNamespace(prefix, uri);
			String defaultNsUri = uri;
			this.xpathNs = StringUtils.isEmpty(defaultNsUri)
					? Namespace.NO_NAMESPACE
					: Namespace.getNamespace("ns", defaultNsUri);
		}
	}

	/**
	 * Returns the XPath from the path‐correlation.json metadata.
	 *
	 * @param attribute key to the correlation data
	 * @return XPath expression as a string
	 */
	protected String getXpath(String attribute) {
		String template = this.getClass()
				.getAnnotation(Decoder.class)
				.value()
				.name();
		return PathCorrelator.getXpath(template, attribute, defaultNs.getURI());
	}

	/**
	 * Executes an XPath for an element and calls the consumer for each match.
	 *
	 * @param element       Element to evaluate
	 * @param expressionStr XPath expression
	 * @param consumer      Consumer to execute on each match
	 * @param filter        Filter to apply for the XPath
	 * @param selectOne     If true, only the first match is passed to consumer
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void setOnNode(Element element,
							 String expressionStr,
							 Consumer consumer,
							 Filter<?> filter,
							 boolean selectOne) {
		XPathExpression<?> expression = XPathFactory.instance()
				.compile(expressionStr, filter, null, xpathNs);

		if (selectOne) {
			Optional.ofNullable(expression.evaluateFirst(element)).ifPresent(consumer);
		} else {
			List<?> elems = expression.evaluate(element);
			Optional.ofNullable(elems)
					.ifPresent(notNullElems -> notNullElems.forEach(consumer));
		}
	}

	/**
	 * Executes an XPath to gather attribute values and passes them as a list to the consumer.
	 *
	 * @param element       Element to evaluate
	 * @param expressionStr XPath expression for attributes
	 * @param consumer      Consumer to execute with list of attribute values
	 * @param filter        Filter to apply for the XPath
	 */
	protected void setMultipleAttributesOnNode(Element element,
											   String expressionStr,
											   Consumer<List<String>> consumer,
											   Filter<Attribute> filter) {
		XPathExpression<Attribute> expression = XPathFactory.instance()
				.compile(expressionStr, filter, null, xpathNs);
		List<Attribute> elems = expression.evaluate(element);

		List<String> values = new ArrayList<>();
		elems.forEach(attr -> values.add(attr.getValue()));
		consumer.accept(values);
	}
}
