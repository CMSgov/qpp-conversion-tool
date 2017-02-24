package gov.cms.qpp.conversion.decode;

import java.io.File;

import org.jdom2.Element;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.xml.XmlException;
import gov.cms.qpp.conversion.xml.XmlUtils;

/**
 * Defines a contract for all input decoders
 *
 */
public class XmlFileDecoder implements InputDecoder {

	
	private File file;
	private XmlInputDecoder subDecoder;
	

	public XmlFileDecoder(File file, XmlInputDecoder subDecoder) {
		this.file = file;
		this.subDecoder = subDecoder;
	}
	
	@Override
	public Node parse() throws DecodeException {
		Element dom;
		try {
			dom = XmlUtils.fileToDom(file);
			subDecoder.setDom(dom);
		} catch (XmlException e) {
			throw new DecodeException("Failed to load file " + file.getAbsolutePath(), e);
		}
		return subDecoder.parse();
	}
	
}
