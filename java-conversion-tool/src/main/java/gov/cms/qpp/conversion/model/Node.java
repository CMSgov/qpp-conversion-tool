package gov.cms.qpp.conversion.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a node of data that should be converted. Consists of a key/value
 * Map that holds the data gleaned from an input file.
 * 
 * Nodes can contain other nodes as children to create a hierarchy.
 * 
 */
public class Node implements Serializable {

	private static final long serialVersionUID = 4602134063479322076L;

	private NodeId internalId;
	private Map<String, Serializable> data;

	private List<Node> childNodes;

	public Node() {
		this.data = new HashMap<>();
		this.setChildNodes(new ArrayList<>());
	}

	public Map<String, Serializable> getData() {
		return this.data;
	}

	public void setData(Map<String, Serializable> inputData) {
		this.data = inputData;
	}

	public void add(String key, Serializable value) {
		this.data.put(key, value);
	}

	public Serializable get(String key) {
		return this.data.get(key);
	}

	public NodeId getInternalId() {
		return internalId;
	}

	public void setInternalId(NodeId internalId) {
		this.internalId = internalId;
	}

	public List<Node> getChildNodes() {
		return childNodes;
	}

	public void setChildNodes(List<Node> childNodes) {
		this.childNodes = childNodes;
	}

	public void addChildNode(Node childNode) {
		this.childNodes.add(childNode);
	}

	@Override
	public String toString() {
		return "Node: internalId: " + internalId + ", data: " + data + ", childNodes: " + childNodes;
	}

}
