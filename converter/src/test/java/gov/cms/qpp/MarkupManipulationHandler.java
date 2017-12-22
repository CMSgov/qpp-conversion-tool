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
		String xPath = getPath(templateId, attribute);
		InputStream inStream = manipulator.upsetTheNorm(xPath, remove);
		Converter converter = new Converter(
				new InputStreamSupplierSource(xPath, () -> inStream));
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
}
