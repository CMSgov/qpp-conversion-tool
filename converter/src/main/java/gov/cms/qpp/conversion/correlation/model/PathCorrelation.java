package gov.cms.qpp.conversion.correlation.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	"uriSubstitution",
	"templates",
	"correlations"
})
public class PathCorrelation {

	@JsonProperty("uriSubstitution")
	private String uriSubstitution;
	@JsonProperty("templates")
	private List<Template> templates = new ArrayList<Template>();
	@JsonProperty("correlations")
	private List<Correlation> correlations = new ArrayList<Correlation>();

	@JsonProperty("uriSubstitution")
	public String getUriSubstitution() {
		return uriSubstitution;
	}

	@JsonProperty("uriSubstitution")
	public void setUriSubstitution(String uriSubstitution) {
		this.uriSubstitution = uriSubstitution;
	}

	@JsonProperty("templates")
	public List<Template> getTemplates() {
		return templates;
	}

	@JsonProperty("templates")
	public void setTemplates(List<Template> templates) {
		this.templates = templates;
	}

	@JsonProperty("correlations")
	public List<Correlation> getCorrelations() {
		return correlations;
	}

	@JsonProperty("correlations")
	public void setCorrelations(List<Correlation> correlations) {
		this.correlations = correlations;
	}

}
