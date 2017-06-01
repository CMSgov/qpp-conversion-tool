package gov.cms.qpp.acceptance;


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
		String xPath = PathCorrelator.prepPath("", wrapper);
		Element element = evaluateXpath(xPath, Filters.element());

		assertEquals("Element name should be: ClinicalDocument",
				"ClinicalDocument", element.getName());
	}

	@Test
	public void compareTopLevelAttributeProgramName() throws XmlException, IOException {
		String jsonPath = "programName";
		String xPath = PathCorrelator.prepPath(jsonPath, wrapper);
		Attribute attribute = evaluateXpath(xPath, Filters.attribute());

		assertEquals("Attribute name should be: extension", "extension", attribute.getName());
		assertEquals("Attribute value should be: MIPS", "MIPS", attribute.getValue());
	}

	@Test
	public void compareTopLevelAttributeTin() throws XmlException, IOException {
		String jsonPath = "taxpayerIdentificationNumber";
		String xPath = PathCorrelator.prepPath(jsonPath, wrapper);
		Attribute attribute = evaluateXpath(xPath, Filters.attribute());

		assertEquals("Attribute name should be: extension", "extension", attribute.getName());
		assertEquals("Attribute value should be: 123456789",
				"123456789", attribute.getValue());
	}

	@Test
	public void compareTopLevelAttributeNpi() throws XmlException, IOException {
		String xPath = PathCorrelator.prepPath("nationalProviderIdentifier", wrapper);
		Attribute attribute = evaluateXpath(xPath, Filters.attribute());

		assertEquals("Attribute name should be: extension", "extension", attribute.getName());
		assertEquals("Attribute value should be: 2567891421",
				"2567891421", attribute.getValue());
	}

	@Test
	public void compareTopLevelAttributePerformanceYear() throws XmlException, IOException {
		String xPath = PathCorrelator.prepPath("performanceYear", wrapper);
		Attribute attribute = evaluateXpath(xPath, Filters.attribute());

		assertEquals("Attribute name should be: value", "value", attribute.getName());
		assertEquals("Attribute value should be: 20170101", "20170101", attribute.getValue());
	}

	@Test
	public void compareTopLevelAttributePerformanceStart() throws XmlException, IOException {
		String xPath = PathCorrelator.prepPath("performanceStart", wrapper);
		Attribute attribute = evaluateXpath(xPath, Filters.attribute());

		assertEquals("Attribute name should be: value", "value", attribute.getName());
		assertEquals("Attribute value should be: 20170101", "20170101", attribute.getValue());
	}

	@Test
	public void compareTopLevelAttributePerformanceEnd() throws XmlException, IOException {
		String xPath = PathCorrelator.prepPath("performanceEnd", wrapper);
		Attribute attribute = evaluateXpath(xPath, Filters.attribute());

		assertEquals("Attribute name should be: value", "value", attribute.getName());
		assertEquals("Attribute value should be: 20171231", "20171231", attribute.getValue());
	}

	//ACI
	@Test
	public void compareAciMeasurePerformedMeasureIdAciPea1() throws IOException, XmlException {
		String jsonPath = "measurementSets[2].measurements[0].measureId";
		String xPath = PathCorrelator.prepPath(jsonPath, wrapper);
		Attribute attribute = evaluateXpath(xPath, Filters.attribute());

		assertEquals("Attribute name should be: extension", "extension", attribute.getName());
		assertEquals("Attribute value should be: MIPS", "ACI-PEA-1", attribute.getValue());
	}

	@Test
	public void compareAciMeasurePerformedMeasureIdAciEp1() throws IOException, XmlException {
		String xPath = PathCorrelator.prepPath("measurementSets[2].measurements[1].measureId", wrapper);
		Attribute attribute = evaluateXpath(xPath, Filters.attribute());

		assertEquals("Attribute name should be: extension", "extension", attribute.getName());
		assertEquals("Attribute value should be: MIPS", "ACI_EP_1", attribute.getValue());
	}

	@Test
	public void compareAciMeasurePerformedMeasureIdAciCctpe3() throws IOException, XmlException {
		String xPath = PathCorrelator.prepPath("measurementSets[2].measurements[2].measureId", wrapper);
		Attribute attribute = evaluateXpath(xPath, Filters.attribute());

		assertEquals("Attribute name should be: extension", "extension", attribute.getName());
		assertEquals("Attribute value should be: MIPS", "ACI_CCTPE_3", attribute.getValue());
	}

	@Test
	public void compareAciMeasurePerformedMeasureIdAciPea1Numerator() throws IOException, XmlException {
		String xPath = PathCorrelator
				.prepPath("measurementSets[2].measurements[0].value.numerator", wrapper);
		Attribute attribute = evaluateXpath(xPath, Filters.attribute());

		assertEquals("Attribute name should be: value", "value", attribute.getName());
		assertEquals("Attribute value should be: 600", "600", attribute.getValue());
	}

	@Test
	public void compareAciMeasurePerformedMeasureIdAciPea1Denominator() throws IOException, XmlException {
		String xPath = PathCorrelator
				.prepPath("measurementSets[2].measurements[0].value.denominator", wrapper);
		Attribute attribute = evaluateXpath(xPath, Filters.attribute());

		assertEquals("Attribute name should be: value", "value", attribute.getName());
		assertEquals("Attribute value should be: 600", "800", attribute.getValue());
	}

	//IA
	@Test
	public void compareIaMeasurePerformedMeasureIdIaEpa1Value() throws IOException, XmlException {
		String xPath = PathCorrelator.prepPath("measurementSets[3].measurements[0].value", wrapper);
		Attribute attribute = evaluateXpath(xPath, Filters.attribute());

		assertEquals("Attribute name should be: code", "code", attribute.getName());
		assertEquals("Attribute value should be: Y", "Y", attribute.getValue());
	}

	@Test
	public void compareIaMeasurePerformedMeasureIdIaEpa1() throws IOException, XmlException {
		String xPath = PathCorrelator
				.prepPath("measurementSets[3].measurements[0].measureId", wrapper);
		Attribute attribute = evaluateXpath(xPath, Filters.attribute());

		assertEquals("Attribute name should be: extension", "extension", attribute.getName());
		assertEquals("Attribute value should be: IA_EPA_1", "IA_EPA_1", attribute.getValue());
	}

	//Quality measure
	@Test
	public void compareQualityMeasureIdValuePerformanceNotMet() throws IOException, XmlException {
		String jsonPath = "measurementSets[1].measurements[0].value.performanceNotMet";
		String xPath = PathCorrelator.prepPath(jsonPath, wrapper);
		Attribute attribute = evaluateXpath(xPath, Filters.attribute());

		assertEquals("Attribute name should be: value", "value", attribute.getName());
		assertEquals("Attribute value should be: 50", "50", attribute.getValue());
	}

	@Test
	public void compareQualityMeasureIdValuePerformanceExclusion() throws IOException, XmlException {
		String jsonPath = "measurementSets[1].measurements[0].value.performanceExclusion";
		String xPath = PathCorrelator.prepPath(jsonPath, wrapper);
		Attribute attribute = evaluateXpath(xPath, Filters.attribute());

		assertEquals("Attribute name should be: value", "value", attribute.getName());
		assertEquals("Attribute value should be: 50", "50", attribute.getValue());
	}

	@Test
	public void compareQualityMeasureIdValuePerformanceMet() throws IOException, XmlException {
		String xPath = PathCorrelator
				.prepPath("measurementSets[1].measurements[0].value.performanceMet", wrapper);
		Attribute attribute = evaluateXpath(xPath, Filters.attribute());

		assertEquals("Attribute name should be: value", "value", attribute.getName());
		assertEquals("Attribute value should be: 800", "800", attribute.getValue());
	}

	@Test
	public void compareQualityMeasureIdValueNumerator() throws IOException, XmlException {
		String xPath = PathCorrelator
				.prepPath("measurementSets[1].measurements[0].value.numerator", wrapper);
		Attribute attribute = evaluateXpath(xPath, Filters.attribute());

		assertEquals("Attribute name should be: value", "value", attribute.getName());
		assertEquals("Attribute value should be: 800", "800", attribute.getValue());
	}

	@Test
	public void compareQualityMeasureIdValueDenominator() throws IOException, XmlException {
		String xPath = PathCorrelator
				.prepPath("measurementSets[1].measurements[0].value.denominator", wrapper);
		Attribute attribute = evaluateXpath(xPath, Filters.attribute());

		assertEquals("Attribute name should be: value", "value", attribute.getName());
		assertEquals("Attribute value should be: 1000", "1000", attribute.getValue());
	}
	@Test
	public void compareQualityMeasureIdValuePopulationTotal() throws IOException, XmlException {
		String xPath = PathCorrelator
				.prepPath("measurementSets[1].measurements[0].value.populationTotal", wrapper);
		Attribute attribute = evaluateXpath(xPath, Filters.attribute());

		assertEquals("Attribute name should be: value", "value", attribute.getName());
		assertEquals("Attribute value should be: 1000", "1000", attribute.getValue());
	}

	@Test
	public void compareQualityMeasureIdValueInitialPopulation() throws IOException, XmlException {
		String xPath = PathCorrelator
				.prepPath("measurementSets[1].measurements[0].value.initialPopulation", wrapper);
		Attribute attribute = evaluateXpath(xPath, Filters.attribute());

		assertEquals("Attribute name should be: value", "value", attribute.getName());
		assertEquals("Attribute value should be: 1000", "1000", attribute.getValue());
	}

	@Test
	public void compareQualityMeasureIdValueDenominatorExclusions() throws IOException, XmlException {
		String xPath = PathCorrelator
				.prepPath("measurementSets[1].measurements[0].value.denominatorExclusions", wrapper);
		Attribute attribute = evaluateXpath(xPath, Filters.attribute());

		assertEquals("Attribute name should be: value", "value", attribute.getName());
		assertEquals("Attribute value should be: 50", "50", attribute.getValue());
	}

	@SuppressWarnings("unchecked")
	private <T> T evaluateXpath(String xPath, Filter filter) throws IOException, XmlException {
		XPathExpression<Attribute> xpath = xpf.compile(xPath, filter);
		return (T) xpath.evaluateFirst(XmlUtils.parseXmlStream(XmlUtils.fileToStream(path)));
	}
}
