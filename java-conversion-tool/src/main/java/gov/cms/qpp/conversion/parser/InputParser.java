package gov.cms.qpp.conversion.parser;

import java.io.File;

import gov.cms.qpp.conversion.model.Node;

/**
 * Defines a contract for all input parsers
 *
 */
public interface InputParser {
	Node parse(File inputFile);
}
