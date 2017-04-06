package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Registry;
import gov.cms.qpp.conversion.model.Validations;
import gov.cms.qpp.conversion.model.XmlDecoder;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Top level Decoder for parsing into QPP format. Contains a map of child
 * Decoders that can Decode an element.
 */
public class QppXmlDecoder extends XmlInputDecoder {
	private static final Logger LOG = LoggerFactory.getLogger(QppXmlDecoder.class);

	private static Registry<String, QppXmlDecoder> decoders = new Registry<>(XmlDecoder.class);
	private static final String TEMPLATE_ID = "templateId";

	/**
	 * Iterates over the element to find all child elements. Finds any elements
	 * that match a templateId in the Decoder registry. If there are any
	 * matches, calls internalDecode with that Element on the Decoder class.
	 * Aggregates Nodes that are returned.
	 * 
	 */
	@Override
	public DecodeResult decode(Element element, Node parentNode) {

		Node currentNode = parentNode;

		if (null == element) {
			return DecodeResult.ERROR;
		}

		setNamespace(element, this);

		List<Element> childElements = element.getChildren();

		DecodeResult decodeResult = decodeChildren(element, parentNode, currentNode, childElements);
		if (decodeResult != null) {
			return decodeResult;
		}

		return DecodeResult.TREE_CONTINUE;
	}

	private DecodeResult decodeChildren(final Element element, final Node parentNode, Node currentNode,
	                                    final List<Element> childElements) {

		for (Element childEl : childElements) {

			if (TEMPLATE_ID.equals(childEl.getName())) {
				String templateId = childEl.getAttributeValue("root");
				LOG.debug("templateIdFound:{}", templateId);

				QppXmlDecoder childDecoder = decoders.get(templateId);

				if (null == childDecoder) {
					continue;
				}
				LOG.debug("Using decoder for {} as {}", templateId, childDecoder.getClass());

				Node childNode = new Node(parentNode, templateId);

				setNamespace(childEl, childDecoder);

				// the child decoder might require the entire its siblings
				DecodeResult result = childDecoder.internalDecode(element, childNode);

				parentNode.addChildNode(childNode); // TODO ensure we need to always add
				currentNode = childNode; // TODO this works for AciSectionDecoder

				DecodeResult placeholderNode = testChildDecodeResult(parentNode, childEl, templateId, childNode, result);
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

	private DecodeResult testChildDecodeResult(final Node parentNode, final Element childEl, final String templateId,
	                                           final Node childNode, final DecodeResult result) {
		if (result == null) {
			// TODO this looks like a continue ????
			// the only time we get here is NullReturnDecoderTest
				Node placeholderNode = new Node(parentNode, "placeholder");
			return decode(childEl, placeholderNode);
		}

		if (result == DecodeResult.TREE_FINISHED) {
			// this child is done
			return DecodeResult.TREE_FINISHED;
		} else if (result == DecodeResult.TREE_CONTINUE) {
			decode(childEl, childNode);
		} else if (result == DecodeResult.ERROR) {
			addValidation(templateId, "Failed to decode.");
			LOG.error("Failed to decode templateId {} ", templateId);
		} else {
			LOG.error("We need to define a default case. Could be TreeContinue?");
		}
		return null;
	}

	/**
	 * Starting at the top of the XML or XML fragment.
	 */
	@Override
	protected Node decodeRoot(Element xmlDoc) {
		Node rootNode = new Node();
		Element rootElement = xmlDoc.getDocument().getRootElement();
		
		QppXmlDecoder rootDecoder = null;
		for (Element e : rootElement.getChildren(TEMPLATE_ID, rootElement.getNamespace())) {
			String templateId = e.getAttributeValue("root");
			rootDecoder = decoders.get(templateId);
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
	
	@Override
	protected boolean accepts(Element xmlDoc) {

		final Element rootElement = xmlDoc.getDocument().getRootElement();

		boolean isValidQrdaFile = containsClinicalDocumentElement(rootElement) &&
		                          containsClinicalDocumentTemplateId(rootElement);

		if (!isValidQrdaFile) {
			LOG.error("The file is not a QRDA-III XML document");
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
			final String templateId = currentChild.getAttributeValue("root");

			if (ClinicalDocumentDecoder.ROOT_TEMPLATEID.equals(templateId)) {

				containsTemplateId = true;
				break;
			}
		}
		return containsTemplateId;
	}

	@Override
	protected DecodeResult internalDecode(Element element, Node thisnode) {
		// this is the top level, so just return null
		return DecodeResult.NO_ACTION;
	}

	@Override
	public Iterable<String> validations() {
		return Validations.values();
	}

	@Override
	public List<String> getValidationsById(String templateId) {
		return Validations.getValidationsById(templateId);
	}

	@Override
	public void addValidation(String templateId, String validation) {
		Validations.addValidation(templateId, validation);
	}


}
