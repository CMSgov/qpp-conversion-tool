package gov.cms.qpp.model;


import java.util.ArrayList;
import java.util.List;

public class AciSection {
	private String category = "aci";
	private List<Object> measurements = new ArrayList<>();

	public String getCategory() {
		return category;
	}

	public void addMeasurement(Object measurement) {
		measurements.add(measurement);
	}

	@Override
	public String toString() {
		return "AciSection{" +
				"category='" + category + '\'' +
				", measurements=" + measurements +
				'}';
	}
}
