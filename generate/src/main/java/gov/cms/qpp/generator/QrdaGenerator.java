package gov.cms.qpp.generator;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import gov.cms.qpp.conversion.model.validation.MeasureConfig;
import gov.cms.qpp.conversion.model.validation.MeasureConfigs;
import gov.cms.qpp.conversion.model.validation.SubPopulationLabel;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Generates QRDA XML files by feeding measure configurations into Mustache templates.
 */
public final class QrdaGenerator {
	private static final List<MeasureConfig> measureConfigs = MeasureConfigs.getMeasureConfigs();

	private final Mustache submission;
	private final Mustache subpopulation;
	private final Mustache performanceRate;

	private final List<MeasureConfig> quality;
	private final List<MeasureConfig> aci;
	private final List<MeasureConfig> ia;

	public static void main(String... args) throws IOException,
			TransformerException, SAXException, ParserConfigurationException,
			XPathExpressionException {
		QrdaGenerator generator = new QrdaGenerator();
		generator.generate();
	}

	private QrdaGenerator() {
		MustacheFactory mf = new DefaultMustacheFactory();
		submission = mf.compile("submission-template.xml");
		subpopulation = mf.compile("subpopulation-template.xml");
		performanceRate = mf.compile("performance-rate-template.xml");

		// Because this class is final, these private methods cannot be overridden.
		quality = filterQualityMeasures();
		aci = filterAciMeasures();
		ia = filterIaMeasures();
	}

	private List<MeasureConfig> filterQualityMeasures() {
		return measureConfigs.stream()
				.filter(measureConfig ->
						"quality".equals(measureConfig.getCategory())
								&& measureConfig.getElectronicMeasureId() != null
								&& !measureConfig.getElectronicMeasureId().isEmpty())
				.collect(Collectors.toList());
	}

	private List<MeasureConfig> filterAciMeasures() {
		return measureConfigs.stream()
				.filter(measureConfig -> "aci".equals(measureConfig.getCategory()))
				.collect(Collectors.toList());
	}

	private List<MeasureConfig> filterIaMeasures() {
		return measureConfigs.stream()
				.filter(measureConfig -> "ia".equals(measureConfig.getCategory()))
				.collect(Collectors.toList());
	}

	/**
	 * Kick off the Mustache rendering and then pretty-print the resulting XML.
	 *
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws TransformerException
	 * @throws XPathExpressionException
	 */
	private void generate() throws IOException, ParserConfigurationException,
			SAXException, TransformerException, XPathExpressionException {
		StringWriter writer = new StringWriter();
		submission.execute(writer, new Context(quality, aci, ia)).flush();
		prettyPrint(writer);
	}

	/**
	 * Parses the raw Mustache output, strips empty text nodes, and writes an indented XML file.
	 *
	 * @param writer the StringWriter containing the raw Mustache output
	 * @throws TransformerException
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws XPathExpressionException
	 */
	private void prettyPrint(StringWriter writer) throws TransformerException, IOException,
			SAXException, ParserConfigurationException, XPathExpressionException {

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = dbFactory.newDocumentBuilder();

		byte[] xmlBytes = writer.toString().getBytes(StandardCharsets.UTF_8);
		try (InputStreamReader isr = new InputStreamReader(
				new ByteArrayInputStream(xmlBytes), StandardCharsets.UTF_8)) {

			InputSource is = new InputSource(isr);
			Document original = documentBuilder.parse(is);
			removeWhitespace(original);

			Transformer transformer = moreThanMeetsTheEye();
			transformer.transform(new DOMSource(original), getDestination());
		}
	}

	/**
	 * Removes purely whitespace text nodes from the DOM.
	 *
	 * @param document the parsed XML Document
	 * @throws XPathExpressionException
	 */
	private void removeWhitespace(Document document) throws XPathExpressionException {
		document.normalize();
		XPath xpath = XPathFactory.newInstance().newXPath();
		NodeList nodeList = (NodeList) xpath.evaluate(
				"//text()[normalize-space()='']", document, XPathConstants.NODESET);

		for (int i = 0; i < nodeList.getLength(); ++i) {
			Node node = nodeList.item(i);
			node.getParentNode().removeChild(node);
		}
	}

