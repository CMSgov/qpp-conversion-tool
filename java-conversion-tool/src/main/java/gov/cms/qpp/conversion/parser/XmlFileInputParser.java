package gov.cms.qpp.conversion.parser;

import java.io.File;

import org.jdom2.Element;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.xml.XmlException;
import gov.cms.qpp.conversion.xml.XmlUtils;

/**
 * Defines a contract for all input parsers
 *
 */
public class XmlFileInputParser implements InputParser {

	
	private File file;
	private XmlInputParser subparser;
	

	public XmlFileInputParser(File file, XmlInputParser subparser) {
		this.file = file;
		this.subparser = subparser;
	}
	
	@Override
	public Node parse() throws DecodeException {
		Element dom;
		try {
			dom = XmlUtils.fileToDom(file);
			subparser.setDom(dom);
		} catch (XmlException e) {
			throw new DecodeException("Failed to load file " + file.getAbsolutePath(), e);
		}
		return subparser.parse();
	}
	
}
