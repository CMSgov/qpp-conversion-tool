package gov.cms.qpp.conversion.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Represents a node of data that should be converted. Consists of a key/value
 * Map that holds the data gleaned from an input file.
 * Nodes can contain other nodes as children to create a hierarchy.
 */
public class Node {
	private TemplateId type;
	private Map<String, String> data = new HashMap<>();

	private List<Node> childNodes;
	private Node parent;
	private boolean validated;
	private String internalId;
	private String path;

	/**
	 * Default constructor initializes internal list of Nodes
	 */
	public Node() {
		this.childNodes = new ArrayList<>();
	}

	/**
	 * Constructor initialized with a template id string
	 *
	 * @param id String of the parsed template id.
	 */
	public Node(String id) {
		this();
		setId(id);
		this.type = TemplateId.getTypeById(id);
	}

	/**
	 * Constructor initialized with a Node
	 *
	 * @param parentNode Node
	 */
	public Node(Node parentNode) {
		this();
		this.parent = parentNode;
	}

	/**
	 * Constructor initialized with a Node and a template id String
	 *
	 * @param parentNode Node
	 * @param id         String representation of a template id
	 */
	public Node(Node parentNode, String id) {
		this(parentNode);
		setId(id);
	}

	/**
	 * getValue returns the string value of the xml fragment parsed into this Node
	 *
	 * @param name String key for the value
	 * @return String
	 */
	public String getValue(String name) {
		return data.get(name);
	}

	/**
	 * putValue stores the Value under the key: name
	 *
	 * @param name  String key to store value under
	 * @param value String that is stored with this xml parsed Node
	 */
	public void putValue(String name, String value) {
		data.put(name, value);
	}

	/**
	 * Check if a value has been assigned to the given key.
	 *
	 * @param name key
	 * @return corresponding value
	 */
	public boolean hasValue(String name) {
		return data.containsKey(name);
	}

	/**
	 * setId locates the appropriate TemplateId and sets the Node Type
	 *
	 * @param templateId String from the parsed xml fragment
	 */
	public void setId(String templateId) {
		this.type = TemplateId.getTypeById(templateId);
		this.internalId = templateId;
	}

	/**
	 * getId returns the internal template id string
	 *
	 * @return String
	 */
	public String getId() {
		return internalId;
	}

	/**
	 * getChildNodes returns the list of child Nodes for this Node
	 *
	 * @return List of child Nodes.
	 */
	public List<Node> getChildNodes() {
		return childNodes;
	}

	/**
	 * Returns a list of child Nodes for this Node that satisfy the predicate.
	 *
	 * @return List of matching child Nodes.
	 * @param filter specifying match criteria
	 */
	public List<Node> getChildNodes(Predicate<Node> filter) {
		return childNodes.stream()
				.filter(filter)
				.collect(Collectors.toList());
	}

	/**
	 * Returns the first child Node from this Node that satisfies the predicate.
	 *
	 * @return matching child Node.
	 * @param filter specifying match criteria
	 */
	public Node findChildNode(Predicate<Node> filter) {
		List<Node> children = getChildNodes(filter);
		return (children.isEmpty()) ? null : children.get(0);
	}

	/**
	 * convenience for adding multiple child nodes
	 *
	 * @param childNodes vararg Node array
	 */
	public void addChildNodes(Node... childNodes) {
		this.setChildNodes(Arrays.asList(childNodes));
	}

	/**
	 * addChildNode will associate a Node to  this Node as a child
	 *
	 * @param childNode Node
	 */
	public void addChildNode(Node childNode) {
		if (childNode == null || childNode == this) {
			return;
		}
		this.childNodes.add(childNode);
	}

	/**
	 * toString will create a readable representation of this {@code Node}.
	 *
	 * @return A string representation
	 */
	@Override
	public String toString() {
		final StringBuilder nodeToString = new StringBuilder("Node{");
		nodeToString.append("type=").append(type);
		nodeToString.append(", data=").append(data);
		nodeToString.append(", childNodes=").append((childNodes == null) ? "null" : "size:" + childNodes.size());
		nodeToString.append(", parent=").append((parent == null) ? "null" : "not null");
		nodeToString.append(", validated=").append(validated);
		nodeToString.append(", internalId='").append(internalId).append('\'');
		nodeToString.append(", path='").append(path).append('\'');
		nodeToString.append('}');
		return nodeToString.toString();
	}

