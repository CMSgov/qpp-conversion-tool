package gov.cms.qpp.conversion;


import gov.cms.qpp.model.AciNumeratorDenominator;
import gov.cms.qpp.model.AciSection;
import org.xml.sax.Attributes;

public class AciSectionDecoder extends TieredDecoder {

	private AciSection section;

	public AciSectionDecoder() {
		section = new AciSection();
		section.setCategory("aci");
	}

	@Override
	public void handleStartElement(String uri, String localName, String qName, Attributes attributes) {

	}

	@Override
	public Object exportDecoded() {
		return section;
	}

	@Override
	public void associateWith(TieredDecoder decoder) {
		((ClinicalDocumentDecoder) decoder).addMeasurement(this.exportDecoded());
	}

	public void addMeasurement(Object measurement) {
		section.getMeasurements().add((AciNumeratorDenominator) measurement);
	}
}
