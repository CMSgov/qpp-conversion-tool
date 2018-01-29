package gov.cms.qpp.conversion.decode;

import org.jdom2.Element;
import org.jdom2.xpath.XPathHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.correlation.PathCorrelator;
import gov.cms.qpp.conversion.model.DecodeData;
import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Registry;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.segmentation.QrdaScope;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * The engine for parsing XML into QPP format.
 */
public class QrdaDecoderEngine extends XmlDecoderEngine {

	private static final Logger DEV_LOG = LoggerFactory.getLogger(QrdaDecoderEngine.class);
	private static final String TEMPLATE_ID = "templateId";
	private static final String NOT_VALID_QRDA_III_FORMAT = "The file is not a QRDA-III XML document";
	private static final String ROOT_STRING = "root";
	private static final String EXTENSION_STRING = "extension";

	protected final Context context;
	private final Set<TemplateId> scope;
	private final Registry<QrdaDecoder> decoders;

	/**
	 * Initialize a QPP xml decoder
	 */
	public QrdaDecoderEngine(Context context) {
		Objects.requireNonNull(context, "converter");

		this.context = context;
		this.scope = context.hasScope() ? QrdaScope.getTemplates(context.getScope()) : null;
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
		rootNode.setPath(XPathHelper.getAbsolutePath(rootElement));

		QrdaDecoder rootDecoder = null;
		for (Element element : rootElement.getChildren(TEMPLATE_ID, rootElement.getNamespace())) {
			rootDecoder = getDecoder(getTemplateId(element));
			if (rootDecoder != null) {
				break;
			}
		}

		if (rootDecoder != null) {
			rootNode = this.decodeTree(rootElement, rootNode).getNode().getChildNodes().get(0);
		} else {
			rootNode = this.decodeTree(rootElement, rootNode).getNode();
		}

		return rootNode;
	}

	/**
	 * Decodes the element specified and the entire tree of child {@link Element}s below.
	 *
	 * @param element The element who's tree to decode.
	 * @param parentNode The node to add any possible decoded child {@link Node}s.
	 * @return The tuple of a {@link DecodeResult} and {@link Node} that was decoded from this tree.
	 */
	private DecodeData decodeTree(final Element element, final Node parentNode) {
		DecodeData result = decodeSingleElement(element, parentNode);
		DecodeResult decodedResult = result.getDecodeResult();
		Node decodedNode = result.getNode();

		decodedNode = decideNewParentNode(decodedNode, parentNode);

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
	 * @param element The element to decode.
	 * @param parentNode The node add the child decoded {@link Node} to.
	 * @return The tuple of a {@link DecodeResult} and {@link Node} that was decoded from the {@link Element}.
	 */
	private DecodeData decodeSingleElement(final Element element, final Node parentNode) {

		QrdaDecoder decoder = decoderForElement(element);

		if (null == decoder) {
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

		childNode.setPath(XPathHelper.getAbsolutePath(parentElement));
		parentNode.addChildNode(childNode);

		return new DecodeData(decodeResult, childNode);
	}

	/**
	 * Iterates over all the children of the passed in {@link Element} and calls {@link #decodeTree(Element, Node)} on them.
	 *
	 * @param element The element who's children will be decoded.
	 * @param parentNode The parent node
	 * @return The tuple of a {@link DecodeResult} and {@link Node} that was decoded from the children.
	 */
	private DecodeData decodeChildren(final Element element, final Node parentNode) {

		List<Element> childElements = element.getChildren();

		DecodeData decodeData = new DecodeData(DecodeResult.TREE_CONTINUE, parentNode);

		Node currentParentNode = parentNode;

		for (Element childElement : childElements) {
			DecodeData childDecodeData = decodeTree(childElement, currentParentNode);

			DecodeResult childDecodeResult = childDecodeData.getDecodeResult();
			Node childDecodedNode = childDecodeData.getNode();

			if (DecodeResult.TREE_FINISHED == childDecodeResult) {
				decodeData = new DecodeData(DecodeResult.TREE_CONTINUE, parentNode);
				break;
			}

			currentParentNode = childDecodedNode == null ? currentParentNode : childDecodedNode;
		}

		return decodeData;
	}

	/**
	 * Get the {@link QrdaDecoder} for the passed in {@link Element}.
	 *
	 * @param element The element.
	 * @return The QRDA decoder.
	 */
	private QrdaDecoder decoderForElement(final Element element) {
		if (!TEMPLATE_ID.equals(element.getName())) {
			return null;
		}

		TemplateId templateId = getTemplateId(element);

		return getDecoder(templateId);
	}

	/**
	 *  If the {@code decodedNode} is not null, it is returned, else the {@code originalParent} is returned.
	 *
	 * @param decodedNode The newly decoded {@link Node}.
	 * @param originalParent The original parent {@link Node}.
	 * @return The new parent node.
	 */
	private Node decideNewParentNode(final Node decodedNode, final Node originalParent) {
		Node newParent = originalParent;

		if (null != decodedNode) {
			newParent = decodedNode;
		}

		return newParent;
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
	 * Retrieve a permitted {@link Decoder}. {@link #scope} is used to determine which DECODERS are allowable.
	 *
	 * @param templateId string representation of a would be decoder's template id
	 * @return decoder that corresponds to the given template id
	 */
	private QrdaDecoder getDecoder(TemplateId templateId) {
		QrdaDecoder qppDecoder = decoders.get(templateId);
		if (qppDecoder != null) {
			if (scope == null) {
				return qppDecoder;
			}

			Decoder decoder = qppDecoder.getClass().getAnnotation(Decoder.class);
			TemplateId template = decoder == null ? TemplateId.DEFAULT : decoder.value();
			return !scope.contains(template) ? null : qppDecoder;
		}

		return null;
	}

	/**
	 * Determines whether the XML Document provided is a valid QRDA-III formatted file
	 *
	 * @param xmlDoc XML Document to be tested
	 * @return If the XML document is a correctly QRDA-III formatted file
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

	/**
	 * Checks whether the element's name is {@code ClinicalDocument}.
	 *
	 * @param rootElement The element to check.
	 * @return True or false.
	 */
	private boolean containsClinicalDocumentElement(Element rootElement) {
		return "ClinicalDocument".equals(rootElement.getName());
	}

	/**
	 * Checks whether the element has a child {@code templateId} element that is the Clinical Document {@link TemplateId}.
	 *
	 * @param rootElement The element to check.
	 * @return True or false.
	 */
	private boolean containsClinicalDocumentTemplateId(Element rootElement) {
		boolean containsTemplateId = false;

		List<Element> clinicalDocumentChildren = rootElement.getChildren(TEMPLATE_ID, rootElement.getNamespace());

		for (Element currentChild : clinicalDocumentChildren) {
			TemplateId templateId = getTemplateId(currentChild);
			if (templateId == TemplateId.CLINICAL_DOCUMENT) {
				containsTemplateId = templateId.getExtension().equals(currentChild.getAttributeValue(EXTENSION_STRING));
				break;
			}
		}

		return containsTemplateId;
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
}
