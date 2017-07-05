package gov.cms.qpp.conversion.model.validation;

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

		if (initialPopulationUuid != null
				? !initialPopulationUuid.equals(that.initialPopulationUuid) :
				that.initialPopulationUuid != null) {
			return false;
		}
		if (numeratorUuid != null
				? !numeratorUuid.equals(that.numeratorUuid) :
				that.numeratorUuid != null) {
			return false;
		}
		if (strata1 != null ? !strata1.equals(that.strata1) : that.strata1 != null) {
			return false;
		}
		boolean isCool = reduceCognitiveComplexity(that);
		if (isCool) {
			return strata2 != null ? strata2.equals(that.strata2) : that.strata2 == null;
		} else {
			return isCool;
		}

	}

	private boolean reduceCognitiveComplexity(SubPopulation that) {
		boolean isCool = true;
		if (denominatorUuid != null
				? !denominatorUuid.equals(that.denominatorUuid) :
				that.denominatorUuid != null) {
			isCool = false;
		}
		if (denominatorExclusionsUuid != null
				? !denominatorExclusionsUuid.equals(that.denominatorExclusionsUuid) :
				that.denominatorExclusionsUuid != null) {
			isCool = false;
		}
		if (denominatorExceptionsUuid != null
				? !denominatorExceptionsUuid.equals(that.denominatorExceptionsUuid) :
				that.denominatorExceptionsUuid != null) {
			isCool = false;
		}
		return isCool;
	}

	@Override
	public int hashCode() {
		int result = initialPopulationUuid != null ? initialPopulationUuid.hashCode() : 0;
		result = 31 * result + (denominatorUuid != null ? denominatorUuid.hashCode() : 0);
		result = 31 * result + (denominatorExclusionsUuid != null ? denominatorExclusionsUuid.hashCode() : 0);
		result = 31 * result + (numeratorUuid != null ? numeratorUuid.hashCode() : 0);
		result = 31 * result + (denominatorExceptionsUuid != null ? denominatorExceptionsUuid.hashCode() : 0);
		result = 31 * result + (strata1 != null ? strata1.hashCode() : 0);
		result = 31 * result + (strata2 != null ? strata2.hashCode() : 0);
		return result;
	}
}
