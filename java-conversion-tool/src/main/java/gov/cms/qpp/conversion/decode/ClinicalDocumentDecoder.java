package gov.cms.qpp.conversion.decode;

import org.jdom2.Element;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.XmlRootDecoder;

@XmlRootDecoder(rootElement = "ClinicalDocument")
public class ClinicalDocumentDecoder extends QppXmlDecoder {

	@Override
	protected Node internalDecode(Element element, Node thisnode) {
		for (Element el : element.getChildren()) {
			if ("templateId".equals(el.getName())) {
				String templateId = el.getAttributeValue("root");
				if ("2.16.840.1.113883.10.20.27.1.2".equals(templateId)) {
					thisnode.setId(templateId);
				}
			}
			
			if ("component".equals(el.getName())) {
				for (Element elc1 : el.getChildren()) {
					if ("structuredBody".equals(elc1.getName())) {
						for (Element elc2 : elc1.getChildren()) {
							if ("component".equals(elc2.getName())) {
								this.decode(elc2, thisnode);
							}
						}
					}
				}
			}
		}
		
		return thisnode;
	}

}
