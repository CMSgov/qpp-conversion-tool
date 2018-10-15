package gov.cms.qpp.updater;

import com.google.common.base.Strings;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import gov.cms.qpp.conversion.model.TemplateId;
import gov.cms.qpp.conversion.model.validation.MeasureConfig;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class QrdaUpdater {
	private static FileSystem fileSystem = FileSystems.getDefault();
	private static final String MEASURE_DATA_FILE_NAME_2017 = "measures-data-2017.json";
	private static final String MEASURE_DATA_FILE_NAME = "measures-data.json";
	private static Map previousYearMeasureConfigMap =
		new MeasureConfigLoader(MEASURE_DATA_FILE_NAME_2017, false).getConfigurationMap();
	private static Map currentYearMeasureConfigMap =
		new MeasureConfigLoader(MEASURE_DATA_FILE_NAME, true).getConfigurationMap();

	public static void main(String... args) {
		Collection<Path> filenames = getFiles(args);
		filenames.parallelStream().forEach(file -> {
			Document document = updateQrdaMeasureData(file);
			writeFile(document);
		});
	}

	static Collection<Path> getFiles(String[] args) {
		Collection<Path> validFiles = new LinkedList<>();
		for (String arg : args) {
			validFiles.addAll(checkFiles(arg));
		}
		return validFiles;

	}

	static Collection<Path> checkFiles(String path) {
		Collection<Path> existingFiles = new LinkedList<>();

		if (Strings.isNullOrEmpty(path)) {
			return existingFiles;
		}

		Path file = fileSystem.getPath(path);
		if (Files.exists(file)) {
			existingFiles.add(file);
		}

		return existingFiles;
	}

	static Document updateQrdaMeasureData(Path path) {
		try {
			SAXBuilder saxBuilder = new SAXBuilder();
			InputStream inputStream = Files.newInputStream(path);
			Document document = saxBuilder.build(inputStream);
			Element rootElement = document.getDocument().getRootElement();
			rootElement.detach();
			Namespace rootNamespace = rootElement.getNamespace();
			List<Element> sections =
				rootElement.getChild("component", rootNamespace)
					.getChild("structuredBody", rootNamespace)
					.getChildren("component", rootNamespace);

			Element measureSection = sections.stream().filter(
				section -> TemplateId.MEASURE_SECTION_V2.equals(section.getChild("section", rootNamespace)
					.getChild("templateId", rootNamespace)
					.getAttribute("root").getValue())).findFirst().orElse(null);

			List<Element> measureEntries = measureSection.getChildren("entry", rootNamespace);

			for (Element measureEntry: measureEntries) {
				Element measureEntryOrganizer = measureEntry.getChild("organizer", rootNamespace);
				Element measureRnrTemplateId = measureEntryOrganizer.getChildren("templateId", rootNamespace)
					.stream()
					.filter(childElement ->
						TemplateId.MEASURE_REFERENCE_RESULTS_CMS_V2.equals(childElement.getAttribute("root").getValue()))
				.findAny().orElse(null);
				if (measureRnrTemplateId != null) {
					String currentMeasureUuid =
						measureEntryOrganizer.getChild("reference", rootNamespace)
						.getChild("externalDocument", rootNamespace)
						.getChild("id", rootNamespace)
						.getAttribute("extension").getValue();
					MeasureConfig previousYearMeasureConfig =
						(MeasureConfig) previousYearMeasureConfigMap.get(currentMeasureUuid);
					MeasureConfig currentYearMeasureConfig =
						(MeasureConfig) currentYearMeasureConfigMap.get(previousYearMeasureConfig.getElectronicMeasureId());

					List<Element> subpopulationComponents = measureEntryOrganizer.getChildren("component", rootNamespace);
					Long numberOfPerformanceRates = subpopulationComponents.stream().filter(
						element -> element.getChild("Observation", rootNamespace)
							.getChildren("templateId", rootNamespace).stream()
							.anyMatch(templateElements -> TemplateId.PERFORMANCE_RATE_PROPORTION_MEASURE
								.equals(templateElements.getAttribute("root").getValue()))).count();

					if (numberOfPerformanceRates == 1L) {
						subpopulationComponents.forEach( component ->  {
							Element componentObservation = component.getChild("Observation", rootNamespace);
							String currentSubpopulationType = componentObservation.getChild("value", rootNamespace)
								.getAttribute("code", rootNamespace)
								.getValue();
							Attribute subpopulationGuidAttribute =
								componentObservation.getChild("reference", rootNamespace)
									.getChild("externalObservation", rootNamespace)
									.getChild("id", rootNamespace)
									.getAttribute("root", rootNamespace);
							if ("IPOP".equals(currentSubpopulationType)) {
								subpopulationGuidAttribute.setValue(currentYearMeasureConfig.getStrata()
										.get(0).getElectronicMeasureUuids().getInitialPopulationUuid());
							} else if ("DENOM".equals(currentSubpopulationType)) {
								subpopulationGuidAttribute.setValue(currentYearMeasureConfig.getStrata()
									.get(0).getElectronicMeasureUuids().getDenominatorUuid());
							} else if ("NUMER".equals(currentSubpopulationType)) {
								subpopulationGuidAttribute.setValue(currentYearMeasureConfig.getStrata()
									.get(0).getElectronicMeasureUuids().getNumeratorUuid());
							} else if ("DENEXCEP".equals(currentSubpopulationType)) {
								subpopulationGuidAttribute.setValue(currentYearMeasureConfig.getStrata()
									.get(0).getElectronicMeasureUuids().getDenominatorExceptionsUuid());
							} else if ("DENEX".equals(currentSubpopulationType)) {
								subpopulationGuidAttribute.setValue(currentYearMeasureConfig.getStrata()
									.get(0).getElectronicMeasureUuids().getDenominatorExclusionsUuid());
							} else if (isPerformanceRate(componentObservation, rootNamespace)) {
								subpopulationGuidAttribute.setValue(currentYearMeasureConfig.getStrata()
									.get(0).getElectronicMeasureUuids().getNumeratorUuid());
							}
						});
					}
				}
			}
			document.addContent(rootElement);

			return document;
		}
		catch (Exception exc) {
			exc.printStackTrace();
		}
		return null;
	}

	private static boolean isPerformanceRate(Element componentObservation, Namespace rootNamespace) {
		return componentObservation.getChildren("templateId", rootNamespace).stream()
			.anyMatch(templateElements -> TemplateId.PERFORMANCE_RATE_PROPORTION_MEASURE
				.equals(templateElements.getAttribute("root").getValue()));
	}

	static void writeFile(Document document) {
		try {
			FileWriter writer = new FileWriter("test.xml");
			XMLOutputter output = new XMLOutputter();
			output.setFormat(Format.getPrettyFormat());
			output.output(document, writer);
			writer.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
