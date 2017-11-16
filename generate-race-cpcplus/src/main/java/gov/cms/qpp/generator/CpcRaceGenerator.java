package gov.cms.qpp.generator;

import com.google.common.base.Strings;
import gov.cms.qpp.conversion.model.TemplateId;
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
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;


public class CpcRaceGenerator {
	private static FileSystem fileSystem = FileSystems.getDefault();

	public static void main(String... args) {
		Collection<Path> filenames = getFiles(args);
		filenames.forEach(file -> {
			Document document = addSupplementalRaceData(file);
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

	static Document addSupplementalRaceData(Path path) {
		try {
			SAXBuilder saxBuilder = new SAXBuilder();
			InputStream inputStream = Files.newInputStream(path);
			Document document = saxBuilder.build(inputStream);

			Element rootElement = document.getDocument().getRootElement();

			rootElement.detach();

			Namespace rootNamespace = rootElement.getNamespace();
			Element initialComponent = rootElement.getChild("component", rootNamespace);
			Element structuredBody = initialComponent.getChild("structuredBody", rootNamespace);
			Element	innerComponent = structuredBody.getChild("component", rootNamespace);

			Element measureSection = innerComponent.getChild("section", rootNamespace);

			List<Element> measureEntries = measureSection.getChildren("entry", rootNamespace);
			Element performancePeriod = measureEntries.remove(0);

			int index;
			for (index = 0; index < measureEntries.size(); index++) {
				Element measureEntry = measureEntries.get(index);
				Element organizer = measureEntry.getChild("organizer", rootNamespace);

				List<Element> components = organizer.getChildren("component", rootNamespace);

				for (Element component: components) {
					Element observation = component.getChild("observation", rootNamespace);
					String measureDataTemplateId =
							observation.getChildren("templateId", rootNamespace).get(1)
									.getAttributeValue("root");
					if (TemplateId.MEASURE_DATA_CMS_V2.getRoot().equalsIgnoreCase(measureDataTemplateId)) {
						observation.addContent(createRaceElement(rootElement.getNamespacesInScope().get(2), "2076-8"));
						observation.addContent(createRaceElement(rootElement.getNamespacesInScope().get(2),"2131-1"));
					}
				}
			}
			measureEntries.add(0, performancePeriod);
			document.addContent(rootElement);

			return document;
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		return null;
	}

	static Element createRaceElement(Namespace xsiNamespace, String raceCode) {
		Element entryRelationship =  new Element("entryRelationship");
		Element observation =  new Element("observation");
		addOuterObservationChildren(observation, xsiNamespace, raceCode);

		entryRelationship.addContent(observation);

		return entryRelationship;
	}

	static void addOuterObservationChildren(Element observation, Namespace xsiNamespace, String raceCode) {
		Element templateId =  new Element("templateId");
		templateId.setAttribute("root", "2.16.840.1.113883.10.20.27.3.8");
		templateId.setAttribute("extension", "2016-09-01");

		Element raceTemplate =  new Element("templateId");
		raceTemplate.setAttribute("root", "2.16.840.1.113883.10.20.27.3.19");
		raceTemplate.setAttribute("extension","2016-11-01");

		Element id =  new Element("id");
		id.setAttribute("root", "D5E68231-5760-11E7-1256-09173F13E4C5");

		Element code =  new Element("code");
		code.setAttribute("code", "72826-1");
		code.setAttribute("codeSystem", "2.16.840.1.113883.6.1");
		code.setAttribute("codeSystemName", "LOINC");
		code.setAttribute("displayName", "Race");

		Element statusCode =  new Element("statusCode");
		statusCode.setAttribute("code", "completed");

		Element effectiveTime = new Element("effectiveTime");
		Element low =  new Element("low");
		low.setAttribute("value", "20170101");

		Element high =  new Element("high");
		high.setAttribute("value", "20171231");

		effectiveTime.addContent(low);
		effectiveTime.addContent(high);

		Element value =  new Element("value");
		value.setAttribute("type","CD", xsiNamespace);
		value.setAttribute("code", raceCode);
		value.setAttribute("codeSystem", "2.16.840.1.113883.6.238");
		value.setAttribute("codeSystemName", "Race &amp; Ethnicity - CDC");
		value.setAttribute("displayName", "Hawaiian or Pacific Islander");

		observation.addContent(templateId);
		observation.addContent(raceTemplate);
		observation.addContent(id);
		observation.addContent(code);
		observation.addContent(statusCode);
		observation.addContent(effectiveTime);
		observation.addContent(value);
		observation.addContent(createAggregateCountEntry(xsiNamespace));
	}

	static Element createAggregateCountEntry(Namespace xsiNamespace) {
		Element aggregateCountEntry =  new Element("entryRelationship");
		aggregateCountEntry.setAttribute("typeCode","SUBJ");
		aggregateCountEntry.setAttribute("inversionInd","true");

		Element observation =  new Element("observation");
		observation.setAttribute("classCode","OBS");
		observation.setAttribute("moodCode","EVN");

		Element aggregateCountTemplateId =  new Element("templateId");
		aggregateCountTemplateId.setAttribute("root","2.16.840.1.113883.10.20.27.3.3");
		Element templateId =  new Element("templateId");
		templateId.setAttribute("root","2.16.840.1.113883.10.20.27.3.24");

		Element code =  new Element("code");
		code.setAttribute("code","MSRAGG");
		code.setAttribute("codeSystem","2.16.840.1.113883.5.4");
		code.setAttribute("codeSystemName","ActCode");
		code.setAttribute("displayName","rate aggregation");

		Element statusCode =  new Element("statusCode");
		statusCode.setAttribute("code", "completed");

		Element value =  new Element("value");
		value.setAttribute("type", "INT", xsiNamespace);
		value.setAttribute("value", "250");

		Element methodCode =  new Element("methodCode");
		methodCode.setAttribute("code", "COUNT");
		methodCode.setAttribute("codeSystem", "2.16.840.1.113883.5.84");
		methodCode.setAttribute("codeSystemName", "ObservationMethod");
		methodCode.setAttribute("displayName", "Count");

		observation.addContent(aggregateCountTemplateId);
		observation.addContent(code);
		observation.addContent(statusCode);
		observation.addContent(value);
		observation.addContent(methodCode);
		aggregateCountEntry.addContent(observation);

		return aggregateCountEntry;
	}

	static void writeFile(Document document) {
		try {
			FileWriter writer = new FileWriter("test.xml");
			XMLOutputter output = new XMLOutputter();
			output.setFormat(Format.getPrettyFormat());
			output.output(document, writer);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
