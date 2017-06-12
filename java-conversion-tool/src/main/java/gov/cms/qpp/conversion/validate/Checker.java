package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Node checker DSL to help abbreviate / simplify single node validations
 */
class Checker {

	private Node node;
	private List<Detail> details;
	private boolean anded;
	private Map<TemplateId, AtomicInteger> nodeCount;
	private Comparable<?> lastAppraised;

	private Checker(Node node, List<Detail> details, boolean anded) {
		this.node = node;
		this.details = details;
		this.anded = anded;
		this.nodeCount = new EnumMap<>(TemplateId.class);
		node.getChildNodes()
			.stream()
			.map(Node::getType)
			.forEach(type -> this.nodeCount.computeIfAbsent(type, key -> new AtomicInteger()).incrementAndGet());
		this.node.setValidated(true);
	}

	/**
	 * static factory that returns a shortcut validator
	 *
	 * @param node node to be validated
	 * @param details holder for validation errors
	 * @return The checker, for chaining method calls.
	 */
	static Checker check(Node node, List<Detail> details) {
		return new Checker(node, details, true);
	}

	/**
	 * static factory that returns a non-shortcut validator
	 *
	 * @param node node to be validated
	 * @param details holder for validation errors
	 * @return The checker, for chaining method calls.
	 */
	static Checker thoroughlyCheck(Node node, List<Detail> details) {
		return new Checker(node, details, false);
	}

	/**
	 * Governs whether or not a check should be performed based on prior anded check failures.
	 *
	 * @return determination as to whether or not a check should be performed
	 */
	private boolean shouldShortcut() {
		return anded && !details.isEmpty();
	}

	/**
	 * checks target node for the existence of a value with the given name key
	 *
	 * @param message error message if searched value is not found
	 * @param name key of expected value
	 * @return The checker, for chaining method calls.
	 */
	Checker value(String message, String name) {
		lastAppraised = node.getValue(name);
		if (!shouldShortcut() && lastAppraised == null) {
			details.add(new Detail(message, node.getPath()));
		}
		return this;
	}

	/**
	 * checks target node for the existence of a single value with the given name key
	 *
	 * @param message error message if searched value is not found
	 * @param name key of expected value
	 * @return The checker, for chaining method calls.
	 */
	Checker singleValue(String message, String name) {
		value(message, name);
		List<String> duplicates = node.getDuplicateValues(name);
		if (duplicates != null && !duplicates.isEmpty()) {
			details.add(new Detail(message, node.getPath()));
		}
		return this;
	}

	/**
	 * checks target node for the existence of a value with the given name key
	 * and matches that value with one of the supplied values.
	 *
	 * @param message error message if searched value is not found
	 * @param name key of expected value
	 * @param values List of strings to check for the existence of.
	 * @return The checker, for chaining method calls.
	 */
	Checker valueIn(String message, String name, String ... values) {
		boolean contains = false;
		if (name == null) {
			setErrorMessage(message);
			return this; //Short circuit on empty key or empty values
		}
		lastAppraised = node.getValue(name);
		if (lastAppraised == null || values == null) {
			setErrorMessage(message);
			return this; //Short circuit on node doesn't contain key
		}
		for (String value : values) {
			if (((String) lastAppraised).equalsIgnoreCase(value)) {
				contains = true;
				break;
			}
		}
		if (!contains) {
			setErrorMessage(message);
		}
		return this;
	}

	private void setErrorMessage(String message) {
		if (! shouldShortcut()) {
			details.add(new Detail(message, node.getPath()));
		}
	}

	/**
	 * Checks target node for the existence of an integer value with the given name key.
	 *
	 * @param message error message if searched value is not found or is not appropriately typed
	 * @param name key of expected value
	 * @return The checker, for chaining method calls.
	 */
	public Checker intValue(String message, String name) {
		if (!shouldShortcut()) {
			try {
				lastAppraised = Integer.parseInt(node.getValue(name));
			} catch (NumberFormatException ex) {
				details.add(new Detail(message, node.getPath()));
			}
		}
		return this;
	}

	/**
	 * Allow for compound comparisons of Node values.
	 *
	 * @param message error message should comparison fail
	 * @param value to be compared against
	 * @return The checker, for chaining method calls.
	 */
	@SuppressWarnings("unchecked")
	public Checker greaterThan(String message, Comparable<?> value) {
		if (!shouldShortcut() && lastAppraised != null && ((Comparable<Object>) lastAppraised).compareTo(value) <= 0) {
			details.add(new Detail(message, node.getPath()));
		}
		lastAppraised = null;
		return this;
	}

