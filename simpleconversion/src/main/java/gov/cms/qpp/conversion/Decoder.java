package gov.cms.qpp.conversion;


import org.xml.sax.Attributes;

public interface Decoder {
	public void handleStartElement(String uri, String localName,
							  String qName, Attributes attributes);
	public Object exportDecoded();
}
