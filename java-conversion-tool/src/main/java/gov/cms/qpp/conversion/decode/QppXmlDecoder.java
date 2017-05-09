package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.ConversionEntry;
import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Registry;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.segmentation.QrdaScope;
import org.jdom2.Element;
import org.jdom2.xpath.XPathHelper;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.Collection;
import java.util.List;

/**
 * Top level Decoder for parsing into QPP format.
 */
public class QppXmlDecoder extends XmlInputDecoder {

	private static final Registry<String, QppXmlDecoder> DECODERS = new Registry<>(Decoder.class);
	private static final String TEMPLATE_ID = "templateId";
	private static final String NOT_VALID_QRDA_III_FORMAT = "The file is not a QRDA-III XML document";
	private static final String ROOT_STRING = "root";
	private static final String EXTENSION_STRING = "extension";
	private Collection<TemplateId> scope;

	/**
	 * Initialize a qpp xml decoder
	 */
	public QppXmlDecoder() {
		Collection<TemplateId> theScope = QrdaScope.getTemplates(ConversionEntry.getScope());
		if (!theScope.isEmpty()) {
			this.scope = theScope;
		}
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

		if (null == element) {
			return DecodeResult.ERROR;
		}

		setNamespace(element, this);

		DecodeResult decodeResult = decodeChildren(element, parentNode);

		return (decodeResult != null) ? decodeResult : DecodeResult.TREE_CONTINUE;
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

		for (Element childEl : childElements) {

			if (TEMPLATE_ID.equals(childEl.getName())) {
				String root = childEl.getAttributeValue(ROOT_STRING);
				String extension = childEl.getAttributeValue(EXTENSION_STRING);
				String templateId = TemplateId.generateTemplateIdString(root, extension);
				Converter.CLIENT_LOG.debug("templateIdFound:{}", templateId);

				QppXmlDecoder childDecoder = getDecoder(templateId);

				if (null == childDecoder) {
					continue;
				}
				Converter.CLIENT_LOG.debug("Using decoder for {} as {}", templateId, childDecoder.getClass());
				Node childNode = new Node(parentNode, templateId);
				childNode.setPath(XPathHelper.getAbsolutePath(element));
				
				setNamespace(childEl, childDecoder);
				
				// the child decoder might require the entire its siblings
				DecodeResult result = childDecoder.internalDecode(element, childNode);
				if (result == DecodeResult.TREE_ESCAPED) {
					return DecodeResult.TREE_FINISHED;
				}

				parentNode.addChildNode(childNode); // TODO ensure we need to always add
				currentNode = childNode; // TODO this works for AciSectionDecoder

				DecodeResult placeholderNode = testChildDecodeResult(result, childEl, childNode);
				if (placeholderNode != null) {
					return placeholderNode;
				}
			} else {
				// TODO might need a child node -- not sure
				decode(childEl, currentNode);
			}
		}

		return null;
	}

	/**
	 * Retrieve a permitted {@link Decoder}. {@link #scope} is used to determine which decoders are allowable.
	 *
	 * @param templateId string representation of a would be decoder's template id
	 * @return decoder that corresponds to the given template id
	 */
	private QppXmlDecoder getDecoder(String templateId) {

		QppXmlDecoder qppDecoder = DECODERS.get(templateId);
		if (qppDecoder != null) {
			Decoder decoder = AnnotationUtils.findAnnotation(qppDecoder.getClass(), Decoder.class);
			return (scope != null && !scope.contains(decoder.value())) ? null : qppDecoder;
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
	private DecodeResult testChildDecodeResult(final DecodeResult result, final Element childElement,
												final Node childNode) {
		if (result == null) {
			Node placeholderNode = new Node(childNode.getParent(), "placeholder");
			return decode(childElement, placeholderNode);
		}

		if (result == DecodeResult.TREE_FINISHED) {
			return DecodeResult.TREE_FINISHED;
		} else if (result == DecodeResult.TREE_CONTINUE) {
			decode(childElement, childNode);
		} else if (result == DecodeResult.ERROR) {
			Converter.CLIENT_LOG.error("Failed to decode templateId {} ", childNode.getId());
		} else {
			Converter.CLIENT_LOG.error("We need to define a default case. Could be TreeContinue?");
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
		for (Element e : rootElement.getChildren(TEMPLATE_ID, rootElement.getNamespace())) {
			String root = e.getAttributeValue(ROOT_STRING);
			String extension = e.getAttributeValue(EXTENSION_STRING);
			String templateId = TemplateId.generateTemplateIdString(root, extension);
			rootDecoder = getDecoder(templateId);
			if (null != rootDecoder) {
				rootNode.setId(templateId);
				break;
			}
		}
		
		if (null != rootDecoder) {
			rootNode.setPath(XPathHelper.getAbsolutePath(rootElement));
			rootDecoder.setNamespace(rootElement, rootDecoder);
			rootDecoder.internalDecode(rootElement, rootNode);
		} else {
			rootNode.setId(TemplateId.PLACEHOLDER.getTemplateId());
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
			Converter.CLIENT_LOG.error(NOT_VALID_QRDA_III_FORMAT);
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

			if (TemplateId.getTypeById(root, extension) == TemplateId.CLINICAL_DOCUMENT) {
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
}
