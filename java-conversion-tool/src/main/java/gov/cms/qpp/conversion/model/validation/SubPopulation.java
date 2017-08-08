package gov.cms.qpp.conversion.model.validation;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Sub Population Identifiers Fields
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubPopulation {

	@JsonProperty("initialPopulationUuid")
	private String initialPopulationUuid;

	@JsonProperty("denominatorUuid")
	private String denominatorUuid;

	@JsonProperty("denominatorExclusionUuid")
	private String denominatorExclusionsUuid;

	@JsonProperty("numeratorUuid")
	private String numeratorUuid;

	@JsonProperty("denominatorExceptionUuid")
	private String denominatorExceptionsUuid;

	private String strata1;
	private String strata2;

	public SubPopulation() {
		//Empty Constructor for Jackson
	}

	public SubPopulation(SubPopulation subPop) {
		initialPopulationUuid = subPop.getInitialPopulationUuid();
		denominatorUuid = subPop.getDenominatorUuid();
		denominatorExclusionsUuid = subPop.getDenominatorExclusionsUuid();
		numeratorUuid = subPop.getNumeratorUuid();
		denominatorExceptionsUuid = subPop.getDenominatorExceptionsUuid();
		strata1 = subPop.getStrata1();
		strata2 = subPop.getStrata2();
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

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		SubPopulation that = (SubPopulation) o;

		return Objects.equals(initialPopulationUuid, that.initialPopulationUuid)
				&& Objects.equals(denominatorUuid, that.denominatorUuid)
				&& Objects.equals(denominatorExclusionsUuid, that.denominatorExclusionsUuid)
				&& Objects.equals(denominatorExceptionsUuid, that.denominatorExceptionsUuid)
				&& Objects.equals(numeratorUuid, that.numeratorUuid)
				&& Objects.equals(strata1, that.strata1)
				&& Objects.equals(strata2, that.strata2);
	}

	@Override
	public int hashCode() {
		return Objects.hash(initialPopulationUuid, denominatorUuid, denominatorExclusionsUuid,
				denominatorExceptionsUuid, numeratorUuid, strata1, strata2);
	}
}
