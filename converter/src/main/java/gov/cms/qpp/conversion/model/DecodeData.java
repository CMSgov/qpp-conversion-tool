package gov.cms.qpp.conversion.model;

import gov.cms.qpp.conversion.decode.DecodeResult;

public class DecodeData {
	private DecodeResult decodeResult;
	private Node node;

	public DecodeData(final DecodeResult decodeResult, final Node node) {
		this.decodeResult = decodeResult;
		this.node = node;
	}

	public DecodeResult getDecodeResult() {
		return decodeResult;
	}

	public void setDecodeResult(final DecodeResult decodeResult) {
		this.decodeResult = decodeResult;
	}

	public Node getNode() {
		return node;
	}

	public void setNode(final Node node) {
		this.node = node;
	}
}
