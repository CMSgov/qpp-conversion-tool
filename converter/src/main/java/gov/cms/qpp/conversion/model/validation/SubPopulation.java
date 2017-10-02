package gov.cms.qpp.conversion.model.validation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

import java.util.Collections;
import java.util.List;

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

	@JsonProperty("strata")
	private List<String> strata = Collections.emptyList();

	public SubPopulation() {
		//Empty Constructor for Jackson
	}

	public SubPopulation(SubPopulation subPop) {
		initialPopulationUuid = subPop.getInitialPopulationUuid();
		denominatorUuid = subPop.getDenominatorUuid();
		denominatorExclusionsUuid = subPop.getDenominatorExclusionsUuid();
		numeratorUuid = subPop.getNumeratorUuid();
		denominatorExceptionsUuid = subPop.getDenominatorExceptionsUuid();
		strata = subPop.getStrata();
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

	public List<String> getStrata() {
		return strata;
	}

	public void setStrata(List<String> strata) {
		this.strata = strata;
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
		if (strata != null ? !strata.equals(that.strata) : that.strata != null) {
			return false;
		}
		return reduceCognitiveComplexity(that);
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
		return Objects.hashCode(initialPopulationUuid, denominatorUuid, denominatorExclusionsUuid,
				numeratorUuid, denominatorExceptionsUuid, strata);
	}

}
