package gov.cms.qpp.conversion.xml;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;

import org.jdom2.Element;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Regression test for XXE exploits. 
 */
class DocumentFunctionDisabledTest {

	@TempDir
	Path tempDir;

	@Test
	void documentFunctionIsDisabled() throws Exception {
		Path target = tempDir.resolve("target.xml");
		Files.writeString(target, "<secret>must-not-be-read</secret>");

		// Loading XmlUtils installs the security guard, mirroring production startup.
		Element context = XmlUtils.stringToDom("<ClinicalDocument xmlns=\"urn:synthetic-test:v0\"/>");

		XPathExpression<?> expression = XPathFactory.instance()
				.compile("document('" + target.toUri() + "')");

		IllegalStateException boom = assertThrows(IllegalStateException.class,
				() -> expression.evaluate(context));
		assertThat(boom).hasMessageThat().contains("Unable to evaluate");
		assertThat(boom).hasCauseThat().hasMessageThat().contains("document()");
	}

	@Test
	void coreXPathFunctionsStillWork() {
		Element context = XmlUtils.stringToDom("<ClinicalDocument xmlns=\"urn:synthetic-test:v0\"/>");
		XPathExpression<?> expression = XPathFactory.instance().compile("string-length('abc')");
		assertThat(expression.evaluate(context).get(0)).isEqualTo(3.0);
	}
}
