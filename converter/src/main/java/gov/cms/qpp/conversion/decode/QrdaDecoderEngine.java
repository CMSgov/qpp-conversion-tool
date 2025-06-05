package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.correlation.PathCorrelator;
import gov.cms.qpp.conversion.model.DecodeData;
import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Registry;
import gov.cms.qpp.conversion.model.TemplateId;
import org.jdom2.Element;
import org.jdom2.located.Located;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static gov.cms.qpp.conversion.model.Constants.*;

/**
 * The engine for parsing XML into QPP format.
 */
public class QrdaDecoderEngine extends XmlDecoderEngine {

	private static final Logger DEV_LOG = LoggerFactory.getLogger(QrdaDecoderEngine.class);
	protected final Context context;
	private final Registry<QrdaDecoder> decoders;

	/**
	 * Initialize a QPP xml decoder
	 *
	 * @param context Establish context for decoder engine
	 */
	public QrdaDecoderEngine(Context context) {
		Objects.requireNonNull(context, "converter");

		// Defensive copy of incoming Context
		Context ctxCopy = new Context();
		ctxCopy.setDoValidation(context.isDoValidation());
		ctxCopy.setHistorical(context.isHistorical());
		this.context = ctxCopy;

		this.decoders = context.getRegistry(Decoder.class);
	}

	/**
	 * Decodes the top of the XML document.
	 *
	 * @param xmlDoc XML Document to be parsed.
	 * @return The root node.
	 */
	@Override
	public Node decode(Element xmlDoc) {
		Node rootNode = new Node();
		Element rootElement = xmlDoc.getDocument().getRootElement();
		defaultNs = rootElement.getNamespace();

		rootNode.setType(TemplateId.PLACEHOLDER);
		rootNode.setElementForLocation(rootElement);

		addLineAndColumnToNode(rootElement, rootNode);

		QrdaDecoder rootDecoder = null;
		for (Element element : rootElement.getChildren(TEMPLATE_ID, rootElement.getNamespace())) {
			rootDecoder = getDecoder(getTemplateId(element));
			if (rootDecoder != null) {
				break;
			}
		}

		if (rootDecoder != null) {
			rootNode = this.decodeTree(rootElement, rootNode)
					.getNode()
					.getChildNodes()
					.get(0);
		} else {
			rootNode = this.decodeTree(rootElement, rootNode).getNode();
		}

		return rootNode;
	}

	/**
	 * Decodes the element specified and the entire tree of child {@link Element}s below.
	 *
	 * @param element    The element whose tree to decode.
	 * @param parentNode The node to add any decoded child {@link Node}s.
	 * @return The tuple of a {@link DecodeData} and {@link Node} that was decoded from this tree.
	 */
	private DecodeData decodeTree(final Element element, final Node parentNode) {
		DecodeData result = decodeSingleElement(element, parentNode);
		DecodeResult decodedResult = result.getDecodeResult();
		Node decodedNode = result.getNode();

		decodedNode = (decodedNode != null) ? decodedNode : parentNode;

		if (DecodeResult.TREE_FINISHED == decodedResult) {
			return new DecodeData(DecodeResult.TREE_FINISHED, decodedNode);
		} else if (DecodeResult.TREE_ESCAPED == decodedResult) {
			return new DecodeData(DecodeResult.TREE_FINISHED, null);
		}

		return decodeChildren(element, decodedNode);
	}

	/**
	 * Decodes the passed in element if it is a {@code templateId} and assigns it to the {@code parentNode}.
	 *
	 * @param element    The element to decode.
	 * @param parentNode The node to add the child decoded {@link Node} to.
	 * @return The tuple of a {@link DecodeData} and {@link Node} that was decoded.
	 */
	private DecodeData decodeSingleElement(Element element, Node parentNode) {
		QrdaDecoder decoder = decoderForElement(element);
		if (decoder == null) {
			return new DecodeData(DecodeResult.TREE_CONTINUE, null);
		}

		TemplateId templateId = getTemplateId(element);
		Node childNode = new Node(templateId, parentNode);
		childNode.setDefaultNsUri(defaultNs.getURI());
		decoder.setNamespace(element.getNamespace());

		Element parentElement = element.getParentElement();
		DecodeResult decodeResult = decoder.decode(parentElement, childNode);

		if (decodeResult == DecodeResult.TREE_ESCAPED) {
			return new DecodeData(DecodeResult.TREE_ESCAPED, null);
		}

		childNode.setElementForLocation(parentElement);
		addLineAndColumnToNode(element, childNode);
		parentNode.addChildNode(childNode);

		return new DecodeData(decodeResult, childNode);
	}

