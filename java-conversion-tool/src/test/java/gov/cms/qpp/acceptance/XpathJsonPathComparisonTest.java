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
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

	@Test
	public void compareAciMeasurePerformedMeasureIdAciPea1Numerator() throws IOException, XmlException {
		String xPath = prepPath("measurementSets[2].measurements[0].value.numerator");
		Attribute attribute = evaluateXpath(xPath, Filters.attribute());

		assertEquals("Attribute name should be: value", "value", attribute.getName());
		assertEquals("Attribute value should be: 600", "600", attribute.getValue());
	}

	@SuppressWarnings("unchecked")
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

		final String attribute = leaf;
		List<Map<String, String>> metaHolder = (List<Map<String, String>>) jsonMap.get("metadata_holder");
		if (metaHolder.size() > 1) {
			Optional<Map<String, String>> blah = metaHolder.stream().filter(entry -> {
				String xPath = PathCorrelator.getXpath(entry.get("template"), attribute, entry.get("nsuri"));
				return xPath != null;
			}).findFirst();
			if (blah.isPresent()) {
				Map<String, String> metadata = blah.get();
				String nsUri = metadata.get("nsuri");
				String baseTemplate = metadata.get("template");
				String baseXpath = metadata.get("path");
				String relativeXpath = PathCorrelator.getXpath(baseTemplate, attribute, nsUri);
				return (relativeXpath != null) ? baseXpath + "/" + relativeXpath : baseXpath;
			}
			throw new RuntimeException("no path available");
		} else {
			Map<String, String> metadata = metaHolder.get(0);
			String nsUri = metadata.get("nsuri");
			String baseTemplate = metadata.get("template");
			String baseXpath = metadata.get("path");
			String relativeXpath = PathCorrelator.getXpath(baseTemplate, attribute, nsUri);
			return (relativeXpath != null) ? baseXpath + "/" + relativeXpath : baseXpath;
		}
	}

	@SuppressWarnings("unchecked")
	private <T> T evaluateXpath(String xPath, Filter filter) throws IOException, XmlException {
		XPathExpression<Attribute> xpath = xpf.compile(xPath, filter);
		return (T) xpath.evaluateFirst(XmlUtils.parseXmlStream(XmlUtils.fileToStream(path)));
	}
}
