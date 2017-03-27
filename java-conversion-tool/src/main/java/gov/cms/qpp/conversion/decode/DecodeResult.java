package gov.cms.qpp.conversion.decode;

/**
 * DecodeResult informs the main decoding process of how to proceed when control is returned from a template decoder.
 * @author David Uselmann
 *
 */
public enum DecodeResult {
	TreeFinished, TreeContinue, NoAction, Error;
}
