package gov.cms.qpp.conversion.model.validation;

import java.util.List;

public class MeasureConfigs {

	private List<MeasureConfig> measureConfigs;

	public MeasureConfigs() {
		// empty for jackson
	}

	public List<MeasureConfig> getMeasureConfigs() {
		return measureConfigs;
	}

	public void setMeasureConfigs(List<MeasureConfig> measureConfigs) {
		this.measureConfigs = measureConfigs;
	}

}
