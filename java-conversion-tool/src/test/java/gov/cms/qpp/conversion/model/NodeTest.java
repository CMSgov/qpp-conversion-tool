package gov.cms.qpp.conversion.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

public class NodeTest {

	@Test
	public void testMembers() {
		TemplateId randomEnumValue1 = TemplateId.PLACEHOLDER;
		TemplateId randomEnumValue2 = TemplateId.ACI_SECTION;
		Node node = new Node(randomEnumValue1);

		node.putValue("DEF", "GHI");

		assertEquals("GHI", node.getValue("DEF"));

		Node childNode = new Node();
		childNode.setType(randomEnumValue2);
		node.addChildNode(childNode);

		assertEquals(childNode, node.getChildNodes().get(0));

		String toString = node.toString();

		assertTrue(toString.contains(randomEnumValue1.name()));
		assertTrue(toString.contains("DEF"));
		assertTrue(toString.contains("GHI"));
		assertFalse(toString.contains(randomEnumValue2.name()));
	}

	@Test
	public void testToString() {
		Node node = new Node();

		Node childNode = new Node();
		childNode.setParent(node);
		node.addChildNode(childNode);

		childNode = new Node();
		childNode.setParent(node);
		node.addChildNode(childNode);

		//ensure that we don't go down all the child nodes
		assertThat("Node#toString must not recurse down its children and print the size instead.", node.toString(),
				containsString("childNodes=size"));
		//ensure we don't recurse up the parent
		assertThat("Node#toString must not recurse down its children and print the size instead.", childNode.toString(),
				containsString("parent=not null"));
	}

	@Test
	public void testValidatedMember() {
		Node node = new Node();
		node.setValidated(true);

		assertTrue(node.isValidated());
	}

	@Test
	public void testParentMember() {
		Node child = new Node();
		Node parent = new Node();
		child.setParent(parent);

		assertTrue(child.getParent() == parent);
	}

	@Test
	public void testAddNullChild() {
		Node node = new Node();
		node.addChildNode(null);

		assertTrue(node.getChildNodes().isEmpty());
	}

	@Test
	public void testAddThisChild() {
		Node node = new Node();
		node.addChildNode(node);

		assertTrue(node.getChildNodes().isEmpty());
	}

	@Test
	public void testFindNode() {
		Node parent = new Node();
		Node childOne = new Node();
		Node childTwo = new Node();
		Node childThree = new Node(TemplateId.PLACEHOLDER);
		parent.addChildNodes(childOne, childTwo, childThree);

		List<Node> results = parent.findNode(TemplateId.PLACEHOLDER);

		assertEquals("should find first child that has the searched id", results.size(), 1);
	}

	@Test
	public void testFindNoNode() {
		Node parent = new Node();
		Node childOne = new Node();
		Node childTwo = new Node();
		parent.addChildNodes(childOne, childTwo);

		List<Node> results = parent.findNode(TemplateId.PLACEHOLDER);

		assertTrue("should find first child that has the searched id", results.isEmpty());
	}

	@Test
	public void testFindNodeSelfIncluded() {
		Node parent = new Node(TemplateId.PLACEHOLDER);
		Node childOne = new Node(TemplateId.PLACEHOLDER);
		parent.addChildNode(childOne);

		List<Node> results = parent.findNode(TemplateId.PLACEHOLDER);

		assertEquals("should find itself if it has the searched id", results.size(), 2);
	}

	@Test
	public void testFindFirstNodeSelf() {
		Node parent = new Node(TemplateId.PLACEHOLDER);
		Node childOne = new Node(TemplateId.PLACEHOLDER);
		parent.addChildNode(childOne);

		assertSame("should find itself if it has the searched id", parent.findFirstNode(TemplateId.PLACEHOLDER), parent);
	}

	@Test
	public void testFindFirstNodeChildNode() {
		Node parent = new Node();
		Node childOne = new Node();
		Node childTwo = new Node(TemplateId.PLACEHOLDER);
		Node childThree = new Node(TemplateId.PLACEHOLDER);
		parent.addChildNodes(childOne, childTwo, childThree);

		assertSame("should find first child that has the searched id", parent.findFirstNode(TemplateId.PLACEHOLDER), childTwo);
	}

	@Test
	public void testFindFirstNoNode() {
		Node parent = new Node();
		Node childOne = new Node();
		Node childTwo = new Node();
		Node childThree = new Node();
		parent.addChildNodes(childOne, childTwo, childThree);

		assertNull("should not find a node that has the searched id", parent.findFirstNode(TemplateId.PLACEHOLDER));
	}

	@Test
	public void testRemoveValue() {
		Node node = new Node();
		node.putValue("test", "hello");
		assertTrue(node.hasValue("test"));
		node.removeValue("test");
		assertFalse(node.hasValue("test"));
	}
}
