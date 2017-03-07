package gov.cms.qpp.conversion.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

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
		assertTrue(toString.contains("JKL"));
	}

	@Test
	public void testToString() {

		Node node = new Node();

		node.setId("ABC");
		node.putValue("DEF", "GHI");

		assertEquals("GHI", node.getValue("DEF"));

		Node childNode = new Node();
		childNode.setId("JKL");
		node.addChildNode(childNode);

		Node subChild = new Node();
		subChild.setId("I");
		childNode.addChildNode(subChild);
		subChild = new Node();
		subChild.setId("II");
		childNode.addChildNode(subChild);

		childNode = new Node();
		childNode.setId("PQD");
		node.addChildNode(childNode);

		String nodeStr = node.toString();
		// This is to test the visual nature of the Node.toString();

		// test a few rows of node string
		assertFalse(nodeStr.contains("\tNode: internalId: ABC, data: {DEF=GHI}"));
		assertTrue(nodeStr.contains("Node: internalId: ABC, data: {DEF=GHI}"));
		assertTrue(nodeStr.contains("\t\tchildNodes of JKL:"));
		assertTrue(nodeStr.contains("\t\t\tchildNodes of II -> (none)"));
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
}
