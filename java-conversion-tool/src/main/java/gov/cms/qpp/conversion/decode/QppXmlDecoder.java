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
 * @author David Uselmann
 */
public class QppXmlDecoder extends XmlInputDecoder {
	private static final Logger LOG = LoggerFactory.getLogger(QppXmlDecoder.class);

	private static Registry<String, QppXmlDecoder> decoders = new Registry<>(XmlDecoder.class);

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

		for (Element childeEl : childElements) {

			if ("templateId".equals(childeEl.getName())) {
				String templateId = childeEl.getAttributeValue("root");
				LOG.debug("templateIdFound:{}", templateId);

				QppXmlDecoder childDecoder = decoders.get(templateId);

				if (null == childDecoder) {
					continue;
				}
				LOG.debug("Using decoder for {} as {}", templateId, childDecoder.getClass());
				
				Node childNode = new Node(parentNode, templateId);
				
				setNamespace(childeEl, childDecoder);
				
				// the child decoder might require the entire its siblings
				DecodeResult result = childDecoder.internalDecode(element, childNode);
				
				parentNode.addChildNode(childNode); // TODO ensure we need to always add
				currentNode = childNode; // TODO this works for AciSectionDecoder
				
				if (result == null) {
					// TODO this looks like a continue ????
					// the only time we get here is NullReturnDecoderTest
						Node placeholderNode = new Node(parentNode, "placeholder");
					return decode(childeEl, placeholderNode);
				}
				switch (result) {
					case TREE_FINISHED:
						// this child is done
						return DecodeResult.TREE_FINISHED;
					case TREE_CONTINUE:
						decode(childeEl, childNode);
						break;
					case ERROR:
						// TODO Validation Error, include element data ????
						addValidation(templateId, "Failed to decode.");
						LOG.error("Failed to decode temlateId {} ", templateId);
						break;
					default:
						LOG.error("We need to define a default case. Could be TreeContiue?");
				}
			} else {
				// TODO might need a child node -- not sure
				decode(childeEl, currentNode);
			}
		}

		return DecodeResult.TREE_CONTINUE;
	}

	/**
	 * Starting at the top of the XML or XML fragment.
	 */
	@Override
	protected Node decodeRoot(Element xmlDoc) {
		Node rootNode = new Node();
		Element rootElement = xmlDoc.getDocument().getRootElement();
		
		QppXmlDecoder rootDecoder = null;
		for (Element e : rootElement.getChildren("templateId", rootElement.getNamespace())) {
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

		final List<Element> clinicalDocumentChildren = rootElement.getChildren("templateId",
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
