package gov.cms.qpp.conversion.correlation.model;

import java.util.ArrayList;
import java.util.List;

public class PathCorrelation {

	private String uriSubstitution;
	private List<Template> templates = new ArrayList<>();
	private List<Correlation> correlations = new ArrayList<>();

	public String getUriSubstitution() {
		return uriSubstitution;
	}

	public void setUriSubstitution(String uriSubstitution) {
		this.uriSubstitution = uriSubstitution;
	}

	public List<Template> getTemplates() {
		return templates;
	}

	public void setTemplates(List<Template> templates) {
		this.templates = templates;
	}

	public List<Correlation> getCorrelations() {
		return correlations;
	}

	public void setCorrelations(List<Correlation> correlations) {
		this.correlations = correlations;
	}

}
