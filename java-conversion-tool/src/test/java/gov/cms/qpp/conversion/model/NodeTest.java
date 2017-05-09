package gov.cms.qpp.conversion.model;

import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NodeTest {

	@Test
	public void testMembers() {
		Node node = new Node();

		node.setId("ABC");
		node.putValue("DEF", "GHI");

		assertEquals("GHI", node.getValue("DEF"));

		Node childNode = new Node();
		childNode.setId("JKL");
		node.addChildNode(childNode);

		assertEquals(childNode, node.getChildNodes().get(0));

		String toString = node.toString();

		assertTrue(toString.contains("ABC"));
		assertTrue(toString.contains("DEF"));
		assertTrue(toString.contains("GHI"));
		assertFalse(toString.contains("JKL"));
	}

	@Test
	public void testToString() {
		Node node = new Node();

		node.setId("ABC");
		node.putValue("DEF", "GHI");

		assertEquals("GHI", node.getValue("DEF"));

		Node childNode = new Node();
		childNode.setId("JKL");
		childNode.setParent(node);
		node.addChildNode(childNode);

		childNode = new Node();
		childNode.setId("PQD");
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
		Node node = new Node();
		Node parent = new Node("parent");

		node.setParent(parent);

		assertTrue(node.getParent().getId().equals("parent"));
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
		Node childOne = new Node("don't.find.me");
		Node childTwo = new Node("don't.find.me.either");
		Node childThree = new Node("find.me.please");
		parent.addChildNodes(childOne, childTwo, childThree);

		List<Node> results = parent.findNode("find.me.please");

		assertEquals("should find first child that has the searched id", results.size(), 1);
	}

	@Test
	public void testFindNoNode() {
		Node parent = new Node();
		Node childOne = new Node("don't.find.me");
		Node childTwo = new Node("don't.find.me.either");
		parent.addChildNodes(childOne, childTwo);

		List<Node> results = parent.findNode("find.me.please");

		assertTrue("should find first child that has the searched id", results.isEmpty());
	}

	@Test
	public void testFindNodeSelfIncluded() {
		Node parent = new Node("findMe");
		Node childOne = new Node("findMe");
		parent.addChildNode(childOne);

		List<Node> results = parent.findNode("findMe");

		assertEquals("should find itself if it has the searched id", results.size(), 2);
	}

	@Test
	public void testFindFirstNodeSelf() {
		Node parent = new Node("findMe");
		Node childOne = new Node("findMe");
		parent.addChildNode(childOne);

		assertEquals("should find itself if it has the searched id", parent.findFirstNode("findMe"), parent);
	}

	@Test
	public void testFindFirstNodeChildNode() {
		Node parent = new Node();
		Node childOne = new Node("don'tFindMe");
		Node childTwo = new Node("findMe");
		Node childThree = new Node("findMe");
		parent.addChildNodes(childOne, childTwo, childThree);

		assertEquals("should find first child that has the searched id", parent.findFirstNode("findMe"), childTwo);
	}

	@Test
	public void testFindFirstNoNode() {
		Node parent = new Node();
		Node childOne = new Node("don't.find.me");
		Node childTwo = new Node("don't.find.me");
		Node childThree = new Node("don't.find.me");
		parent.addChildNodes(childOne, childTwo, childThree);

		assertEquals("should not find a node that has the searched id", parent.findFirstNode("findMe"), null);
	}

	@Test
	public void testRemoveValue() {
		Node node = new Node();
		node.putValue("test", "hello");
		assertTrue(node.hasValue("test"));
		node.removeValue("test");
		assertFalse(node.hasValue("test"));
	}

	@Test
	public void toDebugStringTest() {
		Node root = new Node();
		root.putValue("name", "root");
		Node child1 = new Node(root, "child1");
		child1.putValue("name", "child1");
		Node child2 = new Node(root, "child2");
		child2.putValue("name", "child2");
		Node grandChild1 = new Node(child1, "grandChild1");
		grandChild1.putValue("name", "grandChild1");
		Node grandChild2 = new Node(child2, "grandChild2");
		grandChild2.putValue("name", "grandChild2");
		child1.addChildNode(grandChild1);
		child2.addChildNode(grandChild2);
		root.addChildNode(child1);
		root.addChildNode(child2);

		String expected = "Node: internalId: null , data: {name=root}\n" +
				"\tchildNodes of null : \n" +
				"\tNode: internalId: DEFAULT , data: {name=child1}\n" +
				"\t\tchildNodes of DEFAULT : \n" +
				"\t\tNode: internalId: DEFAULT , data: {name=grandChild1}\n" +
				"\t\t\tchildNodes of DEFAULT  -> (none)\n" +
				"\tNode: internalId: DEFAULT , data: {name=child2}\n" +
				"\t\tchildNodes of DEFAULT : \n" +
				"\t\tNode: internalId: DEFAULT , data: {name=grandChild2}\n" +
				"\t\t\tchildNodes of DEFAULT  -> (none)";

		String debugString = root.toDebugString();
		assertThat("Expect the node hierarchy debug string", debugString, is(expected));
	}
}
