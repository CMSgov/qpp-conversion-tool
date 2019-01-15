package gov.cms.qpp.conversion.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CpcValidationInfo {
	private String npi;
	private String tin;
	private String apm;

	CpcValidationInfo() {
		// Empty Constructor for Jackson
	}

	CpcValidationInfo(String npi, String tin, String apm) {
		this.npi = npi;
		this.tin = tin;
		this.apm = apm;
	}

	public String getNpi() {
		return npi;
	}

	public void setNpi(final String npi) {
		this.npi = npi;
	}

	public String getTin() {
		return tin;
	}

	public void setTin(final String tin) {
		this.tin = tin;
	}

	@JsonProperty("apm_entity_id")
	public String getApm() {
		return apm;
	}

	@JsonProperty("apm_entity_id")
	public void setApm(final String apm) {
		this.apm = apm;
	}
}
