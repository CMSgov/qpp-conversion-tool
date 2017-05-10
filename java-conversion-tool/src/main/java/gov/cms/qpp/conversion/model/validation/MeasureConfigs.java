package gov.cms.qpp.conversion.model.validation;

import java.util.List;

public class MeasureConfigs {

	private List<MeasureConfig> configurations;

	public MeasureConfigs() {
		// empty for jackson
	}

	public List<MeasureConfig> getConfigurations() {
		return configurations;
	}

	public void setConfigurations(List<MeasureConfig> measureConfigs) {
		this.configurations = measureConfigs;
	}

}
