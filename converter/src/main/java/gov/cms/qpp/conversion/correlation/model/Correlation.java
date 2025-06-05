package gov.cms.qpp.conversion.correlation.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Correlation {

	private String correlationId;
	private List<CorrelationConfig> config = new ArrayList<>();

	public String getCorrelationId() {
		return correlationId;
	}

	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}

	/**
	 * Returns an unmodifiable view of the configuration list.
	 */
	public List<CorrelationConfig> getConfig() {
		return Collections.unmodifiableList(config);
	}

	/**
	 * Replaces the internal config list with a defensive copy of the provided list.
	 */
	public void setConfig(List<CorrelationConfig> config) {
		if (config == null) {
			this.config = new ArrayList<>();
		} else {
			this.config = new ArrayList<>(config);
		}
	}
}