	/**
	 * Checks target node for the existence of a specified parent.
	 *
	 * @param message validation error message
	 * @return The checker, for chaining method calls.
	 */
	public Checker hasParent(String message, TemplateId type) {
		if (!shouldShortcut()) {
			TemplateId parentType = Optional.ofNullable(node.getParent())
					.orElse(new Node()).getType();
			if (parentType != type) {
				details.add(new Detail(message, node.getPath()));
			}
		}
		return this;
	}

	/**
	 * Checks target node for the existence of any child nodes.
	 *
	 * @param message validation error message
	 * @return The checker, for chaining method calls.
	 */
	public Checker hasChildren(String message) {
		if (!shouldShortcut() && node.getChildNodes().isEmpty()) {
			details.add(new Detail(message, node.getPath()));
		}
		return this;
	}

	/**
	 * Verifies that the target node has at least the given minimum or more of the given {@link TemplateId}s.
	 *
	 * @param message validation error message
	 * @param minimum minimum required children of specified types
	 * @param types types of children to filter by
	 * @return The checker, for chaining method calls.
	 */
	public Checker childMinimum(String message, int minimum, TemplateId... types) {
		if (!shouldShortcut()) {
			int count = tallyNodes(types);
			if (count < minimum) {
				details.add(new Detail(message, node.getPath()));
			}
		}
		return this;
	}

	/**
	 * Verifies that the target node has less than the given maximum of the given {@link TemplateId}s.
	 *
	 * @param message validation error message
	 * @param maximum maximum required children of specified types
	 * @param types types of children to filter by
	 * @return The checker, for chaining method calls.
	 */
	public Checker childMaximum(String message, int maximum, TemplateId... types) {
		if (!shouldShortcut()) {
			int count = tallyNodes(types);
			if (count > maximum) {
				details.add(new Detail(message, node.getPath()));
			}
		}
		return this;
	}

	/**
	 * Verifies that the measures specified are contained within the current node's children
	 *
	 * @param message validation error message
	 * @param measureIds measures specified for given node
	 * @return The checker, for chaining method calls
	 */
	public Checker hasMeasures(String message, String... measureIds) {
		if (!shouldShortcut()) {
			int numberOfMeasuresRequired = Arrays.asList(measureIds).size();

			long numNodesWithWantedMeasureIds = node.getChildNodes(currentNode -> {
				String measureIdOfNode = currentNode.getValue("measureId");
				if (null == measureIdOfNode) {
					return false;
				}
				for (String measureIdLookingFor : measureIds) {
					if (measureIdOfNode.equals(measureIdLookingFor)) {
						return true;
					}
				}
				return false;
			}).count();

			if (numberOfMeasuresRequired != numNodesWithWantedMeasureIds) {
				details.add(new Detail(message, node.getPath()));
			}
		}
		return this;
	}

	/**
	 * Verifies that the target node contains only children of specified template ids
	 *
	 * @param message validation error message
	 * @param types types of template ids to filter
	 * @return The checker, for chaining method calls.
	 */
	public Checker onlyHasChildren(String message, TemplateId... types) {
		if (!shouldShortcut()) {
			Set<TemplateId> templateIds = EnumSet.noneOf(TemplateId.class);
			for (TemplateId templateId : types) {
				templateIds.add(templateId);
			}

			boolean valid = node.getChildNodes()
				.stream()
				.allMatch(childNode -> templateIds.contains(childNode.getType()));
			if (!valid) {
				details.add(new Detail(message, node.getPath()));
			}
		}
		return this;
	}

	/**
	 * Marks the checked node as being incompletely validated.
	 *
	 * @return The checker, for chaining method calls.
	 */
	public Checker incompleteValidation() {
		node.setValidated(false);
		return this;
	}

	/**
	 * Aggregate count of nodes of the given types
	 *
	 * @param types types of nodes to filter by
	 * @return count
	 */
	private int tallyNodes(TemplateId... types) {
		return Arrays.stream(types)
			.map(nodeCount::get)
			.filter(Objects::nonNull)
			.mapToInt(AtomicInteger::get)
			.sum();
	}
}
