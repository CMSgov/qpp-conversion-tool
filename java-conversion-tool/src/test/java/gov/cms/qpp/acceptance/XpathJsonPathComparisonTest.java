package gov.cms.qpp.acceptance;


import com.jayway.jsonpath.JsonPath;
import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.encode.QppOutputEncoder;
import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.xml.XmlException;
import gov.cms.qpp.conversion.xml.XmlUtils;
import org.jdom2.Element;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class XpathJsonPathComparisonTest {
	private static Converter converter;
	private static InputStream xmlStream;
	private static Path path = Paths.get("src/test/resources/valid-QRDA-III.xml");
	private static XPathFactory xpf = XPathFactory.instance();
	private static Node decoded;
	private static QppOutputEncoder encoder = new QppOutputEncoder();
	private static JsonWrapper wrapper = new JsonWrapper(false);

	@BeforeClass
	public static void setup() throws IOException {
		xmlStream = XmlUtils.fileToStream(path);
		converter = new Converter(xmlStream);
		converter.transform();
		decoded = converter.getDecoded();
		encoder.encode(wrapper, decoded);
	}

	@Test
	public void compareTopLevelPaths() throws XmlException, IOException {
		Map<String, Object> root = JsonPath.read(wrapper.toString(), "$");
		Element model = getXmlElement(root);

		assertEquals("", "ClinicalDocument", model.getName());
	}

	@Test
	public void compareSectionPath() throws XmlException, IOException {
		Map<String, Object> root = JsonPath.read(wrapper.toString(), "measurementSets[0]");
		Element model = getXmlElement(root);

		assertEquals("", "section", model.getName());
	}

	private Element getXmlElement(Map<String, Object> root) throws IOException, XmlException {
		XPathExpression<Element> xpath = xpf.compile(
				(String) root.get("metadata_path"),
				Filters.element());
		return xpath.evaluateFirst(
				XmlUtils.parseXmlStream(
						XmlUtils.fileToStream(path)));
	}
}
