package gov.cms.qpp.conversion.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.jdom2.Element;
import org.jdom2.xpath.XPathHelper;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;

/**
 * Represents a node of data that should be converted. Consists of a key/value
 * Map that holds the data gleaned from an input file.
 * Nodes can contain other nodes as children to create a hierarchy.
 */
public class Node {

	public static final int DEFAULT_LOCATION_NUMBER = -1;

	private final List<Node> childNodes = new ArrayList<>();
	private final Map<String, String> data = new HashMap<>();
	private final Map<String, List<String>> duplicateData = new HashMap<>();

	private TemplateId type;
	private Node parent;
	private boolean validated;

	private Element elementForLocation;
	private String defaultNsUri;
	private String path;
	private int line = DEFAULT_LOCATION_NUMBER;
	private int column = DEFAULT_LOCATION_NUMBER;

	/**
	 * Default constructor initializes internal list of Nodes
	 */
	public Node() {
		this(TemplateId.DEFAULT);
	}

	/**
	 * Constructor initialized with a Node and a template id String
	 *
	 * @param type of node
	 * @param parent this node's parent node
	 */
	public Node(TemplateId type, Node parent) {
		this(type);
		this.parent = parent;
	}

	/**
	 * Constructor initialized with a template id string
	 *
	 * @param templateId String of the parsed template id.
	 */
	public Node(TemplateId templateId) {
		this.type = templateId;
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
	 * Returns the string value of the xml fragment parsed into this Node or defaults to the passed in value
	 *
	 * @param name String key for the value
	 * @param defaultValue default value if the original value is null
	 * @return node value or default value
	 */
	public String getValueOrDefault(String name, String defaultValue) {
		String nodeValue = getValue(name);
		return nodeValue != null ? nodeValue : defaultValue;
	}

	/**
	 * getDuplicateValues returns the string value of the xml fragment parsed into this Node
	 *
	 * @param name String key for the value
	 * @return mapped duplicates of target value
	 */
	public List<String> getDuplicateValues(String name) {
		return duplicateData.get(name);
	}

	/**
	 * putValue stores the Value under the key: name
	 *
	 * @param name  String key to store value under
	 * @param value String that is stored with this xml parsed Node
	 */
	public void putValue(String name, String value) {
		putValue(name, value, true);
	}

	/**
	 * putValue stores the Value under the key: name
	 *
	 * @param name  String key to store value under
	 * @param value String that is stored with this xml parsed Node
	 * @param replace replace existing value
	 */
	public void putValue(String name, String value, boolean replace) {
		if (getValue(name) == null || replace) {
			data.put(name, value);
		} else {
			duplicateData.computeIfAbsent(name, ignore -> new ArrayList<>()).add(value);
		}
	}

	/**
	 * removeValue deletes the Value under the key: name
	 *
	 * @param name String key to remove value under
	 */
	public void removeValue(String name) {
		data.remove(name);
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
	 * getChildNodes returns the list of child Nodes for this Node
	 *
	 * @return List of child Nodes.
	 */
	public List<Node> getChildNodes() {
		return childNodes;
	}

	/**
	 * Returns a list of child Nodes for each template id specified
	 *
	 * @param templateIds we're looking for these.
	 * @return List of matching child Nodes.
	 */
	public Stream<Node> getChildNodes(TemplateId... templateIds) {
		Set<TemplateId> lookupTemplateIds = EnumSet.noneOf(TemplateId.class);
		Collections.addAll(lookupTemplateIds, templateIds);
		return getChildNodes(node -> lookupTemplateIds.contains(node.getType()));
	}

	/**
	 * Returns a list of child Nodes for this Node that satisfy the predicate.
	 *
	 * @param filter specifying match criteria
	 * @return List of matching child Nodes.
	 */
	public Stream<Node> getChildNodes(Predicate<Node> filter) {
		return childNodes.stream()
				.filter(filter);
	}

	/**
	 * Returns the first child Node from this Node that satisfies the predicate.
	 *
	 * @param filter specifying match criteria
	 * @return matching child Node.
	 */
	public Node findChildNode(Predicate<Node> filter) {
		return getChildNodes(filter).findFirst().orElse(null);
	}

	/**
	 * clears the old child nodes, replacing them with the given array
	 *
	 * @param childNodes vararg Node array
	 */
	public void setChildNodes(Node... childNodes) {
		this.childNodes.clear();
		this.childNodes.addAll(Arrays.asList(childNodes));
	}

	/**
	 * convenience for adding multiple child nodes
	 *
	 * @param childNodes vararg Node array
	 */
	public void addChildNodes(Node... childNodes) {
		this.childNodes.addAll(Arrays.asList(childNodes));
	}

	/**
	 * addChildNode will associate a Node to  this Node as a child
	 *
	 * @param childNode Node
	 */
	public void addChildNode(Node childNode) {
		if (childNode == null || childNode == this) { // checking identity equals on purpose
			return;
		}
		this.childNodes.add(childNode);
	}

	/**
	 * Delete a child {@code Node} of this {@code Node}.
	 *
	 * @param childNode The {@code Node} to be deleted.
	 * @return <tt>true</tt> if a child matched such that it was deleted.
	 */
	public boolean removeChildNode(Node childNode) {
		return this.childNodes.remove(childNode);
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
	 * setType sets the TemplateId backing the node
	 *
	 * @param type TemplateId
	 */
	public void setType(TemplateId type) {
		this.type = type;
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
	 * setLine sets the line number of the xml element backing the node
	 *
	 * @param line Line number
	 */
	public void setLine(int line) {
		this.line = line;
	}

	/**
	 * getLine returns the line number of the xml element backing the node
	 *
	 * @return line
	 */
	public int getLine() {
		return line;
	}

	/**
	 * setColumn sets the column number of the xml element backing the node
	 *
	 * @param column Column number
	 */
	public void setColumn(int column) {
		this.column = column;
	}

	/**
	 * getColumn returns the column number of the xml element backing the node
	 *
	 * @return column
	 */
	public int getColumn() {
		return column;
	}

	/**
	 * Returns the path from the original document this {@code Node} is associated with.
	 *
	 * @return The path.
	 */
	public String getOrComputePath() {
		if (path == null && elementForLocation != null) {
			path = XPathHelper.getAbsolutePath(elementForLocation);
		}

		return path;
	}

	/**
	 * Returns the element location of the node
	 *
	 * @return The element location of the node.
	 */
	public Element getElementForLocation() {
		return elementForLocation;
	}

	/**
	 * Sets the element location of the node
	 *
	 * @param elementForLocation The element location for the node
	 */
	public void setElementForLocation(Element elementForLocation) {
		this.elementForLocation = elementForLocation;
	}

	/**
	 * Returns the defaultNsUri from the original document this {@code Node} is associated with.
	 *
	 * @return The default namespace URI.
	 */
	public String getDefaultNsUri() {
		return defaultNsUri;
	}

	/**
	 * Sets the defaultNsUri from the original document that this {@code Node} is associated with.
	 *
	 * @param newDefaultNsUri updated default namespace URI.
	 */
	public void setDefaultNsUri(String newDefaultNsUri) {
		defaultNsUri = newDefaultNsUri;
	}

	/**
	 * Search of this and child nodes for matching ids
	 *
	 * @param templateId templateid that identifies matching {@link gov.cms.qpp.conversion.model.Node}s
	 * @return a list of {@link gov.cms.qpp.conversion.model.Node}s in this
	 * {@link gov.cms.qpp.conversion.model.Node}'s hierarchy that match the searched id
	 */
	public List<Node> findNode(TemplateId templateId) {
		return findNode(templateId, null);
	}

	/**
	 * Search of this and child nodes for matching ids
	 *
	 * @param templateId templateid that identifies matching {@link gov.cms.qpp.conversion.model.Node}s
	 * @param bail lambda that consumes a list and returns a boolean that governs early exit
	 * @return a list of {@link gov.cms.qpp.conversion.model.Node}s in this
	 * {@link gov.cms.qpp.conversion.model.Node}'s hierarchy that match the searched id
	 */
	private List<Node> findNode(TemplateId templateId, Predicate<List<Node>> bail) {
		List<Node> foundNodes = new ArrayList<>();
		List<Node> toSearch = Lists.newArrayList(childNodes);
		Consumer<Node> templateCheck = node -> {
			if (node.getType() == templateId) {
				foundNodes.add(node);
			}
		};
		templateCheck.accept(this);

		for (int i = 0; i < toSearch.size(); i++) {
			if (bail != null && bail.test(foundNodes)) {
				break;
			}
			Node childNode = toSearch.get(i);
			templateCheck.accept(childNode);
			toSearch.addAll(childNode.getChildNodes());
		}

		return foundNodes;
	}

	/**
	 * Search this and child nodes for first node with matching id
	 *
	 * @param templateId TemplateId that identifies matching {@link gov.cms.qpp.conversion.model.Node}s
	 * @return the first {@link gov.cms.qpp.conversion.model.Node} in this
	 * {@link gov.cms.qpp.conversion.model.Node}'s hierarchy that match the searched id or null
	 * if no matches are found
	 */
	public Node findFirstNode(TemplateId templateId) {
		List<Node> nodes = this.findNode(templateId, Node::foundNode);
		return nodes.isEmpty() ? null : nodes.get(0);
	}

	/**
	 * setValidated sets the internal state of this Node validation.
	 * Used to control the recursion of nested validations
	 *
	 * @param validated boolean
	 * @see Node#isValidated()
	 * @see Node#isNotValidated()
	 */
	public void setValidated(boolean validated) {
		this.validated = validated;
	}

	/**
	 * The state of this Node validation
	 *
	 * @return boolean if the node has been validated
	 * @see Node#isNotValidated()
	 * @see Node#setValidated(boolean)
	 */
	boolean isValidated() {
		return validated;
	}

	/**
	 * The state of this Node validation
	 *
	 * @return boolean if the node has NOT been validated
	 * @see Node#isValidated()
	 * @see Node#setValidated(boolean)
	 */
	public boolean isNotValidated() {
		return !isValidated();
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

	/**
	 * Finds the first parent of this {@code Node} that has a human readable {@link TemplateId}.
	 *
	 * @return The parent node.
	 */
	public Node findParentNodeWithHumanReadableTemplateId() {
		return findParentNodeWithHumanReadableTemplateId(this);
	}

	/**
	 * Recursively searches for a parent {@code Node} with a human readable {@link TemplateId}.
	 *
	 * @param node The {@code Node} to see if it or its parent has a human readable {@link TemplateId}
	 * @return The passed in {@code Node} if it has a human readable {@link TemplateId}, {@code null} if this {@code Node} is {@code null}, or
	 * whatever the parent {@code Node} has.
	 */
	private Node findParentNodeWithHumanReadableTemplateId(Node node) {
		if (node == null) {
			return null;
		}

		if (!StringUtils.isEmpty(node.getType().getHumanReadableTitle())) {
			return node;
		}

		return findParentNodeWithHumanReadableTemplateId(node.getParent());
	}

	/**
	 * creates a readable representation of this {@code Node}.
	 *
	 * @return A string representation
	 */
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("type", type)
				.add("data", data)
				.add("childNodesSize", childNodes.size())
				.add("parent", parent == null ? "null" : "not null")
				.add("validated", validated)
				.add("defaultNsUri", defaultNsUri)
				.add("path", path)
				.add("elementForLocation", elementForLocation)
				.add("line", line)
				.add("column", column)
				.toString();
	}

	/**
	 * Returns whether this object is equal to another.
	 *
	 * @param o The other object
	 * @return {@code true} if this object equals {@code o}.
	 */
	@Override
	public final boolean equals(final Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof Node)) {
			return false;
		}

		final Node node = (Node) o;

		return new EqualsBuilder().append(isValidated(), node.isValidated())
				.append(getChildNodes(), node.getChildNodes())
				.append(data, node.data)
				.append(duplicateData, node.duplicateData)
				.append(getType(), node.getType())
				.append(getDefaultNsUri(), node.getDefaultNsUri())
				.append(path, node.path)
				.append(getElementForLocation(), node.getElementForLocation())
				.append(getLine(), node.getLine())
				.append(getColumn(), node.getColumn())
				.isEquals();
	}

	/**
	 * Computes and returns the hash code for this object.
	 *
	 * @return The hash code.
	 */
	@Override
	public final int hashCode() {
		return Objects.hash(getChildNodes(), data, duplicateData, getType(), isValidated(), getDefaultNsUri(),
				path, getElementForLocation(), getLine(), getColumn());
	}

}
