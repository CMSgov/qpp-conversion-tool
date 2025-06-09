package gov.cms.qpp.conversion.model;

import gov.cms.qpp.conversion.decode.DecodeResult;

import java.util.Objects;

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

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		DecodeData that = (DecodeData) o;
		return Objects.equals(decodeResult, that.decodeResult)
				&& Objects.equals(node, that.node);
	}

	@Override
	public int hashCode() {
		return Objects.hash(decodeResult, node);
	}

	@Override
	public String toString() {
		return "DecodeData{" +
				"decodeResult=" + decodeResult +
				", node=" + node +
				'}';
	}
}
