package gov.cms.qpp.conversion.decode;


import org.jdom2.Element;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.XmlDecoder;

/**
 * Decoder to read XML Data for an ACI Section.
 *
 */
@XmlDecoder(templateId="2.16.840.1.113883.10.20.27.2.5")
public class AciSectionDecoder extends QppXmlDecoder {

	/**
	 * Decodes an ACI Section into the QPP format
	 *
	 * @param element XML element that represents the ACI Section
	 * @param thisnode represents the aci section
	 * @return
	 */
	@Override
	protected DecodeResult internalDecode(Element element, Node thisnode) {
		thisnode.putValue("category", "aci");
		return DecodeResult.TreeContinue;
	}
		
}
