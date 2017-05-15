package gov.cms.qpp.conversion.model.validation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Sub Population Identifiers Fields
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubPopulation {

	@JsonProperty("initialPopulationUUID")
	private String initialPopulationUuid;

	@JsonProperty("denominatorUUID")
	private String denominatorUuid;

	@JsonProperty("denominatorExclusionsUUID")
	private String denominatorExclusionsUuid;

	@JsonProperty("numeratorUUID")
	private String numeratorUuid;

	@JsonProperty("denominatorExceptionsUUID")
	private String denominatorExceptionsUuid;

	private String strata1;
	private String strata2;

	public SubPopulation() {
		//Empty Constructor for Jackson
	}

	public String getInitialPopulationUuid() {
		return initialPopulationUuid;
	}

	public void setInitialPopulationUuid(String initialPopulationUuid) {
		this.initialPopulationUuid = initialPopulationUuid;
	}

	public String getDenominatorUuid() {
		return denominatorUuid;
	}

	public void setDenominatorUuid(String denominatorUuid) {
		this.denominatorUuid = denominatorUuid;
	}

	public String getDenominatorExclusionsUuid() {
		return denominatorExclusionsUuid;
	}

	public void setDenominatorExclusionsUuid(String denominatorExclusionsUuid) {
		this.denominatorExclusionsUuid = denominatorExclusionsUuid;
	}

	public String getNumeratorUuid() {
		return numeratorUuid;
	}

	public void setNumeratorUuid(String numeratorUuid) {
		this.numeratorUuid = numeratorUuid;
	}

	public String getDenominatorExceptionsUuid() {
		return denominatorExceptionsUuid;
	}

	public void setDenominatorExceptionsUuid(String denominatorExceptionsUuid) {
		this.denominatorExceptionsUuid = denominatorExceptionsUuid;
	}

	public String getStrata1() {
		return strata1;
	}

	public void setStrata1(String strata1) {
		this.strata1 = strata1;
	}

	public String getStrata2() {
		return strata2;
	}

	public void setStrata2(String strata2) {
		this.strata2 = strata2;
	}
}
