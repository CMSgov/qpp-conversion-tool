package gov.cms.qpp.acceptance;


import com.jayway.jsonpath.JsonPath;
import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.correlation.PathCorrelator;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.encode.QppOutputEncoder;
import gov.cms.qpp.conversion.xml.XmlException;
import gov.cms.qpp.conversion.xml.XmlUtils;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.filter.Filter;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class XpathJsonPathComparisonTest {
	private static Path path = Paths.get("src/test/resources/valid-QRDA-III.xml");
	private static XPathFactory xpf = XPathFactory.instance();
	private static JsonWrapper wrapper = new JsonWrapper(false);

	@BeforeClass
	public static void setup() throws IOException {
		InputStream xmlStream = XmlUtils.fileToStream(path);
		Converter converter = new Converter(xmlStream);
		converter.transform();
		QppOutputEncoder encoder = new QppOutputEncoder();
		encoder.encode(wrapper, converter.getDecoded());
	}

	@Test
	public void compareTopLevelElement() throws XmlException, IOException {
		String xPath = prepPath("");
		Element element = evaluateXpath(xPath, Filters.element());

		assertEquals("Element name should be: ClinicalDocument",
				"ClinicalDocument", element.getName());
	}

	@Test
	public void compareTopLevelAttribute() throws XmlException, IOException {
		String xPath = prepPath("programName");
		Attribute attribute = evaluateXpath(xPath, Filters.attribute());

		assertEquals("Attribute name should be: extension", "extension", attribute.getName());
		assertEquals("Attribute value should be: MIPS", "MIPS", attribute.getValue());
	}

	@Test
	public void compareAciMeasurePerformedMeasureIdAciPea1() throws IOException, XmlException {
		String xPath = prepPath("measurementSets[2].measurements[0].measureId");
		Attribute attribute = evaluateXpath(xPath, Filters.attribute());

		assertEquals("Attribute name should be: extension", "extension", attribute.getName());
		assertEquals("Attribute value should be: MIPS", "ACI-PEA-1", attribute.getValue());
	}

	@Test
	public void compareAciMeasurePerformedMeasureIdAciEp1() throws IOException, XmlException {
		String xPath = prepPath("measurementSets[2].measurements[1].measureId");
		Attribute attribute = evaluateXpath(xPath, Filters.attribute());

		assertEquals("Attribute name should be: extension", "extension", attribute.getName());
		assertEquals("Attribute value should be: MIPS", "ACI_EP_1", attribute.getValue());
	}

	@Test
	public void compareAciMeasurePerformedMeasureIdAciCctpe3() throws IOException, XmlException {
		String xPath = prepPath("measurementSets[2].measurements[2].measureId");
		Attribute attribute = evaluateXpath(xPath, Filters.attribute());

		assertEquals("Attribute name should be: extension", "extension", attribute.getName());
		assertEquals("Attribute value should be: MIPS", "ACI_CCTPE_3", attribute.getValue());
	}

	private String prepPath(String jsonPath) {
		String base = "$";
		String leaf = jsonPath;
		int lastIndex = jsonPath.lastIndexOf(".");

		if (lastIndex > 0) {
			base = jsonPath.substring(0, lastIndex);
			leaf = jsonPath.substring(lastIndex + 1);
		}

		JsonPath compiledPath = JsonPath.compile(base);
		Map<String, Object> jsonMap = compiledPath.read(wrapper.toString());

		String nsUrl = (String) jsonMap.get("metadata_nsuri");
		String baseTemplate = (String) jsonMap.get("metadata_template");
		String baseXpath = (String) jsonMap.get("metadata_path");
		String relativeXpath = PathCorrelator.getXpath(baseTemplate, leaf, nsUrl);

		return (relativeXpath != null) ? baseXpath + "/" + relativeXpath : baseXpath;
	}

	@SuppressWarnings("unchecked")
	private <T> T evaluateXpath(String xPath, Filter filter) throws IOException, XmlException {
		XPathExpression<Attribute> xpath = xpf.compile(xPath, filter);
		return (T) xpath.evaluateFirst(XmlUtils.parseXmlStream(XmlUtils.fileToStream(path)));
	}
}
