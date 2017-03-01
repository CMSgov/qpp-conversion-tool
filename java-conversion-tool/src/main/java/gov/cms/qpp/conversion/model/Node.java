package gov.cms.qpp.conversion.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents a node of data that should be converted. Consists of a key/value
 * Map that holds the data gleaned from an input file.
 * 
 * Nodes can contain other nodes as children to create a hierarchy.
 * 
 */
public class Node implements Serializable {

	private static final long serialVersionUID = 4602134063479322076L;

	private String internalId;
	private Map<String, String> data = new HashMap<>();

	private List<Node> childNodes;

	private boolean validated;

	public Node() {
		this.data = new HashMap<>();
		this.setChildNodes(new ArrayList<>());
	}

	public String getValue(String name) {
		return data.get(name);
	}

	public void putValue(String name, String value) {
		data.put(name, value);
	}

	public void setId(String templateId) {
		this.internalId = templateId;
	}

	public String getId() {
		return internalId;
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

	public Set<String> getKeys() {
		return data.keySet();
	}

	public boolean isValidated() {
		return validated;
	}

	public void setValidated(boolean validated) {
		this.validated = validated;
	}

	public List<Node> findNode(String id) {
		List<Node> foundNodes = new ArrayList<>();

		if (id.equals(this.internalId)) {
			foundNodes.add(this);
		}

		for (Node childNode : childNodes) {
			foundNodes.addAll(childNode.findNode(id));
		}

		return foundNodes;
	}
}