	/**
	 * Iterates over all children of the passed in {@link Element} and calls {@link #decodeTree(Element, Node)} on them.
	 *
	 * @param element    The element whose children will be decoded.
	 * @param parentNode The parent node.
	 * @return The tuple of a {@link DecodeData} and {@link Node} decoded from the children.
	 */
	private DecodeData decodeChildren(final Element element, final Node parentNode) {
		List<Element> filteredChildElements = getUniqueTemplateIdElements(element.getChildren());

		DecodeData decodeData = new DecodeData(DecodeResult.TREE_CONTINUE, parentNode);
		Node currentParentNode = parentNode;

		for (Element childElement : filteredChildElements) {
			DecodeData childDecodeData = decodeTree(childElement, currentParentNode);
			DecodeResult childDecodeResult = childDecodeData.getDecodeResult();
			Node childDecodedNode = childDecodeData.getNode();

			if (DecodeResult.TREE_FINISHED == childDecodeResult) {
				decodeData = new DecodeData(DecodeResult.TREE_CONTINUE, parentNode);
				break;
			}
			currentParentNode = (childDecodedNode == null ? currentParentNode : childDecodedNode);
		}

		return decodeData;
	}

	/**
	 * Reduces the {@code templateId} {@link Element}s so there are no duplicates.
	 *
	 * @param childElements The elements to filter.
	 * @return A {@link List} of filtered {@link Element}s.
	 */
	private List<Element> getUniqueTemplateIdElements(final List<Element> childElements) {
		Set<TemplateId> uniqueTemplates = EnumSet.noneOf(TemplateId.class);

		List<Element> children = childElements.stream()
				.filter(filterElement -> {
					boolean isTemplateId = TEMPLATE_ID.equals(filterElement.getName());
					TemplateId filterTemplateId = getTemplateId(filterElement);

					boolean elementWillStay = true;
					if (isTemplateId) {
						if (getDecoder(filterTemplateId) == null || uniqueTemplates.contains(filterTemplateId)) {
							elementWillStay = false;
						}
						uniqueTemplates.add(filterTemplateId);
					}
					return elementWillStay;
				})
				.collect(Collectors.toList());

		return (uniqueTemplates.isEmpty()
				|| uniqueTemplates.stream().anyMatch(template -> TemplateId.UNIMPLEMENTED != template))
				? children
				: new ArrayList<>();
	}

	/**
	 * Get the {@link QrdaDecoder} for the passed in {@link Element}.
	 *
	 * @param element The element.
	 * @return The QRDA decoder, or null if none.
	 */
	private QrdaDecoder decoderForElement(final Element element) {
		if (!TEMPLATE_ID.equals(element.getName())) {
			return null;
		}
		return getDecoder(getTemplateId(element));
	}

	/**
	 * Get the {@link TemplateId} for the given element.
	 *
	 * @param idElement The element to convert into a {@link TemplateId}.
	 * @return The {@link TemplateId}.
	 */
	private TemplateId getTemplateId(final Element idElement) {
		String root = idElement.getAttributeValue(ROOT_STRING);
		String extension = idElement.getAttributeValue(EXTENSION_STRING);
		return TemplateId.getTemplateId(root, extension, context);
	}

	/**
	 * Retrieve a permitted {@link Decoder}.
	 *
	 * @param templateId The template ID.
	 * @return decoder that corresponds to the given template ID.
	 */
	private QrdaDecoder getDecoder(TemplateId templateId) {
		return decoders.get(templateId);
	}

	/**
	 * Determines whether the XML Document provided is a valid QRDA-III formatted file.
	 *
	 * @param xmlDoc XML Document to be tested.
	 * @return true if the XML document is correctly formatted.
	 */
	@Override
	protected boolean accepts(Element xmlDoc) {
		final Element rootElement = xmlDoc.getDocument().getRootElement();
		boolean isValidQrdaFile = containsClinicalDocumentElement(rootElement)
				&& containsClinicalDocumentTemplateId(rootElement);
		if (!isValidQrdaFile) {
			DEV_LOG.error(NOT_VALID_QRDA_III_FORMAT);
		}
		return isValidQrdaFile;
	}

	private boolean containsClinicalDocumentElement(Element rootElement) {
		return "ClinicalDocument".equals(rootElement.getName());
	}

	private boolean containsClinicalDocumentTemplateId(Element rootElement) {
		boolean containsTemplateId = false;
		List<Element> clinicalDocumentChildren = rootElement.getChildren(TEMPLATE_ID, rootElement.getNamespace());
		for (Element currentChild : clinicalDocumentChildren) {
			TemplateId templateId = getTemplateId(currentChild);
			if (templateId == TemplateId.CLINICAL_DOCUMENT) {
				containsTemplateId = true;
				break;
			}
		}
		return containsTemplateId;
	}

	private void addLineAndColumnToNode(Element element, Node node) {
		if (element instanceof Located) {
			Located located = (Located) element;
			node.setLine(located.getLine());
			node.setColumn(located.getColumn());
		}
	}

	/**
	 * Returns the XPath from the path-correlation.json metadata.
	 *
	 * @param attribute Key to the correlation data.
	 * @return XPath expression as a string.
	 */
	protected String getXpath(String attribute) {
		String template = this.getClass()
				.getAnnotation(Decoder.class)
				.value()
				.name();
		return PathCorrelator.getXpath(template, attribute, defaultNs.getURI());
	}
}
