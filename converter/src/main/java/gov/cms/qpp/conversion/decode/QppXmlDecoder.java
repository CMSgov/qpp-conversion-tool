package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.correlation.PathCorrelator;
import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Registry;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.validation.SupplementalData.SupplementalType;
import gov.cms.qpp.conversion.segmentation.QrdaScope;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import static gov.cms.qpp.conversion.decode.SupplementalDataEthnicityDecoder.SUPPLEMENTAL_DATA_CODE;
import static gov.cms.qpp.conversion.decode.SupplementalDataEthnicityDecoder.SUPPLEMENTAL_DATA_KEY;
import static gov.cms.qpp.conversion.decode.SupplementalDataPayerDecoder.SUPPLEMENTAL_DATA_PAYER_CODE;

/**
 * Top level Decoder for parsing into QPP format.
 */
public class QppXmlDecoder extends XmlInputDecoder {

	private static final Logger DEV_LOG = LoggerFactory.getLogger(QppXmlDecoder.class);
	private static final String TEMPLATE_ID = "templateId";
	private static final String NOT_VALID_QRDA_III_FORMAT = "The file is not a QRDA-III XML document";
	private static final String ROOT_STRING = "root";
	private static final String EXTENSION_STRING = "extension";

	protected final Context context;
	private final Set<TemplateId> scope;
	private final Registry<QppXmlDecoder> decoders;

	/**
	 * Initialize a qpp xml decoder
	 */
	public QppXmlDecoder(Context context) {
		Objects.requireNonNull(context, "converter");

		this.context = context;
		this.scope = context.hasScope() ? QrdaScope.getTemplates(context.getScope()) : null;
		this.decoders = context.getRegistry(Decoder.class);
	}

	/**
	 * Decode iterates over the elements to find all child elements
	 * to decode any matching elements found in the Decoder Registry
	 *
	 * @param element Current highest level XML element
	 * @param parentNode Parent of the current nodes to be parsed
	 * @return Status of the child element
	 */
	@Override
	public DecodeResult decode(Element element, Node parentNode) {
		if (element == null) {
			return DecodeResult.ERROR;
		}

		setNamespace(element, this);

		return decodeChildren(element, parentNode);
	}

	/**
	 * Decodes Parent element children using recursion.
	 *
	 * @param element parent element to be decoded
	 * @param parentNode parent node to decode into
	 * @return status of current decode
	 */
	private DecodeResult decodeChildren(final Element element, final Node parentNode) {
		Node currentNode = parentNode;

		List<Element> childElements = element.getChildren();

		for (Element childElement : childElements) {

			if (TEMPLATE_ID.equals(childElement.getName())) {
				String root = childElement.getAttributeValue(ROOT_STRING);
				String extension = childElement.getAttributeValue(EXTENSION_STRING);
				TemplateId templateId = TemplateId.getTemplateId(root, extension, context);
				DEV_LOG.debug("templateIdFound:{}", templateId);

				QppXmlDecoder childDecoder = getDecoder(templateId);

				if (null == childDecoder) {
					continue;
				}
				DEV_LOG.debug("Using decoder for {} as {}", templateId, childDecoder.getClass());
				Node childNode = new Node(templateId, parentNode);

				childNode.setDefaultNsUri(defaultNs.getURI());
				
				setNamespace(childElement, childDecoder);
				
				// the child decoder might require the entire its siblings
				DecodeResult result = childDecoder.internalDecode(element, childNode);
				if (result == DecodeResult.TREE_ESCAPED) {
					return DecodeResult.TREE_FINISHED;
				}

				childNode.setPath(XPathHelper.getAbsolutePath(element));

				parentNode.addChildNode(childNode);
				currentNode = childNode;

				DecodeResult placeholderNode = testChildDecodeResult(result, childElement, childNode);
				if (placeholderNode != null) {
					return placeholderNode;
				}
			} else {
				decode(childElement, currentNode);
			}
		}

		return null;
	}

