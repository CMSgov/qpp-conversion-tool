package gov.cms.qpp.conversion.model.validation;

import java.util.List;

public class MeasureConfigs {

	private List<MeasureConfig> configurations;

	public MeasureConfigs() {
		// empty for jackson
	}

	public List<MeasureConfig> getMeasureConfigs() {
		return configurations;
	}

	public void setMeasureConfigs(List<MeasureConfig> measureConfigs) {
		this.configurations = measureConfigs;
	}

}
