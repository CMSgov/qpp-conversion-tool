package gov.cms.qpp.conversion.decode;

import org.jdom2.Element;

import gov.cms.qpp.conversion.model.Node;

/**
 * Defines a contract for all input decoders
 */
public interface InputDecoderEngine {
	Node decode(Element xmlDoc);
}
