package gov.cms.qpp.conversion.decode;

import java.util.List;

import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.Registry;
import gov.cms.qpp.conversion.model.Validations;
import gov.cms.qpp.conversion.model.XmlDecoder;

/**
 * Top level Decoder for parsing into QPP format. Contains a map of child Decoders
 * that can Decode an element.
 */
public class QppXmlDecoder extends XmlInputDecoder {
    final Logger LOG = LoggerFactory.getLogger(getClass());
 	
	protected static Registry<String, QppXmlDecoder> decoders = new Registry<>(XmlDecoder.class);

	public QppXmlDecoder() {}

	/**
	 * Iterates over the element to find all child elements. Finds any elements
	 * that match a templateId in the Decoder registry. If there are any matches,
	 * calls internalDecode with that Element on the Decoder class. Aggregates Nodes
	 * that are returned.
	 * 
	 */
	@Override
	public DecodeResult decode(Element element, Node parentNode) {

		Node currentNode = parentNode;
		
		if (null == element) {
			return DecodeResult.Error;
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
				
				Node childNode = new Node(templateId);
				
				setNamespace(childeEl, childDecoder);
				
				// the child decoder might require the entire its siblings
				DecodeResult result = childDecoder.internalDecode(element, childNode);
				
				parentNode.addChildNode(childNode); // TODO ensure we need to always add
				
				switch (result) {
					case TreeFinished:
						// this child is done
						return DecodeResult.TreeFinished;
					case TreeContinue:
						decode(childeEl, childNode);
						break;
					case Error:
						// TODO Validation Error, include element data ????
						addValidation(templateId, "Failed to decode.");
						LOG.error("Failed to decode temlateId {} ", templateId);
						break;
					default: //result == null
						// the only time we get here is NullReturnDecoderTest
						Node placeholderNode = new Node("placeholder");
						return decode(childeEl, placeholderNode);
				}
			} else {
				// TODO might need a child node -- not sure
				decode(childeEl, currentNode);
			}
		}

		return DecodeResult.TreeContinue;
	}

	@Override
	protected DecodeResult internalDecode(Element element, Node thisnode) {
		// this is the top level, so just return null
		return DecodeResult.NoAction;
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
