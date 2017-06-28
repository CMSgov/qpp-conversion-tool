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
		//empty constructor for jackson
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public SubPopulation getElectronicMeasureUuids() {
		return electronicMeasureUuids;
	}

	public void setElectronicMeasureUuids(final SubPopulation electronicMeasureUuids) {
		this.electronicMeasureUuids = electronicMeasureUuids;
	}
}
