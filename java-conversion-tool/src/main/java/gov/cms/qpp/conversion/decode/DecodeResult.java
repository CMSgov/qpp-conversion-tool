package gov.cms.qpp.conversion.decode;

/**
 * DecodeResult informs the main decoding process of how to proceed when control is returned from a template decoder.
 */
public enum DecodeResult {
	TREE_FINISHED, TREE_CONTINUE, NO_ACTION, ERROR;
}
