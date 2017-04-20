package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.model.Decoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Registry;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.Validations;
import org.jdom2.Element;
import org.jdom2.xpath.XPathHelper;

import java.util.List;

/**
 * Top level Decoder for parsing into QPP format.
 */
public class QppXmlDecoder extends XmlInputDecoder {

	private static final Registry<String, QppXmlDecoder> DECODERS = new Registry<>(Decoder.class);
	private static final String TEMPLATE_ID = "templateId";

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
				String root = childEl.getAttributeValue("root");
				String extension = childEl.getAttributeValue("extension");
				String templateId = TemplateId.generateTemplateIdString(root, extension);
				Converter.CLIENT_LOG.debug("templateIdFound:{}", templateId);

				QppXmlDecoder childDecoder = DECODERS.get(templateId);

				if (null == childDecoder) {
					continue;
				}
				Converter.CLIENT_LOG.debug("Using decoder for {} as {}", templateId, childDecoder.getClass());
				Node childNode = new Node(parentNode, templateId);
				childNode.setPath(XPathHelper.getAbsolutePath(element));
				
				setNamespace(childEl, childDecoder);
				
				// the child decoder might require the entire its siblings
				DecodeResult result = childDecoder.internalDecode(element, childNode);

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
			addValidation(childNode.getId(), "Failed to decode.");
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
			String root = e.getAttributeValue("root");
			String extension = e.getAttributeValue("extension");
			String templateId = TemplateId.generateTemplateIdString(root, extension);
			rootDecoder = DECODERS.get(templateId);
			if (null != rootDecoder) {
				rootNode.setId(templateId);
				break;
			}
		}
		
		if (null != rootDecoder) {
			rootDecoder.setNamespace(rootElement, rootDecoder);
			rootDecoder.internalDecode(rootElement, rootNode);
		} else {
			rootNode.setId("placeholder");
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
			Converter.CLIENT_LOG.error("The file is not a QRDA-III XML document");
		}
		
		return isValidQrdaFile;
	}

	private boolean containsClinicalDocumentElement(final Element rootElement) {
		return "ClinicalDocument".equals(rootElement.getName());
	}

	private boolean containsClinicalDocumentTemplateId(final Element rootElement) {
		boolean containsTemplateId = false;

		final List<Element> clinicalDocumentChildren = rootElement.getChildren(TEMPLATE_ID,
		                                                                       rootElement.getNamespace());

		for (Element currentChild : clinicalDocumentChildren) {
			final String root = currentChild.getAttributeValue("root");
			final String extension = currentChild.getAttributeValue("extension");

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

	/**
	 * Retrieves all validations
	 *
	 * @return validations
	 */
	@Override
	public Iterable<String> validations() {
		return Validations.values();
	}

	/**
	 * Retrieves all validations for a specific template id
	 *
	 * @param templateId Identification of Element
	 * @return validations
	 */
	@Override
	public List<String> getValidationsById(String templateId) {
		return Validations.getValidationsById(templateId);
	}

	/**
	 * Adds a validation to the current list of validation errors
	 *
	 * @param templateId Identification of the validation error
	 * @param validation Validation error to be added
	 */
	@Override
	public void addValidation(String templateId, String validation) {
		Validations.addValidation(templateId, validation);
	}
}
