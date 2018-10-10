package gov.cms.qpp.conversion.decode;

/**
 * DecodeResult informs the main decoding process of how to proceed when control is returned from a template decoder.
 */
public enum DecodeResult {
	/**
	 * Stop decoding children and sibling elements, and throw away the {@link gov.cms.qpp.conversion.model.Node} that created
	 * this result.
	 */
	TREE_ESCAPED,

	/**
	 * Stop decoding children and sibling elements, but keep the decoded {@link gov.cms.qpp.conversion.model.Node} that created
	 * this result.
	 */
	TREE_FINISHED,

	/**
	 * Continue decoding children elements and sibling elements.
	 */
	TREE_CONTINUE
}
