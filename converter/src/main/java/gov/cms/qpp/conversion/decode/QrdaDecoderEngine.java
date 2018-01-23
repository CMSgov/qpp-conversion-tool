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
 * Top level Decoder for parsing into QPP format.
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
	 * Initialize a qpp xml decoder
	 */
	public QrdaDecoderEngine(Context context) {
		Objects.requireNonNull(context, "converter");

		this.context = context;
		this.scope = context.hasScope() ? QrdaScope.getTemplates(context.getScope()) : null;
		this.decoders = context.getRegistry(Decoder.class);
	}

	/**
	 * Decodes the top of the XML document
	 *
	 * @param xmlDoc XML Document to be parsed
	 * @return Root node
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
			TemplateId templateId = getTemplateId(element);
			rootDecoder = getDecoder(templateId);
			if (rootDecoder != null) {
				break;
			}
		}

		if (rootDecoder != null) {
			return this.decodeTree(rootElement, rootNode).getNode().getChildNodes().get(0);
		} else {
			return this.decodeTree(rootElement, rootNode).getNode();
		}
	}

	private DecodeData decodeTree(final Element element, final Node parentNode) {
		DecodeData result = decodeSingleElement(element, parentNode);
		DecodeResult decodedResult = result.getDecodeResult();
		Node decodedNode = result.getNode();

		if (null == decodedNode) {
			decodedNode = parentNode;
		}

		if (DecodeResult.TREE_FINISHED == decodedResult) {
			return new DecodeData(DecodeResult.TREE_FINISHED, decodedNode);
		} else if (DecodeResult.TREE_ESCAPED == decodedResult) {
			return new DecodeData(DecodeResult.TREE_FINISHED, null);
		}

		return decodeChildren(element, decodedNode);
	}

	private DecodeData decodeSingleElement(final Element element, final Node parentNode) {

		if (!TEMPLATE_ID.equals(element.getName())) {
			return new DecodeData(DecodeResult.TREE_CONTINUE, null);
		}

		TemplateId templateId = getTemplateId(element);
		QrdaDecoder decoder = getDecoder(templateId);

		if (null == decoder) {
			return new DecodeData(DecodeResult.TREE_CONTINUE, null);
		}

		Node childNode = new Node(templateId, parentNode);
		childNode.setDefaultNsUri(defaultNs.getURI());
		decoder.setNamespace(element.getNamespace());

		Element parentElement = element.getParentElement();

		DecodeResult decodeResult = decoder.decode(parentElement, childNode);

		if (decodeResult == DecodeResult.TREE_ESCAPED) {
			return new DecodeData(DecodeResult.TREE_ESCAPED, null);
		}

		childNode.setPath(XPathHelper.getAbsolutePath(parentElement));
		if (null != parentNode) {
			parentNode.addChildNode(childNode);
		}

		return new DecodeData(decodeResult, childNode);
	}

	private DecodeData decodeChildren(final Element element, final Node parentNode) {

		List<Element> childElements = element.getChildren();

		DecodeData decodeData = new DecodeData(DecodeResult.TREE_CONTINUE, parentNode);

		Node currentParentNode = parentNode;

		for (Element childElement : childElements) {
			DecodeData childDecodeData = decodeTree(childElement, currentParentNode);

			DecodeResult childDecodeResult = childDecodeData.getDecodeResult();
			Node childDecodedNode = childDecodeData.getNode();

			if (DecodeResult.TREE_FINISHED == childDecodeResult) {
				decodeData = new DecodeData(DecodeResult.TREE_CONTINUE, currentParentNode);
				break;
			}

			currentParentNode = childDecodedNode == null ? currentParentNode : childDecodedNode;
		}

		return decodeData;
	}

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

	private boolean containsClinicalDocumentElement(Element rootElement) {
		return "ClinicalDocument".equals(rootElement.getName());
	}

	private boolean containsClinicalDocumentTemplateId(Element rootElement) {
		boolean containsTemplateId = false;

		List<Element> clinicalDocumentChildren = rootElement.getChildren(TEMPLATE_ID,
																			rootElement.getNamespace());

		for (Element currentChild : clinicalDocumentChildren) {
			final String root = currentChild.getAttributeValue(ROOT_STRING);
			final String extension = currentChild.getAttributeValue(EXTENSION_STRING);

			if (TemplateId.getTemplateId(root, extension, context) == TemplateId.CLINICAL_DOCUMENT) {
				containsTemplateId = true;
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
