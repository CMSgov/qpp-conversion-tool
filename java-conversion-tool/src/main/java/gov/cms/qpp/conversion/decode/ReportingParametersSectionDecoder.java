package gov.cms.qpp.conversion.decode;

import org.jdom2.Element;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.XmlDecoder;

/**
 * Decoder to parse QRDA Category III Reporting Parameters Section.
 * @author David Puglielli
 *
 */
@XmlDecoder(templateId="2.16.840.1.113883.10.20.27.2.6")
public class ReportingParametersSectionDecoder extends QppXmlDecoder {
	@Override
	protected DecodeResult internalDecode(Element element, Node thisnode) {
		thisnode.putValue("source", "provider");
		return DecodeResult.TreeContinue;
	}
		
}
