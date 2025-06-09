package gov.cms.qpp.conversion.model.validation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Holds stratification information about sub-populations.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Strata {

	private String name;

	@JsonProperty("eMeasureUuids")
	private SubPopulation electronicMeasureUuids;

	public Strata() {
		// empty constructor for Jackson
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Returns a defensive copy of the internal SubPopulation, so callers
	 * cannot mutate our internal representation.
	 */
	public SubPopulation getElectronicMeasureUuids() {
		if (this.electronicMeasureUuids == null) {
			return null;
		}
		// SubPopulation has a copy constructor, so we can return a new instance:
		return new SubPopulation(this.electronicMeasureUuids);
	}

	/**
	 * Sets the internal SubPopulation reference. If the caller passes null,
	 * we store null. Otherwise, we store their object directly (Jackson uses this during deserialization).
	 */
	public void setElectronicMeasureUuids(final SubPopulation electronicMeasureUuids) {
		this.electronicMeasureUuids = electronicMeasureUuids;
	}
}
