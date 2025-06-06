package gov.cms.qpp.conversion.correlation.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a correlation with an ID and a list of configurations.
 */
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
	 * Returns a defensive copy of the internal list to avoid exposing mutable state.
	 *
	 * @return a new List containing the same CorrelationConfig elements
	 */
	public List<CorrelationConfig> getConfig() {
		return new ArrayList<>(config);
	}

	/**
	 * Replaces the internal list with a copy of the provided list, preventing external references
	 * from modifying internal state.
	 *
	 * @param config new list of CorrelationConfig
	 */
	public void setConfig(List<CorrelationConfig> config) {
		this.config = new ArrayList<>(config);
	}

}
