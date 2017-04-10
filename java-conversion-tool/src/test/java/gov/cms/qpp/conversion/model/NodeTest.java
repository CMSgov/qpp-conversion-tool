package gov.cms.qpp.conversion.model;

import org.junit.Test;

import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
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
		assertFalse(nodeStr.contains("\tNode: templateId: ABC, data: {DEF=GHI}"));
		assertTrue(nodeStr.contains("Node: templateId: ABC, data: {DEF=GHI}"));
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

	@Test
	public void testFindFirstNodeSelf() {
		Node parent = new Node( "findMe" );
		Node childOne = new Node( "findMe" );
		parent.addChildNode( childOne );

		assertEquals("should find itself if it has the searched id", parent.findFirstNode("findMe"), parent);
	}

	@Test
	public void testFindFirstNodeChildNode() {
		Node parent = new Node();
		Node childOne = new Node( "don'tFindMe" );
		Node childTwo = new Node( "findMe" );
		Node childThree = new Node( "findMe" );
		parent.addChildNodes( childOne, childTwo, childThree );

		assertEquals("should find first child that has the searched id", parent.findFirstNode("findMe"), childTwo);
	}
}
