package gov.cms.qpp.conversion.model;

import gov.cms.qpp.conversion.decode.DecodeResult;
import gov.cms.qpp.conversion.util.CloneHelper;

/**
 * A "tuple" that contains the {@link DecodeResult} and decoded {@link Node}.
 */
public class DecodeData {
	private final DecodeResult decodeResult;
	private final Node node;

	/**
	 * Constructor!
	 *
	 * @param decodeResult The {@link DecodeResult} to hold.
	 * @param node         The {@link Node} to hold.
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
	 * Gets a defensive copy of the held {@link Node}.
	 *
	 * @return A deep clone of the node.
	 */
	public Node getNode() {
		return CloneHelper.deepClone(node);
	}
}
