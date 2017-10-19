package gov.cms.qpp.generator;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import gov.cms.qpp.conversion.model.validation.MeasureConfig;
import gov.cms.qpp.conversion.model.validation.MeasureConfigs;
import gov.cms.qpp.conversion.model.validation.SubPopulations;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

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
import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class QrdaGenerator {
	private static List<MeasureConfig> measureConfigs = MeasureConfigs.getMeasureConfigs();

	private Mustache submission;
	private Mustache subpopulation;
	private Mustache performanceRate;

	private List<MeasureConfig> quality;
	private List<MeasureConfig> aci;
	private List<MeasureConfig> ia;

	public static void main(String... args) throws IOException, TransformerException,
			SAXException, ParserConfigurationException, XPathExpressionException {
		QrdaGenerator generator = new QrdaGenerator();
		generator.generate();
	}

	private QrdaGenerator() throws IOException, TransformerConfigurationException {
		MustacheFactory mf = new DefaultMustacheFactory();
		submission = mf.compile("submission-template.xml");
		subpopulation = mf.compile("subpopulation-template.xml");
		performanceRate = mf.compile("performance-rate-template.xml");

		quality = filterQualityMeasures();
		aci = filterAciMeasures();
		ia = filterIaMeasures();
	}

	private List<MeasureConfig> filterQualityMeasures() throws IOException {
		return measureConfigs.stream()
				.filter(measureConfig -> measureConfig.getCategory().equals("quality") &&
						measureConfig.getElectronicMeasureId() != null &&
						!measureConfig.getElectronicMeasureId().isEmpty())
				.collect(Collectors.toList());
	}

	private List<MeasureConfig> filterAciMeasures() {
		return measureConfigs.stream()
				.filter(measureConfig -> measureConfig.getCategory().equals("aci"))
				.collect(Collectors.toList());
	}

	private List<MeasureConfig> filterIaMeasures() {
		return measureConfigs.stream()
				.filter(measureConfig -> measureConfig.getCategory().equals("ia"))
				.collect(Collectors.toList());
	}

	private void generate() throws IOException, ParserConfigurationException,
			SAXException, TransformerException, XPathExpressionException {
		StringWriter writer = new StringWriter();

		submission.execute(writer, new Context(quality, aci, ia)).flush();
		prettyPrint(writer);
	}

	private void prettyPrint(StringWriter writer) throws TransformerException, IOException,
			SAXException, ParserConfigurationException, XPathExpressionException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		InputSource is = new InputSource(new InputStreamReader(
				new ByteArrayInputStream(writer.toString().getBytes())));

		Document original = dBuilder.parse(is);
		removeWhitespace(original);
		moreThanMeetsTheEye().transform(new DOMSource(original), getDestination());
	}

	private void removeWhitespace(Document document) throws XPathExpressionException {
		document.normalize();
		XPath xPath = XPathFactory.newInstance().newXPath();
		NodeList nodeList = (NodeList) xPath.evaluate("//text()[normalize-space()='']",
				document,
				XPathConstants.NODESET);

		for (int i = 0; i < nodeList.getLength(); ++i) {
			Node node = nodeList.item(i);
			node.getParentNode().removeChild(node);
		}
	}

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

	private Result getDestination() throws IOException {
		Instant instant = Instant.now();
		Writer writer = new FileWriter("./sample-files/generated" + instant.getEpochSecond() + ".xml");
		return new StreamResult(writer);
	}

	private class Context {
		List<MeasureConfig> quality;
		List<MeasureConfig> aci;
		List<MeasureConfig> ia;
		Function<String, Object> generateIpop = uuid -> generateSubpopulation(uuid, SubPopulations.IPOP);
		Function<String, Object> generateDenom = uuid -> generateSubpopulation(uuid, SubPopulations.DENOM);
		Function<String, Object> generateDenex = uuid -> generateSubpopulation(uuid, SubPopulations.DENEX);
		Function<String, Object> generateDenexcep = uuid -> generateSubpopulation(uuid, SubPopulations.DENEXCEP);
		Function<String, Object> generateNumer = uuid -> generateSubpopulation(uuid, SubPopulations.NUMER);
		Function<String, Object> generatePerformanceRate = this::generatePerformanceRate;


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

		private String generateSubpopulation(Object uuid, String type) {
			if (uuid == null || ((String) uuid).isEmpty()) {
				return "";
			}

			Map<String, Object> ctx = new HashMap<>();
			ctx.put("uuid", uuid);
			ctx.put("label", type);
			ctx.put("value", 10);
			ctx.put("total", 120);

			return subpopulation.execute(new StringWriter(), ctx).toString();
		}
	}
}
