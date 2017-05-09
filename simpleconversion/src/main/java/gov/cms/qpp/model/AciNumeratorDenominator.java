package gov.cms.qpp.model;


import java.util.HashMap;
import java.util.Map;

public class AciNumeratorDenominator {
	private String measureId;
	private Map<String, Integer> value = new HashMap<>();

	public String getMeasureId() {
		return measureId;
	}

	public void setMeasureId(String measureId) {
		this.measureId = measureId;
	}

	public void setNumerator(int numerator) {
		value.put("numerator", numerator);
	}

	public void setDenominator(int denominator) {
		value.put("denominator", denominator);
	}

	@Override
	public String toString() {
		return "AciNumeratorDenominator{" +
				"measureId='" + measureId + '\'' +
				", value=" + value +
				'}';
	}
}
