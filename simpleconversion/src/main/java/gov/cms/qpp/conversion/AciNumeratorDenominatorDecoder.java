package gov.cms.qpp.conversion;

import gov.cms.qpp.model.AciNumeratorDenominator;
import gov.cms.qpp.model.AciNumeratorDenominatorValue;
import org.xml.sax.Attributes;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class AciNumeratorDenominatorDecoder extends TieredDecoder {
	private AciNumeratorDenominator numDen;
	private AciNumeratorDenominatorValue value;
	private TemplateId context = TemplateId.DEFAULT;
	private Consumer<Integer> valueSetter;
	private Map<TemplateId, Consumer<Integer>> setterHash = new HashMap<>();

	public AciNumeratorDenominatorDecoder() {
		value = new AciNumeratorDenominatorValue();
		numDen = new AciNumeratorDenominator();
		numDen.setValue(value);
		setterHash.put(TemplateId.ACI_DENOMINATOR, value::setDenominator);
		setterHash.put(TemplateId.ACI_NUMERATOR, value::setNumerator);
	}

	@Override
	public void handleStartElement(String uri, String localName, String qName, Attributes attributes) {
		if (qName.equals("templateId")) {
			setContext(attributes);
		}

		if (context == TemplateId.ACI_AGGREGATE_COUNT && qName.equals("value")) {
			valueSetter.accept(Integer.valueOf(attributes.getValue("value")));
		}
	}

	private void setContext(Attributes attributes) {
		TemplateId template = TemplateId.getTypeById(
				attributes.getValue("root"), attributes.getValue("extension"));
		context = template;
		valueSetter = (setterHash.get(template) != null) ? setterHash.get(template) : valueSetter;
	}

	@Override
	public Object exportDecoded() {
		return numDen;
	}

	@Override
	public void associateWith(TieredDecoder decoder) {
		((AciSectionDecoder) decoder).addMeasurement(this.exportDecoded());
	}
}
