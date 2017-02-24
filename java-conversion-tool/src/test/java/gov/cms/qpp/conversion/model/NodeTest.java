package gov.cms.qpp.conversion.model;

import static org.junit.Assert.*;

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
		// 		return "Node: internalId: " + internalId + ", data: " + data + ", childNodes: " + childNodes;

	}

}