	/**
	 * Retrieve a permitted {@link Decoder}. {@link #scope} is used to determine which DECODERS are allowable.
	 *
	 * @param templateId string representation of a would be decoder's template id
	 * @return decoder that corresponds to the given template id
	 */
	private QppXmlDecoder getDecoder(TemplateId templateId) {
		QppXmlDecoder qppDecoder = decoders.get(templateId);
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
	 * Checks children internal decode result for DecodeResult action
	 *
	 * @param result object that holds the value to be analyzed
	 * @param childElement next child to decode if continued
	 * @param childNode object to decode into
	 * @return status of current decode
	 */
	private DecodeResult testChildDecodeResult(DecodeResult result, Element childElement,
												Node childNode) {
		if (result == null) {
			Node placeholderNode = new Node(TemplateId.PLACEHOLDER, childNode.getParent());
			return decode(childElement, placeholderNode);
		}

		switch (result) {
			case TREE_FINISHED:
				return DecodeResult.TREE_FINISHED;

			case TREE_CONTINUE:
				decode(childElement, childNode);
				break;

			case ERROR:
				DEV_LOG.error("Failed to decode templateId {} ", childNode.getType());
				break;

			default:
				DEV_LOG.error("We need to define a default case. Could be TreeContinue?");
		}

		return null;
	}

	/**
	 * Decodes the top of the XML document
	 *
	 * @param xmlDoc XML Document to be parsed
	 * @return Root node
	 */
	@Override
	protected Node decodeRoot(Element xmlDoc) {
		Node rootNode = new Node();
		Element rootElement = xmlDoc.getDocument().getRootElement();
		
		QppXmlDecoder rootDecoder = null;
		for (Element element : rootElement.getChildren(TEMPLATE_ID, rootElement.getNamespace())) {
			String root = element.getAttributeValue(ROOT_STRING);
			String extension = element.getAttributeValue(EXTENSION_STRING);
			TemplateId templateId = TemplateId.getTemplateId(root, extension, context);
			rootDecoder = getDecoder(templateId);
			if (rootDecoder != null) {
				rootNode.setType(templateId);
				break;
			}
		}
		
		if (rootDecoder != null) {
			rootDecoder.setNamespace(rootElement, rootDecoder);
			rootNode.setDefaultNsUri(rootDecoder.defaultNs.getURI());
			rootNode.setPath(XPathHelper.getAbsolutePath(rootElement));
			rootDecoder.internalDecode(rootElement, rootNode);
		} else {
			rootNode.setType(TemplateId.PLACEHOLDER);
			rootNode.setPath(XPathHelper.getAbsolutePath(rootElement));
			this.decode(rootElement, rootNode);
		}
		
		return rootNode;
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
	 * Top level decode
	 *
	 * @param element Top element in the XML document
	 * @param thisNode Top node created in the XML document
	 * @return No action is returned for the top level internalDecode.
	 */
	@Override
	protected DecodeResult internalDecode(Element element, Node thisNode) {
		return DecodeResult.NO_ACTION;
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
	 * Sets a given Supplemental Data by type in the current Node
	 *
	 * @param element XML element that represents SupplementalDataCode
	 * @param thisNode Current Node to decode into
	 * @param type Current Supplemental Type to put onto this node
	 */
	public void setSupplementalDataOnNode(Element element, Node thisNode, SupplementalType type) {
		String supplementalXpathCode = type.equals(SupplementalType.PAYER) ?
				SUPPLEMENTAL_DATA_PAYER_CODE :  SUPPLEMENTAL_DATA_CODE;
		String expressionStr = getXpath(supplementalXpathCode);
		Consumer<? super Attribute> consumer = attr -> {
			String code = attr.getValue();
			thisNode.putValue(SUPPLEMENTAL_DATA_KEY, code, false);
		};
		setOnNode(element, expressionStr, consumer, Filters.attribute(), false);
	}
}
