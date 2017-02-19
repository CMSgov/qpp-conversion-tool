package gov.cms.qpp.conversion.xml;

import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClinicalDocumentHandler extends BaseNodeHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ClinicalDocumentHandler.class);
	
	private Map<String, NodeHandler> childHandlers;

	public ClinicalDocumentHandler() {
		super("ClinicalDocument");
		childHandlers =  new HashMap<>();
		childHandlers.put("participant", new XsltHandler("participant"));
		childHandlers.put("authorization", new XsltHandler("authorization"));
		childHandlers.put("custodian", new XsltHandler("custodian"));
	}

	@Override
	public void doHandle(XMLStreamReader reader) throws XMLStreamException {
		LOG.info(reader.getLocalName());
		this.assign(reader, childHandlers);
//		int depth = 0;
//		while (reader.hasNext()) {
//			// Element level
//			int event = reader.next();
//			switch (event) {
//			case XMLStreamConstants.START_ELEMENT:
//				if(0 == depth){
//					LOG.info(reader.getLocalName() + " depth: " + depth);
//				}
//				
//				depth++;
//				break;
//			case XMLStreamConstants.END_ELEMENT:
//				depth--;
//				break;
//			default:
//				break;
//			}
//		}

	}

}
