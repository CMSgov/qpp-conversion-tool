package gov.cms.qpp.conversion.model;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

class NodeTest {

	@Test
	void testPut() {
		Node node = new Node(TemplateId.PLACEHOLDER);
		node.putValue("DEF", "GHI");

		assertWithMessage("get value should equal put value")
				.that(node.getValue("DEF")).isSameAs("GHI");
	}

	@Test
	void testChild() {
		Node node = new Node(TemplateId.PLACEHOLDER);
		Node childNode = new Node();
		childNode.setType(TemplateId.ACI_SECTION);
		node.addChildNode(childNode);

		assertWithMessage("Did not retrieve expected node")
				.that(node.getChildNodes().get(0)).isSameAs(childNode);
	}

	@Test
	void testToString() {
		Node node = new Node(TemplateId.PLACEHOLDER);
		node.putValue("DEF", "GHI");

		String toString = node.toString();

		assertThat(toString).contains(TemplateId.PLACEHOLDER.name());
		assertThat(toString).contains("GHI");
	}

	@Test
	void testToStringDepth() {
		Node node = new Node();

		Node childNode = new Node();
		childNode.setParent(node);
		node.addChildNode(childNode);

		childNode = new Node();
		childNode.setParent(node);
		node.addChildNode(childNode);

		//ensure that we don't go down all the child nodes
		assertWithMessage("Node#toString must not recurse down its children and print the size instead.")
				.that(node.toString()).contains("childNodesSize=");
		//ensure we don't recurse up the parent
		assertWithMessage("Node#toString must not recurse down its children and print the size instead.")
				.that(childNode.toString()).contains("parent=not null");
	}

	@Test
	void testValidatedMember() {
		Node node = new Node();
		node.setValidated(true);

		assertThat(node.isValidated()).isTrue();
	}

	@Test
	void testNotValidatedMember() {
		Node node = new Node();
		assertThat(node.isNotValidated()).isTrue();
	}

	@Test
	void testParentMember() {
		Node child = new Node();
		Node parent = new Node();
		child.setParent(parent);

		assertThat(child.getParent()).isSameAs(parent);
	}

	@Test
	void testAddNullChild() {
		Node node = new Node();
		node.addChildNode(null);

		assertThat(node.getChildNodes()).isEmpty();
	}

	@Test
	void testAddThisChild() {
		Node node = new Node();
		node.addChildNode(node);

		assertThat(node.getChildNodes()).isEmpty();
	}

	@Test
	void testFindNode() {
		Node parent = new Node();
		Node childOne = new Node();
		Node childTwo = new Node();
		Node childThree = new Node(TemplateId.PLACEHOLDER);
		parent.addChildNodes(childOne, childTwo, childThree);

		List<Node> results = parent.findNode(TemplateId.PLACEHOLDER);

		assertWithMessage("should find first child that has the searched id")
				.that(results).hasSize(1);
	}

	@Test
	void testFindNodeLoveThySelf() {
		Node parent = new Node(TemplateId.PLACEHOLDER);
		Node onlyChild = new Node(TemplateId.PLACEHOLDER);
		parent.addChildNodes(onlyChild);

		List<Node> results = parent.findNode(TemplateId.PLACEHOLDER);

		assertWithMessage("should find first node that has the searched id")
			.that(results).hasSize(2);
		assertWithMessage("should search self first")
			.that(results.get(0)).isSameAs(parent);
	}

	@Test
	void testFindNodeOrder() {
		Node carter = new Node(TemplateId.PLACEHOLDER);
		Node lois = new Node(TemplateId.PLACEHOLDER);
		Node chris = new Node(TemplateId.PLACEHOLDER);
		Node meg = new Node(TemplateId.PLACEHOLDER);
		Node stewie = new Node(TemplateId.PLACEHOLDER);
		carter.addChildNodes(lois);
		lois.addChildNodes(chris, meg, stewie);
		List<Node> order = Arrays.asList(carter, lois, chris, meg, stewie);

		List<Node> results = carter.findNode(TemplateId.PLACEHOLDER);

		assertWithMessage("should prioritize by generation and birth / add order")
			.that(results).containsExactlyElementsIn(order).inOrder();
	}

	@Test
	void testFindNoNode() {
		Node parent = new Node();
		Node childOne = new Node();
		Node childTwo = new Node();
		parent.addChildNodes(childOne, childTwo);

		List<Node> results = parent.findNode(TemplateId.PLACEHOLDER);

		assertWithMessage("should find first child that has the searched id")
				.that(results).isEmpty();
	}

	@Test
	void testFindNodeSelfIncluded() {
		Node parent = new Node(TemplateId.PLACEHOLDER);
		Node childOne = new Node(TemplateId.PLACEHOLDER);
		parent.addChildNode(childOne);

		List<Node> results = parent.findNode(TemplateId.PLACEHOLDER);

		assertWithMessage("should find itself if it has the searched id")
				.that(results).hasSize(2);
	}

	@Test
	void testFindFirstNodeSelf() {
		Node parent = new Node(TemplateId.PLACEHOLDER);
		Node childOne = new Node(TemplateId.PLACEHOLDER);
		parent.addChildNode(childOne);

		assertWithMessage("should find itself if it has the searched id")
				.that(parent.findFirstNode(TemplateId.PLACEHOLDER))
				.isSameAs(parent);
	}

	@Test
	void testFindFirstNodeChildNode() {
		Node parent = new Node();
		Node childOne = new Node();
		Node childTwo = new Node(TemplateId.PLACEHOLDER);
		Node childThree = new Node(TemplateId.PLACEHOLDER);
		parent.addChildNodes(childOne, childTwo, childThree);

		assertWithMessage("should find first child that has the searched id")
				.that(parent.findFirstNode(TemplateId.PLACEHOLDER))
				.isSameAs(childTwo);
	}

	@Test
	void testFindFirstNoNode() {
		Node parent = new Node();
		Node childOne = new Node();
		Node childTwo = new Node();
		Node childThree = new Node();
		parent.addChildNodes(childOne, childTwo, childThree);

		assertWithMessage("should not find a node that has the searched id")
				.that(parent.findFirstNode(TemplateId.PLACEHOLDER))
				.isNull();
	}

	@Test
	void testRemoveValue() {
		Node node = new Node();
		node.putValue("test", "hello");
		node.removeValue("test");

		assertThat(node.hasValue("test")).isFalse();
	}

	@Test
	void testRemoveChildNodeNull() {
		Node node = new Node();
		assertThat(node.removeChildNode(null)).isFalse();
	}

	@Test
	void testRemoveChildNodeSelf() {
		Node node = new Node();
		assertThat(node.removeChildNode(node)).isFalse();
	}

	@Test
	void testEquals() {
		Node parent = new Node(TemplateId.CLINICAL_DOCUMENT);
		Node child1 = new Node(TemplateId.IA_SECTION);
		child1.setParent(parent);
		Node child2 = new Node(TemplateId.ACI_SECTION);
		child2.setParent(parent);
		parent.setChildNodes(child1, child2);

		EqualsVerifier.forClass(Node.class)
			.withPrefabValues(List.class, Lists.newArrayList(new Node()), Lists.newArrayList(new Node(TemplateId.CLINICAL_DOCUMENT), new Node(TemplateId.ACI_NUMERATOR)))
			.withPrefabValues(Node.class, new Node(TemplateId.ACI_DENOMINATOR), parent)
			.withIgnoredFields("parent")
			.suppress(Warning.NONFINAL_FIELDS)
			.verify();
	}
}
