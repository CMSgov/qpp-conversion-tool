package gov.cms.qpp;


import com.sun.org.apache.xerces.internal.dom.DeferredAttrNSImpl;
import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.correlation.PathCorrelator;
import gov.cms.qpp.conversion.decode.ClinicalDocumentDecoder;
import gov.cms.qpp.conversion.decode.MultipleTinsDecoder;
import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.TransformException;
import gov.cms.qpp.conversion.validate.ClinicalDocumentValidator;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static gov.cms.qpp.conversion.model.error.ValidationErrorMatcher.hasValidationErrorsIgnoringPath;
import static org.junit.Assert.assertThat;

public class SingularAttributeTest {

	private static Document document;
	private static DocumentBuilderFactory dbf;
	private TransformerFactory tf = TransformerFactory.newInstance();
	private XPathFactory xpf = XPathFactory.newInstance();

	@BeforeClass
	public static void before() {
		dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
	}

	@Test
	public void doubleUpProgramName() {
		List<Detail> details = executeScenario(TemplateId.CLINICAL_DOCUMENT,
				ClinicalDocumentDecoder.PROGRAM_NAME, false);

		assertThat("error should be about missing missing program name", details,
				hasValidationErrorsIgnoringPath(
						ClinicalDocumentValidator.CONTAINS_PROGRAM_NAME));
	}

	@Test
	public void noProgramName() {
		List<Detail> details = executeScenario(TemplateId.CLINICAL_DOCUMENT,
				ClinicalDocumentDecoder.PROGRAM_NAME, true);

		assertThat("error should be about missing missing program name", details,
				hasValidationErrorsIgnoringPath(
						ClinicalDocumentValidator.CONTAINS_PROGRAM_NAME,
						ClinicalDocumentValidator.INCORRECT_PROGRAM_NAME));
	}

	@Test
	public void doubleUpTin() {
		List<Detail> details = executeScenario(TemplateId.CLINICAL_DOCUMENT,
				MultipleTinsDecoder.TAX_PAYER_IDENTIFICATION_NUMBER, false);

		assertThat("error should be about missing missing program name", details,
				hasValidationErrorsIgnoringPath(
						ClinicalDocumentValidator.CONTAINS_TAX_ID_NUMBER));
	}

	@Test
	public void noTin() {
		List<Detail> details = executeScenario(TemplateId.CLINICAL_DOCUMENT,
				MultipleTinsDecoder.TAX_PAYER_IDENTIFICATION_NUMBER, true);

		assertThat("error should be about missing missing program name", details,
				hasValidationErrorsIgnoringPath(
						ClinicalDocumentValidator.CONTAINS_TAX_ID_NUMBER));
	}

	//TODO: look into ENTITY_TYPE w/ multiple tin example
	//TODO: MultipleTinsDecoder.NATIONAL_PROVIDER_IDENTIFIER

	private List<Detail> executeScenario(TemplateId templateId, String attribute, boolean remove) {
		String xPath = getPath(templateId, attribute);
		InputStream inStream = upsetTheNorm(xPath, remove);
		Converter converter = new Converter(inStream);
		List<Detail> details = null;
		try {
			converter.transform();
		} catch (TransformException exception) {
			AllErrors errors = exception.getDetails();
			details = errors.getErrors().get(0).getDetails();
		}
		return details;
	}

	private String getPath(TemplateId templateId, String attribute) {
		return "//" + PathCorrelator.getXpath(templateId.name(), attribute, "urn:hl7-org:v3");
	}

	private InputStream upsetTheNorm(String xPath, boolean remove) {
		try {
			document = dbf.newDocumentBuilder().parse(
					new File("../qrda-files/valid-QRDA-III-latest.xml"));
			XPath xpath = xpf.newXPath();
			XPathExpression expression = xpath.compile(xPath);

			Node searchedNode = (Node) expression.evaluate(document, XPathConstants.NODE);
			Element owningElement = ((DeferredAttrNSImpl) searchedNode).getOwnerElement();
			Node containingParent = owningElement.getParentNode();

			if (remove) {
				containingParent.removeChild(owningElement);
			} else {
				containingParent.appendChild(owningElement.cloneNode(true));
			}

			Transformer t = tf.newTransformer();
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			Result result = new StreamResult(os);
			t.transform(new DOMSource(document), result);
			return new ByteArrayInputStream(os.toByteArray());
		} catch (ParserConfigurationException | XPathExpressionException | TransformerException |
				IOException | SAXException ex) {
			throw new RuntimeException(ex);
		}
	}


//	@Test
//	public void doubleUpMeasurePerformedRnR() {
//		String xPath = getPath(
//				TemplateId.ACI_MEASURE_PERFORMED_REFERENCE_AND_RESULTS,
//				AciMeasurePerformedRnRDecoder.MEASURE_ID);
//		doubleUp(xPath);
//	}
}
