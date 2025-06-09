package gov.cms.qpp.conversion.correlation.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Holds URI substitution data along with lists of templates and correlations.
 */
public class PathCorrelation {

	private String uriSubstitution;
	private final List<Template> templates = new ArrayList<>();
	private final List<Correlation> correlations = new ArrayList<>();

	/**
	 * Gets the URI substitution.
	 *
	 * @return the URI substitution string
	 */
	public String getUriSubstitution() {
		return uriSubstitution;
	}

	/**
	 * Sets the URI substitution.
	 *
	 * @param uriSubstitution the URI substitution to set
	 */
	public void setUriSubstitution(String uriSubstitution) {
		this.uriSubstitution = uriSubstitution;
	}

	/**
	 * Gets a copy of the internal list of templates.
	 *
	 * @return a new List containing all Template objects
	 */
	public List<Template> getTemplates() {
		return new ArrayList<>(templates);
	}

	/**
	 * Replaces the internal list of templates with a copy of the provided list.
	 *
	 * @param templates the list of Template objects to store
	 */
	public void setTemplates(List<Template> templates) {
		this.templates.clear();
		if (templates != null) {
			this.templates.addAll(templates);
		}
	}

	/**
	 * Gets a copy of the internal list of correlations.
	 *
	 * @return a new List containing all Correlation objects
	 */
	public List<Correlation> getCorrelations() {
		return new ArrayList<>(correlations);
	}

	/**
	 * Replaces the internal list of correlations with a copy of the provided list.
	 *
	 * @param correlations the list of Correlation objects to store
	 */
	public void setCorrelations(List<Correlation> correlations) {
		this.correlations.clear();
		if (correlations != null) {
			this.correlations.addAll(correlations);
		}
	}
}
