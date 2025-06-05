package gov.cms.qpp.conversion.model.validation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Holds stratification information about sub-populations.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Strata {

	private String name;

	/**
	 * Originally: private SubPopulation electronicMeasureUuids;
	 * We now store a defensive copy whenever set, and return a copy on get.
	 */
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
	 * Returns a defensive copy of the internal SubPopulation.
	 * If null, returns null.
	 */
	public SubPopulation getElectronicMeasureUuids() {
		if (electronicMeasureUuids == null) {
			return null;
		}
		return new SubPopulation(electronicMeasureUuids);
	}

	/**
	 * Stores a defensive copy of the incoming SubPopulation.
	 * If null, stores null.
	 */
	public void setElectronicMeasureUuids(final SubPopulation electronicMeasureUuids) {
		if (electronicMeasureUuids == null) {
			this.electronicMeasureUuids = null;
		} else {
			this.electronicMeasureUuids = new SubPopulation(electronicMeasureUuids);
		}
	}
}
