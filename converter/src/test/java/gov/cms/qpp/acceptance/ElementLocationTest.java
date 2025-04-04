package gov.cms.qpp.acceptance;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.BiPredicate;

import org.jdom2.Element;
import org.jdom2.located.Located;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.junit.jupiter.api.Test;

import com.google.common.truth.Truth;

import gov.cms.qpp.conversion.Context;
import gov.cms.qpp.conversion.decode.QrdaDecoderEngine;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.xml.XmlUtils;

class ElementLocationTest {

	@Test
	void testGetLineNumber() throws IOException {
		runTest((child, node) -> child.getLine() == node.getLine());
	}

	@Test
	void testGetColumnNumber() throws IOException {
		runTest((child, node) -> child.getColumn() == node.getColumn());
	}

	private void runTest(BiPredicate<Located, Node> elementFinder) throws IOException {
		Path qrdaPath = Path.of("../qrda-files/valid-QRDA-III-latest.xml");
		String qrda = new String(Files.readAllBytes(qrdaPath), StandardCharsets.UTF_8);

		Element document = XmlUtils.stringToDom(qrda);
		Node someChildNode = new QrdaDecoderEngine(new Context()).decode(document).getChildNodes().get(2);
		XPathExpression<?> xpathLocationOfSomeChildNode = XPathFactory.instance().compile(someChildNode.getOrComputePath());
		Element element = (Element) xpathLocationOfSomeChildNode.evaluate(document).get(0);
		Object elementOfNode = element.getChildren()
			.stream()
			.map(Located.class::cast)
			.filter(child -> elementFinder.test(child, someChildNode))
			.findAny()
			.orElse(null);

		Truth.assertWithMessage("Could not find a an element for node " + someChildNode).that(elementOfNode).isNotNull();
	}

}
