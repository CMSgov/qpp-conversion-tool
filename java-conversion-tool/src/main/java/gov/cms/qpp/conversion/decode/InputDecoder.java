package gov.cms.qpp.conversion.decode;

import org.jdom2.Element;

import gov.cms.qpp.conversion.model.Node;

/**
 * Defines a contract for all input decoders
 * @author David Uselmann
 *
 */
public interface InputDecoder {
	Node decode(Element xmlDoc) throws DecodeException;
}
