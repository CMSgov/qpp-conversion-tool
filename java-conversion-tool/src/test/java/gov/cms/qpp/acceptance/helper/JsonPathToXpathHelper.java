package gov.cms.qpp.acceptance.helper;

import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.PathQrdaSource;
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

import java.io.IOException;
import java.nio.file.Path;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JsonPathToXpathHelper {

	private static XPathFactory xpf = XPathFactory.instance();
	private Path path;
	private JsonWrapper wrapper;

	public JsonPathToXpathHelper(Path inPath, JsonWrapper inWrapper) throws IOException {
		this(inPath, inWrapper, true);
	}

	public JsonPathToXpathHelper(Path inPath, JsonWrapper inWrapper, boolean doDefaults) throws IOException {
		path = inPath;
		wrapper = inWrapper;
		Converter converter = new Converter(new PathQrdaSource(inPath)).doDefaults(doDefaults);
		converter.transform();
		QppOutputEncoder encoder = new QppOutputEncoder();
		encoder.encode(wrapper, converter.getDecoded());
	}

	public void executeElementTest(String jsonPath, String xmlElementName)
			throws IOException, XmlException {
		String xPath = PathCorrelator.prepPath(jsonPath, wrapper);
		Element element = evaluateXpath(xPath, Filters.element());

		assertEquals("Element name should be: " + xmlElementName,
				xmlElementName, element.getName());
	}

	public void executeAttributeTest(String jsonPath, String expectedValue) {
		String xPath = PathCorrelator.prepPath(jsonPath, wrapper);

		Attribute attribute = null;
		try {
			attribute = evaluateXpath(xPath, Filters.attribute());
		} catch (IOException | XmlException e) {
			fail(e.getMessage());
		}

		if (attribute == null) {
			System.out.println("no attribute for path: " + jsonPath
					+ "\n xpath: " + xPath);
		}

		if (!expectedValue.equals(attribute.getValue())) {
			System.err.println("( " + jsonPath + " ) value ( " + expectedValue +
					" ) does not equal ( " + attribute.getValue() +
					" ) at \n( " + xPath + " ). \nPlease investigate.");
		}

		assertNotNull("Attribute value should not be null. json value:" + expectedValue,
				attribute.getValue());
	}

	public void executeAttributeTest(String jsonPath, String xmlAttributeName, String expectedValue)
			throws IOException, XmlException {
		String xPath = PathCorrelator.prepPath(jsonPath, wrapper);
		Attribute attribute = evaluateXpath(xPath, Filters.attribute());

		assertEquals("Attribute name should be: " + xmlAttributeName,
				xmlAttributeName, attribute.getName());
		assertEquals("Attribute value should be: " + expectedValue,
				expectedValue, attribute.getValue());
	}

	@SuppressWarnings("unchecked")
	public <T> T evaluateXpath(String xPath, Filter filter) throws IOException, XmlException {
		XPathExpression<Attribute> xpath = xpf.compile(xPath, filter);
		return (T) xpath.evaluateFirst(XmlUtils.parseXmlStream(XmlUtils.fileToStream(path)));
	}
}
