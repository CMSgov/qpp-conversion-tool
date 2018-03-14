package gov.cms.qpp.conversion.correlation.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	"correlationId",
	"config"
})
public class Correlation {

	@JsonProperty("correlationId")
	private String correlationId;
	@JsonProperty("config")
	private List<Config> config = new ArrayList<Config>();

	@JsonProperty("correlationId")
	public String getCorrelationId() {
		return correlationId;
	}

	@JsonProperty("correlationId")
	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}

	@JsonProperty("config")
	public List<Config> getConfig() {
		return config;
	}

	@JsonProperty("config")
	public void setConfig(List<Config> config) {
		this.config = config;
	}

}
