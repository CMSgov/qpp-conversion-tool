package gov.cms.qpp.conversion.decode;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import gov.cms.qpp.conversion.model.Node;

public class XmlFileDecoderTest {

	@Test
	public void testDecode() throws Exception {
		
		File testFile = new File("target/test-classes/test.xml");

		
		final Node testNode = new Node();
		
		QppXmlDecoder mockDecoder = new QppXmlDecoder() {
			public gov.cms.qpp.conversion.model.Node decode() {
				return testNode;
			};
		};
		
		XmlFileDecoder decoder = new XmlFileDecoder(testFile, mockDecoder);
		Node returnedNode = decoder.decode();
		
		assertEquals("decode should be called on the subdecoder", testNode, returnedNode);
		assertNotNull("decode should set the dom on the subdecoder", mockDecoder.xmlDoc);
	}
	
	@Test(expected=DecodeException.class)
	public void testDecode_Exception() throws Exception {
		QppXmlDecoder mockDecoder = new QppXmlDecoder() {
			public gov.cms.qpp.conversion.model.Node decode() {
				return null;
			};
		};
		XmlFileDecoder decoder = new XmlFileDecoder(new File(""), mockDecoder);
		decoder.decode();
		// this should throw Decoder exception from an XmlException
		
		fail("Should not make it here to fail");
	}

}
