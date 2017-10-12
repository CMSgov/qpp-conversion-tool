package gov.cms.qpp.conversion.api.model;


import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.model.Node;

public class TransformResult {
	private Node decoded;
	private JsonWrapper encoded;

	public TransformResult(Node node, JsonWrapper wrapper) {
		decoded = node;
		encoded = wrapper;
	}

	public Node getDecoded() {
		return decoded;
	}

	public JsonWrapper getEncoded() {
		return encoded;
	}
}
