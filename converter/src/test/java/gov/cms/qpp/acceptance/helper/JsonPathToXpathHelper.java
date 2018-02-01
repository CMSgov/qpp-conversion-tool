package gov.cms.qpp.acceptance.helper;

import com.google.common.collect.Sets;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.filter.Filter;
import org.jdom2.filter.Filters;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.PathSource;
import gov.cms.qpp.conversion.correlation.PathCorrelator;
import gov.cms.qpp.conversion.encode.JsonWrapper;
import gov.cms.qpp.conversion.encode.QppOutputEncoder;
import gov.cms.qpp.conversion.xml.XmlException;
import gov.cms.qpp.conversion.xml.XmlUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static junit.framework.TestCase.fail;

public class JsonPathToXpathHelper {

	private static final Set<String> CHECK_FOR_NULL_SET = Sets
		.newHashSet("entityType", "performanceYear", "performanceNotMet", "performanceStart", "performanceEnd", "programName", "measureId", "stratum", "value")
		.stream().map(ignored -> ".*" + ignored + "$").collect(Collectors.toCollection(HashSet::new));
	private static XPathFactory xpf = XPathFactory.instance();
	private Path path;
	private JsonWrapper wrapper;

	public JsonPathToXpathHelper(Path inPath, JsonWrapper inWrapper) {
		this(inPath, inWrapper, true);
	}

	public JsonPathToXpathHelper(Path inPath, JsonWrapper inWrapper, boolean doDefaults) {
		path = inPath;
		wrapper = inWrapper;
		Converter converter = new Converter(new PathSource(inPath));
		converter.getContext().setDoDefaults(doDefaults);
		converter.transform();
		QppOutputEncoder encoder = new QppOutputEncoder(converter.getContext());
		encoder.encode(wrapper, converter.getReport().getDecoded());
	}

	public void executeElementTest(String jsonPath, String xmlElementName)
			throws IOException, XmlException {
		String xPath = PathCorrelator.prepPath(jsonPath, wrapper);
		Element element = evaluateXpath(xPath, Filters.element());

		assertThat(xmlElementName).isEqualTo(element.getName());
	}

	public void executeAttributeTest(String jsonPath, String expectedValue) {
		String xPath = PathCorrelator.prepPath(jsonPath, wrapper);

		Attribute attribute = null;
		try {
			attribute = evaluateXpath(xPath, Filters.attribute());
		} catch (IOException | XmlException e) {
			fail(e.getMessage());
		}

		if (CHECK_FOR_NULL_SET.stream().anyMatch(jsonPath::matches)) {
			assertThat(attribute.getValue()).isNotNull();
		} else {
			assertWithMessage("( %s ) value ( %s ) does not equal ( %s ) at \n( %s ). \nPlease investigate.", jsonPath, expectedValue, attribute.getValue(), xPath)
				.that(attribute.getValue()).isEqualTo(expectedValue);
		}
	}

	public void executeAttributeTest(String jsonPath, String xmlAttributeName, String expectedValue)
			throws IOException, XmlException {
		String xPath = PathCorrelator.prepPath(jsonPath, wrapper);
		Attribute attribute = evaluateXpath(xPath, Filters.attribute());

		assertThat(attribute.getName())
				.isEqualTo(xmlAttributeName);
		assertThat(attribute.getValue())
				.isEqualTo(expectedValue);
	}

	@SuppressWarnings("unchecked")
	private <T> T evaluateXpath(String xPath, Filter filter) throws IOException, XmlException {
		XPathExpression<Attribute> xpath = xpf.compile(xPath, filter);
		return (T) xpath.evaluateFirst(XmlUtils.parseXmlStream(XmlUtils.fileToStream(path)));
	}
}
