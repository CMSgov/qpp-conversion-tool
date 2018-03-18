package gov.cms.qpp.conversion.correlation.model;

import java.util.ArrayList;
import java.util.List;

public class Correlation {

	private String correlationId;
	private List<CorrelationConfig> config = new ArrayList<>();

	public String getCorrelationId() {
		return correlationId;
	}

	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}

	public List<CorrelationConfig> getConfig() {
		return config;
	}

	public void setConfig(List<CorrelationConfig> config) {
		this.config = config;
	}

}
