package gov.cms.qpp.conversion.decode;

import org.jdom2.Element;
import org.jdom2.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.model.Node;

/**
 * Abstraction to parse XML files within the decoder structure.
 */
public abstract class XmlDecoderEngine implements InputDecoderEngine {
	private static final Logger DEV_LOG = LoggerFactory.getLogger(XmlDecoderEngine.class);
	Namespace defaultNs;

	/**
	 * decodeXml Determines what formats of xml we accept and decode to
	 *
	 * @param xmlDoc XML document whose format is to be determined
	 * @return Root intermediate format node
	 */
	public static Node decodeXml(Context context, Element xmlDoc) {
		XmlDecoderEngine decoder = new QrdaDecoderEngine(context);
		if (decoder.accepts(xmlDoc)) {
			return decoder.decode(xmlDoc);
		}

		DEV_LOG.error("The XML file is an unknown document");

		return null;
	}

	/**
	 * Determines if the Decoder can handle the input
	 *
	 * @param xmlDoc XML document
	 * @return Whether or not the decoder can handle the element
	 */
	protected abstract boolean accepts(Element xmlDoc);
}
