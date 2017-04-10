package gov.cms.qpp.conversion.decode;

import gov.cms.qpp.conversion.model.TemplateId;
import org.jdom2.Element;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.XmlDecoder;

/**
 * Decoder to parse QRDA Category III Reporting Parameters Section.
 * @author David Puglielli
 *
 */
@XmlDecoder(templateId = TemplateId.REPORTING_PARAMETERS_SECTION)
public class ReportingParametersSectionDecoder extends QppXmlDecoder {

	@Override
	protected DecodeResult internalDecode(Element element, Node thisnode) {
		thisnode.putValue("source", "provider");
		return DecodeResult.TREE_CONTINUE;
	}
}
