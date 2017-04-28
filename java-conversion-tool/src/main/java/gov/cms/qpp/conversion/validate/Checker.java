package gov.cms.qpp.conversion.validate;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.ValidationError;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Node checker DSL to help abbreviate / simplify single node validations
 */
class Checker {

	private Node node;
	private List<ValidationError> validationErrors;
	private boolean anded;
	private Map<TemplateId, AtomicInteger> nodeCount;
	private Comparable lastAppraised;

	private Checker(Node node, List<ValidationError> validationErrors, boolean anded) {
		this.node = node;
		this.validationErrors = validationErrors;
		this.anded = anded;
		this.nodeCount = new EnumMap<>(TemplateId.class);
		node.getChildNodes()
			.stream()
			.map(Node::getType)
			.forEach(type -> this.nodeCount.computeIfAbsent(type, key -> new AtomicInteger()).incrementAndGet());
	}

	/**
	 * static factory that returns a shortcut validator
	 *
	 * @param node node to be validated
	 * @param validationErrors holder for validation errors
	 * @return The checker, for chaining method calls.
	 */
	static Checker check(Node node, List<ValidationError> validationErrors) {
		return new Checker(node, validationErrors, true);
	}

	/**
	 * static factory that returns a non-shortcut validator
	 *
	 * @param node node to be validated
	 * @param validationErrors holder for validation errors
	 * @return The checker, for chaining method calls.
	 */
	static Checker thoroughlyCheck(Node node, List<ValidationError> validationErrors) {
		return new Checker(node, validationErrors, false);
	}

	private boolean shouldShortcut() {
		return anded && !validationErrors.isEmpty();
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
			validationErrors.add(new ValidationError(message, node.getPath()));
		}
		return this;
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
				validationErrors.add(new ValidationError(message, node.getPath()));
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
	public Checker greaterThan(String message, Comparable value) {
		if (lastAppraised == null) {
			throw new IllegalStateException("No last appraised value.");
		}
		if (lastAppraised.compareTo(value) <= 0) {
			validationErrors.add(new ValidationError(message, node.getPath()));
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
		if (!shouldShortcut()){
			TemplateId parentType = Optional.ofNullable(node.getParent())
					.orElse(new Node()).getType();
			if (parentType != type) {
				validationErrors.add(new ValidationError(message, node.getPath()));
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
			validationErrors.add(new ValidationError(message, node.getPath()));
		}
		return this;
	}

	/**
	 * Verifies that the target node has more than the given minimum of the given {@link TemplateId}s.
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
				validationErrors.add(new ValidationError(message, node.getPath()));
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
				validationErrors.add(new ValidationError(message, node.getPath()));
			}
		}
		return this;
	}

	public Checker checkChild(Node child, boolean anded) {
		return new Checker(child, validationErrors, anded);
	}

	public Checker markValidated() {
		node.setValidated(true);
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
