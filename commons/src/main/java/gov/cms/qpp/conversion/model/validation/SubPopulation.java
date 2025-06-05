package gov.cms.qpp.conversion.model.validation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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

	@JsonProperty("numeratorExclusionUuid")
	private String numeratorExclusionUuid;

	@JsonProperty("denominatorExceptionUuid")
	private String denominatorExceptionsUuid;

	/**
	 * Originally: private List<String> strata = Collections.emptyList();
	 * We keep it private and mutable, but only expose via unmodifiable copy.
	 */
	@JsonProperty("strata")
	private List<String> strata = Collections.emptyList();

	public SubPopulation() {
		// Empty Constructor for Jackson
	}

	/**
	 * Copy constructor performs a deep-enough copy of the strata list
	 */
	public SubPopulation(SubPopulation subPop) {
		this.initialPopulationUuid = subPop.getInitialPopulationUuid();
		this.denominatorUuid = subPop.getDenominatorUuid();
		this.denominatorExclusionsUuid = subPop.getDenominatorExclusionsUuid();
		this.numeratorUuid = subPop.getNumeratorUuid();
		this.numeratorExclusionUuid = subPop.getNumeratorExclusionUuid();
		this.denominatorExceptionsUuid = subPop.getDenominatorExceptionsUuid();
		// Defensive copy of the list
		this.strata = (subPop.getStrata() == null)
				? Collections.emptyList()
				: new ArrayList<>(subPop.getStrata());
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

	public String getNumeratorExclusionUuid() {
		return numeratorExclusionUuid;
	}

	public void setNumeratorExclusionUuid(final String numeratorExclusionUuid) {
		this.numeratorExclusionUuid = numeratorExclusionUuid;
	}

	public String getDenominatorExceptionsUuid() {
		return denominatorExceptionsUuid;
	}

	public void setDenominatorExceptionsUuid(String denominatorExceptionsUuid) {
		this.denominatorExceptionsUuid = denominatorExceptionsUuid;
	}

	/**
	 * Returns an unmodifiable copy of the internal strata list.
	 */
	public List<String> getStrata() {
		if (strata == null) {
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(new ArrayList<>(strata));
	}

	/**
	 * Makes a defensive copy of the incoming list so that external callers
	 * cannot mutate this SubPopulation's internal list.
	 */
	public void setStrata(List<String> strata) {
		if (strata == null) {
			this.strata = Collections.emptyList();
		} else {
			this.strata = new ArrayList<>(strata);
		}
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
				&& Objects.equals(numeratorUuid, that.numeratorUuid)
				&& Objects.equals(numeratorExclusionUuid, that.numeratorExclusionUuid)
				&& Objects.equals(denominatorExceptionsUuid, that.denominatorExceptionsUuid)
				&& Objects.equals(getStrata(), that.getStrata());
	}

	@Override
	public int hashCode() {
		return Objects.hash(
				initialPopulationUuid,
				denominatorUuid,
				denominatorExclusionsUuid,
				numeratorUuid,
				numeratorExclusionUuid,
				denominatorExceptionsUuid,
				getStrata()
		);
	}
}
