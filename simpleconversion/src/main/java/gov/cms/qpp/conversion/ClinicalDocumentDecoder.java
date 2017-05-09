package gov.cms.qpp.conversion;

import gov.cms.qpp.model.ClinicalDocument;
import org.xml.sax.Attributes;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ClinicalDocumentDecoder extends TieredDecoder {

	private Map<String, Consumer<String>> legend = new HashMap<>();
	private ClinicalDocument doc;

	public ClinicalDocumentDecoder() {
		doc = new ClinicalDocument();

		legend.put("2.16.840.1.113883.3.249.7", doc::setProgramName); //program name
		legend.put("2.16.840.1.113883.4.6", doc::setNationalProviderIdentifier); //national provider
		legend.put("2.16.840.1.113883.4.2", doc::setTaxpayerIdentificationNumber); //tax provider
	}

	private static final String COMPONENT_ELEMENT =
			"./ns:component/ns:structuredBody/ns:component";

	@Override
	public void handleStartElement(String uri, String localName, String qName, Attributes attributes) {
		if (qName.equals("id") && legend.get(attributes.getValue("root")) != null) {
			legend.get(attributes.getValue("root")).accept(attributes.getValue("extension"));
		}
	}

	@Override
	public Object exportDecoded() {
		return doc;
	}

	@Override
	public void associateWith(TieredDecoder decoder) {
		//no op
	}

	public void addMeasurement(Object measurement) {
		doc.getMeasurements().add(measurement);
	}
}
