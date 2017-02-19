package gov.cms.qpp.conversion.xml;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public interface NodeHandler {

	void handle(XMLStreamReader reader) throws XMLStreamException;

	String getHandledElementName();
	
	public abstract void doHandle(XMLStreamReader reader) throws XMLStreamException;

}