package gov.cms.qpp.conversion.model;

import gov.cms.qpp.conversion.decode.DecodeResult;

/**
 * A "tuple" that contains the {@link DecodeResult} and decoded {@link Node}.
 */
public class DecodeData {
	private DecodeResult decodeResult;
	private Node node;

	/**
	 * Constructor!
	 *
	 * @param decodeResult The {@link DecodeResult} to hold.
	 * @param node The {@link Node} to hold.
	 */
	public DecodeData(final DecodeResult decodeResult, final Node node) {
		this.decodeResult = decodeResult;
		this.node = node;
	}

	/**
	 * Gets the held {@link DecodeResult}.
	 *
	 * @return The decoded result.
	 */
	public DecodeResult getDecodeResult() {
		return decodeResult;
	}

	/**
	 * Gets the held {@link Node}.
	 *
	 * @return The node.
	 */
	public Node getNode() {
		return node;
	}
}
