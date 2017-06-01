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
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class XpathJsonPathComparisonTest {
	private static Path path = Paths.get("../qrda-files/valid-QRDA-III.xml");
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
	public void compareTopLevelAttributeProgramName() throws XmlException, IOException {
		String jsonPath = "programName";
		String xPath = prepPath(jsonPath);
		Attribute attribute = evaluateXpath(xPath, Filters.attribute());

		assertEquals("Attribute name should be: extension", "extension", attribute.getName());
		assertEquals("Attribute value should be: MIPS", "MIPS", attribute.getValue());
	}

	@Test
	public void compareTopLevelAttributeTin() throws XmlException, IOException {
		String jsonPath = "taxpayerIdentificationNumber";
		String xPath = prepPath(jsonPath);
		Attribute attribute = evaluateXpath(xPath, Filters.attribute());

		assertEquals("Attribute name should be: extension", "extension", attribute.getName());
		assertEquals("Attribute value should be: 123456789",
				"123456789", attribute.getValue());
	}

	@Test
	public void compareTopLevelAttributeNpi() throws XmlException, IOException {
		String xPath = prepPath("nationalProviderIdentifier");
		Attribute attribute = evaluateXpath(xPath, Filters.attribute());

		assertEquals("Attribute name should be: extension", "extension", attribute.getName());
		assertEquals("Attribute value should be: 2567891421",
				"2567891421", attribute.getValue());
	}

	@Test
	public void compareTopLevelAttributePerformanceYear() throws XmlException, IOException {
		String xPath = prepPath("performanceYear");
		Attribute attribute = evaluateXpath(xPath, Filters.attribute());

		assertEquals("Attribute name should be: value", "value", attribute.getName());
		assertEquals("Attribute value should be: 20170101", "20170101", attribute.getValue());
	}

	@Test
	public void compareTopLevelAttributePerformanceStart() throws XmlException, IOException {
		String xPath = prepPath("performanceStart");
		Attribute attribute = evaluateXpath(xPath, Filters.attribute());

		assertEquals("Attribute name should be: value", "value", attribute.getName());
		assertEquals("Attribute value should be: 20170101", "20170101", attribute.getValue());
	}

	@Test
	public void compareTopLevelAttributePerformanceEnd() throws XmlException, IOException {
		String xPath = prepPath("performanceEnd");
		Attribute attribute = evaluateXpath(xPath, Filters.attribute());

		assertEquals("Attribute name should be: value", "value", attribute.getName());
		assertEquals("Attribute value should be: 20171231", "20171231", attribute.getValue());
	}

	//ACI
	@Test
	public void compareAciMeasurePerformedMeasureIdAciPea1() throws IOException, XmlException {
		String jsonPath = "measurementSets[2].measurements[0].measureId";
		String xPath = prepPath(jsonPath);
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

	@Test
	public void compareAciMeasurePerformedMeasureIdAciPea1Denominator() throws IOException, XmlException {
		String xPath = prepPath("measurementSets[2].measurements[0].value.denominator");
		Attribute attribute = evaluateXpath(xPath, Filters.attribute());

		assertEquals("Attribute name should be: value", "value", attribute.getName());
		assertEquals("Attribute value should be: 600", "800", attribute.getValue());
	}

	//IA
	@Test
	public void compareIaMeasurePerformedMeasureIdIaEpa1Value() throws IOException, XmlException {
		String xPath = prepPath("measurementSets[3].measurements[0].value");
		Attribute attribute = evaluateXpath(xPath, Filters.attribute());

		assertEquals("Attribute name should be: code", "code", attribute.getName());
		assertEquals("Attribute value should be: Y", "Y", attribute.getValue());
	}

	@Test
	public void compareIaMeasurePerformedMeasureIdIaEpa1() throws IOException, XmlException {
		String xPath = prepPath("measurementSets[3].measurements[0].measureId");
		Attribute attribute = evaluateXpath(xPath, Filters.attribute());

		assertEquals("Attribute name should be: extension", "extension", attribute.getName());
		assertEquals("Attribute value should be: IA_EPA_1", "IA_EPA_1", attribute.getValue());
	}

	//Quality measure
	@Test
	public void compareQualityMeasureIdValuePerformanceNotMet() throws IOException, XmlException {
		String jsonPath = "measurementSets[1].measurements[0].value.performanceNotMet";
		String xPath = prepPath(jsonPath);
		Attribute attribute = evaluateXpath(xPath, Filters.attribute());

		assertEquals("Attribute name should be: value", "value", attribute.getName());
		assertEquals("Attribute value should be: 50", "50", attribute.getValue());
	}

	@Test
	public void compareQualityMeasureIdValuePerformanceExclusion() throws IOException, XmlException {
		String jsonPath = "measurementSets[1].measurements[0].value.performanceExclusion";
		String xPath = prepPath(jsonPath);
		Attribute attribute = evaluateXpath(xPath, Filters.attribute());

		assertEquals("Attribute name should be: value", "value", attribute.getName());
		assertEquals("Attribute value should be: 50", "50", attribute.getValue());
	}

	@Test
	public void compareQualityMeasureIdValuePerformanceMet() throws IOException, XmlException {
		String xPath = prepPath("measurementSets[1].measurements[0].value.performanceMet");
		Attribute attribute = evaluateXpath(xPath, Filters.attribute());

		assertEquals("Attribute name should be: value", "value", attribute.getName());
		assertEquals("Attribute value should be: 800", "800", attribute.getValue());
	}

	@Test
	public void compareQualityMeasureIdValueNumerator() throws IOException, XmlException {
		String xPath = prepPath("measurementSets[1].measurements[0].value.numerator");
		Attribute attribute = evaluateXpath(xPath, Filters.attribute());

		assertEquals("Attribute name should be: value", "value", attribute.getName());
		assertEquals("Attribute value should be: 800", "800", attribute.getValue());
	}

	@Test
	public void compareQualityMeasureIdValueDenominator() throws IOException, XmlException {
		String xPath = prepPath("measurementSets[1].measurements[0].value.denominator");
		Attribute attribute = evaluateXpath(xPath, Filters.attribute());

		assertEquals("Attribute name should be: value", "value", attribute.getName());
		assertEquals("Attribute value should be: 1000", "1000", attribute.getValue());
	}
	@Test
	public void compareQualityMeasureIdValueEligiblePopulation() throws IOException, XmlException {
		String xPath = prepPath("measurementSets[1].measurements[0].value.eligiblePopulation");
		Attribute attribute = evaluateXpath(xPath, Filters.attribute());
		assertEquals("Attribute name for EligiblePopulation should be: value", "value", attribute.getName());
		assertEquals("Attribute value should be: 1000", "1000", attribute.getValue());
	}

	@Test
	public void compareQualityMeasureIdValueDenominatorExclusions() throws IOException, XmlException {
		String xPath = prepPath("measurementSets[1].measurements[0].value.denominatorExclusions");
		Attribute attribute = evaluateXpath(xPath, Filters.attribute());

		assertEquals("Attribute name should be: value", "value", attribute.getName());
		assertEquals("Attribute value should be: 50", "50", attribute.getValue());
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
		Map<String, String> metaMap = getMetaMap(jsonMap, leaf);
		return makePath(metaMap, leaf);
	}

	@SuppressWarnings("unchecked")
	private Map<String, String> getMetaMap(Map<String, Object> jsonMap, final String leaf) {
		List<Map<String, String>> metaHolder = (List<Map<String, String>>) jsonMap.get("metadata_holder");
		Stream<Map<String, String>> sorted = metaHolder.stream()
					.sorted(labeledFirst());
		return sorted.filter(entry -> {
			if (entry.get("encodeLabel").equals(leaf)) {
				return leaf.isEmpty() ||
						PathCorrelator.getXpath(entry.get("template"), leaf, entry.get("nsuri")) != null;
			} else {
				return entry.get("encodeLabel").isEmpty();
			}
		}).findFirst().orElse(null);
	}

	private Comparator<Map<String, String>> labeledFirst() {
		return (Map<String, String> map1, Map<String, String> map2) -> {
			String map1Label = map1.get("encodeLabel");
			String map2Label = map2.get("encodeLabel");
			int reply;
			if ((!map1Label.isEmpty() && !map2Label.isEmpty()) ||
					(map1Label.isEmpty() && map2Label.isEmpty())) {
				reply = 0;
			} else if (map1Label.isEmpty()) {
				reply = 1;
			} else {
				reply = -1;
			}
			return reply;
		};
	}

	private String makePath(Map<String, String> metadata, final String leaf) {
		String nsUri = metadata.get("nsuri");
		String baseTemplate = metadata.get("template");
		String baseXpath = metadata.get("path");
		String relativeXpath = PathCorrelator.getXpath(baseTemplate, leaf, nsUri);
		return (relativeXpath != null) ? baseXpath + "/" + relativeXpath : baseXpath;
	}

	@SuppressWarnings("unchecked")
	private <T> T evaluateXpath(String xPath, Filter filter) throws IOException, XmlException {
		XPathExpression<Attribute> xpath = xpf.compile(xPath, filter);
		return (T) xpath.evaluateFirst(XmlUtils.parseXmlStream(XmlUtils.fileToStream(path)));
	}
}