	/**
	 * Configures an XML Transformer for indentation using UTF-8 encoding.
	 *
	 * @return a Transformer that will output indented XML
	 * @throws TransformerConfigurationException
	 */
	private Transformer moreThanMeetsTheEye() throws TransformerConfigurationException {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		return transformer;
	}

	/**
	 * Creates the output directory (if necessary) and opens a BufferedWriter to a new XML file.
	 *
	 * @return a StreamResult wrapping a BufferedWriter in UTF-8
	 * @throws IOException
	 */
	private Result getDestination() throws IOException {
		Instant instant = Instant.now();
		Path dir = Path.of("./sample-files/generated" + instant.getEpochSecond());
		Files.createDirectories(dir);
		BufferedWriter writer = Files.newBufferedWriter(dir.resolve("output.xml"), StandardCharsets.UTF_8);
		return new StreamResult(writer);
	}

	/**
	 * Inner context class whose fields are only accessed by Mustache at runtime.
	 * SpotBugs cannot see reflective usage, so we suppress URF here.
	 */
	@edu.umd.cs.findbugs.annotations.SuppressFBWarnings("URF_UNREAD_FIELD")
	private class Context {
		// Mustache will reflectively pull these fields by name.
		private final List<MeasureConfig> quality;
		private final List<MeasureConfig> aci;
		private final List<MeasureConfig> ia;

		private final Function<String, Object> generateIpop =
				uuid -> generateSubpopulation(uuid, PopulationValue.IPOP);
		private final Function<String, Object> generateDenom =
				uuid -> generateSubpopulation(uuid, PopulationValue.DENOM);
		private final Function<String, Object> generateDenex =
				uuid -> generateSubpopulation(uuid, PopulationValue.DENEX);
		private final Function<String, Object> generateDenexcep =
				uuid -> generateSubpopulation(uuid, PopulationValue.DENEXCEP);
		private final Function<String, Object> generateNumer =
				uuid -> generateSubpopulation(uuid, PopulationValue.NUMER);
		private final Function<String, Object> generatePerformanceRate = this::generatePerformanceRate;

		private Context(List<MeasureConfig> quality, List<MeasureConfig> aci, List<MeasureConfig> ia) {
			this.quality = quality;
			this.aci = aci;
			this.ia = ia;
		}

		private String generatePerformanceRate(Object uuid) {
			Map<String, Object> ctx = new HashMap<>();
			ctx.put("uuid", uuid);
			return performanceRate.execute(new StringWriter(), ctx).toString();
		}

		private String generateSubpopulation(Object uuid, PopulationValue type) {
			if (uuid == null || ((String) uuid).isEmpty()) {
				return "";
			}

			Map<String, Object> ctx = new HashMap<>();
			ctx.put("uuid", uuid);
			ctx.put("label", type.measure);
			ctx.put("value", type.value / PopulationValue.getSubPopCount());
			ctx.put("total", type.value);

			return subpopulation.execute(new StringWriter(), ctx).toString();
		}
	}

	private enum PopulationValue {
		IPOP(SubPopulationLabel.IPOP, 100),
		DENOM(SubPopulationLabel.DENOM, 100),
		DENEX(SubPopulationLabel.DENEX, 10),
		DENEXCEP(SubPopulationLabel.DENEXCEP, 10),
		NUMER(SubPopulationLabel.NUMER, 80);

		private final String measure;
		private final int value;

		PopulationValue(SubPopulationLabel measure, int factor) {
			this.measure = measure.name();
			this.value = factor * getSubPopCount();
		}

		private static int getSubPopCount() {
			return 12;
		}
	}
}