	/**
	 * getKeys gets the internal keyset for the list of Nodes
	 *
	 * @return The keys the value's set on this Node.
	 */
	public Set<String> getKeys() {
		return data.keySet();
	}

	/**
	 * getParent returns the Node associated to this Node as its parent
	 *
	 * @return Node
	 */
	public Node getParent() {
		return parent;
	}

	/**
	 * setParent associates a Node as this Node's parent
	 *
	 * @param parent Node
	 */
	public void setParent(Node parent) {
		this.parent = parent;
	}

	/**
	 * getTemplateId returns the Internal TemplateId associated to this Node
	 *
	 * @return TemplateId
	 */
	public TemplateId getType() {
		return type;
	}

	/**
	 * Returns the path from the original document this {@code Node} is associated with.
	 *
	 * @return The path.
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Sets the path from the original document that this {@code Node} is associated with.
	 *
	 * @param newPath The path.
	 */
	public void setPath(String newPath) {
		path = newPath;
	}

	/**
	 * Search of this and child nodes for matching ids
	 *
	 * @param id templateid that identifies matching {@link gov.cms.qpp.conversion.model.Node}s
	 * @return a list of {@link gov.cms.qpp.conversion.model.Node}s in this
	 * {@link gov.cms.qpp.conversion.model.Node}'s hierarchy that match the searched id
	 */
	public List<Node> findNode(String id) {
		return findNode(id, null);
	}

	/**
	 * Search of this and child nodes for matching ids
	 *
	 * @param id   templateid that identifies matching {@link gov.cms.qpp.conversion.model.Node}s
	 * @param bail lambda that consumes a list and returns a boolean that governs early exit
	 * @return a list of {@link gov.cms.qpp.conversion.model.Node}s in this
	 * {@link gov.cms.qpp.conversion.model.Node}'s hierarchy that match the searched id
	 */
	private List<Node> findNode(String id, Predicate<List<?>> bail) {
		List<Node> foundNodes = new ArrayList<>();
		if (id.equals(getId())) {
			foundNodes.add(this);
		}
		for (Node childNode : childNodes) {
			if (bail != null && bail.test(foundNodes)) {
				break;
			}
			List<Node> matches = childNode.findNode(id, bail);
			foundNodes.addAll(matches);
		}
		return foundNodes;
	}

	/**
	 * Search this and child nodes for first node with matching id
	 *
	 * @param id templateid that identifies matching {@link gov.cms.qpp.conversion.model.Node}s
	 * @return the first {@link gov.cms.qpp.conversion.model.Node} in this
	 * {@link gov.cms.qpp.conversion.model.Node}'s hierarchy that match the searched id or null
	 * if no matches are found
	 */
	public Node findFirstNode(String id) {
		List<Node> nodes = this.findNode(id, Node::foundNode);
		return nodes.isEmpty() ? null : nodes.get(0);
	}

	/**
	 * setValidated sets the internal state of this Node validation.
	 * Used to control the recursion of nested validations
	 *
	 * @param validated boolean
	 */
	protected void setValidated(boolean validated) {
		this.validated = validated;
	}

	/**
	 * isValidated returns the internal state of this Node validation
	 *
	 * @return boolean
	 */
	protected boolean isValidated() {
		return validated;
	}

	/**
	 * setChildNodes will associate nested xml components with this parsed xml
	 * fragment Node
	 *
	 * @param childNodes The list of Nodes to become children.
	 */
	private void setChildNodes(List<Node> childNodes) {
		this.childNodes = childNodes;
	}


	/**
	 * foundNode checks to see if any Node exists in the List
	 *
	 * @param nodes A list
	 * @return Whether the list has an element or not
	 */
	private static boolean foundNode(List<?> nodes) {
		return !nodes.isEmpty();
	}
}
