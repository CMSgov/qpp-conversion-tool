package gov.cms.qpp;

import gov.cms.qpp.acceptance.helper.MarkupManipulator;
import gov.cms.qpp.conversion.Converter;
import gov.cms.qpp.conversion.InputStreamSupplierSource;
import gov.cms.qpp.conversion.correlation.PathCorrelator;
import gov.cms.qpp.conversion.model.error.AllErrors;
import gov.cms.qpp.conversion.model.error.Detail;
import gov.cms.qpp.conversion.model.error.Error;
import gov.cms.qpp.conversion.model.error.TransformException;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MarkupManipulationHandler {
	private static final String NAMESPACE_URI = "urn:hl7-org:v3";
	private MarkupManipulator manipulator;

	public MarkupManipulationHandler(String path) throws ParserConfigurationException, SAXException, IOException {
		manipulator = new MarkupManipulator.MarkupManipulatorBuilder()
				.setPathname(path)
				.setNsAware(true)
				.build();
	}

	public List<Detail> executeScenario(String templateId, String attribute, boolean remove) {
		String xpath = getPath(templateId, attribute);
		return executeScenario(xpath, remove);
	}

	public List<Detail> executeScenario(String xpath, boolean remove) {
		InputStream inStream = manipulator.upsetTheNorm(xpath, remove);
		Converter converter = new Converter(
				new InputStreamSupplierSource(xpath, () -> inStream));
		try {
			converter.transform();
		} catch (TransformException exception) {
			AllErrors errors = exception.getDetails();
			return errors.getErrors().stream().map(Error::getDetails).flatMap(List::stream).collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	public String getPath(String templateId, String attribute) {
		String path = PathCorrelator.getXpath(templateId, attribute, NAMESPACE_URI);
		if (path == null) {
			System.out.println("Bad combo templateId: " + templateId + " attribute: " + attribute);
		}
		return "//" + path;
	}

	public String getCannedPath(CannedPath cannedPath) {
		return cannedPath.path;
	}

	public enum CannedPath {

		ECQM_PARENT("/*[local-name() = 'ClinicalDocument' and namespace-uri() = 'urn:hl7-org:v3']/*[local-name() = 'component' and namespace-uri() = 'urn:hl7-org:v3']/*[local-name() = 'structuredBody' and namespace-uri() = 'urn:hl7-org:v3']/*[local-name() = 'component' and namespace-uri() = 'urn:hl7-org:v3'][1]/*[local-name() = 'section' and namespace-uri() = 'urn:hl7-org:v3']/*[local-name() = 'entry' and namespace-uri() = 'urn:hl7-org:v3'][1]/*[local-name() = 'organizer' and namespace-uri() = 'urn:hl7-org:v3']/..");

		private String path;

		CannedPath(String path) {
			this.path = path;
		}
	}
}
