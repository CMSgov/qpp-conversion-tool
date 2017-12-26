package gov.cms.qpp.conversion.validate;

import com.google.common.base.Strings;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.LocalizedError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Node checker DSL to help abbreviate / simplify single node validations
 */
class Checker {
	private static final Logger DEV_LOG = LoggerFactory.getLogger(Checker.class);
	private Node node;
	private Set<Detail> details;
	private boolean anded;
	private Map<TemplateId, AtomicInteger> nodeCount;
	private Comparable<?> lastAppraised;

	private Checker(Node node, Set<Detail> details, boolean anded) {
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
	static Checker check(Node node, Set<Detail> details) {
		return new Checker(node, details, true);
	}

	/**
	 * static factory that returns a non-shortcut validator
	 *
	 * @param node node to be validated
	 * @param details holder for validation errors
	 * @return The checker, for chaining method calls.
	 */
	static Checker thoroughlyCheck(Node node, Set<Detail> details) {
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
	 * @param code that identifies the error
	 * @param name key of expected value
	 * @return The checker, for chaining method calls.
	 */
	public Checker value(LocalizedError code, String name) {
		lastAppraised = node.getValue(name);
		if (!shouldShortcut() && lastAppraised == null) {
			details.add(detail(code));
		}
		return this;
	}

	/**
	 * checks target node to ensure a value is retrieved with given name key
	 *
	 * @param code that identifies the error
	 * @param name key of expected value
	 * @return The checker, for chaining method calls.
	 */
	Checker valueIsNotEmpty(LocalizedError code, String name) {
		lastAppraised = node.getValue(name);
		if (!shouldShortcut() && Strings.isNullOrEmpty((String) lastAppraised)) {
			details.add(detail(code));
		}
		return this;
	}

	/**
	 * checks target node for the existence of a single value with the given name key
	 *
	 * @param code that identifies the error
	 * @param name key of expected value
	 * @return The checker, for chaining method calls.
	 */
	public Checker singleValue(LocalizedError code, String name) {
		value(code, name);
		List<String> duplicates = node.getDuplicateValues(name);
		if (duplicates != null && !duplicates.isEmpty()) {
			details.add(detail(code));
		}
		return this;
	}

	/**
	 * checks target node for the existence of a value with the given name key
	 * and matches that value with one of the supplied values.
	 *
	 * @param code that identifies the error
	 * @param name key of expected value
	 * @param expected the expected value
	 * @return The checker, for chaining method calls.
	 */
	Checker valueIs(LocalizedError code, String name, String expected) {
		return valueIn(code, name, expected);
	}

	/**
	 * checks target node for the existence of a value with the given name key
	 * and matches that value with one of the supplied values.
	 *
	 * @param code that identifies the error
	 * @param name key of expected value
	 * @param values List of strings to check for the existence of.
	 * @return The checker, for chaining method calls.
	 */
	Checker valueIn(LocalizedError code, String name, String... values) {
		boolean contains = false;
		if (name == null) {
			details.add(detail(code));
			return this; //Short circuit on empty key or empty values
		}
		lastAppraised = node.getValue(name);
		if (lastAppraised == null || values == null) {
			details.add(detail(code));
			return this; //Short circuit on node doesn't contain key
		}
		for (String value : values) {
			if (((String) lastAppraised).equalsIgnoreCase(value)) {
				contains = true;
				break;
			}
		}
		if (!contains) {
			details.add(detail(code));
		}
		return this;
	}

	/**
	 * Checks target node for the existence of an integer value with the given name key.
	 *
	 * @param code that identifies the error
	 * @param name key of expected value
	 * @return The checker, for chaining method calls.
	 */
	Checker intValue(LocalizedError code, String name) {
		if (!shouldShortcut()) {
			try {
				lastAppraised = Integer.parseInt(node.getValue(name));
			} catch (NumberFormatException ex) {
				DEV_LOG.warn("Problem with non int value: " + node.getValue(name), ex);
				details.add(detail(code));
			}
		}
		return this;
	}

	/**
	 * Allow for compound comparisons of Node values.
	 *
	 * @param code that identifies the error
	 * @param value to be compared against
	 * @return The checker, for chaining method calls.
	 */
	@SuppressWarnings("unchecked")
	Checker greaterThan(LocalizedError code, Comparable<?> value) {
		if (!shouldShortcut() && lastAppraised != null && ((Comparable<Object>) lastAppraised).compareTo(value) <= 0) {
			details.add(detail(code));
		}
		lastAppraised = null;
		return this;
	}

	/**
	 * Allow for compound comparisons of Node values.
	 *
	 * @param code that identifies the error
	 * @param value to be compared against
	 * @return The checker, for chaining method calls.
	 */
	@SuppressWarnings("unchecked")
	Checker lessThanOrEqualTo(LocalizedError code, Comparable<?> value) {
		if (!shouldShortcut() && lastAppraised != null && ((Comparable<Object>) lastAppraised).compareTo(value) > 0) {
			details.add(detail(code));
		}
		lastAppraised = null;
		return this;
	}

	/**
	 * Checks target node value to be between a specific range
	 *
	 * @param code that identifies the error
	 * @param name key of expected value
	 * @param startValue starting value for range
	 * @param endValue ending value for range
	 * @return The checker, for chaining method calls
	 */
	@SuppressWarnings("unchecked")
	Checker inDecimalRangeOf(LocalizedError code, String name, float startValue, float endValue) {
		if (!shouldShortcut()) {
			try {
				lastAppraised = Float.parseFloat(node.getValue(name));
				if (((Comparable<Float>) lastAppraised).compareTo(startValue) < 0
						|| ((Comparable<Float>) lastAppraised).compareTo(endValue) > 0) {
					details.add(detail(code));
				}
			} catch (NumberFormatException | NullPointerException exc) {
				DEV_LOG.warn("Problem with non float value: " + node.getValue(name), exc);
				details.add(detail(code));
			}
		}
		return this;
	}

	/**
	 * Checks target node for the existence of a specified parent.
	 *
	 * @param code that identifies the error
	 * @return The checker, for chaining method calls.
	 */
	Checker hasParent(LocalizedError code, TemplateId type) {
		if (!shouldShortcut()) {
			TemplateId parentType = Optional.ofNullable(node.getParent())
					.orElse(new Node()).getType();
			if (parentType != type) {
				details.add(detail(code));
			}
		}
		return this;
	}

	/**
	 * Checks target node for the existence of any child nodes.
	 *
	 * @param code that identifies the error
	 * @return The checker, for chaining method calls.
	 */
	Checker hasChildren(LocalizedError code) {
		if (!shouldShortcut() && node.getChildNodes().isEmpty()) {
			details.add(detail(code));
		}
		return this;
	}

	/**
	 * Verifies that the target node has at least the given minimum or more of the given {@link TemplateId}s.
	 *
	 * @param code that identifies the error
	 * @param minimum minimum required children of specified types
	 * @param types types of children to filter by
	 * @return The checker, for chaining method calls.
	 */
	public Checker childMinimum(LocalizedError code, int minimum, TemplateId... types) {
		if (!shouldShortcut()) {
			int count = tallyNodes(types);
			if (count < minimum) {
				details.add(detail(code));
			}
		}
		return this;
	}

	/**
	 * Verifies that the target node has less than the given maximum of the given {@link TemplateId}s.
	 *
	 * @param code that identifies the error
	 * @param maximum maximum required children of specified types
	 * @param types types of children to filter by
	 * @return The checker, for chaining method calls.
	 */
	public Checker childMaximum(LocalizedError code, int maximum, TemplateId... types) {
		if (!shouldShortcut()) {
			int count = tallyNodes(types);
			if (count > maximum) {
				details.add(detail(code));
			}
		}
		return this;
	}

	/**
	 * Verifies that the measures specified are contained within the current node's children
	 *
	 * @param code that identifies the error
	 * @param measureIds measures specified for given node
	 * @return The checker, for chaining method calls
	 */
	Checker hasMeasures(LocalizedError code, String... measureIds) {
		return hasMeasures(code, measureIds.length, measureIds);
	}

	Checker hasMeasures(LocalizedError code, int numberOfMeasuresRequired, String... measureIds) {
		if (!shouldShortcut()) {

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

			if (numNodesWithWantedMeasureIds < numberOfMeasuresRequired) {
				details.add(detail(code));
			}
		}
		return this;
	}

	/**
	 * Verifies that the target node contains only children of specified template ids
	 *
	 * @param code that identifies the error
	 * @param types types of template ids to filter
	 * @return The checker, for chaining method calls.
	 */
	Checker onlyHasChildren(LocalizedError code, TemplateId... types) {
		if (!shouldShortcut()) {
			Set<TemplateId> templateIds = EnumSet.noneOf(TemplateId.class);
			for (TemplateId templateId : types) {
				templateIds.add(templateId);
			}

			boolean valid = node.getChildNodes()
				.stream()
				.allMatch(childNode -> templateIds.contains(childNode.getType()));
			if (!valid) {
				details.add(detail(code));
			}
		}
		return this;
	}

	/**
	 * Allows for the assurance that a {@link Node} has children of a {@link TemplateId} type
	 * that are distinct according to a given criteria.
	 *
	 * @param code that identifies the error
	 * @param type template id type
	 * @param dedup distinction criteria
	 * @param <T> type that can serve as a hash key value
	 * @return The checker, for chaining method calls.
	 */
	<T> Checker oneChildPolicy(LocalizedError code, TemplateId type, Function<Node, T> dedup) {
		List<Node> nodes = node.getChildNodes(type).collect(Collectors.toList());
		Map<T, Node> distinct =
				nodes.stream().collect(
						Collectors.toMap(dedup, Function.identity(), (pre, current) -> pre));

		if (distinct.size() < nodes.size()) {
			details.add(detail(code));
		}
		return this;
	}

	/**
	 * Marks the checked node as being incompletely validated.
	 *
	 * @return The checker, for chaining method calls.
	 */
	Checker incompleteValidation() {
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

	/**
	 * Creates a detail for the given error, and the present node
	 * @param code LocalizedError to use when generating details
	 * @return details for the given error and present node
	 */
	private Detail detail(LocalizedError code) {
		return Detail.forErrorAndNode(code, node);
	}
}
