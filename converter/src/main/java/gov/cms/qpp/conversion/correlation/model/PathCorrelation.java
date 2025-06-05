package gov.cms.qpp.conversion.correlation.model;

import java.util.ArrayList;
import java.util.Collections;
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

	/**
	 * Returns an unmodifiable view of the templates list.
	 */
	public List<Template> getTemplates() {
		return Collections.unmodifiableList(templates);
	}

	/**
	 * Replaces the internal templates list with a defensive copy of the provided list.
	 */
	public void setTemplates(List<Template> templates) {
		if (templates == null) {
			this.templates = new ArrayList<>();
		} else {
			this.templates = new ArrayList<>(templates);
		}
	}

	/**
	 * Returns an unmodifiable view of the correlations list.
	 */
	public List<Correlation> getCorrelations() {
		return Collections.unmodifiableList(correlations);
	}

	/**
	 * Replaces the internal correlations list with a defensive copy of the provided list.
	 */
	public void setCorrelations(List<Correlation> correlations) {
		if (correlations == null) {
			this.correlations = new ArrayList<>();
		} else {
			this.correlations = new ArrayList<>(correlations);
		}
	}
}
