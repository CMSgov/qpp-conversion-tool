package gov.cms.qpp.conversion.model.validation;

/**
 * Sub Population Identifiers Fields
 */
public class SubPopulation {
	private String initialPopulationUUID;
	private String denominatorUUID;
	private String denominatorExclusionsUUID;
	private String numeratorUUID;
	private String denominatorExceptionsUUID;
	private String strata1;
	private String strata2;

	public SubPopulation() {
		//Empty Constructor for Jackson
	}

	public String getInitialPopulationUUID() {
		return initialPopulationUUID;
	}

	public void setInitialPopulationUUID(String initialPopulationUUID) {
		this.initialPopulationUUID = initialPopulationUUID;
	}

	public String getDenominatorUUID() {
		return denominatorUUID;
	}

	public void setDenominatorUUID(String denominatorUUID) {
		this.denominatorUUID = denominatorUUID;
	}

	public String getDenominatorExclusionsUUID() {
		return denominatorExclusionsUUID;
	}

	public void setDenominatorExclusionsUUID(String denominatorExclusionsUUID) {
		this.denominatorExclusionsUUID = denominatorExclusionsUUID;
	}

	public String getNumeratorUUID() {
		return numeratorUUID;
	}

	public void setNumeratorUUID(String numeratorUUID) {
		this.numeratorUUID = numeratorUUID;
	}

	public String getDenominatorExceptionsUUID() {
		return denominatorExceptionsUUID;
	}

	public void setDenominatorExceptionsUUID(String denominatorExceptionsUUID) {
		this.denominatorExceptionsUUID = denominatorExceptionsUUID;
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
