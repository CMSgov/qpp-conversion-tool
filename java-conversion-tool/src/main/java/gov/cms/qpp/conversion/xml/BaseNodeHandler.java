package gov.cms.qpp.conversion.xml;

import java.util.Map;
import java.util.TreeMap;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public abstract class BaseNodeHandler implements NodeHandler {
    //private static final Logger LOG = LoggerFactory.getLogger(BaseNodeHandler.class);
	private final String handledElementName;

	public BaseNodeHandler(final String handledElementName) {
		super();
		this.handledElementName = handledElementName;
	}
	

	/* (non-Javadoc)
	 * @see gov.cms.qpp.conversion.xml.NodeHandler#handle(javax.xml.stream.XMLStreamReader)
	 */
	@Override
	public void handle(XMLStreamReader reader) throws XMLStreamException {
		assign(reader); 
	}
	
	/* (non-Javadoc)
	 * @see gov.cms.qpp.conversion.xml.NodeHandler#getHandledElementName()
	 */
	@Override
	public String getHandledElementName() {
		return handledElementName;
	}
	
	protected void assign(final XMLStreamReader reader) throws XMLStreamException {
		Map<String, NodeHandler> param = new TreeMap<>();
		param.put(getHandledElementName(), this);
		assign(reader, param);
	}
	
	protected void assign(final XMLStreamReader reader, Map<String, NodeHandler> nodeHandlers) throws XMLStreamException {
		assign(reader, nodeHandlers, 0);
	}
	
	protected void assign(final XMLStreamReader reader, Map<String, NodeHandler> nodeHandlers, final int depth) throws XMLStreamException {
		int elementDepth = 0;
		while (reader.hasNext()) {
			int event = reader.next();
			switch (event) {
			case XMLStreamConstants.START_DOCUMENT:
				break;
			case XMLStreamConstants.ATTRIBUTE:
				break;
			case XMLStreamConstants.START_ELEMENT:
				if (depth == elementDepth) {
					NodeHandler handler = nodeHandlers.get(reader.getLocalName());
					if (null != handler) {
						handler.doHandle(reader);
					}
				}
				elementDepth++;
				break;
			case XMLStreamConstants.END_ELEMENT:
				elementDepth--;
				break;
			default:
				break;
			}
		}
	}


	public abstract void doHandle(XMLStreamReader reader) throws XMLStreamException;
}
